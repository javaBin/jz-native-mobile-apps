import UIKit
import ObjectMapper
import RealmSwift
import ObjectMapper_Realm

class Partner : Object, Mappable {    
    @objc dynamic var name: String?
    @objc dynamic var uriImage: String?
    @objc dynamic var hasStamped: String?
    
    required convenience init?(map: Map) {
        self.init()
    }
    
    func mapping(map: Map) {
        name            <- map["name"]
        uriImage        <- map["uriImage"]
        hasStamped      <- map["hasStamped"]
    }
}
