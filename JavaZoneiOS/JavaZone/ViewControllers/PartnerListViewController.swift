import UIKit
import DisplaySwitcher
import SVProgressHUD
import FirebaseDatabase
import FirebaseStorage

private let animationDuration: TimeInterval = 0.3

private let listLayoutStaticCellHeight: CGFloat = 80
private let gridLayoutStaticCellHeight: CGFloat = 120
var partnerRepository: PartnerRepository?

class PartnerListViewController: UIViewController , UISearchBarDelegate, UICollectionViewDataSource, UICollectionViewDelegate {
    
    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    fileprivate var tap: UITapGestureRecognizer!
    fileprivate var collectionViewTap: UITapGestureRecognizer!
    
    fileprivate var partners = [Partner]()
    fileprivate var searchPartners = [Partner]()
    fileprivate var isTransitionAvailable = true
    fileprivate lazy var gridLayout = DisplaySwitchLayout(staticCellHeight: gridLayoutStaticCellHeight, nextLayoutStaticCellHeight: listLayoutStaticCellHeight, layoutState: .grid)
    var partnerRepository: PartnerRepository?
    
    // MARK: - Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tap = UITapGestureRecognizer(target: self, action: #selector(handleTap))
        collectionViewTap = UITapGestureRecognizer(target: self, action: #selector(gesture(_:)))
        collectionViewTap.numberOfTapsRequired = 1
        collectionViewTap.numberOfTouchesRequired = 1
        collectionView?.addGestureRecognizer(collectionViewTap)
        searchPartners = partners
        setupCollectionView()
        collectionView.delegate = self
        collectionView.dataSource = self
        searchBar.delegate = self
        getAllPartners()        
    }
    
    // MARK: - Private methods
    fileprivate func setupCollectionView() {
        collectionView.collectionViewLayout = gridLayout
        collectionView.register(PartnerCollectionViewCell.cellNib, forCellWithReuseIdentifier:PartnerCollectionViewCell.id)
    }
    
    // MARK: - Actions
    @IBAction func buttonTapped(_ sender: AnyObject) {

    }
    
    func getAllPartners() {
        SVProgressHUD.show()
        let partnerList = partnerRepository!.getAllPartners()
        if partnerList != nil && partnerList!.count > 0 {
            for partner in partnerList! {
                self.partners.append(partner)
            }
            
            reInitializeCollectionView()
        } else {
            partnerRepository!.deleteAll()
            getAllPartnersFromFirebase()
        }
    }
    
    func getAllPartnersFromFirebase()
    {
        let ref = Database.database().reference(withPath: "partners")
        self.partners.removeAll()
        
        _ = ref.queryLimited(toFirst: 100).observe(.value) { snapshot in
            for child in snapshot.children {
                let partner = self.createPartner(snapshot: child as! DataSnapshot)
                self.partners.append(partner)
            }
            self.reInitializeCollectionView()
        }
    
        self.partnerRepository?.addAsync(items: self.partners)
    }
    
    private func reInitializeCollectionView() {
        self.searchPartners = self.partners
        self.collectionView.reloadData()
        SVProgressHUD.dismiss()
        
        Database.database().reference(withPath: "partners").observe(.childChanged) { (snapshot, key) in
            var changedOrNewPartnerData = self.createPartner(snapshot: snapshot)
            let getPartner = self.partnerRepository!.getPartner(name: changedOrNewPartnerData.name!)
            
            if getPartner != nil {
                // TODO
                self.partnerRepository!.updatePartnerData(updatedData: changedOrNewPartnerData)
                
            } else {
                self.partnerRepository!.addPartnerAsync(partner: changedOrNewPartnerData)
            }
        }
    }
    
    private func createPartner(snapshot: DataSnapshot) -> Partner {
        let dict = snapshot.value as! [String: Any]
        let partner = Partner()
        partner.name = dict["name"] as? String
        partner.logoUrl = dict["logoUrl_png"] as? String
        partner.homepageUrl = dict["homepageUrl"] as? String
        partner.latitude = dict["latitude"] as? String
        partner.longitude = dict["longitude"] as? String
        
        return partner
    }
    
    @objc func gesture(_ sender: UITapGestureRecognizer) {
        let point = sender.location(in: collectionView)
        if let indexPath = collectionView?.indexPathForItem(at: point) {
            let cell = collectionView?.cellForItem(at: indexPath) as! PartnerCollectionViewCell
            self.performSegue(withIdentifier: "partnerInformationSegue", sender: cell)

        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "partnerInformationSegue" {
            let newViewController = segue.destination as! PartnerDetailViewController
            let partnerCell = sender as! PartnerCollectionViewCell
            newViewController.partner = partnerCell.partner as? Partner
        }
    }
}

extension PartnerListViewController {
    
    // MARK: - UICollectionViewDataSource
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return searchPartners.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: PartnerCollectionViewCell.id, for: indexPath) as! PartnerCollectionViewCell
        cell.setupGridLayoutConstraints(1, cellWidth: cell.frame.width)
        cell.bind(searchPartners[(indexPath as NSIndexPath).row])
        cell.partner = searchPartners[(indexPath as NSIndexPath).row]
        return cell
    }
    
    // MARK: - UICollectionViewDelegate
    func collectionView(_ collectionView: UICollectionView, transitionLayoutForOldLayout fromLayout: UICollectionViewLayout, newLayout toLayout: UICollectionViewLayout) -> UICollectionViewTransitionLayout {
        let customTransitionLayout = TransitionLayout(currentLayout: fromLayout, nextLayout: toLayout)
        return customTransitionLayout
    }
    
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        isTransitionAvailable = false
    }
    
    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        isTransitionAvailable = true
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        view.endEditing(true)
    }
    
}

extension PartnerListViewController {
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchText.isEmpty {
            searchPartners = partners
        } else {
            searchPartners = searchPartners.filter { return $0.name!.contains(searchText) }
        }
        
        collectionView.reloadData()
    }
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        view.addGestureRecognizer(tap)
    }
    
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        view.removeGestureRecognizer(tap)
    }
    
    @objc func handleTap() {
        view.endEditing(true)
    }
    
}
