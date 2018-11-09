import Foundation
import UIKit

extension UITextView {
    func setHTML(html: String) {
        do {
            let at : NSAttributedString = try NSAttributedString(data: html.data(using: .utf8)!,
                                                                 options: [.documentType: NSAttributedString.DocumentType.html,
                                                                           .characterEncoding: String.Encoding.utf8.rawValue]
                                                                 , documentAttributes: nil);
            self.attributedText = at;
        } catch {
            self.text = html;
        }
    }
}
