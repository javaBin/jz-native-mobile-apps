import UIKit
import AVFoundation
import QRCodeReader
import Contacts
import SVProgressHUD

class DigitalPassTicketViewController: UIViewController, QRCodeReaderViewControllerDelegate {
    @IBOutlet weak var qrCodeTicketImageView: UIImageView!
    @IBOutlet weak var scanTicketButton: UIButton!
    @IBOutlet weak var deleteTicketButton: UIButton!
    
    var hasDeletedTicket : Bool = false
    var ticketRepository : TicketRepository?
    var partnerRepository : PartnerRepository?
    
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
        deleteTicketButton.isEnabled = false
        scanTicketButton.isEnabled = true
        // Do any additional setup after loading the view.
        let ticketData = ticketRepository!.getTicket()

        CheckAndCreateQrCodeByTicket(ticketData: ticketData)
        
    }
    
    private func CheckAndCreateQrCodeByTicket(ticketData: Ticket?) {
        // Check if we are in a simulator
        var ticketCheck: Ticket? = ticketData
        #if targetEnvironment(simulator)
        ticketCheck = TicketProvider.sharedInstance.mockTicket()
        #endif
        
        if ticketCheck != nil && ticketCheck!.vCardData != nil {
            let date = Date()

            let calendar = Calendar.current
            let components = calendar.dateComponents([.year, .month, .day], from: date)

            if ticketCheck!.jzYear != components.year {
                self.deleteTicket(ticket: ticketCheck!)
                return
            }
    
            do {
                
                guard let qrFilter = CIFilter(name: "CIQRCodeGenerator") else {
                    return
                }
                
                var qrDataTransformed = ticketCheck!.vCardData!.data(using: String.Encoding.ascii)
                qrFilter.setValue(qrDataTransformed, forKey: "inputMessage")
                
                // Get the output image
                guard let qrImage = qrFilter.outputImage else { return }
                // Scale the image
                let transform = CGAffineTransform(scaleX: 10, y: 10)
                let scaledQrImage = qrImage.transformed(by: transform)
                
                let context = CIContext()
                guard let cgImage = context.createCGImage(scaledQrImage, from: scaledQrImage.extent) else { return }
                let processedImage = UIImage(cgImage: cgImage)
                
                qrCodeTicketImageView.image = processedImage
                qrCodeTicketImageView.contentMode = UIView.ContentMode.scaleAspectFit
                deleteTicketButton.isEnabled = true
                scanTicketButton.isEnabled = false
            }
            catch {
                
            }
        } else {
            qrCodeTicketImageView.image = UIImage(named: "mysteryman")
            qrCodeTicketImageView.contentMode = UIView.ContentMode.scaleAspectFit
        }
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
    
    @IBAction func deleteTicketAction(_ sender: Any) {
        let getTicket = ticketRepository!.getTicket()
        let alert: UIAlertController
        
        if(getTicket != nil) {
            alert = UIAlertController(title: "Information", message: "Are you sure you want to delete your ticket? All your scanned partners will also disappear", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "Yes", style: .default, handler: { action in
            self.deleteTicket(ticket: getTicket!)
            self.hasDeletedTicket = true

            }))
            alert.addAction(UIAlertAction(title: "No", style: .cancel, handler: nil))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    private func deleteTicket(ticket: Ticket) {
        SVProgressHUD.showInfo(withStatus: "Deleted ticket and scanned partners")
        self.partnerRepository?.deleteAll()
        self.ticketRepository?.deleteTicket(item: ticket)
        self.scanTicketButton.isEnabled = true
        self.deleteTicketButton.isEnabled = false
        self.CheckAndCreateQrCodeByTicket(ticketData: nil)
        
    }
    
    func reader(_ reader: QRCodeReaderViewController, didScanResult result: QRCodeReaderResult) {
        reader.stopScanning()
        SVProgressHUD.show()
        dismiss(animated: true) { [weak self] in
            if let data = result.value.data(using: .utf8) {
                do {
                    let ticketCard = try CNContactVCardSerialization.contacts(with: data)
                    let ticketData = ticketCard.first
                    
                    if(ticketData == nil) {
                        throw NSError(domain: "Error", code: 92, userInfo: ["":""])
                    }
                    
                    let ticket = Ticket()
                    let year = Int(RemoteConfigValues.sharedInstance.string(key: "javazone_year"))
                    ticket.vCardData = result.value
                    ticket.jzYear = year!
                    self!.ticketRepository!.addTicketAsync(ticket: ticket)
                    
                    SVProgressHUD.showSuccess(withStatus: "Successfully scanned JavaZone ticket")
                    
                    self?.CheckAndCreateQrCodeByTicket(ticketData: ticket)
                    
                    
                }
                catch let error as NSError  {
                    let alert: UIAlertController
                    alert = UIAlertController(title: "Error", message: "Ticket failed to read, please use a valid JavaZone QR Code ticket", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: nil))
                    self!.present(alert, animated: true, completion: nil)
                    SVProgressHUD.showError(withStatus: "Failed to scan ticket, please check if it is this years JavaZone ticket or contact javaBiners")

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
