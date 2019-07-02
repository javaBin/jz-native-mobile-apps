import Foundation
import RealmSwift

class PartnerRepository : Repository {
    func addPartnerAsync(partner: Partner!) {
        DispatchQueue.global().async {
            let realm = try! Realm()
            realm.beginWrite()
            realm.create(Partner.self, value: partner)
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
        DispatchQueue.global().async {
            let realm = try! Realm()
            let partnerData = realm.objects(Partner.self).filter("name = %@", name)
            if let partnerRetrieved = partnerData.first {
                try! realm.write {
                    partnerRetrieved.hasStamped = stamp
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
