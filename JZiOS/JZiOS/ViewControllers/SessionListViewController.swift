import UIKit

protocol SessionCellDelegate {
    func favoriteButtonTapped(cell: SessionTableViewCell!)
}

class SessionListViewController: UITableViewController, UISearchBarDelegate, SessionCellDelegate {
    var sessions: [Session]?
    var sections = Dictionary<String, Array<Session>>()
    var sortedSections = [String]()
    var searchActive : Bool = false
    var refresher: UIRefreshControl?
    var sessionRepository: SessionRepository?
    var speakerRepository: SpeakerRepository?
    var mySessionRepository: MySessionRepository?

    
    func favoriteButtonTapped(cell: SessionTableViewCell!) {
        let findMySession = mySessionRepository!.getMySession(sessionId: cell.session.sessionId!)
        
        if findMySession != nil {
            mySessionRepository!.delete(item: findMySession!)
            cell.favoriteButton.setImage(UIImage.init(named: "ic_star_border_2x"), for: .normal)
        } else {
            let mySessionObject = MySession()
            mySessionObject.sessionId = cell.session!.sessionId!
            
            mySessionRepository!.add(item: mySessionObject)
            cell.favoriteButton.setImage(UIImage.init(named: "ic_star_2x"), for: .normal)
        }
    }

    
    @IBOutlet weak var sessionSearchBar: UISearchBar!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        sessionSearchBar.delegate = self
        refresher = UIRefreshControl()

        if #available(iOS 10.0, *) {
            self.tableView.refreshControl = self.refresher
        } else {
            self.tableView.addSubview(self.refresher!)
        }
        
        refresher?.attributedTitle = NSAttributedString(string: "Pull to refresh")
        refresher?.tintColor = UIColor(red:1.00, green: 0.21, blue: 0.55, alpha: 1.0)
        refresher?.addTarget(self, action: "getAllSessionsFromSleepingPill", for: .valueChanged)
        
        let results = sessionRepository!.getAll()
        
        if(results != nil && results!.count == 0) {
            getAllSessionsFromSleepingPill()
        } else {
            getAllSessionsFromDb(results)
        }
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false
        
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
        // Show spinner here
    }
    
    func getAllSessionsFromSleepingPill() {
        // TODO delete all sessions in local db
        sessionRepository!.deleteAll()
        speakerRepository!.deleteAll()
        
        SessionApiService.sharedInstance.getAllSessions().then { result -> Void in
            self.loadSessions(sessionResult: result)
            self.refresher?.endRefreshing()
            }.always {
                // Hide spinner here
            }
            .catch { error in
                print(error)
                self.refresher?.endRefreshing()
        }
    }
    
    func getAllSessionsFromDb(_ results: [Session]?) {
        self.refresher?.endRefreshing()
        loadDataToTableView(results)
    }
    
    func loadSessions(sessionResult:SessionResult) {
        sessions = sessionResult.sessions
        sessionRepository!.addAsync(items: sessions!)
        
        loadDataToTableView(sessions)
    }
    
    
    func loadDataToTableView(_ sessions: [Session]?) {
        self.sections.removeAll()
        for session in sessions! {
            if let sectionDate = CommonDate.formatDate(dateString: session.startTime!, dateFormat: "MMM dd yyyy") {
                if self.sections.index(forKey: sectionDate) == nil {
                    self.sections[sectionDate] = [session]
                }
                else {
                    self.sections[sectionDate]!.append(session)
                    self.sections[sectionDate]!.sort(by: { $0.startTime! < $1.startTime! })
                    self.sections[sectionDate]!.sort(by: { $0.endTime! < $1.endTime! })
                }
                
                self.sortedSections = self.sections.keys.sorted()
            }
        }
        
        self.tableView!.reloadData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return sections.count
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return sortedSections[section]
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        
        if(searchActive) {
            //   return filtered.count
        }
        
        return sections[sortedSections[section]]!.count
    }
    
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "SessionCell", for: indexPath) as! SessionTableViewCell
        let section = sections[sortedSections[indexPath.section]]
        let session = section![indexPath.row]
        
        cell.session = session
        cell.titleLabel?.text = session.title
        cell.startTimeLabel?.text = CommonDate.formatDate(dateString: session.startTime, dateFormat: "HH:mm")
        cell.endTimeLabel?.text = CommonDate.formatDate(dateString: session.endTime, dateFormat: "HH:mm")
        cell.roomLabel?.text = session.room
        cell.delegate = self

        
        let findMySession = mySessionRepository!.getMySession(sessionId: cell.session.sessionId!)
        
        if findMySession != nil && findMySession!.sessionId == session.sessionId! {
            cell.favoriteButton.setImage(UIImage.init(named: "ic_star_2x"), for: .normal)
        } else {
            cell.favoriteButton.setImage(UIImage.init(named: "ic_star_border_2x"), for: .normal)

        }

        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == "sessionDetailSegue"{
            var vc = segue.destination as! SessionDetailViewController
            let indexPath = tableView.indexPathForSelectedRow
            let section = sections[sortedSections[indexPath!.section]]
            let session = section![indexPath!.row]
            
            vc.session = session
            
            
            //Data has to be a variable name in your RandomViewController
        }
    }
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        searchActive = true;
    }
    
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        searchActive = false;
    }
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        searchActive = false;
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchActive = false;
        
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        print("in here")
        // if(filtered.count == 0){
        // searchActive = false;
        //  } else {
        //  searchActive = true;
        //  }
    }
}

