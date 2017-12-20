import UIKit
import AvatarImageView

class SpeakerDetailViewController: UIViewController {

    @IBOutlet weak var speakerImageView: AvatarImageView! {
        didSet {
           //  configureRoundAvatar()
            CommonImageUtil.setDefaultSpeakerAvatarImage(imageView: self.speakerImageView, imageName: "mysteryman")
        }
    }
    
    @IBOutlet weak var speakerNameLabel: UILabel!
    
    @IBOutlet weak var speakerBioGraphyLabel: UITextView!
    
    var speaker: Speaker?

    override func viewDidLoad() {
        super.viewDidLoad()
        
        if speaker != nil {
            
            if !speaker!.pictureUrl!.isEmpty {
                speakerImageView.imageFromUrl(urlString: speaker!.pictureUrl!)
            }
            speakerNameLabel.text = speaker!.name
            speakerBioGraphyLabel.text = speaker!.bio
            speakerBioGraphyLabel.contentInset = UIEdgeInsetsMake(-7.0,0.0,0.0, 0.0)
            
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func configureRoundAvatar() {
        struct Config: AvatarImageViewConfiguration { var shape: Shape = .circle }
        self.speakerImageView.configuration = Config()
    }
    
    func imageFromUrl(avatarImageView: AvatarImageView, urlString: String) {
        
        URLSession.shared.dataTask(with: NSURL(string: urlString)! as URL, completionHandler: { (data, response, error) -> Void in
            
            if error != nil {
                print(error!)
                return
            }
            DispatchQueue.main.async(execute: { () -> Void in
                var dataSpeakerDetailSource = SpeakerImageViewDataSource()
                dataSpeakerDetailSource.avatar =  UIImage(data: data!)
                self.speakerImageView.dataSource = dataSpeakerDetailSource
                self.speakerImageView.contentMode = UIViewContentMode.scaleAspectFit
            })
            
        }).resume()
    }
}
