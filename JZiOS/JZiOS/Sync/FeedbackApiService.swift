import Foundation
import Alamofire
import PromiseKit

class FeedbackApiService {
    static let sharedInstance = FeedbackApiService()
    private var manager: SessionManager
    
    private init() {
        self.manager = Alamofire.SessionManager.default
    }
    
    private func generateUniqueDeviceId() -> String? {
        let deviceId = UIDevice.current.identifierForVendor!.uuidString
        // TODO
        return nil
    }
    
    
    func getAllSessions() -> Promise<SessionResult>
    {
        let url = JZURL.GetDevNullUrl
        return Promise { fulfill, reject in
            self.manager.request(url).validate(statusCode: 200..<300).responseJSON { response in
                switch response.result {
                case .success:
                    //to get JSON return value
                    guard let responseJSON = response.result.value as? AnyObject else {
                        reject(NSError(domain: "domainN", code: 0, userInfo: [NSLocalizedDescriptionKey: "Some error reading response"]))
                        return
                    }

                   // fulfill(sessionResult)
                case .failure(let error):
                    reject(error)
                }
            }
        }
    }
}
