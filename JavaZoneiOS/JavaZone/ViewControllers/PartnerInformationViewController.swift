//
//  PartnerInformationViewController.swift
//  JavaZone
//
//  Created by Khiem-Kim Ho Xuan on 22/05/2019.
//  Copyright © 2019 javaBin. All rights reserved.
//

import UIKit

class PartnerInformationViewController: UIViewController {
    @IBOutlet weak var informationTextField: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        informationTextField.setContentOffset(CGPoint.zero, animated: false)
        informationTextField.flashScrollIndicators()
    }

}
