import Foundation
import RealmSwift
import ObjectMapper

class SessionResult: Mappable {
    var sessions: [Session]?
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        sessions    <- map["sessions"]
    }
}

class Session: Object, Mappable {
    var intendedAudience: String?
    var endTimeZulu: String?
    var keywords: Array<String>?
    var format: String?
    var language: String?
    var sessionId: String?
    var abstract: String?
    var published: String?
    var video: String?
    var title: String?
    var room: String?
    var conferenceId: String?
    var startTimeZulu: String?
    var speakers: Array<Speaker>?
    var startTime: String?
    var endTime: String?
    var slug: String?
    
    required convenience init?(map: Map) {
        self.init()
    }
    
    func mapping(map: Map) {
        intendedAudience    <- map["intendedAudience"]
        endTimeZulu         <- map["endTimeZulu"]
        keywords            <- map["keywords"]
        format              <- map["format"]
        language            <- map["language"]
        sessionId           <- map["sessionId"]
        abstract            <- map["abstract"]
        published           <- map["published"]
        video               <- map["video"]
        title               <- map["title"]
        room                <- map["room"]
        conferenceId        <- map["conferenceId"]
        startTimeZulu       <- map["startTimeZulu"]
        speakers            <- map["speakers"]
        startTime           <- map["startTime"]
        endTime             <- map["endTime"]
        slug                <- map["slug"]
        
    }
}

class Speaker: Mappable {
    var pictureUrl: String?
    var name: String?
    var bio: String?
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        pictureUrl      <- map["pictureUrl"]
        name            <- map["name"]
        bio             <- map["bio"]
    }
}
