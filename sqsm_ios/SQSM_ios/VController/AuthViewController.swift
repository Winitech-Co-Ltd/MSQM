//
//  SettingViewController.swift
//  TestSampleApp
//
//  Created by wini on 2020/02/12.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import UIKit
import Alamofire

class AuthViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource, UITextFieldDelegate {
    typealias response = (AFDataResponse<Any>) -> Void
    
    //MARK: - picker view function
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        switch pickerView.tag {
        //department
        case 12:
            return self.department_List.count
        //province
        case 13:
            return self.province_List.count
        //district
        default:
            return self.district_List.count
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        switch pickerView.tag {
        case 12:
            return self.department_List[row].1
        case 13:
            return self.province_List[row].1
        default :
            return self.district_List[row].1
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if row != 0 {
            self.subView?.enableSelect()
            switch pickerView.tag {
            case 12:
                let department = self.department_List[row]
                self.DEPARTMENT_CODE = department.0!
                self.DEPARTMENT_NM = department.1
            case 13:
                let province = self.province_List[row]
                self.PROVINCE_CODE = province.0!
                self.PROVINCE_NM = province.1
            default :
                let district = self.district_List[row]
                self.DISTRICT_CODE = district.0!
                self.DISTRICT_NM = district.1
            }
        }
    }
    
    //MARK: - outlet
    
    @IBOutlet var citizenID: UITextField!
    @IBOutlet var scrollView: UIScrollView!
    @IBOutlet var name: UITextField!
    @IBOutlet var gender: UISegmentedControl!
    @IBOutlet var etcCountry: UITextField!
    @IBOutlet var phoneNumber: UITextField!
    @IBOutlet var emergencyNumber: UITextField!
    @IBOutlet var passPortNumber: UITextField!
    @IBOutlet var birth: UITextField!
    @IBOutlet var DEPARTMENT: UITextField!
    @IBOutlet var PROVINCE: UITextField!
    @IBOutlet var DISTRICT: UITextField!
    @IBOutlet var stackView: UIStackView!
    @IBOutlet var adressSearch: UIButton!
    @IBOutlet var passPortStack: UIStackView!
    @IBOutlet var _detailAdress: UITextField!
    @IBOutlet var SaveLocationLabel: UILabel!
    @IBOutlet var naviItem: UINavigationItem!
    @IBOutlet var selectStack: UIStackView!
    @IBOutlet var citizenStack: UIStackView!
    @IBOutlet var passportStack: UIStackView!
    @IBOutlet var selectSegment: UISegmentedControl!
    @IBOutlet var regionalBtn: UIButton!
    @IBOutlet var provincialBtn: UIButton!
    @IBOutlet var districtBtn: UIButton!
    
    //MARK: - variable
    var paramCountryCode: String!
    var paramCountryName: String?
    
    var department_List : Array<(String?, String)> = [(code: String?, codeName: String)]()
    var province_List : Array<(String?, String)> = [(code: String?, codeName: String)]()
    var district_List : Array<(String?, String)> = [(code: String?, codeName: String)]()
    
    let userInfo = UserInfo()
    let toolBar = UIToolbar().ToolbarPiker(mySelect: #selector(done(_:)))
    
    var keyboardHeight : CGFloat?
    var DEPARTMENT_NM = ""
    var PROVINCE_NM = ""
    var DISTRICT_NM = ""
    var DEPARTMENT_CODE : String?
    var PROVINCE_CODE : String?
    var DISTRICT_CODE : String?
    var departmentPicker = UIPickerView()
    var provincePicker = UIPickerView()
    var districtPicker = UIPickerView()
    
    var subView : CustomPickerDialog?
    
    private var publicOfficialHandler : response!
    private var userRegisterHandler : response!
    private var departmentHandler : response!
    private var provinceHandler : response!
    private var districtHandler : response!
    
    //MARK: - lifeCycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        publicOfficialHandler = ({
            response in
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                Spinner.stop()
                guard let json = value as? [String: Any] else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    let _ = json["RES_MSG"] as? String
                    self.certificationDialogError(message: StaticString.serviceErrorText.serviceError.rawValue.localized, sleepTime: 2, type: .default)
                    return
                }
                
                //[Parte que descifra las partes encriptadas y cambiar a forma de diccionario]
                let msg = decryptMSG.shared.msgDecrypt(msg: resp)
                //Convertir a formato diccionario
                let data :[Dictionary<String,Any>] = decryptMSG.shared.decrypt(enData: msg)
                if data.count < 1 {
                    return
                }
                
                let value = data[0]
                print(value)
                
                for row in value{
                    if type(of: row.value) == NSNull.self{
                        commFunction.comm.preference.set("", forKey: row.key)
                    }else{
                        commFunction.comm.preference.set(row.value, forKey: row.key)
                    }
                }
                commFunction.comm.preference.synchronize()
                //                if isCallMain {
                //                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: StaticString.NotificationName.officialContact.rawValue), object: nil)
                //                }
                
                
            case .failure(let error):
                print(error)
                Spinner.stop()
                self.certificationDialogError(message: StaticString.serviceErrorText.coommunicationError.rawValue.localized, sleepTime: 2, type: .default)
            }
        })
        
        userRegisterHandler = ({
            response in
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                Spinner.stop()
                
                guard let json = value as? [String: Any] else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    _ = json["RES_MSG"] as? String
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                
                //[Parte que descifra las partes encriptadas y cambiar a forma de diccionario]
                let msg = decryptMSG.shared.msgDecrypt(msg: resp)
                //Convertir a formato diccionario
                let data :[Dictionary<String,Any>] = decryptMSG.shared.decrypt(enData: msg)
                if data.count < 1 {
                    return
                }
                
                let value = data[0]
                print(value)
                
                for row in value{
                    if type(of: row.value) == NSNull.self{
                        commFunction.comm.preference.setValue("", forKey: row.key)
                    } else if row.key == "ENCPT_DECD_KEY" {
                        let value = row.value as? String
                        if value?.count == 32 {
                            let key = value?.subString(from: 0, to: 15)
                            let vector = value?.subString(from: 16, to: 31)
                            commFunction.comm.preference.set(key, forKey: StaticString.encryption.SECURE_KEY.rawValue)
                            commFunction.comm.preference.set(vector, forKey: StaticString.encryption.SECURE_VECTOR.rawValue)
                        } else {
                            commFunction.comm.showExitAlert(vc: self)
                            return
                        }
                    } else {
                        commFunction.comm.preference.setValue(row.value, forKey: row.key) //필요한 정보만 나중에 가지고 있을것
                    }
                }
                
                commFunction.comm.preference.set(true, forKey: StaticString.preference.registerationCheck.rawValue)
                commFunction.comm.preference.synchronize()
                self.performSegue(withIdentifier: "goMain", sender: self)
                
            case .failure(let error):
                print(error)
                Spinner.stop()
                commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.coommunicationError.rawValue.localized, result: nil)
            }
        })
        
        departmentHandler = ({
            response in
            switch response.result {
            case .success(let value):
                Spinner.stop()
                
                guard let json = value as? [String: Any] else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                let msg = decryptMSG.shared.msgDecrypt(msg: resp)
                let data :[Dictionary<String,Any>] = decryptMSG.shared.decrypt(enData: msg)
                if data.count < 1 {return}
                
                self.department_List.insert(("", StaticString.displayText.choose.rawValue.localized), at: 0)
                
                print(data)
                for value in data {
                    if value["CODE_NM"] as! String != "전체" {
                        self.department_List.append((code: value["CODE"] as? String, codeName: value["CODE_NM"] as! String))
                    }
                }
                
                self.showCustomPickerDialog(tag: 12)
                
            case .failure(let error):
                print(error)
                commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.coommunicationError.rawValue.localized, result: nil)
                Spinner.stop()
            }
        })
        
        provinceHandler = ({
            response in
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                Spinner.stop()
                
                guard let json = value as? [String: Any] else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                //****** Descifrar datos cifrados
                let msg = decryptMSG.shared.msgDecrypt(msg: resp)
                //Convertir a tipo diccionario
                let data :[Dictionary<String,Any>] = decryptMSG.shared.decrypt(enData: msg)
                if data.count < 1 {return}
                
                self.province_List.removeAll()
                self.province_List.insert(("", StaticString.displayText.choose.rawValue.localized), at: 0)
                
                print(data)
                for value in data {
                    if value["CODE_NM"] as! String != "전체" {
                        self.province_List.append((code: value["CODE"] as? String, codeName: value["CODE_NM"] as! String))
                    }
                }
                
                self.showCustomPickerDialog(tag: 13)
                
            case .failure(let error):
                print(error)
                commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.coommunicationError.rawValue.localized, result: nil)
                Spinner.stop()
            }
        })
        
        districtHandler = ({
            response in
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                Spinner.stop()
                
                guard let json = value as? [String: Any] else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                let msg = decryptMSG.shared.msgDecrypt(msg: resp)
                //Convertir a tipo diccionario
                let data :[Dictionary<String,Any>] = decryptMSG.shared.decrypt(enData: msg)
                if data.count < 1 {return}
                
                self.district_List.removeAll()
                self.district_List.insert(("", StaticString.displayText.choose.rawValue.localized), at: 0)
                
                print(data)
                for value in data {
                    if value["CODE_NM"] as! String != "전체" {
                        self.district_List.append((code: value["CODE"] as? String, codeName: value["CODE_NM"] as! String))
                    }
                }
                
                self.showCustomPickerDialog(tag: 14)
                
            case .failure(let error):
                print(error)
                commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.coommunicationError.rawValue.localized, result: nil)
                Spinner.stop()
            }
        })
        
        self.hideKeyboardOnTouchOnView()
        //Colocar la información del país que recibió el consentimiento de recopilación de información
        switch self.paramCountryCode {
        case "PE":
            self.etcCountry.text = StaticString.displayText.PERU.rawValue
            self.passPortStack.isHidden = true
        default:
            self.etcCountry.text = self.paramCountryName!
            self.selectStack.isHidden = false
            self.citizenStack.isHidden = true
        }
        self.userInfo.NLTY_CODE = paramCountryCode
        
        //button, segment border setting
        commFunction.comm.setButtonBorder(sender: self.adressSearch)
        commFunction.comm.setSegmentFont(segment: self.gender, font: UIFont.systemFont(ofSize: 17))
        
        //date of birth picker setting
        let datePicker = UIDatePicker()
        datePicker.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: self.view.frame.height / 3)
        datePicker.datePickerMode = .date
        datePicker.locale = Locale(identifier: "PE")
        datePicker.setDate(Date(timeIntervalSince1970: 0), animated: false)
        datePicker.addTarget(self, action: #selector(changeDate(_:)), for: .valueChanged)
        self.birth.inputView = datePicker
        
        self.settingPickerView(pickerView: departmentPicker, target: DEPARTMENT, tag: 12)
        self.settingPickerView(pickerView: provincePicker, target: PROVINCE, tag: 13)
        self.settingPickerView(pickerView: districtPicker, target: DISTRICT, tag: 14)
        
        self.birth.inputAccessoryView = toolBar
        self.etcCountry.inputAccessoryView = toolBar
        self.phoneNumber.inputAccessoryView = toolBar
        self.emergencyNumber.inputAccessoryView = toolBar
        self._detailAdress.inputAccessoryView = toolBar
    }
    
    override func viewDidAppear(_ animated: Bool) {
        self.regionalBtn.setTitle("Escoger".localized, for: .normal)
        self.provincialBtn.setTitle("Escoger".localized, for: .normal)
        self.districtBtn.setTitle("Escoger".localized, for: .normal)
        
        //is user certificated by The official in charge?
        if commFunction.comm.preference.string(forKey: StaticString.OfficialInfoPreference.officialID.rawValue) == nil {
            self.certificationDialog()
        }
        
        //is user selected quarantine location?
        if commFunction.comm.preference.string(forKey: StaticString.userInfoPreference.xCoordinate.rawValue) != nil {
            self.SaveLocationLabel.isHidden = false
        }
        
        self.addNotification()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.removeNotification()
    }
    
    //MARK: - ui action
    
    /// Ir a la pantalla del mapa
    @IBAction func onClickMapMove(_ sender: UIButton) {
        self.scrollView.contentInset = UIEdgeInsets.zero
        self.scrollView.scrollIndicatorInsets = UIEdgeInsets.zero
        self.view.endEditing(true)
        
        self.performSegue(withIdentifier: StaticString.segueString.authToMap.rawValue, sender: self)
    }
    
    /// Seleccionar género
    @IBAction func onClickGender(_ sender: UISegmentedControl) {
        switch sender.selectedSegmentIndex {
        case 0:
            self.userInfo.SEXDSTN_CODE = StaticString.genderCode.male.rawValue
        case 1:
            self.userInfo.SEXDSTN_CODE = StaticString.genderCode.female.rawValue
        default:
            break
        }
    }
    
    /// Hacer clic en el botón Registro
    @IBAction func onClickRegister(_ sender: UIButton) {
        self.userInfo.INHT_ID = self.isSet(text: self.citizenID.text)
        self.userInfo.ISLPRSN_NM = self.isSet(text: self.name.text)
        self.userInfo.TELNO = self.isSet(text: self.phoneNumber.text)
        self.userInfo.TRMNL_KND_CODE = StaticString.deviceCode.IPhone.rawValue
        self.userInfo.TRMNL_NM = UIDevice().model
        self.userInfo.PUSHID = commFunction.comm.preference.string(forKey: StaticString.preference.token.rawValue)
        self.userInfo.USE_LANG = "PE"
        self.userInfo.CRTFC_NO = commFunction.comm.preference.string(forKey:
            StaticString.OfficialInfoPreference.officialID.rawValue) ?? nil
        self.userInfo.ECSHG_MNGR_SN = commFunction.comm.preference.string(forKey: StaticString.OfficialInfoPreference.officialSerialNumber.rawValue) ?? nil
        
        self.userInfo.EMGNC_TELNO = self.isSet(text: self.emergencyNumber.text)
        self.userInfo.PSPRNBR = self.isSet(text: self.passPortNumber.text)
        self.userInfo.ISLLC_ETC_ADRES  = self.isSet(text: self._detailAdress.text)
        self.userInfo.ISLLC_XCNTS = commFunction.comm.preference.string(forKey: StaticString.userInfoPreference.xCoordinate.rawValue) ?? nil
        self.userInfo.ISLLC_YDNTS = commFunction.comm.preference.string(forKey: StaticString.userInfoPreference.yCoordinate.rawValue) ?? nil
        
        //Asegurar de haber ingresado el número de teléfono correctamente
        if !commFunction.comm.isPhone(candidate: (self.userInfo.TELNO ?? ""), identifier: StaticString.numberType.phoneNumber.rawValue) {
            commFunction.comm.makeToast(controller: self, message: StaticString.authString.wrongCellPhone.rawValue.localized, result: nil)
        } else if !commFunction.comm.isPhone(candidate: (self.userInfo.EMGNC_TELNO ?? ""), identifier: StaticString.numberType.emrgencyNumber.rawValue) {
            commFunction.comm.makeToast(controller: self, message: StaticString.authString.wrongTutorCellPhone.rawValue.localized, result: nil)
        } else {
            switch self.userInfo.NLTY_CODE {
            case "PE":
                if self.isUserInfoCorrect() && self.isCoordinateCorrect() && self.userInfo.INHT_ID != nil {
                    Services.Service.userRegistrationService(vc: self, userInfo: userInfo, resp: userRegisterHandler)
                } else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.displayText.nonInputText.rawValue.localized, result: nil)
                }
            default:
                if self.isUserInfoCorrect() && self.isCoordinateCorrect() && self.isForeginerInfoCorrect() {
                    Services.Service.userRegistrationService(vc: self, userInfo: userInfo, resp: userRegisterHandler)
                } else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.displayText.nonInputText.rawValue.localized, result: nil)
                }
            }
        }
    }
    
    /// Seleccionar número de identificación o número de pasaporte
    @IBAction func onClickSelectStack(_ sender: UISegmentedControl) {
        switch sender.selectedSegmentIndex {
        case 0:
            self.selectCitizen()
        default:
            self.selectPassport()
        }
    }
    
    /// Seleccionar Región
    @IBAction func onClickRegional(_ sender: UIButton) {
        self.view.endEditing(true)
        if self.department_List.isEmpty {
            Services.Service.departmentService(view: self.view, resp: departmentHandler)
        } else {
            self.showCustomPickerDialog(tag: 12)
        }
    }
    
    /// Seleccionar Provincia
    @IBAction func onClickProvincial(_ sender: UIButton) {
        self.view.endEditing(true)
        if self.DEPARTMENT.text == "" {
            commFunction.comm.makeToast(controller: self, message: StaticString.authString.regionalText.rawValue.localized, result: nil)
        } else {
            Services.Service.provinceService(view: self.view, resp: provinceHandler, DPRTMNT_CODE: self.DEPARTMENT_CODE ?? "")
        }
    }
    
    /// Seleccionar Distrito
    @IBAction func onClickDistrict(_ sender: UIButton) {
        self.view.endEditing(true)
        if self.PROVINCE.text != "" && self.DEPARTMENT.text != "" {
            Services.Service.districtService(view: self.view, resp: districtHandler, DEPARTMENT_CODE: DEPARTMENT_CODE ?? "", PROVINCE_CODE: PROVINCE_CODE ?? "")
        } else {
            commFunction.comm.makeToast(controller: self, message: StaticString.authString.provincialText.rawValue.localized, result: nil)
        }
    }
    
    
    //MARK: - function
    
    /// Limitar cantidad de dígitos en un campo de texto
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        switch textField.tag {
        //Límitar la longitud de ingresar el número de teléfono móvil
        case 2:
            let newLength = (textField.text?.count)! + string.count - range.length
            return !(newLength > 15)
        //Otras restricciones de longitud de entrada de dirección
        case 5:
            let newLength = (textField.text?.count)! + string.count - range.length
            return !(newLength > 250)
        default:
            return true
        }
    }
    
    
    /// Al hacer clic en la tecla Fin, baja el teclado
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        self.scrollView.contentInset = UIEdgeInsets.zero
        self.scrollView.scrollIndicatorInsets = UIEdgeInsets.zero
        return true
    }
    
    /// Al hacer clic en la parte superior de la tecla Fin, baja el teclado
    @objc func done(_ sender:UITextField){
        self.scrollView.contentInset = UIEdgeInsets.zero
        self.scrollView.scrollIndicatorInsets = UIEdgeInsets.zero
        self.view.endEditing(true)
    }
    
    /// Cambiar el formato de fecha
    @objc func changeDate(_ sender: UIDatePicker) {
        let dateFormatter: DateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd/MM/yyyy"
        let selectedDate: String = dateFormatter.string(from: sender.date)
        self.birth.text = selectedDate
        dateFormatter.dateFormat = "yyyyMMdd"
        self.userInfo.BRTHDY = dateFormatter.string(from: sender.date)
    }
    
    /// Evitar que aparezca el teclado vacío al hacer clic por primera vez en la ventana Seleccionar Región
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        switch textField.tag {
        case 5:
            return false
        default:
            return true
        }
    }
    
    ///Registrar observador
    private func addNotification(){
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(confirmDepartment), name: NSNotification.Name(rawValue: StaticString.observerName.department.rawValue), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(confirmProvince), name: NSNotification.Name(rawValue:
            StaticString.observerName.province.rawValue), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(confirmDistrict), name: NSNotification.Name(rawValue:
            StaticString.observerName.district.rawValue), object: nil)
    }
    
    ///Eliminar observador
    private func removeNotification() {
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue: StaticString.observerName.department.rawValue), object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue: StaticString.observerName.province.rawValue), object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue: StaticString.observerName.district.rawValue), object: nil)
    }
    
    /// Cuando aparece el teclado cambiar la posición de los items internos del scroll view (vista de desplazamiento)
    @objc private func keyboardWillShow(_ notification: Notification) {
        if let keyboardFrame: NSValue = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            let keybaordRectangle = keyboardFrame.cgRectValue
            self.keyboardHeight = keybaordRectangle.height
            
            let contentInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: self.keyboardHeight! - 50, right: 0.0)
            self.scrollView.contentInset = contentInsets
            self.scrollView.scrollIndicatorInsets = contentInsets
        }
    }
    
    /// Cuando el teclado baja devolver la ubicación de los items internos del scroll view (vista de desplazamiento)
    @objc func dismissKeyboard() {
        view.endEditing(true)
        self.scrollView.contentInset = UIEdgeInsets.zero
        self.scrollView.scrollIndicatorInsets = UIEdgeInsets.zero
    }
    
    /// Configuración inivial del pickerView (vista del selector)
    func settingPickerView(pickerView: UIPickerView, target: UITextField, tag: Int) {
        pickerView.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: self.view.frame.height / 3)
        pickerView.delegate = self
        pickerView.dataSource = self
        pickerView.tag = tag
        target.inputView = pickerView
    }
    
    /// Ocultar la ventana de entrada de No. de identificación del ciudadano
    func selectPassport() {
        self.citizenStack.isHidden = true
        self.passPortStack.isHidden = false
        self.citizenID.text = ""
    }
    
    /// Ocultar la ventana de entrada del número de pasaporte
    func selectCitizen() {
        self.citizenStack.isHidden = false
        self.passPortStack.isHidden = true
        self.passPortNumber.text = ""
    }
    
    /// Devuelve nil si no hay valor
    func isSet(text: String?) -> String? {
        switch text {
        case nil, "":
            return nil
        default:
            return text
        }
    }
    
    /// Verifica si el usuario ha ingresado todos los valores requeridos
    func isUserInfoCorrect() -> Bool {
        if userInfo.ISLPRSN_NM != nil && userInfo.BRTHDY != nil && userInfo.SEXDSTN_CODE != nil && userInfo.NLTY_CODE != nil && userInfo.TELNO != nil {
            return true
        } else {
            return false
        }
    }
    
    /// Verifica que el usuario haya ingresado toda la información relacionada con la ubicación
    func isCoordinateCorrect() -> Bool {
        if userInfo.ISLLC_DPRTMNT_CODE != nil && userInfo.ISLLC_PRVNCA_CODE != nil && userInfo.ISLLC_DSTRT_CODE != nil && userInfo.ISLLC_XCNTS != nil && userInfo.ISLLC_YDNTS != nil {
            return true
        } else {
            return false
        }
    }
    
    /// Verifica si el extranjero ingresó el No. de identificación o No. de pasaporte
    func isForeginerInfoCorrect() -> Bool {
        switch self.selectSegment.selectedSegmentIndex {
        case 0:
            if self.userInfo.INHT_ID != nil {
                return true
            } else {
                return false
            }
        case 1:
            if self.userInfo.PSPRNBR != nil {
                return true
            } else {
                return false
            }
        default:
            return false
        }
    }
    
    func hideKeyboardOnTouchOnView()
    {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(
            target: self,
            action: #selector(self.dismissKeyboard))
        
        scrollView.addGestureRecognizer(tap)
    }
    
    func showCustomPickerDialog(tag: Int) {
        self.subView = CustomPickerDialog(frame: CGRect(x: 0, y: 0,width: self.view.bounds.maxX, height: self.view.bounds.maxY))
        
        guard let view = self.subView else {return}
        view.pickerSetting(tag: tag)
        view.pickerView.delegate = self
        view.pickerView.dataSource = self
        self.view.addSubview(view)
    }
    
    @objc func confirmDepartment() {
        self.view.endEditing(true)
        self.DEPARTMENT.text = self.DEPARTMENT_NM
        self.userInfo.ISLLC_DPRTMNT_CODE = self.DEPARTMENT_CODE
        
        self.PROVINCE.text = ""
        self.userInfo.ISLLC_PRVNCA_CODE = nil
        
        self.DISTRICT.text = ""
        self.userInfo.ISLLC_DSTRT_CODE = nil
    }
    
    @objc func confirmProvince() {
        self.PROVINCE.text = self.PROVINCE_NM
        self.userInfo.ISLLC_PRVNCA_CODE = self.PROVINCE_CODE
        
        self.DISTRICT.text = ""
        self.userInfo.ISLLC_DSTRT_CODE = nil
    }
    
    @objc func confirmDistrict() {
        self.DISTRICT.text = self.DISTRICT_NM
        self.userInfo.ISLLC_DSTRT_CODE = self.DISTRICT_CODE
    }
    
    //Diálogo de entrada de ID de encargado oficial
    func certificationDialog() {
        let alert = UIAlertController(title: "Introducir ID encargado".localized, message: nil, preferredStyle: .alert)
        alert.addTextField { (id) in
            id.placeholder = "Por favor, introduzca 6 dígitos.".localized
        }
        let okAction = UIAlertAction(title: "Guardar".localized, style: .default) { (UIAlertAction) in
            if alert.textFields?[0].text != "" {
                Services.Service.publicOfficialCertificationService(view: self.view, resp: self.publicOfficialHandler , MNGR_LOGIN_ID: alert.textFields![0].text!)
            } else {
                self.certificationDialogError(message: "entrada vacía o inválida".localized, sleepTime: 1, type: .default)
            }
        }
        
        let cancelAction = UIAlertAction(title: "Cerrar".localized, style: .cancel) { (UIAlertAction) in
            exit(1)
        }
        
        alert.addAction(okAction)
        alert.addAction(cancelAction)
        present(alert, animated: false, completion: nil)
    }
    
    //Diálogo de error de autenticación del encargado oficial
    func certificationDialogError(message: String, sleepTime: UInt32, type: UIAlertAction.Style) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .actionSheet)
        present(alert, animated: false) {
            switch type {
            case UIAlertAction.Style.default :
                sleep(sleepTime)
                alert.dismiss(animated: false) {
                    self.certificationDialog()
                }
            case UIAlertAction.Style.cancel :
                sleep(sleepTime)
                exit(0)
            default:
                break
            }
        }
    }
}
