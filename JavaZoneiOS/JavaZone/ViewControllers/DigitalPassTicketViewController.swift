import UIKit

class DigitalPassTicketViewController: UIViewController {
    @IBOutlet weak var qrCodeTicketImageView: UIImageView!
    @IBOutlet weak var scanTicketButton: UIButton!
    @IBOutlet weak var deleteTicketButton: UIButton!
    
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
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
