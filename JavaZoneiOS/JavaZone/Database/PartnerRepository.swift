import Foundation
import RealmSwift

class PartneRepository : Repository {
    func addCompanyAsync(partner: Partner!, session: Session) {
        DispatchQueue.global().async {
            // Get new realm and table since we are in a new thread
            let realm = try! Realm()
            realm.beginWrite()
            realm.create(Partner.self, value: partner)
            try! realm.commitWrite()
        }
    }
    
//    func getPartners() -> List<Partner>? {
//        let realm = try! Realm()
//        let getPartners = realm.objects(Speaker.self).filter("sessionId = %@", session.sessionId)
//        let returnResult = getSpeakersForSession.reduce(List<Speaker>()) { (list, element) -> List<Speaker> in
//            list.append(element)
//            return list
//        }
//        
//        return returnResult
//    }
    
    
}
