//
//  LoginVIewController.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/22.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import Alamofire
import Foundation

/// Pantalla de inicio de sesión del usuario
class LoginViewController: UIViewController, UITextFieldDelegate {
    
    //Variables de salida de pantalla
    @IBOutlet var titleLB: UILabel!
    @IBOutlet var LoginTitle: UILabel!
    @IBOutlet var uId: UITextField!
    @IBOutlet var uNumber: UITextField!
    @IBOutlet var LoginBtn: UIButton!
    @IBOutlet var st_main: UIStackView!
    
    //Variables de diálogo comunes
    var mpAlert : UIAlertController!
    var cpAlert : UIAlertController!
    var count : Int!
    
    //Constante para almacenamiento interno de datos
    let pList = UserDefaults.standard
    
    //Registro para la configuración de los botones que oculta la pantalla del teclado
    var keyBoardSelector:Bool = false
    
    
    //Bajar el teclado al hacer clic en el fondo
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if self.keyBoardSelector && self.view.frame.origin.y < 0 {
            self.view.frame.origin.y += 150
        }
        self.keyBoardSelector = false
        
        self.view.endEditing(true)
    }
    
    //Configuración de pantalla inicial
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Inicialización para la primera ejecución (utilizada en la página siguiente)
        self.pList.set(nil, forKey: "showPage")
        self.pList.synchronize()
        
        //Título de inicio de sesión
        commFunction().setLable(LB: self.titleLB)
        
        //Font de inicio de sesión
        self.LoginTitle.font = UIFont.boldSystemFont(ofSize: 22)
        
        //Configuración de campo de texto común
        commFunction().setTextFeildLG(sender: self.uId, height: 60, placeholder: "ex) ABCD12".localized, fontSize: 18, isEnable: true)
        commFunction().setTextFeildLG(sender: self.uNumber, height: 60, placeholder: "Ex: 994123456".localized, fontSize: 18, isEnable: true)
        self.commTF(sender: self.uId)
        self.commTF(sender: self.uNumber)
        
        //Crear botón vista izquierda
        commFunction().loginLeftView(tf: self.uId, image: "id.png")
        commFunction().loginLeftView(tf: self.uNumber, image: "password.png")
        
        //Configurar botón de inicio de sesión
        commFunction().setButtonUI(btn: self.LoginBtn, title: "login".localized)
        self.LoginBtn.addTarget(self, action: #selector(userLogin), for: .touchUpInside)
        self.addKeyboardNotification()
    }
    
    /// Configuración en común para introducir inicio de sesión
    /// - Parameter sender: UITextField
    func commTF(sender:UITextField){
        let toolBar = UIToolbar().ToolbarPiker(mySelect: #selector(done(_:)))
        sender.inputAccessoryView = toolBar
        sender.addTarget(self, action: #selector(onTouchIsolation(_:)), for: .touchDown)
        sender.autocorrectionType = .no
        sender.autocapitalizationType = .none
        sender.autoresizingMask = .flexibleHeight
    }
    
    /// Servicio de inicio de sesión
    @objc func userLogin() {
        
        //------- test sample -----------
        self.uId.text = "test11"
        self.uNumber.text = "1111"
        //--------------------------
        
        guard self.uId.text != "", self.uNumber.text != "" else {
            self.showAlert(message: "Enter all initial information".localized)
            return
        }
        
        let url = serverAddress().main
        
        //        로그인ID   LOGIN_ID
        //        비밀번호   PASSWORD
        //        PUSHID   PUSHID
        //        단말기종류  TRMNL_KND_CODE (00402 : IOS)
        var PARM = Dictionary<String,String>()
        PARM.updateValue(self.uId.text!, forKey: "LOGIN_ID")
        PARM.updateValue(self.uNumber.text!, forKey: "PASSWORD")
        PARM.updateValue("00402", forKey: "TRMNL_KND_CODE")
        PARM.updateValue(pList.string(forKey: "token") ?? "", forKey: "PUSHID")
        
        //******** Parte que cifra el parámetro existente
        let param = PARM.printJson()
        print(param)
        let paramEN = try! param.aesEncrypt(key: decryptMSG.instance.key128, iv: decryptMSG.instance.iv)
        let listService = sendModelEN(IFID: "PERUO0001", PARM: paramEN)
        //***************************************************************
        
        
        //Call back de inicio de sesión
        let resp : (AFDataResponse<Any>) -> Void = ({
            response in
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                
                //Cuando sale error en el resultado de la comunicación
                guard let json = value as? [String: Any] else {
                    self.makeAlert(title: "Login failed".localized, message: "Communication and server network is not fluid.".localized)
                    Spinner.stop()
                    return
                }
                
                //Cuando sale error en los datos de comunicación
                guard let resp = json["RES_DATA"] as? String else {
                    let msg = json["RES_MSG"] as? String
                    self.makeAlert(title: "Login failed".localized, message: msg ?? "Account information does not match".localized)
                    Spinner.stop()
                    return
                }
                
                
                //****** Parte que descifra las partes encriptadas y cambiar a tipo de diccionario
                let msg = decryptMSG.instance.msgDecrypt(msg: resp)
                //Convertir a formato diccionario
                let data :[Dictionary<String,Any>] = decryptMSG.instance.decrypt(enData: msg)
                if data.count < 1 {self.makeAlert(title: "Login failed".localized, message: "There is no session information.".localized); Spinner.stop();return}
                //****************************************************************************************
                
                let RES_CD = json["RES_CD"] as? String
                
                if RES_CD == "100" {
                    
                    //관리자일련번호    MNGR_SN
                    //관리자이름    MNGR_NM
                    //휴대전화번호    MBTLNUM
                    //소속주정부코드    PSITN_DPRTMNT_CODE
                    //소속주정부코드명    PSITN_DPRTMNT_CODE_NM
                    //소속시정구코드    PSITN_PRVNCA_CODE
                    //소속시정구코드명    PSITN_PRVNCA_CODE_NM
                    //소속구청코드    PSITN_DSTRT_CODE
                    //소속구청코드명    PSITN_DSTRT_CODE_NM
                    //소속부서명    PSITN_DEPT_NM
                    //사무실전화번호    OFFM_TELNO
                    //스카이프아이디    SKYPE_ID
                    //왓츠업아이디    WHATSUP_ID
                    //PUSHID    PUSHID
                    //관리자아이디    LOGIN_ID
                    //운영자여부    OPRTR_AT
                    for row in data[0]{
                        print("\(row.key) \(row.value)")
                        
                        if type(of: row.value) == NSNull.self{
                            self.pList.set("", forKey: row.key)
                        }else{
                            if row.key == "ENCPT_DECD_KEY"{
                                let key = row.value as! String
                                let point = key.index(key.startIndex, offsetBy: 16)
                                let secKey = key[key.startIndex..<point]
                                let secVector = key[point..<key.endIndex]
                                self.pList.set(secKey, forKey: "seckey")
                                self.pList.set(secVector, forKey: "seckeyVector")
                                
                            }else{
                                self.pList.set(row.value, forKey: row.key)
                            }
                        }
                    }
                    //Desplazarse de pantalla después de guardar resultado de inicio de sesión
                    self.pList.synchronize()
                    self.performSegue(withIdentifier: "goMain", sender: self)
                    
                } else {
                    self.makeAlert(title: "Login failed".localized, message: json["RES_MSG"] as? String ?? "There is no information.".localized)
                }
                
            case .failure(let error):
                print(error)
                self.makeAlert(title: "Communication failure".localized, message: "Communication and server network is not fluid.".localized)
            }
            Spinner.stop()
        })
        
        //서버호출
        let req = serviceForm(url: url, parametersEN: listService)
        req.getPostEN(view: self.view ,response: resp)
        
    }
    
    /// Notificación de falla de inicio de sesión
    func showAlert(message:String) {
        let alert = UIAlertController(title: "Login failed".localized,
                                      message: message, preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm".localized, style: .default)
        alert.addAction(confirm)
        self.present(alert, animated: true)
    }
    
    /// Ocultar teclado
    /// - Parameter sender: UITextField
    @objc func done(_ sender:UITextField){
        self.view.endEditing(true)
    }
    
    /// Proceso sobre la parte de la pantalla donde tapa el teclado
    @objc func onTouchIsolation(_ sender:UITextField){
        
        print(self.view.frame.height - 250)
        print(self.st_main.frame.maxY)
        
        let defaultSize = self.view.frame.height - 250
        let viewSize = self.st_main.frame.maxY
        //Subir solamente el último número del teléfono móvil
        if viewSize > defaultSize && (sender.tag == 5 || sender.tag == 4) {
            self.keyBoardSelector = true
        }else{
            self.keyBoardSelector = false
        }
        
    }
    
    /// Proceso sobre la parte de la pantalla donde tapa el teclado
    private func addKeyboardNotification(){
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    /// Proceso sobre la parte de la pantalla donde tapa el teclado
    private func removeKeyboardNotification(){
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    /// Proceso sobre la parte de la pantalla donde tapa el teclado
    @objc private func keyboardWillShow(_ notification: Notification) {
        if let _: NSValue = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            if self.keyBoardSelector && self.view.frame.origin.y == 0{
                self.view.frame.origin.y -= 150
            }
        }
    }
    
    /// Proceso sobre la parte de la pantalla donde tapa el teclado
    @objc private func keyboardWillHide(_ notification: Notification) {
        if let _: NSValue = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            if self.view.frame.origin.y < 0 {
                self.view.frame.origin.y += 150
            }
        }
    }
    
    /// Alerta común
    func makeAlert(title:String, message:String){
        let mAlert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let cancle = UIAlertAction(title: "Confirm".localized, style: .cancel, handler: nil)
        mAlert.addAction(cancle)
        self.present(mAlert, animated: true, completion: nil)
    }
    
    /// Configuración del valor antes de pasar a la siguiente pantalla
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goMain" {
            let VC = segue.destination as? MainViewController
            VC?.LoginID = self.uId.text!
            VC?.LoginPhoneNumber = self.uNumber.text!
            
            self.uId.text = ""
            self.uNumber.text = ""
        }
    }
}
