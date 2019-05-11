import UIKit

class GeneralSettingsCell: UITableViewCell {
    
    @IBAction func toggleLocalSessionNotificationSwitch(_ sender: Any) {
        
        let switchState = sender is UISwitch
        if switchState {
            
        } else {
            
        }
    }
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
