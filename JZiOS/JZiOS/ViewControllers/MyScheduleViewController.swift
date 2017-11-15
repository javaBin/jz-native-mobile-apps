import UIKit

class MyScheduleViewController: UIViewController, UITableViewDataSource, UITableViewDelegate  {
    var repository: MySessionRepository?
    @IBOutlet weak var firstDayBtn: UIButton!
    @IBOutlet weak var secondDayBtn: UIButton!
    @IBOutlet weak var thirdDayBtn: UIButton!
    @IBOutlet weak var tableView: UITableView!

    var fruits: [String] = []

    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("hello my schedule")
        fruits = ["Apple", "Pineapple", "Orange", "Blackberry", "Banana", "Pear", "Kiwi", "Strawberry", "Mango", "Walnut", "Apricot", "Tomato", "Almond", "Date", "Melon", "Water Melon", "Lemon", "Coconut", "Fig", "Passionfruit", "Star Fruit", "Clementin", "Citron", "Cherry", "Cranberry"]

        // Do any additional setup after loading the view.
        self.tableView.dataSource = self
        self.tableView.delegate = self
        
        firstDayBtn.isSelected = true
        firstDayBtn.backgroundColor = UIColor.blue
        firstDayBtn.isEnabled = false
        firstDayBtn.setTitleColor(UIColor.white, for: .normal)
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        let numberOfRows = fruits.count
        return numberOfRows
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return fruits.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "mySessionIdentifier", for: indexPath)
        
        // Fetch Fruit
        let fruit = fruits[indexPath.row]
        
        // Configure Cell
        cell.textLabel?.text = fruit
        
        return cell
    }
    
    
}
