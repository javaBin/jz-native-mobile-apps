import UIKit
import Cosmos

class FeedbackViewController: UIViewController {
    @IBOutlet weak var sessionTitleLabel: UILabel!
    @IBOutlet weak var sessionRatingCosmosView: CosmosView!
    @IBOutlet weak var releveanceCosmosView: CosmosView!
    @IBOutlet weak var contentCosmosView: CosmosView!
    @IBOutlet weak var speakerQualityCosmosView: CosmosView!
    @IBOutlet weak var otherCommentTextView: UITextView!
    @IBOutlet weak var submitFeedbackButton: UIButton!
    var session: Session!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        sessionTitleLabel.text = session!.title
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func onSubmitFeedbackClicked(_ sender: Any) {
        let uniqueId = FeedbackApiService.sharedInstance.generateUniqueDeviceId()

        // TODO remember to go back to session details when successful call.
        // FeedbackApiService.sharedInstance.submitFeedback()
        self.navigationController?.popViewController(animated: true)
        
    }
    

}
