//
//  AdiminDialog.swift
//  TestSampleApp
//
//  Created by MiRan Kim on 2020/02/18.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit

class AdiminDialog: UIView {

    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */
    
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.commonInitialization()
    }
    
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)!
        self.commonInitialization()
    }
    
    func commonInitialization() {
        let view = Bundle.main.loadNibNamed("AdiminDialog", owner: self, options: nil)?.first as! UIView
        view.frame = self.bounds
        self.addSubview(view)
    }

}
