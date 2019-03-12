import UIKit

class Company {
    
    var name: String
    var avatar: UIImage
    var hasStamped: Bool

    init(name: String, avatar: UIImage, hasStamped: Bool) {
        self.name = name
        self.avatar = avatar
        self.hasStamped = hasStamped
    }
    
}
