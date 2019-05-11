import UIKit
import RealmSwift

class MySession: Object {
    @objc dynamic var sessionId = ""
    @objc dynamic var startTime = ""
    @objc dynamic var endTime = ""
    @objc dynamic var sessionTitle = ""

}
