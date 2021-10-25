//
//  AgreeViewController.swift
//  SQSM_ios
//
//  Created by MiRan Kim on 2020/03/02.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import PDFKit

class AgreeViewController: UIViewController,UIPickerViewDelegate, UIPickerViewDataSource, PDFViewDelegate {
    //MARK: - picker view
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return self.nationList.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return self.nationList[row].1 + self.nationList[row].0
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if pickerView.tag == 1 {
            if row != 0 {
                let na = self.nationList[row]
                self.etcCountry.text = "\(na.0)"
                self.etcDetailCountry = na.0
                self.countryCode = na.2
            }
        }
    }
    
    //MARK: - outlet
    @IBOutlet var contentView: UIView!
    @IBOutlet var etcCountry: UITextField!
    @IBOutlet var naviTitle: UINavigationBar!
    @IBOutlet var nationality: UILabel!
    @IBOutlet var country: UISegmentedControl!
    @IBOutlet var confirm: UIButton!
    @IBOutlet var cancel: UIButton!
    @IBOutlet var PDFView: PDFView!
    @IBOutlet var naviItem: UINavigationItem!
    @IBOutlet var naviBar: UINavigationBar!
    
    //MARK: - variable
    var countryCode : String?
    var etcDetailCountry: String?
    var nationList : Array<(String, String, String)>!
    //toolBar for non-returnType textField
    let toolBar = UIToolbar().ToolbarPiker(mySelect: #selector(done(_:)))
    
    //MARK: - lifeCycle
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardOnTouchOnView()
        self.etcCountry.inputAccessoryView = toolBar
        
        
        commFunction.comm.setTextFieldBorder(sender: self.etcCountry)
        commFunction.comm.setSegmentFont(segment: self.country, font: UIFont.systemFont(ofSize: 20))
        
        let picker = UIPickerView()
        picker.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: self.view.frame.height / 3)
        picker.delegate = self
        picker.dataSource = self
        picker.tag = 1
        self.nationList = self.searchContry()
        self.etcCountry.inputView = picker
        
        
        self.PDFView.delegate = self
        if let path = Bundle.main.path(forResource: StaticString.agreeString.pdfName.rawValue, ofType: "pdf") {
            let url = URL(fileURLWithPath: path)
            if let pdfDocs = PDFDocument(url: url) {
                PDFView.document = pdfDocs
                PDFView.displayDirection = .vertical
                PDFView.pageBreakMargins = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
                PDFView.autoScales = true
            }
        }
        commFunction.comm.setNaviTitle(navItem: naviItem, titleText: StaticString.agreeString.title.rawValue.localized, font: 11)
    }
    
    //Pasar la información del país seleccionado por el usuario a la pantalla de registro
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let vc = segue.destination as! AuthViewController
        
        switch countryCode {
        case "PE":
            vc.paramCountryCode = "PE"
        default:
            vc.paramCountryCode = countryCode
            vc.paramCountryName = etcDetailCountry!
        }
    }
    
    //MARK: - ui action
    
    @IBAction func onSelectCountry(_ sender: UISegmentedControl) {
        switch sender.selectedSegmentIndex {
        case 0:
            self.countryCode = "PE"
            self.etcCountry.text = ""
            self.etcCountry.isHidden = true
            self.view.endEditing(true)
        case 1:
            self.countryCode = ""
            self.etcCountry.isHidden = false
        default:
            break
        }
    }
    
    @IBAction func onClickConfirm(_ sender: UIButton) {
        switch country.selectedSegmentIndex {
        case 0:
            self.performSegue(withIdentifier: "goCertification", sender: self)
            
        case 1:
            if country.selectedSegmentIndex == 1 && self.etcCountry.text == "" {
                commFunction.comm.makeToast(controller: self, message: StaticString.displayText.selectCoutnry.rawValue.localized, result: nil)
            } else {
                self.performSegue(withIdentifier: "goCertification", sender: self)
            }
            
        default :
            commFunction.comm.makeToast(controller: self, message: StaticString.displayText.selectCoutnry.rawValue.localized, result: nil)
        }
    }
    
    @IBAction func onClickCancel(_ sender: UIButton) {
        exit(1)
    }
    
    @IBAction func onClickCheckBox(_ sender: CheckBox) {
        if !sender.isChecked {
            self.confirm.isEnabled = true
        } else {
            self.confirm.isEnabled = false
        }
    }
    
    //MARK: - function
    //Bajar el teclado
    @objc func done(_ sender:UITextField){
        self.view.endEditing(true)
    }
    
    /// Obtener lista de códigos de países y nombres traducidos al español
    /// - Returns: Array <Set(país, código de país)>
    func searchContry() -> Array<(String, String, String)> {
        var countriesData = [(name: String, flag: String, CODE: String)]()
        
        let deviceLocale = UserDefaults.standard.string(forKey: StaticString.introString.appLang.rawValue) ?? "en"
        for code in NSLocale.isoCountryCodes  {
            
            let flag = String.emojiFlag(for: code)
            let id = NSLocale.localeIdentifier(fromComponents: [NSLocale.Key.countryCode.rawValue: code])
            
            if let name = NSLocale(localeIdentifier: deviceLocale).displayName(forKey: NSLocale.Key.identifier, value: id) {
                countriesData.append((name: name, flag: flag!, CODE: code))
            }else{
                //"Country not found for code: \(code)"
            }
        }
        countriesData.sort(by: {$0.name < $1.name})
        countriesData.insert(("Seleccionar Nacionalidad".localized,"",""), at: 0)
        return countriesData
    }
    
    /// Al toque en la pantalla se baja el techado
    func hideKeyboardOnTouchOnView()
    {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(
            target: self,
            action: #selector(self.dismissKeyboard))
        
        self.view.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
}
