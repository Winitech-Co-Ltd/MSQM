//
//  File.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/04/20.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit

//Date Picker 아래 함수와 같이쓰면됨
//    @objc func tapDone() {
//        if let datePicker = self.startTimeTF.inputView as? UIDatePicker {
//            let dateformatter = DateFormatter()
//            dateformatter.dateStyle = .none
//            dateformatter.timeStyle = .short
////            dateformatter.dateFormat = "HH"
//            self.startTimeTF.text = dateformatter.string(from: datePicker.date)
//        }
//        self.startTimeTF.resignFirstResponder()
//    }
//extension UITextField {
//
//    func setInputViewDatePicker(target: Any, selector: Selector) {
//        // Create a UIDatePicker object and assign to inputView
//        let screenWidth = UIScreen.main.bounds.width
//        let datePicker = UIDatePicker(frame: CGRect(x: 0, y: 0, width: screenWidth, height: 216))//1
//        datePicker.datePickerMode = .time //2
//        self.inputView = datePicker //3
//
//        // Create a toolbar and assign it to inputAccessoryView
//        let toolBar = UIToolbar(frame: CGRect(x: 0.0, y: 0.0, width: screenWidth, height: 44.0)) //4
//        let flexible = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil) //5
//        let cancel = UIBarButtonItem(title: "취소", style: .plain, target: nil, action: #selector(tapCancel)) // 6
//        let barButton = UIBarButtonItem(title: "선택", style: .plain, target: target, action: selector) //7
//        toolBar.setItems([cancel, flexible, barButton], animated: false) //8
//        self.inputAccessoryView = toolBar //9
//    }
//
//    @objc func tapCancel() {
//        self.resignFirstResponder()
//    }
//
//}

class SensorView: UIView {

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
//        self.backgroundColor = UIColor.systemYellow
    }
}
