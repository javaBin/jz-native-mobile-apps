import Foundation
import RealmSwift

class SpeakerRepository: Repository {
    
    func addSpeakerAsync(speakers: List<Speaker>?, session: Session) {
        DispatchQueue.global().async {
            // Get new realm and table since we are in a new thread
            let realm = try! Realm()
            realm.beginWrite()
            for item in speakers! {
                item.sessionId = session.sessionId
                realm.create(Speaker.self, value: item)
            }
            try! realm.commitWrite()
        }
    }
    
    func getSpeakersForSession(session: Session) -> List<Speaker>? {
        let realm = try! Realm()
        let getSpeakersForSession = realm.objects(Speaker.self).filter("sessionId = %@", session.sessionId)
        let returnResult = getSpeakersForSession.reduce(List<Speaker>()) { (list, element) -> List<Speaker> in
            list.append(element)
            return list
        }
        
        return returnResult
    }
    
    override func deleteAll() {
        DispatchQueue.global().async {
                let otherRealm = try! Realm()
                let allSpeakerObjects = otherRealm.objects(Speaker.self)
            try! otherRealm.write {
                
                otherRealm.delete(allSpeakerObjects)
            }
        }
    }
}
