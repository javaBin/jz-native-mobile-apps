import Foundation
import Firebase

class RemoteConfigValues {
    static let sharedInstance = RemoteConfigValues()
    var loadingDoneCallback: (() -> Void)?
    var fetchComplete = false
    
    private init() {
        loadDefaultValues()
        fetchCloudValues()
    }
    
    func loadDefaultValues() {
        let appDefaults: [String: Any?] = [
            "event_after_hours_description": "Some description",
            "default_wifi_network": "javazone",
            "default_wifi_password": "",
            "salted_partner_name": "JavaZoneErKulestIVerden!!!1",
            "javazone_year": "2019"
        ]
        
        RemoteConfig.remoteConfig().setDefaults(appDefaults as? [String: NSObject])
    }
    
    func fetchCloudValues() {
        let fetchDuration: TimeInterval = 1800
        RemoteConfig.remoteConfig().fetch(withExpirationDuration: fetchDuration) {
            [weak self] (status, error) in
            
            if let error = error {
                print ("Uh-oh. Got an error fetching remote values \(error)")
                return
            }
            
            RemoteConfig.remoteConfig().activateFetched()
            print ("Retrieved values from the cloud!")
            self?.fetchComplete = true
            self?.loadingDoneCallback?()
        }
    }
    
    func bool(key: String) -> Bool {
        return RemoteConfig.remoteConfig()[key].boolValue
    }
    
    func string(key: String) -> String {
        return RemoteConfig.remoteConfig()[key].stringValue ?? ""
    }
    
    func double(key: String) -> Double {
        if let numberValue = RemoteConfig.remoteConfig()[key].numberValue {
            return numberValue.doubleValue
        } else {
            return 0.0
        }
    }
    
    
}
