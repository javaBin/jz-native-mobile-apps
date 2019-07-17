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
        
        infoSegmentedControl.removeSegment(at: 1, animated: false)
        infoSegmentedControl.addTarget(self, action: #selector(selectionDidChange(_:)), for: .valueChanged)
    }
    
    @objc func selectionDidChange(_ sender: UISegmentedControl) {
        switch(sender.selectedSegmentIndex) {
        case 0:
            eventContainer.isHidden = false
            travelContainer.isHidden = true
            settingsContainer.isHidden = true
            aboutContainer.isHidden = true
        case 1:
            eventContainer.isHidden = true
            travelContainer.isHidden = true
            settingsContainer.isHidden = false
            aboutContainer.isHidden = true
            break;
        case 2:
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
}
