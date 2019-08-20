import UIKit
import NetworkExtension
import Cosmos
import QuartzCore
import SVProgressHUD

class EventViewController: UIViewController {
    @IBOutlet weak var afterHoursTextView: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        afterHoursTextView.text = (RemoteConfigValues.sharedInstance.string(key: "event_after_hours_description"))
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
