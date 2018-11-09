import Foundation
import UIKit

class SessionTableViewCell: UITableViewCell {
    weak var session: Session!
    var delegate: SessionCellDelegate?
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var subTitleLabel: UILabel!
    @IBOutlet weak var favoriteButton: UIButton!
    @IBOutlet weak var startTimeLabel: UILabel!
    @IBOutlet weak var endTimeLabel: UILabel!
    @IBOutlet weak var roomLabel: UILabel!
    @IBAction func favoriteButtonTapped(_ sender: Any) {
        if let _ = delegate {
            delegate?.favoriteButtonTapped(cell: self)
        }
    }
}
