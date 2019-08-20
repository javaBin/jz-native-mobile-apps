import UIKit
import UserNotifications
import SVProgressHUD


protocol SessionCellDelegate {
    func favoriteButtonTapped(cell: SessionTableViewCell!)
}

class SessionListViewController: UIViewController, UISearchBarDelegate, UITableViewDataSource, UITableViewDelegate, SessionCellDelegate {
    @IBOutlet weak var sessionSearchBar: UISearchBar!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var mySessionSegmentedControl: UISegmentedControl!
    
    var sessions: [Session]?
    var sections = Dictionary<String, Array<Session>>()
    var sortedSections = [String]()
    var filteredSections = Dictionary<String, Array<Session>>()
    var searchActive : Bool = false
    var refresher: UIRefreshControl?
    var sessionRepository: SessionRepository?
    var speakerRepository: SpeakerRepository?
    var mySessionRepository: MySessionRepository?
    var segmentedSelected = 0
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
       // sessionSearchBar.text = ""
        sessionSearchBar.resignFirstResponder()
        mySessionSegmentedControl.selectedSegmentIndex = segmentedSelected
    }
    
    func favoriteButtonTapped(cell: SessionTableViewCell!) {
        let findMySession = mySessionRepository!.getMySession(sessionId: cell.session.sessionId!)
        
        if findMySession != nil {
            mySessionRepository!.delete(item: findMySession!)
            cell.favoriteButton.setImage(UIImage.init(named: "ic_favorite_border_48pt"), for: .normal)
            
            let isGrantedNotificationAccess = UserDefaults.standard.bool(forKey: "notifySwitch")
            if isGrantedNotificationAccess {
                CommonNotificationUtil.scheduleNotification(session: cell.session!, withDate: nil, sessionRemove: true)
            }
            
        } else {
            let mySessionObject = MySession()
            mySessionObject.sessionId = cell.session!.sessionId!
            mySessionObject.startTime = cell.session!.startTime!
            mySessionObject.endTime = cell.session!.endTime!
            mySessionObject.sessionTitle = cell.session!.title!
            
            mySessionRepository!.add(item: mySessionObject)
            cell.favoriteButton.setImage(UIImage.init(named: "ic_favorite_48pt"), for: .normal)
            
            let sessionDate = CommonNotificationUtil.getStartDate(startTime: cell.session!.startTime!)
            let isGrantedNotificationAccess = UserDefaults.standard.bool(forKey: "notifySwitch")
            if isGrantedNotificationAccess {
                CommonNotificationUtil.scheduleNotification(session: cell.session!, withDate: sessionDate!, sessionRemove: false)
            }
            
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        sessionSearchBar.delegate = self
        self.tableView.dataSource = self
        self.tableView.delegate = self
        
        refresher = UIRefreshControl()
        
        if #available(iOS 10.0, *) {
            self.tableView.refreshControl = self.refresher
        } else {
            self.tableView.addSubview(self.refresher!)
        }
        
        refresher?.attributedTitle = NSAttributedString(string: "Pull to refresh")
        refresher?.tintColor = UIColor(red:1.00, green: 0.21, blue: 0.55, alpha: 1.0)
        refresher?.addTarget(self, action: #selector(self.getAllSessionsFromSleepingPill), for: UIControl.Event.valueChanged)
        
        self.refreshData()
        self.mySessionSegmentedControl.removeAllSegments()
        
        for (index, conferenceDate) in CommonDate.conferenceDates().enumerated() {
            self.mySessionSegmentedControl.insertSegment(withTitle: conferenceDate, at: index, animated: false)
        }
        
        
        self.mySessionSegmentedControl.addTarget(self, action: #selector(self.selectedSegmentedDate), for: UIControl.Event.valueChanged)
    }
    
    @objc func selectedSegmentedDate(sender: UISegmentedControl) {
        segmentedSelected = sender.selectedSegmentIndex
        self.refreshData()
    }
    
    func refreshData() {
        let results = sessionRepository!.getAll()
        
        if(results != nil && results!.count == 0) {
            getAllSessionsFromSleepingPill()
        } else {
            getAllSessionsFromDb(results)
        }
    }
    
    @objc func getAllSessionsFromSleepingPill() {
        sessionRepository!.deleteAll()
        speakerRepository!.deleteAll()
        
        SessionApiService.sharedInstance.getAllSessions().done { (result) in
            self.loadSessions(sessionResult: result)
            self.refresher?.endRefreshing()
            }.ensure {
                // Hide spinner here
                SVProgressHUD.dismiss()
            }
            .catch { error in
                print(error)
                self.refresher?.endRefreshing()
                SVProgressHUD.showError(withStatus: "Could not get sessions. Please pull to refresh")
        }
    }
    
    func getAllSessionsFromDb(_ results: [Session]?) {
        self.refresher?.endRefreshing()
        
        let selectedSegmentDate = CommonDate.conferenceDates()[self.segmentedSelected]
        loadDataToTableView(results, selectedDate: selectedSegmentDate)
    }
    
    func loadSessions(sessionResult:SessionResult) {
        sessions = sessionResult.sessions
        sessionRepository!.addAsync(items: sessions!)
        let selectedSegmentDate = CommonDate.conferenceDates()[self.segmentedSelected]
        
        loadDataToTableView(sessions, selectedDate: selectedSegmentDate)
    }
    
    
    func loadDataToTableView(_ sessions: [Session]?, selectedDate: String) {
        self.sections.removeAll()
        
        let sessionFilteredByDate = sessions?.filter {
            (session) -> Bool in
            let sessionStartDate = CommonDate.formatDate(dateString: session.startTime!, dateFormat: "dd.MM.YYYY")
            return sessionStartDate == selectedDate
            }.map { $0 }
        
        
        for session in sessionFilteredByDate! {
            if let sectionDate = CommonDate.resetMinutesFromDate(dateString: session.startTime!, dateFormat: "HH:mm") {
                if self.sections.index(forKey: sectionDate) == nil {
                    self.sections[sectionDate] = [session]
                }
                else {
                    self.sections[sectionDate]!.append(session)
                    self.sections[sectionDate]!.sort(by: { $0.startTime! < $1.startTime! })
                    self.sections[sectionDate]!.sort(by: { $0.endTime! < $1.endTime! })
                }
            }
        }
        
        self.sortedSections = self.sections.keys.sorted()
        
        if(searchActive) {
            filterSections(searchText: self.sessionSearchBar!.text!)
        }
        
        DispatchQueue.main.async {
            self.tableView!.reloadData()
        }
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        if(searchActive) {
            return filteredSections.count
        }
        
        return sections.count
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return sortedSections[section]
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(searchActive) {
            return filteredSections[sortedSections[section]] != nil ? filteredSections[sortedSections[section]]!.count : 0
        }
        
        return sections[sortedSections[section]]!.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "SessionCell", for: indexPath) as! SessionTableViewCell
        
        var data = sections[sortedSections[indexPath.section]]
        
        if(searchActive) {
            data = filteredSections[sortedSections[indexPath.section]]
        }
        
        _ = data
        let session = data![indexPath.row]
        
        
        cell.session = session
        cell.titleLabel?.text = session.title
        cell.subTitleLabel?.text = session.makeSpeakerNamesCommaSeparatedString(speakers: session.speakers)
        cell.startTimeLabel?.text = CommonDate.formatDate(dateString: session.startTime, dateFormat: "HH:mm")
        cell.endTimeLabel?.text = CommonDate.formatDate(dateString: session.endTime, dateFormat: "HH:mm")
        cell.roomLabel?.text = session.room
        cell.delegate = self
        
        let findMySession = mySessionRepository!.getMySession(sessionId: cell.session.sessionId!)
        
        if findMySession != nil && findMySession!.sessionId == session.sessionId! {
            cell.favoriteButton.setImage(UIImage.init(named: "ic_favorite_48pt"), for: .normal)
        } else {
            cell.favoriteButton.setImage(UIImage.init(named: "ic_favorite_border_48pt"), for: .normal)
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == "sessionDetailSegue"{
            let vc = segue.destination as! SessionDetailsViewController
            let indexPath = tableView.indexPathForSelectedRow
            var data = sections[sortedSections[indexPath!.section]]
            
            if data == nil {
                return
            }
            
            if(searchActive && filteredSections.count > 0) {
                data = filteredSections[sortedSections[indexPath!.section]]
            }
            
            _ = data
            let session = data![indexPath!.row]
            
            vc.session = session
            
            
            //Data has to be a variable name in your RandomViewController
        }
    }
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        searchActive = true;
        sessionSearchBar.showsCancelButton = true
    }
    
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        searchActive = false
        sessionSearchBar.showsCancelButton = false
    }
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        searchActive = false;
        sessionSearchBar.text = ""
        sessionSearchBar.showsCancelButton = false
        sessionSearchBar.endEditing(true)
        
        DispatchQueue.main.async {
            self.refreshData()
        }
    }
    
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchActive = false;
        sessionSearchBar.showsCancelButton = true
        
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if(sections.count == 0) {
            searchActive = false;
        } else {
            searchActive = true;
            
            if(searchText.isEmpty) {
                filteredSections = sections
                searchActive = false
                
            } else {
                filterSections(searchText: searchText)
            }
            
            self.sortedSections = filteredSections.keys.sorted()
            
            DispatchQueue.main.async {
                self.tableView.reloadData()
            }
        }
    }
    
    private func filterSections(searchText: String) {
        filteredSections.removeAll()
        for section in self.sections {
            
            let filteredContent = section.value.filter { $0.title!.range(of: searchText, options: .caseInsensitive) != nil
                || $0.makeSpeakerNamesCommaSeparatedString(speakers: $0.speakers)!.range(of: searchText, options: .caseInsensitive) != nil
                || $0.room!.range(of: searchText) != nil
                //   || $0.sentence.range(of: searchText, options: .caseInsensitive) != nil
            }
            
            if !filteredContent.isEmpty {
                filteredSections[section.key] = filteredContent
            }
        }
        
        self.sortedSections = filteredSections.keys.sorted()
    }
}

