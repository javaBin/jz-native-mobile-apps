import UIKit
import DisplaySwitcher

private let avatarListLayoutSize: CGFloat = 70.0

class PartnerCollectionViewCell: UICollectionViewCell, CellInterface {
    
    @IBOutlet weak var partnerImageView: UIImageView!
    @IBOutlet weak var checkMarkImageView: UIImageView!
    @IBOutlet fileprivate weak var backgroundGradientView: UIView!
    @IBOutlet fileprivate weak var nameListLabel: UILabel!
    
    var partner : PartnerView!
    var partnerName : String!
    
    // avatarImageView constraints
    @IBOutlet fileprivate weak var avatarImageViewWidthConstraint: NSLayoutConstraint!
    @IBOutlet fileprivate weak var avatarImageViewHeightConstraint: NSLayoutConstraint!
    
    // nameListLabel constraints
    @IBOutlet var nameListLabelLeadingConstraint: NSLayoutConstraint! {
        didSet {
            initialLabelsLeadingConstraintValue = nameListLabelLeadingConstraint.constant
        }
    }
    
    fileprivate var avatarGridLayoutSize: CGFloat = 0.0
    fileprivate var initialLabelsLeadingConstraintValue: CGFloat = 0.0
    
    func bind(partnerView : PartnerView) {
        partnerImageView!.imageFromUrl(urlString: partnerView.logoUrl!)
        if(!partnerView.hasStamped) {
            checkMarkImageView.isHidden = true
        }
        else {
            checkMarkImageView.isHidden = false
        }
        
        nameListLabel.text = partnerView.name
    }
    
    func setupGridLayoutConstraints(_ transitionProgress: CGFloat, cellWidth: CGFloat) {
        avatarImageViewHeightConstraint.constant = ceil((cellWidth - avatarListLayoutSize) * transitionProgress + avatarListLayoutSize)
        avatarImageViewWidthConstraint.constant = ceil(avatarImageViewHeightConstraint.constant)
        nameListLabelLeadingConstraint.constant = -avatarImageViewWidthConstraint.constant * transitionProgress + initialLabelsLeadingConstraintValue
        backgroundGradientView.alpha = transitionProgress <= 0.5 ? 1 - transitionProgress : transitionProgress
        nameListLabel.alpha = 1 - transitionProgress
    }
    
    override func apply(_ layoutAttributes: UICollectionViewLayoutAttributes) {
        super.apply(layoutAttributes)
        if let attributes = layoutAttributes as? DisplaySwitchLayoutAttributes {
            if attributes.transitionProgress > 0 {
                if attributes.layoutState == .grid {
                    setupGridLayoutConstraints(attributes.transitionProgress, cellWidth: attributes.nextLayoutCellFrame.width)
                    avatarGridLayoutSize = attributes.nextLayoutCellFrame.width
                }
            }
        }
    }
    
    override func prepareForReuse() {
        self.partnerImageView.image = nil
        self.checkMarkImageView.isHidden = true
    }
}
