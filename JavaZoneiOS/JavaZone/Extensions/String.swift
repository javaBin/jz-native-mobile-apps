import Foundation

extension String {
    func toBase64() -> String {
        return Data(self.utf8).base64EncodedString()
    }
    
    func fromBase64() -> String? {
        guard let data = Data(base64Encoded: self) else { return nil }
        return String(data: data, encoding: .utf8)
    }
    
    func convertHtml() -> NSAttributedString{
        guard let data = data(using: .utf8) else { return NSAttributedString() }
        do{
            return try NSAttributedString(data: data, options: [NSAttributedString.DocumentReadingOptionKey.documentType: NSAttributedString.DocumentType.html], documentAttributes: nil)
        }catch{
            return NSAttributedString()
        }
    }
}
