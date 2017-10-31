import Foundation
import RealmSwift

protocol RepositoryProtocol {
    func add<T: Object>(item: T)
    func addAsync<T: Object>(items: [T])
    func delete<T: Object>(item: T)
    func deleteAll()
}

class Repository: RepositoryProtocol {
    var realm: Realm! = try! Realm()

    func add<T>(item: T) where T : Object {
    }
    
    func addAsync<T>(items: [T]) where T : Object {
    }
    
    func delete<T>(item: T) where T : Object {
    }
    
    func deleteAll() {
    }

}
