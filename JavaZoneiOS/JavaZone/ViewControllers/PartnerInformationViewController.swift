import UIKit

class PartnerInformationViewController: UIViewController {
    @IBOutlet weak var informationTextField: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        informationTextField.setContentOffset(CGPoint.zero, animated: false)
        informationTextField.flashScrollIndicators()
    }

}
