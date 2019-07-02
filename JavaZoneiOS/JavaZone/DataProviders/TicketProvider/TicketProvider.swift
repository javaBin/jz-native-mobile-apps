import Foundation

class TicketProvider {
    static let sharedInstance = TicketProvider()
    
    public func mockTicket() -> Ticket {
        var ticketData = Ticket()
        ticketData.jzYear = 2019
        ticketData.vCardData = "BEGIN:VCARD\nVERSION:2.1\nN:Ho Xuan;Khiem-Kim\nFN:Khiem-Kim Ho Xuan\nEMAIL:khiem_89@hotmail.com\nORG:Systek AS\nEND:VCARD"
        
        return ticketData
    }
    
    
}
