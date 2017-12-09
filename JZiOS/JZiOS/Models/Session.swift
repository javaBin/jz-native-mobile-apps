import Foundation
import ObjectMapper
import RealmSwift
import ObjectMapper_Realm

class SessionResult: Mappable {
    var sessions: [Session]?
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        sessions    <- map["sessions"]
    }
}

class Session: Object, Mappable {
    dynamic var intendedAudience: String?
    dynamic var endTimeZulu: String?
    var tags: List<Tag>?
    dynamic var format: String?
    dynamic var language: String?
    dynamic var sessionId: String?
    dynamic var abstract: String?
    dynamic var published: String?
    dynamic var video: String?
    dynamic var title: String?
    dynamic var room: String?
    dynamic var conferenceId: String?
    dynamic var startTimeZulu: String?
    var speakers: List<Speaker>?
    dynamic var startTime: String?
    dynamic var endTime: String?
    dynamic var slug: String?
    
    required convenience init?(map: Map) {
        self.init()
    }
    
    func mapping(map: Map) {
        if let unwrappedTags = map.JSON["keywords"] as? [String] {
            tags = mapKeywordsToTags(keyWords: unwrappedTags)
        }
        
        
        intendedAudience    <- map["intendedAudience"]
        endTimeZulu         <- map["endTimeZulu"]
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
        speakers            <- (map["speakers"], ListTransformCustom<Speaker>())
        startTime           <- map["startTime"]
        endTime             <- map["endTime"]
        slug                <- map["slug"]
        
        
    }
    
    public func makeSpeakerNamesCommaSeparatedString(speakers: List<Speaker>? ) -> String? {
        var speakerNames: String? = nil
        
        if !speakers!.isEmpty {
            speakerNames = speakers!.map { (speaker: Speaker) -> String in
                var speakerName = speaker.name
                return speakerName!
                }.joined(separator: ", ")
        }
        
        return speakerNames
    }
    
    public func mapKeywordsToTags(keyWords: [String]?) -> List<Tag>? {
        var tags: List<Tag>? = nil
        
        if !keyWords!.isEmpty {
            tags = List<Tag>()
        }
        
        for keyWord in keyWords! {
            let tag = Tag()
            tag.keyWord = keyWord
            tags!.append(tag)
        }
        
        return tags
    }
}

class Speaker: Object, Mappable {
    @objc dynamic var pictureUrl: String?
    @objc dynamic var name: String?
    @objc dynamic var bio: String?
    @objc dynamic var sessionId: String?
    
    required convenience init?(map: Map) {
        self.init()
    }
    
    func mapping(map: Map) {
        pictureUrl      <- map["pictureUrl"]
        name            <- map["name"]
        bio             <- map["bio"]
        sessionId         <- map["sessionId"]
    }
}

class Tag: Object {
    @objc dynamic var keyWord = ""
}

class ListTransformCustom<T:RealmSwift.Object> : TransformType where T:Mappable {
    typealias Object = List<T>
    typealias JSON = [AnyObject]
    
    let mapper = Mapper<T>()
    
    func transformFromJSON(_ value: Any?) -> Object? {
        let results = List<T>()
        if let objects = mapper.mapArray(JSONObject: value) {
            for object in objects {
                results.append(object)
            }
        }
        return results
    }
    
    func transformToJSON(_ value: Object?) -> JSON? {
        var results = [AnyObject]()
        if let value = value {
            for obj in value {
                let json = mapper.toJSON(obj)
                results.append(json as AnyObject)
            }
        }
        return results
    }
}
