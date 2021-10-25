//
//  CheckBOx.swift
//  TestSampleApp
//
//  Created by wini on 2020/02/13.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit

//Botón agregado con función de casilla de chequeo o selección
class CheckBox: UIButton {
    // Images
    let checkedImage = UIImage(named: "check_box_on")! as UIImage
    let uncheckedImage = UIImage(named: "check_box_off")! as UIImage

    // Bool property
    var isChecked: Bool = false {
        didSet{
            if isChecked == true {
                self.setImage(checkedImage, for: UIControl.State.normal)
            } else {
                self.setImage(uncheckedImage, for: UIControl.State.normal)
            }
        }
    }

    override func awakeFromNib() {
        self.addTarget(self, action:#selector(buttonClicked(sender:)), for: UIControl.Event.touchUpInside)
        self.isChecked = false
    }

    @objc func buttonClicked(sender: UIButton) {
        if sender == self {
            isChecked = !isChecked
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        
        self.addTarget(self, action:#selector(buttonClicked(sender:)), for: UIControl.Event.touchUpInside)
        self.isChecked = false
        self.setImage(uncheckedImage, for: UIControl.State.normal)
    }

    init() {
        super.init(frame: CGRect.zero)
    }
    
    required init(coder: NSCoder) {
        super.init(coder: coder)!
    }
    
    
}
