//
//  CustomAlertView.swift
//  SQSM_ios_peru
//
//  Created by 개발용 맥북 on 2020/04/28.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import UIKit

extension UIView {
    func loadView(nibName: String) -> UIView? {
        let bundle = Bundle(for: type(of: self))
        let nib = UINib(nibName: nibName, bundle: bundle)
        return nib.instantiate(withOwner: self, options: nil).first as? UIView
    }
    
    var mainView: UIView? {
        return subviews.first
    }
}

//Pantalla de visualización de información del encargado oficial
class CustomAlertView : UIView {
    @IBOutlet var department: UITextView!
    @IBOutlet var name: UITextView!
    @IBOutlet var officeNumber: UITextView!
    @IBOutlet var phoneNumber: UITextView!
    @IBOutlet var childView: UIView!
    @IBOutlet var officeNum: UIButton!
    @IBOutlet var phoneNum: UIButton!
    @IBOutlet var officeNumView: UIView!
    @IBOutlet var phoneNumView: UIView!
    @IBOutlet var navigationBar: UINavigationBar!
    @IBOutlet var parentView: UIView!
    @IBOutlet var naviItem: UINavigationItem!
    @IBOutlet var InstitutionLabel: UILabel!
    @IBOutlet var nameLabel: UILabel!
    @IBOutlet var telNumLabel: UILabel!
    @IBOutlet var officeNumLabel: UILabel!
    @IBOutlet var close: UIButton!
    
    var mSelector:Selector?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.commonInitialization()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        self.commonInitialization()
    }
    
    /// Cambiar el tamaño y el color del texto
    func commonInitialization() {
        guard let view = loadView(nibName: "CustomAlertView") else {return}
        view.frame = self.bounds
        self.addSubview(view)
        self.setLabel()
        setText(textView: name, preference: StaticString.OfficialInfoPreference.officialName.rawValue)
        setText(textView: department, preference: StaticString.OfficialInfoPreference.officialDepartment.rawValue)
        setText(textView: officeNumber, preference: StaticString.OfficialInfoPreference.officeTelNumber.rawValue)
        setText(textView: phoneNumber, preference: StaticString.OfficialInfoPreference.officialPhoneNumber.rawValue)
        
        commFunction.comm.setTextView(sender: name)
        commFunction.comm.setTextView(sender: department)
        commFunction.comm.setView(sender: officeNumView)
        commFunction.comm.setView(sender: phoneNumView)
        commFunction.comm.setView(sender: childView)
        
        let textAttributes = [NSAttributedString.Key.foregroundColor:UIColor.white]
        navigationBar.titleTextAttributes = textAttributes
    }
    
    /// Clic en el botón Cerrar
    @IBAction func onClickClose(_ sender: UIButton) {
        self.removeFromSuperview()
    }
    
    
    /// Llamer al teléfono de la oficina del encargado oficial
    @IBAction func onClickOfficeCall(_ sender: UIButton) {
        callPhone(number: officeNumber.text)
    }
    
    
    /// Llamer al teléfono móvil del encargado oficial
    @IBAction func onClickPhoneCall(_ sender: UIButton) {
        callPhone(number: phoneNumber.text)
    }
    
    //Hacer llamada
    func callPhone(number : String){
        if number != "" && number != "-" {
            if let phoneCallURL = URL(string: "tel://\(number)") {
                let application:UIApplication = UIApplication.shared
                if (application.canOpenURL(phoneCallURL)) {
                    application.open(phoneCallURL, options: [:], completionHandler: nil)
                }
            }
        }
    }
    
    func setText(textView : UITextView, preference: String) {
        textView.text = commFunction.comm.preference.string(forKey: preference) ?? "-"
    }
    
    
    /// Introducir contenido de texto
    func setLabel() {
        commFunction.comm.setNaviTitle(navItem: self.naviItem, titleText: "Información del Encargado Oficial".localized, font: 16)
        self.InstitutionLabel.text = "Institución".localized
        self.nameLabel.text = "Nombre".localized
        self.telNumLabel.text = "Teléfono móvil".localized
        self.officeNumLabel.text = "Teléfono de oficina".localized
        self.close.setTitle("Cerrar".localized, for: .normal)
    }
}
