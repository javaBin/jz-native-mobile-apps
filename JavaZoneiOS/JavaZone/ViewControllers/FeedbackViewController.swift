import UIKit
import Cosmos
import PromiseKit
import QuartzCore
import SVProgressHUD

class FeedbackViewController: UIViewController, UITextViewDelegate {
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
        
        self.otherCommentTextView.delegate = self
        self.hideKeyboard()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        if text == "\n" {
            self.view.endEditing(true)
            return false
        }
        
        return true
    }
    
    @IBAction func onSubmitFeedbackClicked(_ sender: Any) {
        let rating = Int(sessionRatingCosmosView.rating)
        let relevance = Int(relevanceCosmosView.rating)
        let content = Int(contentCosmosView.rating)
        let speakerQuality = Int(speakerQualityCosmosView.rating)
        let otherComment = otherCommentTextView.text
        
        let newFeedback = Feedback(overall: rating, relevance: relevance, content: content, quality: speakerQuality, comments: otherComment != nil ? otherComment! : "")

        // TODO remember to go back to session details when successful call.
        FeedbackApiService.sharedInstance.submitFeedback(session: self.session, feedback: newFeedback).done { (result) in
            self.navigationController?.popViewController(animated: true)
            
            SVProgressHUD.showSuccess(withStatus: "Thank you! Feedback has been given")
            }.ensure {
                // Hide spinner here
                SVProgressHUD.dismiss()
            }
            .catch { error in
                print(error)
                SVProgressHUD.showError(withStatus: "Failed to give feedback. Please try again")
        }
    }
}
