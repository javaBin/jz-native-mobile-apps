import UIKit
import UserNotifications

class SettingsViewController: UITableViewController {
    var mySessionRepository: MySessionRepository?
    var sessionRepository: SessionRepository?
    @IBOutlet weak var generalSettingsCell: GeneralSettingsCell!
    var isGrantedNotificationAccess = false

    override func viewDidLoad() {
        super.viewDidLoad()
        let center = UNUserNotificationCenter.current()
        let options: UNAuthorizationOptions = [.alert, .sound];
        
        
        center.requestAuthorization(options: options) {
            (granted, error) in
            self.isGrantedNotificationAccess = granted
            if !granted {
                print("Something went wrong")
            }
        }
        
        center.getNotificationSettings { (settings) in
            if settings.authorizationStatus != .authorized {
                // Notifications not allowed
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

}
