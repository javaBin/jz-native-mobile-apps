import UIKit

class SessionDetailViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    @IBOutlet weak var sessionTitleLabel: UILabel!
    @IBOutlet weak var roomLabel: UILabel!
    @IBOutlet weak var abstractTextView: UITextView!
    @IBOutlet weak var intendedAudienceTextView: UITextView!
    @IBOutlet weak var speakerTableView: UITableView!
    
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var subScrollView: UIView!
    
    var session: Session?
    
    override func viewDidLoad() {
        super.viewDidLoad()        
        sessionTitleLabel?.text = session!.title
        roomLabel?.text = session!.room
        abstractTextView?.text = session!.abstract
        intendedAudienceTextView?.text = session!.intendedAudience
        
        speakerTableView.delegate = self
        speakerTableView.dataSource = self
        speakerTableView.tableFooterView = UIView()
        speakerTableView.isUserInteractionEnabled = true
        speakerTableView.allowsSelection = true
    
    }
    
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return true
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func viewDidLayoutSubviews() {
    
        var calculatedHeight: CGFloat = 0.0
        let getSubView = self.scrollView.subviews[0]
        var contentRect = CGRect.zero
        for uiView in getSubView.subviews {
            calculatedHeight += uiView.frame.size.height
            contentRect = contentRect.union(uiView.frame)
        }
        
        
        contentRect.size = CGSize(width:  scrollView.contentSize.width, height: calculatedHeight + 200)
        scrollView.contentSize.height = calculatedHeight + 200
        
        
        var visibleRect = CGRect.zero;
        visibleRect.origin = scrollView.contentOffset;
        visibleRect.size = scrollView.contentSize
        getSubView.frame = visibleRect
        self.view.layoutIfNeeded()
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return session!.speakers!.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = speakerTableView.dequeueReusableCell(withIdentifier: "SpeakerCell", for: indexPath) as! SpeakerTableViewCell
        cell.speakerLabel?.text = session!.speakers![indexPath.row].name
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print("IN here")
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == "speakerDetailSegue"{
            var vc = segue.destination as! SpeakerDetailController
            let indexPath = speakerTableView.indexPathForSelectedRow
            
            // vc.speaker = speaker
        }
    }
}

class SpeakerTableViewCell: UITableViewCell {
    @IBOutlet weak var speakerLabel: UILabel!
}
