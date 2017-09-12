import Foundation
import SwiftyJSON

struct SessionData {
    
}

class Session: NSObject, UITableViewDataSource {
    let cellIdentifier: String
    var sessions = [SessionData]()
    
    init(cellIdentifier: String) {
        self.cellIdentifier = cellIdentifier
    }
    
}
