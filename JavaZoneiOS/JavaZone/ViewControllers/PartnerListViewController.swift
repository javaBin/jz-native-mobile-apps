import UIKit
import DisplaySwitcher

private let animationDuration: TimeInterval = 0.3

private let listLayoutStaticCellHeight: CGFloat = 80
private let gridLayoutStaticCellHeight: CGFloat = 120

class PartnerListViewController: UIViewController , UISearchBarDelegate, UICollectionViewDataSource, UICollectionViewDelegate {
    
    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    fileprivate var tap: UITapGestureRecognizer!
    fileprivate var collectionViewTap: UITapGestureRecognizer!
    
    fileprivate var partners = PartnerDataProvider().generateFakeCompanies()
    fileprivate var searchPartners = [Partner]()
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
    }
    
    // MARK: - Private methods
    fileprivate func setupCollectionView() {
        collectionView.collectionViewLayout = gridLayout
        collectionView.register(PartnerCollectionViewCell.cellNib, forCellWithReuseIdentifier:PartnerCollectionViewCell.id)
    }
    
    // MARK: - Actions
    @IBAction func buttonTapped(_ sender: AnyObject) {

    }
    
    @objc func gesture(_ sender: UITapGestureRecognizer) {
        let point = sender.location(in: collectionView)
        if let indexPath = collectionView?.indexPathForItem(at: point) {
            let cell = collectionView?.cellForItem(at: indexPath) as! PartnerCollectionViewCell
            self.performSegue(withIdentifier: "partnerInformationSegue", sender: cell)

        }
    }
    
    
    func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if segue.identifier == "partnerInformationSegue" {
            let newViewController = segue.destination as! PartnerDetailViewController
            let partner = sender as! PartnerCollectionViewCell
            newViewController.partner = partner as! Partner
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
