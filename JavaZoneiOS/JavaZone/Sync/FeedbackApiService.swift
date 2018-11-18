import Foundation
import Alamofire
import PromiseKit
import SVProgressHUD

class FeedbackApiService {
    static let sharedInstance = FeedbackApiService()
    private var manager: SessionManager
    
    private init() {
        self.manager = Alamofire.SessionManager.default
    }
    
    public func generateUniqueDeviceId() -> String? {
        let deviceId = UIDevice.current.identifierForVendor!.uuidString
        return deviceId.toBase64()
    }
    
    public func submitFeedback(session: Session!, feedback: Feedback) -> Promise<String> {
        let eventId = session!.conferenceId!
        let sessionId = session!.sessionId!
        let url = "\(JZURL.GetDevNullUrl)/events/\(eventId)/sessions/\(sessionId)/feedbacks"
        let parameters = feedback.toJSON()
        
        let headers: HTTPHeaders = [
            "Voter-ID": "\(generateUniqueDeviceId()!.toBase64())",
            "Content-Type": "application/json",
            "Accept": "application/json"]
        
        SVProgressHUD.show()
        return Promise<String> { seal in
            self.manager.request(url, method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: headers).validate(statusCode: 200..<300).responseJSON { response in
                switch response.result {
                case .success:
                    //to get JSON return value
                    guard (response.result.value as? AnyObject) != nil else {
                        seal.reject(NSError(domain: "domain", code: 0, userInfo: [NSLocalizedDescriptionKey: "Some error reading response"]))
                        return
                    }
                    
                    seal.fulfill(response.result.description)
                case .failure(let error):
                    seal.reject(error)
                }
            }
        }
    }
}
