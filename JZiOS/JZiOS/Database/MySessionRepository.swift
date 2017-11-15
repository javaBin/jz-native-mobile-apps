import Foundation
import RealmSwift

class MySessionRepository: Repository {
    override func add<T>(item: T) where T : MySession {
        try! realm.write {
            realm.create(MySession.self, value: item)
        }
    }
    
    override func addAsync<T>(items: [T]) where T : MySession {
        DispatchQueue.global().async {
            // Get new realm and table since we are in a new thread
            let realm = try! Realm()
            realm.beginWrite()
            for item in items {
                realm.create(MySession.self, value: item)
            }
            try! realm.commitWrite()
        }
    }
    
    override func delete<T>(item: T) where T : MySession {
        realm.beginWrite()
        realm.delete(item)
        try! realm.commitWrite()
    }
    
    override func deleteAll() {
        let allSessionObjects = realm.objects(MySession.self)
        try! realm.write {
            realm.delete(allSessionObjects)
        }
    }
    
    func getAll() -> [MySession]? {
        let result = realm.objects(MySession.self) //.filter(NSPredicate(format: "name contains 'x'"))
        return Array(result)
    }
    
    func getMySession(sessionId: String) -> MySession? {
        let result = realm.objects(MySession.self).filter("sessionId = %@", sessionId).first
        return result
    }
    
    
    func getAllMySessionsAsync() -> [MySession]? {
        let results: [MySession]? = nil
        DispatchQueue.global().async {
            let otherRealm = try! Realm()
            let results = otherRealm.objects(MySession.self) //.filter(NSPredicate(format: "name contains 'Rex'"))
            print("Number of result \(results.count)")
        }
        
        return Array(results!)
    }
}
