//
//  CustomPickerDialog.swift
//  SQSM_ios
//
//  Created by 개발용 맥북 on 2020/05/13.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import Foundation

class CustomPickerDialog: UIView{
    @IBOutlet var masterView: UIView!
    @IBOutlet var pickerView: UIPickerView!
    @IBOutlet var select: UIButton!
    @IBOutlet var cancel: UIButton!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.commonInitialization()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        self.commonInitialization()
    }
    
    func commonInitialization() {
        guard let view = loadView(nibName: "CustomPickerDialog") else {return}
        view.frame = self.bounds
        self.addSubview(view)
        self.cancel.setTitle("Cancel".localized, for: .normal)
        self.select.setTitle("Select".localized, for: .normal)
    }
    
    @IBAction func onClickSelect(_ sender: UIButton) {
        print(pickerView.tag)
        switch pickerView.tag {
        case 12:
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: StaticString.observerName.department.rawValue), object: nil)
        case 13:
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: StaticString.observerName.province.rawValue), object: nil)
        default:
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: StaticString.observerName.district.rawValue), object: nil)
        }
        self.removeFromSuperview()
    }
    
    @IBAction func onClickCancel(_ sender: UIButton) {
        self.removeFromSuperview()
    }
    
    func pickerSetting(tag: Int) {
        pickerView.tag = tag
    }
    
    func disableSelect() {
        select.isEnabled = false
        select.setTitleColor(UIColor.gray, for: .normal)
    }
    
    func enableSelect() {
        select.isEnabled = true
        select.setTitleColor(UIColor.white, for: .normal)
    }
}
