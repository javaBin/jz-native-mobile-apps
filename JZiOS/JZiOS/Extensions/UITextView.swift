import Foundation
import UIKit

extension UITextView {
    func setHTML(html: String) {
        do {
            let at : NSAttributedString = try NSAttributedString(data: html.data(using: .utf8)!, options: [NSDocumentTypeDocumentAttribute : NSHTMLTextDocumentType], documentAttributes: nil);
            self.attributedText = at;
        } catch {
            self.text = html;
        }
    }
}
