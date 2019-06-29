import UIKit
import ObjectMapper
import RealmSwift
import ObjectMapper_Realm
import Contacts

class Ticket : Object, Mappable {
    @objc dynamic var vCardData: String?
    @objc dynamic var jzYear: Int = 0
    
    required convenience init?(map: Map) {
        self.init()
    }
    
    func mapping(map: Map) {
        vCardData            <- map["vCardData"]
        jzYear        <- map["jzYear"]
    }
}
