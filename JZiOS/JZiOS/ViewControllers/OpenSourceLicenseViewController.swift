import UIKit

class OpenSourceLicenseViewController: UIViewController {
    @IBOutlet weak var openSourceDescriptionTextView: UITextView!
    

    override func viewDidLoad() {
        super.viewDidLoad()
        openSourceDescriptionTextView.setContentOffset(CGPoint.zero, animated: false)
        openSourceDescriptionTextView.flashScrollIndicators()
        let path = Bundle.main.path(forResource: "licenses", ofType: "html")
        do {
            let content = try String(contentsOfFile:path!, encoding: String.Encoding.utf8)
            openSourceDescriptionTextView.setHTML(html: content)

            print(content)
        } catch {
            print("nil")
        }


    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}
