import UIKit
import QRCodeReader
import Contacts

class DigitalPassTicketViewController: UIViewController, QRCodeReaderViewControllerDelegate {
    @IBOutlet weak var qrCodeTicketImageView: UIImageView!
    @IBOutlet weak var scanTicketButton: UIButton!
    @IBOutlet weak var deleteTicketButton: UIButton!
    
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
        
        // Do any additional setup after loading the view.
        CheckAndCreateQrCodeByTicket()
        
    }
    
    private func CheckAndCreateQrCodeByTicket() {
        let ticketData = "http://pennlabs.org"
        
        self.deleteTicketButton.isHidden = true
        let data = ticketData.data(using: String.Encoding.ascii)
        
        guard let qrFilter = CIFilter(name: "CIQRCodeGenerator") else {
            return
        }
        
        qrFilter.setValue(data, forKey: "inputMessage")
        
        // Get the output image
        guard let qrImage = qrFilter.outputImage else { return }
        // Scale the image
        let transform = CGAffineTransform(scaleX: 10, y: 10)
        let scaledQrImage = qrImage.transformed(by: transform)
        
        let context = CIContext()
        guard let cgImage = context.createCGImage(scaledQrImage, from: scaledQrImage.extent) else { return }
        let processedImage = UIImage(cgImage: cgImage)
        
        qrCodeTicketImageView.image = processedImage
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
                    let ticketCard = try CNContactVCardSerialization.contacts(with: data)
                    let ticket = ticketCard.first
                    print("\(String(describing: ticket?.familyName))")
                }
                catch {
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
