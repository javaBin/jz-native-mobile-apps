import UIKit
import UserNotifications
import Firebase

class SettingsViewController: UITableViewController {
    var mySessionRepository: MySessionRepository?
    var sessionRepository: SessionRepository?
    
    var isGrantedNotificationAccess = false

    override func viewDidLoad() {
        super.viewDidLoad()
        let center = UNUserNotificationCenter.current()
        let options: UNAuthorizationOptions = [.alert, .badge, .sound];
        
        center.requestAuthorization(options: options) {
            (granted, error) in
            self.isGrantedNotificationAccess = granted
            // IF turned on, set switch to on!
            
            
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
    
    @IBAction func notificationSwitchValueChanged(_ sender: UISwitch!) {
        if(!sender.isOn) {
            UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
        } else {
            let mySessions = mySessionRepository!.getAll()
            
            // TODO check if anything happens here
            if(mySessions!.count > 0) {
                for var mySession in mySessions! {
                    let sessionDate = CommonNotificationUtil.getStartDate(startTime: mySession.startTime!)
                    CommonNotificationUtil.createAndAddNotification(sessionId: mySession.sessionId!, sessionTitle: mySession.title!, date: sessionDate)
                }
            }
        }
    }
    
    @IBAction func anonymousSwitchValueChanged(_ sender: UISwitch!) {
        AnalyticsConfiguration.shared().setAnalyticsCollectionEnabled(sender.isOn)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
