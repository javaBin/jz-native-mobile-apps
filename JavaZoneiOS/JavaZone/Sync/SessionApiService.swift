

import Foundation
import Alamofire
import PromiseKit
import ObjectMapper
import SVProgressHUD

class SessionApiService {
    static let sharedInstance = SessionApiService()
    private var manager: SessionManager
    
    private init() {
        self.manager = Alamofire.SessionManager.default
    }
    
    func getAllSessions() -> Promise<SessionResult>
    {
        let url = JZURL.GetAllSessions
        SVProgressHUD.show()
        return Promise<SessionResult> { seal in
            self.manager.request(url).validate(statusCode: 200..<300).responseJSON { response in
                switch response.result {
                case .success:
                    //to get JSON return value
                    guard let responseJSON = response.result.value as? AnyObject else {
                        seal.reject(NSError(domain: "domainN", code: 0, userInfo: [NSLocalizedDescriptionKey: "Some error reading response"]))
                        return
                    }

                    guard let sessionResult = Mapper<SessionResult>().map(JSONObject: responseJSON) else {
                        seal.reject(NSError(domain: "domainN", code: 1, userInfo: [NSLocalizedDescriptionKey: "Some error mapping the object"]))
                        return
                    }
                    seal.fulfill(sessionResult)
                case .failure(let error):
                    seal.reject(error)
                }
            }
        }
    }
}
