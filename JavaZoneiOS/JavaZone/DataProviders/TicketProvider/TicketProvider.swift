import Foundation

class TicketProvider {
    static let sharedInstance = TicketProvider()
    
    public func getTicketData(ticket: Ticket) {
        let fullPath = getDocumentsDirectory().appendingPathComponent("jzTicket")
        do {
            let data = try NSKeyedArchiver.archivedData(withRootObject: ticket, requiringSecureCoding: true)
            try data.write(to: fullPath)
            
        } catch {
            print("Problems writing file")
        }
        
        
        
    }
    
    func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return paths[0]
    }
    
    
}
