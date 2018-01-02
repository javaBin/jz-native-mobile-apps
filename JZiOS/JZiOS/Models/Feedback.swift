import Foundation

struct Feedback {
    var overall: Int
    var relevance: Int
    var content: Int
    var quality: Int
    var comments: String
    
    public func toJSON() -> [String: Any] {
        return [
            "overall": overall as Any,
            "relevance": relevance as Any,
            "content": content as Any,
            "quality": quality as Any,
            "comments": comments as Any
        ]
    }
}
