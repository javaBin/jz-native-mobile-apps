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
        
        
        //notification trigger can be based on time, calendar or location
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval:2.0, repeats: false)
        
        //create request to display
        let request = UNNotificationRequest(identifier: "ContentIdentifier", content: createNotificationContent(), trigger: trigger)
        
        //add request to notification center
        center.add(request) { (error) in
            if error != nil {
                print("error \(String(describing: error))")
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    private func createNotificationContent() -> UNMutableNotificationContent {
        let content = UNMutableNotificationContent()
        content.title = " Jurassic Park"
        content.subtitle = "Lunch"
        content.body = "Its lunch time at the park, please join us for a dinosaur feeding"
        content.sound = UNNotificationSound.default
        
        
        
        return content
    }
    

}
