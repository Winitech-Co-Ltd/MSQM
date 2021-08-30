//
//  CustomTextField.swift
//  SQSM_ios
//
//  Created by 개발용 맥북 on 2020/04/07.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import UIKit

class CustomTextField : UITextField {
    
    //Evita el cambio del valor de la ventana de introducción mediante un clic largo (long click)
    override func canPerformAction(_ action: Selector, withSender sender: Any?) -> Bool {
        switch action {
        case #selector(UIResponderStandardEditActions.paste(_:)): return false
        default:
            return super.canPerformAction(action, withSender: sender)
        }
    }
}
