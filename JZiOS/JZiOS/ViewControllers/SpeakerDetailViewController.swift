import UIKit

class SpeakerDetailViewController: UIViewController {
    @IBOutlet weak var speakerImageView: UIImageView!
    @IBOutlet weak var speakerNameLabel: UILabel!
    
    @IBOutlet weak var speakerBioGraphyLabel: UITextView!
    
    var speaker: Speaker?

    override func viewDidLoad() {
        super.viewDidLoad()
        
        if speaker != nil && !speaker!.pictureUrl!.isEmpty {
            speakerImageView.imageFromUrl(urlString: speaker!.pictureUrl!)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
