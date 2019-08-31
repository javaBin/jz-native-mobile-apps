import UIKit

class PartnerInformationViewController: UIViewController {
    @IBOutlet weak var informationTextField: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        informationTextField.attributedText = informationTextField.text.convertHtml()
        
    }
    

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        informationTextField.setContentOffset(CGPoint.zero, animated: false)
        informationTextField.flashScrollIndicators()
    }

}
