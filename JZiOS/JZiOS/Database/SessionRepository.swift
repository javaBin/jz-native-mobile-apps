import Foundation
import RealmSwift

class SessionRepository {
    let realm = try! Realm()

    func add(item: Session) {
        try! realm.write {
            realm.create(Session.self, value: item)
        }
    }
    
    func addAsync(items: [Session]) {
        DispatchQueue.global().async {
            // Get new realm and table since we are in a new thread
            let realm = try! Realm()
            realm.beginWrite()
            for item in items {
                realm.create(Session.self, value: item)
            }
            try! realm.commitWrite()
        }
    }
    
    func delete(item: Session) {
        let realm = try! Realm()
        realm.beginWrite()
        realm.delete(item)
        try! realm.commitWrite()
    }
    
    func deleteAll() {
        let realm = try! Realm()
        let allSessionObjects = realm.objects(Session.self)
        try! realm.write {
            realm.delete(allSessionObjects)
        }
    }
    
    func getAll() -> [Session]? {
        let realm = try! Realm()
        let result = realm.objects(Session.self).sorted( by: [SortDescriptor(keyPath: "startTime", ascending: true), SortDescriptor(keyPath: "endTime", ascending: true)])
        
        if result == nil {
            return nil
        }
        
        return Array(result)
    }
    
    func getAllSessionsAsync() -> [Session]? {
        let realm = try! Realm()
        let results: [Session]? = nil
        DispatchQueue.global().async {
            let otherRealm = try! Realm()
            let results = otherRealm.objects(Session.self)
            print("Number of result \(results.count)")
        }
        
        return Array(results!)
    }
}
