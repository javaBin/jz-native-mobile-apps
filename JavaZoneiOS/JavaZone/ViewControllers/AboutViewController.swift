import UIKit

class AboutViewController: UIViewController {
    @IBOutlet weak var aboutTextArea: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        aboutTextArea.setContentOffset(CGPoint.zero, animated: false)
        aboutTextArea.flashScrollIndicators()
    }
}
