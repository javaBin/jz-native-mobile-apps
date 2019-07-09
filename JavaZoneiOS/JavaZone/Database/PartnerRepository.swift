import Foundation
import RealmSwift

class PartnerRepository : Repository {
    func addPartner(partner: Partner!) {
        let realm = try! Realm()
        try! realm.write {
            realm.create(Partner.self, value: partner)
        }
    }
    
    func addAsync(items: [Partner]) {
        DispatchQueue.global().async {
            // Get new realm and table since we are in a new thread
            let realm = try! Realm()
            realm.beginWrite()
            for item in items {
                realm.create(Partner.self, value: item)
            }
            try! realm.commitWrite()
        }
    }
    
    func getPartner(name: String) -> Partner? {
        let result = realm.objects(Partner.self).filter("name = %@", name).first
        return result
    }
    
    func updatePartnerData(updatedData: Partner!) {
        
    }
    
    func updatePartner(stamp: Bool, name: String) {
        DispatchQueue(label: "background").async {
            autoreleasepool {
                let realm = try! Realm()
                let partnerRetrieved = realm.objects(Partner.self).filter("name = %@", name).first
                try! realm.write {
                    partnerRetrieved?.hasStamped = stamp
                }
            }
        }
    }
    
    func getAllPartners() -> Array<Partner>? {
        let realm = try! Realm()
        let getPartners = realm.objects(Partner.self)
        let returnResult = Array(getPartners)
        return returnResult
    }
    
    override func deleteAll() {
        DispatchQueue.global().async {
            let otherRealm = try! Realm()
            let partnerObjects = otherRealm.objects(Partner.self)
            try! otherRealm.write {
                
                otherRealm.delete(partnerObjects)
            }
        }
    }
    
}
