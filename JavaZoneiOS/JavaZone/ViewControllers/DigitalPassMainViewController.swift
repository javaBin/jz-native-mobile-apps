import UIKit
import SVProgressHUD

class DigitalPassMainViewController: UIViewController {
    @IBOutlet weak var ticketContainer: UIView!
    @IBOutlet weak var partnerContainer: UIView!
    @IBOutlet weak var infoContainer: UIView!
    @IBOutlet weak var digitalPassSegmentedControl: UISegmentedControl!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        digitalPassSegmentedControl.selectedSegmentIndex = 0;
        ticketContainer.isHidden = false
        partnerContainer.isHidden = true
        infoContainer.isHidden = true
        
        digitalPassSegmentedControl.addTarget(self, action: #selector(selectionDidChange(_:)), for: .valueChanged)
    }
    
    @objc func selectionDidChange(_ sender: UISegmentedControl) {
        switch(sender.selectedSegmentIndex) {
        case 0:
            ticketContainer.isHidden = false
            partnerContainer.isHidden = true
            infoContainer.isHidden = true
        case 1:
            ticketContainer.isHidden = true
            partnerContainer.isHidden = false
            infoContainer.isHidden = true
            
            var partnerListVC = self.children[1] as! PartnerListViewController
            var ticketVC = self.children[0] as! DigitalPassTicketViewController
            if(partnerListVC.partnerRepository!.getAllPartners()!.count < 1 && ticketVC.hasDeletedTicket) {
                SVProgressHUD.show()
                partnerListVC.getAllPartnersFromFirebase()
                ticketVC.hasDeletedTicket = false
            }
            
            break;
        case 2:
            ticketContainer.isHidden = true
            partnerContainer.isHidden = true
            infoContainer.isHidden = false
            break;
        default:
            break;
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
