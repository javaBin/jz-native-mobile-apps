import UIKit
import AVFoundation
import QRCodeReader
import Contacts
import ObjectMapper
import SVProgressHUD
import RealmSwift

class PartnerDetailViewController: UIViewController, QRCodeReaderViewControllerDelegate {
    var partnerCell: PartnerCollectionViewCell!
    var partnerCellIndex: IndexPath!
    weak var parentVC: PartnerListViewController!
    @IBOutlet weak var partnerImageView: UIImageView!
    @IBOutlet weak var partnerUrlTextView: UITextView!
    @IBOutlet weak var partnerName: UITextField!
    var partnerRepository: PartnerRepository?
    var ticketRepository: TicketRepository?
    
    lazy var reader: QRCodeReader = QRCodeReader()
    lazy var readerVC: QRCodeReaderViewController = {
        let builder = QRCodeReaderViewControllerBuilder {
            $0.reader                  = QRCodeReader(metadataObjectTypes: [.qr], captureDevicePosition: .back)
            $0.showTorchButton         = true
            $0.preferredStatusBarStyle = .lightContent
            $0.showOverlayView        = true
            $0.showSwitchCameraButton  = false
            $0.rectOfInterest = CGRect(x: 0.15, y: 0.15, width: 0.7, height: 0.7)
            
            $0.reader.stopScanningWhenCodeIsFound = false
        }
        
        return QRCodeReaderViewController(builder: builder)
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let paragraph = NSMutableParagraphStyle()
        paragraph.alignment = .center
        let font = UIFont.systemFont(ofSize: 18)
        
        let attributedString = NSMutableAttributedString(string: partnerCell.partner!.homepageUrl!, attributes:[NSAttributedString.Key.link: URL(string: partnerCell.partner!.homepageUrl!)!, NSAttributedString.Key.paragraphStyle: paragraph, NSAttributedString.Key.font: font ])
        partnerImageView.imageFromUrl(urlString: (partnerCell.partner!.logoUrl!))
        partnerImageView.contentMode = UIView.ContentMode.scaleAspectFit
        partnerUrlTextView.attributedText = attributedString
        partnerName.text = partnerCell.partner!.name
    }
    
    private func checkScanPermissions() -> Bool {
        do {
            return try QRCodeReader.supportsMetadataObjectTypes()
        } catch let error as NSError {
            let alert: UIAlertController
            
            switch error.code {
            case -11852:
                alert = UIAlertController(title: "Error", message: "This app is not authorized to use Back Camera.", preferredStyle: .alert)
                
                alert.addAction(UIAlertAction(title: "Setting", style: .default, handler: { (_) in
                    DispatchQueue.main.async {
                        if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
                            UIApplication.shared.openURL(settingsURL)
                        }
                    }
                }))
                
                alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
            default:
                alert = UIAlertController(title: "Error", message: "Reader not supported by the current device", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: nil))
            }
            
            present(alert, animated: true, completion: nil)
            
            return false
        }
    }
    
    @IBAction func scanInModalAction(_ sender: AnyObject) {
        if ticketRepository?.getTicket() == nil {
            let alert = UIAlertController(title: "Wait...", message: "Please scan your JavaZone pass to scan the partners QR Code", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: nil))
            present(alert, animated: true, completion: nil)

            return
        }
        
        let partnerName = partnerCell.partnerName!
        let partnerTicket = partnerRepository?.getPartner(name: partnerName)
        let hasStamped = partnerTicket?.hasStamped as! Bool
        
        if(!hasStamped) {
            guard checkScanPermissions() else { return }
            
            readerVC.modalPresentationStyle = .formSheet
            readerVC.delegate               = self
            
            readerVC.completionBlock = { (result: QRCodeReaderResult?) in
                if let result = result {
                    print("Completion with result: \(result.value) of type \(result.metadataType)")
                }
            }
            
            present(readerVC, animated: true, completion: nil)
        } else {
            let alert = UIAlertController(title: "Wait...", message: "Partner QR code has already been scanned", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: nil))
            present(alert, animated: true, completion: nil)
        }
    }
    
    func reader(_ reader: QRCodeReaderViewController, didScanResult result: QRCodeReaderResult) {
        reader.stopScanning()
        
        dismiss(animated: true) { [weak self] in
            if let data = result.value.data(using: .utf8) {
                
                
                do {
                    guard let qrPartnerResult = try? JSONDecoder().decode(QRPartnerResult.self, from: data ) else {
                        print("Error: Could not decode data into QRPartnerResult")
                        SVProgressHUD.showError(withStatus: "Error! Could not decode partner data! Reason: wrong data was scanned!")
                        return
                    }
                
                    
                    if qrPartnerResult.Name! != self!.partnerCell.partner!.name {
                        SVProgressHUD.dismiss()
                        SVProgressHUD.showError(withStatus: "Error! This is the wrong Partner, please scan \(String(describing: self!.partnerCell.partner!.name!))")
                        return
                    }
                    
                    SVProgressHUD.show(withStatus: "Reading and checking valid partner data")
                    DispatchQueue.global().async {
                        let getPartnerByScannedName = self!.partnerRepository!.getPartner(name: qrPartnerResult.Name!)
                        
                        if getPartnerByScannedName == nil {
                            print("Error: Could not decode data into QRPartnerResult")
                            SVProgressHUD.showError(withStatus: "Error! Could not decode partner data! Reason: wrong data was scanned!")
                            return
                        }

                        let generateKey = SecretKeySupplier.generateVerificationKey(value: getPartnerByScannedName!.name!)

                        if(qrPartnerResult.Key == generateKey) {
                            SVProgressHUD.showSuccess(withStatus: "Successfully validated partner")
                            self!.updateQRPartnerResult(qrPartnerResult: qrPartnerResult)
                        } else {
                            SVProgressHUD.dismiss()
                            SVProgressHUD.showError(withStatus: "Error! QR coded partner data is invalid!")
                        }
                    }
                        
                    
                } catch {
                    print(error.localizedDescription)
                    SVProgressHUD.dismiss()
                    SVProgressHUD.showError(withStatus: "Error, something went wrong while trying to read Partner QR code, please try again")
                }
            }
        }
    }
    
    private func updateQRPartnerResult(qrPartnerResult: QRPartnerResult) {
        _ = partnerRepository!.updatePartner(stamp: true, name: qrPartnerResult.Name!)
        DispatchQueue.main.async {
            self.parentVC.collectionView.performBatchUpdates({

                self.parentVC.partners.first(where: {$0.name! == qrPartnerResult.Name})?.hasStamped = true
                self.parentVC.searchPartners.first(where: {$0.name! == qrPartnerResult.Name})?.hasStamped = true
                self.parentVC.collectionView.reloadItems(at: [self.partnerCellIndex])
            })
        }
    }
    
    func readerDidCancel(_ reader: QRCodeReaderViewController) {
        reader.stopScanning()
        
        dismiss(animated: true, completion: nil)
    }
}
