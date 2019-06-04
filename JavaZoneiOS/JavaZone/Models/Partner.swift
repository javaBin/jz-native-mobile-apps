import UIKit
import ObjectMapper
import RealmSwift
import ObjectMapper_Realm

class Partner : Object, Mappable {    
    @objc dynamic var name: String?
    @objc dynamic var logoUrl: String?
    @objc dynamic var hasStamped: String?
    @objc dynamic var homepageUrl: String?
    var longitude: String?
    var latitude: String?
    
    required convenience init?(map: Map) {
        self.init()
    }
    
    func mapping(map: Map) {
        name            <- map["name"]
        logoUrl        <- map["logoUrl"]
        hasStamped      <- map["hasStamped"]
        homepageUrl    <- map["homepageUrl"]
        longitude       <- map["longitude"]
        latitude        <- map["latitude"]
    }
}
