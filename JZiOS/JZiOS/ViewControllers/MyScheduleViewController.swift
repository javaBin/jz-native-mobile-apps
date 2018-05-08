import UIKit

class MyScheduleViewController: UIViewController,
UISearchDisplayDelegate, UISearchBarDelegate, UITableViewDataSource, UITableViewDelegate {
    var mySessionRepository: MySessionRepository?
    var refresher: UIRefreshControl?
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var myScheduleSegmentedControl: UISegmentedControl!
    @IBOutlet weak var sessionSearchBar: UISearchBar!
    var searchActive : Bool = false
    var sessions: [Session]?
    var sections = Dictionary<String, Array<Session>>()
    var filteredSections = Dictionary<String, Array<Session>>()
    var sortedSections = [String]()
    
    var segmentedSelected = 0
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        //sessionSearchBar.text = ""
        sessionSearchBar.resignFirstResponder()
        myScheduleSegmentedControl.selectedSegmentIndex = segmentedSelected
        
        refreshAllMySessions()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.sessionSearchBar.delegate = self
        self.tableView.dataSource = self
        self.tableView.delegate = self
        self.myScheduleSegmentedControl.removeAllSegments()
        
        for (index, conferenceDate) in CommonDate.conferenceDates().enumerated() {
            self.myScheduleSegmentedControl.insertSegment(withTitle: conferenceDate, at: index, animated: false)
        }
        
        self.myScheduleSegmentedControl.addTarget(self, action: #selector(self.selectedSegmentedDate), for: UIControlEvents.valueChanged)
        
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
        // Show spinner here
    }
    
    func refreshAllMySessions() {
        let results = mySessionRepository!.getAll()
        if(results != nil && results!.count == 0) {
            
        } else {
            self.getAllMySessions(results)
        }
        
        self.refresher?.endRefreshing()
    }
    
    func getAllMySessions(_ results: [Session]?) {
        let selectedSegmentDate = CommonDate.conferenceDates()[self.segmentedSelected]
        loadDataToTableView(results, selectedDate: selectedSegmentDate)
    }
    
    func loadDataToTableView(_ sessions: [Session]?, selectedDate: String) {
        sections.removeAll()
        
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
    
    func selectedSegmentedDate(sender: UISegmentedControl) {
        segmentedSelected = sender.selectedSegmentIndex
        self.refreshAllMySessions()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
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
        
        if(searchActive && filteredSections.count > 0) {
            data = filteredSections[sortedSections[indexPath.section]]
        }
        
        let section = data
        let session = data![indexPath.row]
        
        cell.session = session
        cell.titleLabel?.text = session.title
        cell.subTitleLabel?.text = session.makeSpeakerNamesCommaSeparatedString(speakers: session.speakers)
        cell.startTimeLabel?.text = CommonDate.formatDate(dateString: session.startTime, dateFormat: "HH:mm")
        cell.endTimeLabel?.text = CommonDate.formatDate(dateString: session.endTime, dateFormat: "HH:mm")
        cell.roomLabel?.text = session.room
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "mySessionDetailSegue"{
            let vc = segue.destination as! SessionDetailsViewController
            let indexPath = tableView.indexPathForSelectedRow
            var data = sections[sortedSections[indexPath!.section]]
            
            if(searchActive) {
                data = filteredSections[sortedSections[indexPath!.section]]
            }
            
            let section = data
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
            self.refreshAllMySessions()
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
