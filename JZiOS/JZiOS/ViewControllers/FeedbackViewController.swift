import UIKit
import Cosmos
import QuartzCore

class FeedbackViewController: UIViewController {
    @IBOutlet weak var sessionTitleLabel: UILabel!
    @IBOutlet weak var sessionRatingCosmosView: CosmosView!
    @IBOutlet weak var relevanceCosmosView: CosmosView!
    @IBOutlet weak var contentCosmosView: CosmosView!
    @IBOutlet weak var speakerQualityCosmosView: CosmosView!
    @IBOutlet weak var otherCommentTextView: UITextView!
    @IBOutlet weak var submitFeedbackButton: UIButton!
    var session: Session!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        sessionTitleLabel.text = session!.title
        otherCommentTextView.layer.borderColor = UIColor.darkGray.cgColor
        otherCommentTextView.layer.borderWidth = 1.0
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func onSubmitFeedbackClicked(_ sender: Any) {
        let rating = Int(sessionRatingCosmosView.rating)
        let relevance = Int(relevanceCosmosView.rating)
        let content = Int(contentCosmosView.rating)
        let speakerQuality = Int(speakerQualityCosmosView.rating)
        let otherComment = otherCommentTextView.text
        
        let newFeedback = Feedback(overall: rating, relevance: relevance, content: content, quality: speakerQuality, comments: otherComment != nil ? otherComment! : "")

        // TODO remember to go back to session details when successful call.
        FeedbackApiService.sharedInstance.submitFeedback(session: self.session, feedback: newFeedback)
        self.navigationController?.popViewController(animated: true)
        
    }
    

}
