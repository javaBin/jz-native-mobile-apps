import UIKit
import Cosmos

class FeedbackViewController: UIViewController {
    @IBOutlet weak var sessionTitleLabel: UILabel!
    @IBOutlet weak var sessionRatingCosmosView: CosmosView!
    @IBOutlet weak var releveanceCosmosView: CosmosView!
    @IBOutlet weak var contentCosmosView: CosmosView!
    @IBOutlet weak var speakerQualityCosmosView: CosmosView!
    @IBOutlet weak var otherCommentTextView: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}
