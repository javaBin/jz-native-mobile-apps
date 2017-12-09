import UIKit

class SpeakerUIView: UIView {
    @IBOutlet weak var speakerNameTitleLabel: UILabel!
    @IBOutlet weak var contentView: UIView!
    @IBOutlet weak var speakerImageView: UIImageView!
    
    var speaker: Speaker!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        commonInit()
    }
    
    private func commonInit() {
        /*
        addSubview(contentView)
        contentView.frame = self.bounds
        contentView.autoresizingMask = [.flexibleHeight, .flexibleWidth] */
    }

}
