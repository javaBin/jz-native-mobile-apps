import UIKit
import UserNotifications
import Firebase

class SettingsViewController: UITableViewController {
    var mySessionRepository: MySessionRepository?
    var sessionRepository: SessionRepository?
    
    @IBOutlet weak var notificationSwitch: UISwitch!
    
    @IBOutlet weak var usageStatisticsSwitch: UISwitch!
    
    var isGrantedNotificationAccess = false
    var isGrantedUsageStatisticsAccess = false
    let defaults = UserDefaults.standard
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let center = UNUserNotificationCenter.current()
        let options: UNAuthorizationOptions = [.alert, .badge, .sound];
        
        isGrantedNotificationAccess = defaults.bool(forKey: "notifySwitch")
        isGrantedUsageStatisticsAccess = defaults.bool(forKey: "anonymousSwitch")
        
        if isGrantedNotificationAccess {
            notificationSwitch.setOn(true, animated: false)
        } else {
            notificationSwitch.setOn(false, animated: false)

        }
        
        if !isGrantedUsageStatisticsAccess {
            usageStatisticsSwitch.setOn(false, animated: false)
        } else {
            usageStatisticsSwitch.setOn(true, animated: false)
        }
        
        
        center.requestAuthorization(options: options) {
            (granted, error) in
            
            if !granted {
                print("Something went wrong")
            }
        }
        
        DispatchQueue.main.async {
            self.notificationSwitch.setOn(self.isGrantedNotificationAccess, animated: true)
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
        
        defaults.removeObject(forKey: "notifySwitch")

        defaults.set(sender.isOn, forKey: "notifySwitch")
    }
    
    @IBAction func anonymousSwitchValueChanged(_ sender: UISwitch!) {
        Analytics.setAnalyticsCollectionEnabled(sender.isOn)
        defaults.removeObject(forKey: "anonymousSwitch")
        defaults.set(sender.isOn, forKey: "anonymousSwitch")

    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
