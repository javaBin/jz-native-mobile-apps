import UIKit

class InfoViewController: UIViewController {
    @IBOutlet weak var eventContainer: UIView!
    @IBOutlet weak var travelContainer: UIView!
    @IBOutlet weak var settingsContainer: UIView!
    @IBOutlet weak var aboutContainer: UIView!
    @IBOutlet weak var infoSegmentedControl: UISegmentedControl!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        infoSegmentedControl.selectedSegmentIndex = 0;
        eventContainer.isHidden = false
        travelContainer.isHidden = true
        settingsContainer.isHidden = true
        aboutContainer.isHidden = true
        
        infoSegmentedControl.addTarget(self, action: #selector(selectionDidChange(_:)), for: .valueChanged)
    }
    
    
    
    func selectionDidChange(_ sender: UISegmentedControl) {
        switch(sender.selectedSegmentIndex) {
        case 0:
            eventContainer.isHidden = false
            travelContainer.isHidden = true
            settingsContainer.isHidden = true
            aboutContainer.isHidden = true
        case 3:
            eventContainer.isHidden = true
            travelContainer.isHidden = true
            settingsContainer.isHidden = true
            aboutContainer.isHidden = false
            break;
        default:
            break;
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
