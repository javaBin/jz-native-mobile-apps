import UIKit
import DisplaySwitcher
import SVProgressHUD
import FirebaseDatabase
import FirebaseStorage


class PartnerListViewController: UIViewController , UISearchBarDelegate, UICollectionViewDataSource, UICollectionViewDelegate {
    private let animationDuration: TimeInterval = 0.3
    var searchActive : Bool = false
    private let listLayoutStaticCellHeight: CGFloat = 80
    private let gridLayoutStaticCellHeight: CGFloat = 120
    var partnerRepository: PartnerRepository?
    var refresher: UIRefreshControl?

    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    fileprivate var tap: UITapGestureRecognizer!
    fileprivate var collectionViewTap: UITapGestureRecognizer!
    
    var partners = [PartnerView]()
    var searchPartners = [PartnerView]()
    fileprivate var isTransitionAvailable = true
    fileprivate lazy var gridLayout = DisplaySwitchLayout(staticCellHeight: gridLayoutStaticCellHeight, nextLayoutStaticCellHeight: listLayoutStaticCellHeight, layoutState: .grid)
    
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
        
        
        refresher = UIRefreshControl()
        if #available(iOS 10.0, *) {
            self.collectionView.refreshControl = self.refresher
        } else {
            self.collectionView.addSubview(self.refresher!)
        }
        
        refresher?.attributedTitle = NSAttributedString(string: "Pull to refresh")
        refresher?.tintColor = UIColor(red:1.00, green: 0.21, blue: 0.55, alpha: 1.0)
        refresher?.addTarget(self, action: #selector(self.getAllPartnersFromFirebase), for: UIControl.Event.valueChanged)
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
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        // sessionSearchBar.text = ""
        searchBar.resignFirstResponder()
    }
    
    func getAllPartners() {
        SVProgressHUD.show()
        var partnerList : [Partner]? = nil
        DispatchQueue.global().sync {
            partnerList = self.partnerRepository!.getAllPartners()
        }
        
        if partnerList != nil && partnerList!.count > 0 {
            for partner in partnerList! {
                self.partners.append(self.mapPartnerToPartnerView(partner: partner))
            }
            
            self.reInitializeCollectionView()
        } else {
            self.getAllPartnersFromFirebase()
        }
        
    }
    
    @objc func getAllPartnersFromFirebase()
    {
        let ref = Database.database().reference(withPath: "partners")
        self.partners.removeAll()
        var partnerData = [Partner]()
        _ = ref.queryLimited(toFirst: 100).observe(.value) { snapshot in
            for child in snapshot.children {
                let partner = self.createPartner(snapshot: child as! DataSnapshot)
                
                let getExistingPartner = self.partnerRepository!.getPartner(name: partner.name!)
                
                if(getExistingPartner != nil) {
                    if getExistingPartner!.hasStamped {
                        partner.hasStamped = getExistingPartner!.hasStamped
                    }
                    
                    self.partnerRepository?.updatePartnerData(updatedData: partner)
                    
                    
                } else {
                    partnerData.append(partner)
                }
                
                self.partners.append(self.mapPartnerToPartnerView(partner: partner))
            }
            
            if(partnerData.count > 0) {
                self.partnerRepository?.addAsync(items: partnerData)
            }
            self.reInitializeCollectionView()
            self.refresher?.endRefreshing()
        }
    }
    
    private func mapPartnerToPartnerView(partner: Partner) -> PartnerView {
        var partnerView = PartnerView()
        partnerView.name = partner.name
        partnerView.hasStamped = partner.hasStamped
        partnerView.homepageUrl = partner.homepageUrl
        partnerView.logoUrl = partner.logoUrl
        partnerView.latitude = partner.latitude
        partnerView.longitude = partner.longitude
        return partnerView
    }
    
    private func reInitializeCollectionView() {
        self.partners.sort(by: { $0.name! < $1.name! })
        self.searchPartners = self.partners
        DispatchQueue.main.async {
            self.collectionView.reloadData()
        }
        SVProgressHUD.dismiss()
        
        Database.database().reference(withPath: "partners").observe(.childChanged) { (snapshot, key) in
            var changedOrNewPartnerData = self.createPartner(snapshot: snapshot)
            let getPartner = self.partnerRepository!.getPartner(name: changedOrNewPartnerData.name!)
            
            if getPartner != nil {
                // TODO
                self.partnerRepository!.updatePartnerData(updatedData: changedOrNewPartnerData)
                
            } else {
                self.partnerRepository!.addPartner(partner: changedOrNewPartnerData)
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
            let partnerInformation = PartnerCellData()
            
            partnerInformation.Cell = collectionView?.cellForItem(at: indexPath) as? PartnerCollectionViewCell
            partnerInformation.Index = indexPath
            self.performSegue(withIdentifier: "partnerInformationSegue", sender: partnerInformation)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "partnerInformationSegue" {
            let newViewController = segue.destination as! PartnerDetailViewController
            let partnerInformation = sender as! PartnerCellData
            newViewController.partnerCell = partnerInformation.Cell as? PartnerCollectionViewCell
            newViewController.partnerCellIndex = partnerInformation.Index as? IndexPath
            newViewController.parentVC = self
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
        cell.partner = searchPartners[(indexPath as NSIndexPath).row]
        cell.partnerName = searchPartners[(indexPath as NSIndexPath).row].name!
        cell.bind(partnerView: searchPartners[(indexPath as NSIndexPath).row])

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
            searchPartners = partners.filter { return $0.name!.contains(searchText) }
        }
        
        DispatchQueue.main.async {
            self.collectionView.reloadData()
        }
    }
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        view.addGestureRecognizer(tap)
        searchBar.showsCancelButton = true
    }
    
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        view.removeGestureRecognizer(tap)
        searchBar.showsCancelButton = false
    }
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        searchBar.text = ""
        searchBar.showsCancelButton = false
        searchBar.endEditing(true)
        
        self.searchPartners = self.partners
        print(self.searchPartners)
        DispatchQueue.main.async {
            self.collectionView.reloadData()
        }
    }
    
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchBar.showsCancelButton = true
        
    }
    
    @objc func handleTap() {
        view.endEditing(true)
    }
    
}
