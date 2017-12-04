import UIKit

class TestViewController:  UIViewController  {
    @IBOutlet weak var sessionTitleLabel: UILabel!
    @IBOutlet weak var roomLabel: UILabel!
    @IBOutlet weak var abstractTextView: UITextView!
    @IBOutlet weak var intendedAudienceTextView: UITextView!
    
    @IBOutlet weak var subStackView: UIStackView!
    @IBOutlet weak var scrollView: UIScrollView!
    
    var session: Session?

    
    override func viewDidLoad() {
        super.viewDidLoad()
        sessionTitleLabel?.text = session!.title
        roomLabel?.text = session!.room
        abstractTextView?.text = session!.abstract
        intendedAudienceTextView?.text = session!.intendedAudience
        
        
        if session!.speakers!.count > 0 {
            for speaker in session!.speakers! {
                if let speakerDetailView = Bundle.main.loadNibNamed("SpeakerUIView", owner: self, options: nil)?.first as? SpeakerUIView {
                    speakerDetailView.speakerNameTitleLabel.text = speaker.name
                    
                    if let pictureUrl = speaker.pictureUrl {
                    speakerDetailView.speakerImageView.imageFromUrl(urlString: pictureUrl)
                    }
                    
                    subStackView!.addArrangedSubview(speakerDetailView)
                    scrollToEnd(speakerDetailView)
                    
                }
            }
        }
    }
    
    fileprivate func scrollToEnd(_ addedView: UIView) {
        let contentViewHeight = scrollView.contentSize.height + addedView.bounds.height + subStackView.spacing
        let offsetY = contentViewHeight - scrollView.bounds.height
        if (offsetY > 0) {
            scrollView.setContentOffset(CGPoint(x: scrollView.contentOffset.x, y: offsetY), animated: true)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == "speakerDetailSegue"{
            var vc = segue.destination as! SpeakerDetailViewController
            
            // vc.speaker = speaker
        }
    }
}



