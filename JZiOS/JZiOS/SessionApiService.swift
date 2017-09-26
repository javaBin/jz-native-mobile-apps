

import Foundation
import Alamofire
import PromiseKit
import ObjectMapper

class SessionApiService {
    static let sharedInstance = SessionApiService()
    private var manager: SessionManager
    
    private init() {
        self.manager = Alamofire.SessionManager.default
    }
    
    
    func getAllSessions() -> Promise<SessionResult>
    {
        let url = JZURL.GetAllSessions
        print(url)
        return Promise { fulfill, reject in
            self.manager.request(url).validate(statusCode: 200..<300).responseJSON { response in
                switch response.result {
                case .success:
                    //to get JSON return value
                    guard let responseJSON = response.result.value as? AnyObject else {
                        reject(NSError(domain: "domainN", code: 0, userInfo: [NSLocalizedDescriptionKey: "Some error readinf response"]))
                        return
                    }
                    
                    print(responseJSON)
                    guard let sessionResult = Mapper<SessionResult>().map(JSONObject: responseJSON) else {
                        reject(NSError(domain: "domainN", code: 1, userInfo: [NSLocalizedDescriptionKey: "Some error mapping the object"]))
                        return
                    }
                    fulfill(sessionResult)
                case .failure(let error):
                    reject(error)
                }
            }
        }
    }
}
