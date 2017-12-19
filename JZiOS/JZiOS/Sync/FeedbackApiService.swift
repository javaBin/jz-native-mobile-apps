import Foundation
import Alamofire
import PromiseKit

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
    
    public func submitFeedback(session: Session) -> Promise<String> {
        let eventId = session.conferenceId
        let sessionId = session.sessionId
        let url = "\(JZURL.GetDevNullUrl)/events/\(eventId)/sessions/\(sessionId)/feedbacks"
        let parameters: Parameters = [
            "param1": "hello",
            "param2": "world"
        ]
        
        let headers: HTTPHeaders = [
            "Voter-ID": "\(generateUniqueDeviceId()?.toBase64())"]
        
        return Promise { fulfill, reject in
            self.manager.request(url, method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: headers).validate(statusCode: 200..<300).responseJSON { response in
                switch response.result {
                case .success:
                    //to get JSON return value
                    guard let responseJSON = response.result.value as? AnyObject else {
                        reject(NSError(domain: "domainN", code: 0, userInfo: [NSLocalizedDescriptionKey: "Some error reading response"]))
                        return
                    }
                    
                    fulfill("OK")
                case .failure(let error):
                    reject(error)
                }
            }
        }
    }
}
