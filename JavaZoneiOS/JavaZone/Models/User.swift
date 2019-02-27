import UIKit

class User {
    
    var name: String
    var surname: String
    var avatar: UIImage
    var postsCount: Int
    var commentsCount: Int
    var likesCount: Int

    init(name: String, surname: String, avatar: UIImage, postsCount: Int, commentsCount: Int, likesCount: Int) {
        self.name = name
        self.surname = surname
        self.avatar = avatar
        self.postsCount = postsCount
        self.commentsCount = commentsCount
        self.likesCount = likesCount
    }
    
}
