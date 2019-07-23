import Foundation
import RealmSwift
import SVProgressHUD

class TicketRepository : Repository {
    
    func addTicketAsync(ticket: Ticket!) {
        DispatchQueue.global().async {
            // Get new realm and table since we are in a new thread
            let realm = try! Realm()
            realm.beginWrite()
            realm.create(Ticket.self, value: ticket)
            try! realm.commitWrite()
        }
    }
    
    func getTicket() -> Ticket? {
        let realm = try! Realm()
        let getTicketObject = realm.objects(Ticket.self).first
        print(getTicketObject?.vCardData)
        return getTicketObject
            != nil ? getTicketObject : nil
    }
    
    
    func deleteTicket(item: Ticket) {
        let realm = try! Realm()
        realm.beginWrite()
        realm.delete(item)
        try! realm.commitWrite()
    }
}

