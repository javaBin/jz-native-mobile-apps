import UIKit
import AVFoundation
import QRCodeReader
import Contacts
import ObjectMapper

class PartnerDetailViewController: UIViewController, QRCodeReaderViewControllerDelegate {
    var partner: Partner!
    @IBOutlet weak var partnerImageView: UIImageView!
    @IBOutlet weak var partnerUrlTextView: UITextView!
    @IBOutlet weak var partnerName: UITextField!
    
    var saltedName: String = ""
    
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
        
        let attributedString = NSMutableAttributedString(string: partner!.homepageUrl!, attributes:[NSAttributedString.Key.link: URL(string: partner!.homepageUrl!)!, NSAttributedString.Key.paragraphStyle: paragraph, NSAttributedString.Key.font: font ])
        partnerImageView.imageFromUrl(urlString: (partner!.logoUrl!))
        partnerImageView.contentMode = UIView.ContentMode.scaleAspectFit
        partnerUrlTextView.attributedText = attributedString
        partnerName.text = partner!.name
        
        saltedName = RemoteConfigValues.sharedInstance.string(key: "salted_partner_name")

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
        guard checkScanPermissions() else { return }
        
        readerVC.modalPresentationStyle = .formSheet
        readerVC.delegate               = self
        
        readerVC.completionBlock = { (result: QRCodeReaderResult?) in
            if let result = result {
                print("Completion with result: \(result.value) of type \(result.metadataType)")
            }
        }
        
        present(readerVC, animated: true, completion: nil)
    }
    
    func reader(_ reader: QRCodeReaderViewController, didScanResult result: QRCodeReaderResult) {
        reader.stopScanning()

        
        dismiss(animated: true) { [weak self] in
            if let data = result.value.data(using: .utf8) {
                do {
                    guard let qrPartnerResult = try? JSONDecoder().decode(QRPartnerResult.self, from: data ) else {
                        print("Error: Could not decode data into QRPartnerResult")
                        return
                    }
                    
                    // TODO
                    
                    let generateKey = SecretKeySupplier.generateVerificationKey(value: self.partner.name!)
                    
                    if(qrPartnerResult.Key == generateKey) {
                        
                    }
                    
                    
                    
                } catch {
                    print(error.localizedDescription)
                }
            }
        }
    }
    
    func readerDidCancel(_ reader: QRCodeReaderViewController) {
        reader.stopScanning()
        
        dismiss(animated: true, completion: nil)
    }
}
