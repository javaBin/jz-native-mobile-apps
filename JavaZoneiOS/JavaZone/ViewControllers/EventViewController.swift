import UIKit
import NetworkExtension
import Cosmos
import QuartzCore
import SVProgressHUD

class EventViewController: UIViewController {
    @IBOutlet weak var connectToWifiButton: UIButton!
    @IBOutlet weak var afterHoursTextView: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        afterHoursTextView.text = (RemoteConfigValues.sharedInstance.string(key: "event_after_hours_description"))
        
        if #available(iOS 11.0, *) {
            connectToWifiButton.isHidden = false
        } else {
            connectToWifiButton.isHidden = true
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func connectToWifiButtonClicked(_ sender: Any) {
            if #available(iOS 11.0, *) {
                let ssid = RemoteConfigValues.sharedInstance.string(key: "default_wifi_network")
                let password = RemoteConfigValues.sharedInstance.string(key:"default_wifi_password")
                let configuration = NEHotspotConfiguration.init(ssid: ssid, passphrase: password, isWEP: false)
                configuration.joinOnce = true
                
                NEHotspotConfigurationManager.shared.apply(configuration) { (error) in
                    if error != nil {
                        //an error accured
                        SVProgressHUD.showSuccess(withStatus: "Failed to connect to wifi")
                        
                        print(error?.localizedDescription)
                    }
                    else {
                        //success
                        SVProgressHUD.showSuccess(withStatus: "Successfully established connection with JavaZone wifi")
                        
                    }
                }
        }
    }
}
