
import Foundation

class QRPartnerTicketProvider {
    static let sharedInstance = QRPartnerTicketProvider()
    
    public func mockTicket() -> QRPartnerResult {
        let ticketData = QRPartnerResult(name: "Computas", key: "9310830f2b644c5ec6baba523faa60d29606c055852bf08a1e6ff7d114e7e2526354c2f450a309ed258702f56711b7ad4e402b9c6d1425bfda5f552f36367b07")

        return ticketData
    }
}

