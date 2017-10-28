//
//  SessionDetailViewController.swift
//  JZiOS
//
//  Created by Khiem-Kim Ho Xuan on 27/10/2017.
//  Copyright © 2017 Khiem-Kim Ho Xuan. All rights reserved.
//

import UIKit

class SessionDetailViewController: UIViewController {
    @IBOutlet weak var sessionTitleLabel: UILabel!
    @IBOutlet weak var roomLabel: UILabel!
    @IBOutlet weak var abstractTextView: UITextView!
    @IBOutlet weak var intendedAudienceTextView: UITextView!
    
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var subScrollView: UIView!
    
    var session: Session?
    
    override func viewDidLoad() {
        super.viewDidLoad()        
        sessionTitleLabel?.text = session!.title
        roomLabel?.text = session!.room
        abstractTextView?.text = session!.abstract
        intendedAudienceTextView?.text = session!.intendedAudience
        
        /*
        
        let fixedWidth = abstractTextView.frame.size.width
        abstractTextView.sizeThatFits(CGSize(width: fixedWidth, height: CGFloat.greatestFiniteMagnitude))
        let newSize = abstractTextView.sizeThatFits(CGSize(width: fixedWidth, height: CGFloat.greatestFiniteMagnitude))
        var newFrame = abstractTextView.frame
        newFrame.size = CGSize(width: max(newSize.width, fixedWidth), height: newSize.height)
        abstractTextView.frame = newFrame */
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidLayoutSubviews() {
    
        var calculatedHeight: CGFloat = 0.0
        var getSubView = self.scrollView.subviews[0]
        for uiView in getSubView.subviews {
            calculatedHeight += uiView.frame.size.height
        }
        
        
        scrollView.contentSize.height = calculatedHeight + 200
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
