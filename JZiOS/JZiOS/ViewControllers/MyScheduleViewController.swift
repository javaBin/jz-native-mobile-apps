import UIKit

class MyScheduleViewController: UIViewController, UISearchBarDelegate, UITableViewDataSource, UITableViewDelegate  {
    var mySessionRepository: MySessionRepository?
    var refresher: UIRefreshControl?
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var myScheduleSegmentedControl: UISegmentedControl!
    @IBOutlet weak var sessionSearchBar: UISearchBar!
    var fruits: [String] = []
    var searchActive : Bool = false
    var sessions: [Session]?
    var sections = Dictionary<String, Array<Session>>()
    var sortedSections = [String]()
    var segmentedSelected = 0
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        myScheduleSegmentedControl.selectedSegmentIndex = segmentedSelected
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.sessionSearchBar.delegate = self
        self.tableView.dataSource = self
        self.tableView.delegate = self
        self.refresher = UIRefreshControl()
        
        if #available(iOS 10.0, *) {
            self.tableView.refreshControl = self.refresher
        } else {
            self.tableView.addSubview(self.refresher!)
        }
        
        self.refresher?.attributedTitle = NSAttributedString(string: "Pull to refresh")
        self.refresher?.tintColor = UIColor(red:1.00, green: 0.21, blue: 0.55, alpha: 1.0)
        self.refresher?.addTarget(self, action: #selector(self.refreshAllMySessions), for: UIControlEvents.valueChanged)
        
        self.myScheduleSegmentedControl.removeAllSegments()
        
        for (index, conferenceDate) in CommonDate.conferenceDates().enumerated() {
            self.myScheduleSegmentedControl.insertSegment(withTitle: conferenceDate, at: index, animated: false)
        }
        
        self.myScheduleSegmentedControl.addTarget(self, action: #selector(self.selectedSegmentedDate), for: UIControlEvents.valueChanged)
        
        refreshAllMySessions()
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false
        
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
        // Show spinner here
    }
    
    func refreshAllMySessions() {
        let results = mySessionRepository!.getAll()
        
        if(results != nil && results!.count == 0) {
            
            
        } else {
            getAllMySessionsFromDb(results)
        }
        
        self.refresher?.endRefreshing()
    }

    func getAllMySessionsFromDb(_ results: [Session]?) {
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
                
                self.sortedSections = self.sections.keys.sorted()
            }
        }
        
        self.tableView!.reloadData()
    }
    
    func selectedSegmentedDate(sender: UISegmentedControl) {
        segmentedSelected = sender.selectedSegmentIndex
        self.refreshAllMySessions()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return sections.count
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return sortedSections[section]
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        
        if(searchActive) {
            //   return filtered.count
        }
        
        return sections[sortedSections[section]]!.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "SessionCell", for: indexPath) as! SessionTableViewCell
        let section = sections[sortedSections[indexPath.section]]
        let session = section![indexPath.row]
        
        cell.session = session
        cell.favoriteButton.isHidden = true
        cell.titleLabel?.text = session.title
        cell.startTimeLabel?.text = CommonDate.formatDate(dateString: session.startTime, dateFormat: "HH:mm")
        cell.endTimeLabel?.text = CommonDate.formatDate(dateString: session.endTime, dateFormat: "HH:mm")
        cell.roomLabel?.text = session.room
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == "mySessionDetailSegue"{
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
