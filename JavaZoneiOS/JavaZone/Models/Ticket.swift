import Foundation
import RealmSwift

struct Ticket {
    var fullName: String
    var email: String
    var phoneNumber: String
    
    public func toJSON() -> [String: Any] {
        return [
            "fullName": fullName as Any,
            "email": email as Any,
            "phoneNumber": phoneNumber as Any
        ]
    }
}
