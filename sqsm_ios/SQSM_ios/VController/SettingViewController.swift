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

class SettingViewController: UIViewController, UITextFieldDelegate {
    typealias response = (AFDataResponse<Any>) -> Void
    //MARK: - outlet
    @IBOutlet var scrollView: UIScrollView!
    @IBOutlet var name: UITextField!
    @IBOutlet var birth: UITextField!
    @IBOutlet var emergencyNumber: UITextField!
    @IBOutlet var scrollSubView: UIView!
    @IBOutlet var passPortNumber: UITextField!
    @IBOutlet var detailAdress: UITextField!
    @IBOutlet var passPortNumberLabel: UILabel!
    @IBOutlet var stackView: UIStackView!
    @IBOutlet var citizenStack: UIStackView!
    @IBOutlet var passportStack: UIStackView!
    @IBOutlet var searchArea: UIButton!
    @IBOutlet var modify: UIButton!
    @IBOutlet var phoneNumber: UITextField!
    @IBOutlet var gender: UISegmentedControl!
    @IBOutlet var nationality: UITextField!
    @IBOutlet var adress: UILabel!
    @IBOutlet var citizenID: UITextField!
    @IBOutlet var naviItem: UINavigationItem!
    
    //MARK: - variable
    var keyBoardSelector:Bool = false
    let screenWidth = UIScreen.main.bounds.size.width
    var pUserDate = UserDefaults.standard
    let userInfo = UserInfo()
    var keyboardHeight : CGFloat?
    let application = UIApplication.shared.delegate as! AppDelegate
    
    private var searchInfoHandler : response!
    private var modifyInfoHandler : response!
    
    //MARK: - lifeCycle
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.isToolbarHidden = true
        
        self.hideKeyboardOnTouchOnView()
        commFunction.comm.setButtonUI(btn: searchArea)
        self.scrollView.delaysContentTouches = false
        commFunction.comm.setButtonUI(btn: modify)
        
        //Color de fondo de la ventana de selección de género, tamaño de texto aplicado
        if #available(iOS 13.0, *) {
            self.gender.selectedSegmentTintColor = commFunction.comm.UIColorFromRGB(rgbValue: 0x0078D7)
        } else {
            self.gender.tintColor = commFunction.comm.UIColorFromRGB(rgbValue: 0x0078D7)
        }
        self.gender.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.white], for: .selected)
        commFunction.comm.setSegmentFont(segment: gender, font: UIFont.systemFont(ofSize: 17))
        
        addKeyboardNotification()
        
        searchInfoHandler = ({
            response in
            Spinner.stop()
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                
                guard let json = value as? [String: Any] else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                guard let code = json["RES_CD"] as? String else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                if commFunction.comm.isCodeAboutSecureKey(code: code) {
                    commFunction.comm.showPreferClearDialog()
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                if commFunction.comm.isSecureKeyExist() {
                    let key = commFunction.comm.preference.string(forKey: StaticString.encryption.SECURE_KEY.rawValue)!
                    let vector = commFunction.comm.preference.string(forKey: StaticString.encryption.SECURE_VECTOR.rawValue)!
                    let msg = decryptMSG.shared.msgDecryptSecure(msg: resp, key: key, iv: vector)
                    
                    let data :[Dictionary<String,Any>] = decryptMSG.shared.decrypt(enData: msg)
                    if data.count < 1 {return}
                    
                    let value = data[0]
                    print(value)
                    
                    for row in value {
                        if type(of: row.value) == NSNull.self {
                            self.pUserDate.setValue("", forKey: row.key)
                        }else {
                            self.pUserDate.setValue(row.value, forKey: row.key)
                        }
                    }
                    
                    self.pUserDate.synchronize()
                    self.name.text = self.pUserDate.string(forKey: "ISLPRSN_NM")
                    self.birth.text = self.pUserDate.string(forKey: "BRTHDY_F")
                    switch self.pUserDate.string(forKey: "SEXDSTN_CODE") {
                    case StaticString.genderCode.male.rawValue :
                        self.gender.selectedSegmentIndex = 0
                    default:
                        self.gender.selectedSegmentIndex = 1
                    }
                    
                    self.phoneNumber.text = self.pUserDate.string(forKey: StaticString.userInfoPreference.cellPhoneNum.rawValue)
                    self.emergencyNumber.text = self.pUserDate.string(forKey: StaticString.userInfoPreference.emergencyPhoneNum.rawValue)
                    
                    self.adress.text = self.pUserDate.string(forKey: StaticString.userInfoPreference.baseAdress.rawValue)
                    self.detailAdress.text = self.pUserDate.string(forKey: StaticString.userInfoPreference.etcAdress.rawValue)
                    
                    //Hide location view button if no coordinate value
                    if self.pUserDate.string(forKey: StaticString.userInfoPreference.xCoordinate.rawValue) != nil && self.pUserDate.string(forKey: StaticString.userInfoPreference.xCoordinate.rawValue) != "" {
                        self.searchArea.isHidden = false
                    } else {
                        self.searchArea.isHidden = true
                    }
                    
                    switch self.pUserDate.string(forKey: "SEXDSTN_CODE") {
                    case StaticString.genderCode.male.rawValue :
                        self.gender.selectedSegmentIndex = 0
                    default:
                        self.gender.selectedSegmentIndex = 1
                    }
                    
                    self.passPortNumber.text = self.pUserDate.string(forKey: StaticString.userInfoPreference.passPortNum.rawValue)
                    self.nationality.text = Locale.current.localizedString(forRegionCode: self.pUserDate.string(forKey: "NLTY_CODE") ?? "PE")
                    
                    switch self.pUserDate.string(forKey: StaticString.userInfoPreference.countryCode.rawValue) {
                    case "PE" :
                        self.citizenID.text = self.pUserDate.string(forKey: StaticString.userInfoPreference.citizenID.rawValue)
                    default:
                        if self.pUserDate.string(forKey: StaticString.userInfoPreference.citizenID.rawValue) != "" {
                            self.citizenID.text = self.pUserDate.string(forKey: StaticString.userInfoPreference.citizenID.rawValue)
                        } else {
                            self.passPortNumber.text = self.pUserDate.string(forKey: StaticString.userInfoPreference.passPortNum.rawValue)
                            self.citizenStack.isHidden = true
                            self.passportStack.isHidden = false
                        }
                    }
                }
                
            case .failure(let error):
                commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.coommunicationError.rawValue.localized, result: nil)
                print(error)
            }
        })
        
        modifyInfoHandler = ({
            response in
            Spinner.stop()
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                guard let json = value as? [String: Any] else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                guard let code = json["RES_CD"] as? String else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                if commFunction.comm.isCodeAboutSecureKey(code: code) {
                    commFunction.comm.showPreferClearDialog()
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                if commFunction.comm.isSecureKeyExist() {
                    let key = commFunction.comm.preference.string(forKey: StaticString.encryption.SECURE_KEY.rawValue)!
                    let vector = commFunction.comm.preference.string(forKey: StaticString.encryption.SECURE_VECTOR.rawValue)!
                    let msg = decryptMSG.shared.msgDecryptSecure(msg: resp, key: key, iv: vector)
                    
                    let data :[Dictionary<String,Any>] = decryptMSG.shared.decrypt(enData: msg)
                    if data.count < 1 {return}
                    
                    
                    let value = data[0]
                    print(value)
                    
                    for row in value{
                        if type(of: row.value) == NSNull.self{
                            self.pUserDate.setValue("", forKey: row.key)
                        }else{
                            self.pUserDate.setValue(row.value, forKey: row.key)
                        }
                    }
                    self.pUserDate.synchronize()
                    self.makeCompleteToast(message: StaticString.diagnoseString.registration.rawValue.localized)
                }
                
            case .failure(let error):
                print(error)
                commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.coommunicationError.rawValue.localized, result: nil)
            }
        })
        
        Services.Service.userInfoService(view: self.view, resp: searchInfoHandler,
        ISLPRSN_SN: pUserDate.string(forKey: StaticString.userInfoPreference.userSerialCode.rawValue) ?? "",
        TRMNL_SN: pUserDate.string(forKey: StaticString.userInfoPreference.userDeviceSerialCode.rawValue) ?? "")
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.removeKeyboardNotification()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == StaticString.segueString.settingToMap.rawValue {
            let vc = segue.destination as! SelectMapViewController
            
            //Pasar coordenadas de ubicación de cuarentena
            if let x = self.pUserDate.string(forKey: "ISLLC_XCNTS"), let y = self.pUserDate.string(forKey: "ISLLC_YDNTS") {
                if x == "" && y == "" {
                    vc.param_x = "0"
                    vc.param_y = "0"
                }else {
                    vc.param_x = x
                    vc.param_y = y
                }
            }
            
            let originH = self.scrollView.frame.height
            let lastH = self.scrollView.contentSize.height
            if keyBoardSelector {
                if originH > lastH {
                    self.scrollView.setContentOffset(CGPoint(x: 0, y: 0), animated: true)
                }else {
                    self.scrollView.setContentOffset(CGPoint(x: 0, y: self.scrollView.contentSize.height - self.scrollView.bounds.height), animated: true)
                }
            }
            self.view.endEditing(true)
        }
    }
    
    //MARK: - ui action
    
    /// Ir atrás
    @IBAction func onClickBack(_ sender: UIBarButtonItem) {
        self.navigationController?.popViewController(animated: true)
    }
    
    /// Ir a la pantalla del mapa
    @IBAction func onClickAdressSearch(_ sender: UIButton) {
        let contentInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: 0.0, right: 0.0)
        self.scrollView.contentInset = contentInsets;
        self.scrollView.scrollIndicatorInsets = contentInsets;
        
        self.performSegue(withIdentifier: StaticString.segueString.settingToMap.rawValue, sender: self)
    }
    
    /// Clic del botón Editar información
    @IBAction func onClickModify(_ sender: UIButton) {
        self.userInfo.PSPRNBR = self.passPortNumber.text
        self.userInfo.EMGNC_TELNO = self.emergencyNumber.text
        self.userInfo.ISLLC_ETC_ADRES = self.detailAdress.text
        
        if !commFunction.comm.isPhone(candidate: (self.userInfo.EMGNC_TELNO) ?? "", identifier: StaticString.numberType.emrgencyNumber.rawValue) {
            commFunction.comm.makeToast(controller: self, message: "Número de teléfono celular del guardián es incorrecto.".localized, result: nil)
        } else {
            Services.Service.userModifyService(view: self.view, resp: modifyInfoHandler, userInfo: userInfo)
        }
    }
    
    //MARK: - function
    
    /// Limitar cantidad de dígitos en un campo de texto
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        switch textField.tag {
        //Límitar la longitud de ingresar el número de teléfono móvil
        case 3:
            let newLength = (textField.text?.count)! + string.count - range.length
            return !(newLength > 14)
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
    
    /// Registrar observador para detectar la aparición del teclado
    private func addKeyboardNotification(){
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
    }
    
    /// Eliminar observador para detectar la aparición del teclado
    private func removeKeyboardNotification() {
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
    }
    
    /// Cuando aparece el teclado cambiar la posición de los items internos del scroll view (vista de desplazamiento) 
    @objc private func keyboardWillShow(_ notification: Notification) {
        if let keyboardFrame: NSValue = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            let keybaordRectangle = keyboardFrame.cgRectValue
            self.keyboardHeight = keybaordRectangle.height
            
            let contentInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: self.keyboardHeight!, right: 0.0)
            self.scrollView.contentInset = contentInsets;
            self.scrollView.scrollIndicatorInsets = contentInsets;
        }
    }
    
    /// Regresar a la pantalla anterior
    func makeCompleteToast(message: String) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .actionSheet)
        self.present(alert, animated: false) {
            sleep(1)
            alert.dismiss(animated: false) {
                self.onClickBack(UIBarButtonItem())
            }
        }
    }
    
    /// Al toque en la pantalla se baja el techado
    func hideKeyboardOnTouchOnView() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(
            target: self,
            action: #selector(self.dismissKeyboard))
        
        scrollView.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboard() {
        self.view.endEditing(true)
        self.scrollView.contentInset = UIEdgeInsets.zero
        self.scrollView.scrollIndicatorInsets = UIEdgeInsets.zero
    }
}
