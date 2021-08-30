//
//  commFunction.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/22.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import UIKit
import Alamofire

//Función común
class commFunction {
    let preference = UserDefaults.standard
    
    /// Función para ingresar la imagen a la izquierda del campo de texto
    /// - Parameters:
    ///   - tf: Campo de texto
    ///   - image: Imagen a aplicar
    func loginLeftView(tf:UITextField, image:String) {
        let Image =  UIImageView(image: UIImage(named: image))
        Image.frame = CGRect(x: 8, y: 8, width: 16, height: 16)
        let leftView = UIView(frame: CGRect(x: 0, y: 0, width: 32, height: 32))
        leftView.addSubview(Image)
        tf.leftViewMode = .always
        tf.leftView = leftView
    }
    
    /// Función para ingresar la imagen a la derecha del campo de texto
    /// - Parameters:
    ///   - tf: Campo de texto
    ///   - image: Imagen a aplicar
    func ImgRightView(tf:UITextField, image:String) {
        let Image =  UIImageView(image: UIImage(named: image))
        Image.frame = CGRect(x: 0, y: 0, width: 12, height: 12)
        let RightView = UIView(frame: CGRect(x: -5, y: 0, width: 15, height: 15))
        RightView.addSubview(Image)
        Image.center = RightView.center
        tf.rightView = RightView
        tf.rightViewMode = .always
    }
    
    //MARK: -Función relacionada a la etiqueta
    
    /// Configuración de etiqueta básica en común
    /// - Parameter LB: Etiqueta
    public func setLable(LB:UILabel) {
        LB.backgroundColor = UIColorFromRGB(rgbValue: 0x0078D7)
        LB.layer.borderColor = UIColor.blue.cgColor
        LB.layer.borderWidth = 1
        LB.layer.cornerRadius = 10
        LB.layer.masksToBounds = true
        LB.textColor = UIColor.white
    }
    
    //MARK: -Funciones relacionadas con el campo de texto
    
    /// Diseño de campo de texto básico común
    /// - Parameters:
    ///   - sender: Campo de texto
    ///   - height: Altura
    ///   - placeholder: Pista de frase
    ///   - fontSize: Tamaño de letra
    ///   - isEnable: Activado o no activado
    func setTextFeild(sender:UITextField, height:CGFloat, placeholder :String?, fontSize:CGFloat, isEnable:Bool){
        sender.frame.size.height = height
        sender.isEnabled = isEnable
        sender.layer.borderWidth = 1
        sender.layer.borderColor = UIColor.systemBlue.cgColor
        sender.layer.cornerRadius = 10
        sender.layer.masksToBounds = true
        sender.backgroundColor = UIColorFromRGB(rgbValue: 0xffffff, alpha: 0.4)
        sender.placeholder = placeholder
        sender.font = UIFont.systemFont(ofSize: fontSize)
        sender.setLeftPaddingPoints(10)
    }
    
    /// Diseño de campo de texto predeterminado común (negro - para deshabilitar)
    /// - Parameters:
    ///   - sender: Campo de texto
    ///   - height: Altura
    ///   - placeholder: Pista de frase
    ///   - fontSize: Tamaño de letra
    ///   - isEnable: Activado o no activado
    func setTextFeild_dark(sender:UITextField, height:CGFloat, placeholder :String?, fontSize:CGFloat, isEnable:Bool){
        sender.frame.size.height = height
        sender.isEnabled = isEnable
        sender.layer.borderWidth = 1
        sender.layer.borderColor = UIColor.systemBlue.cgColor
        sender.layer.cornerRadius = 10
        sender.layer.masksToBounds = true
        sender.backgroundColor = UIColorFromRGB(rgbValue: 0x5d5d5d, alpha: 0.6)
        sender.placeholder = placeholder
        sender.isEnabled = false
        sender.font = UIFont.systemFont(ofSize: fontSize)
        sender.setLeftPaddingPoints(10)
    }
    
    /// Diseño de campo de texto solamente para inicio de sesión
    /// - Parameters:
    ///   - sender: Campo de texto
    ///   - height: Altura
    ///   - placeholder: Pista de frase
    ///   - fontSize: Tamaño de letra
    ///   - isEnable: Activado o no activado
    func setTextFeildLG(sender:UITextField, height:CGFloat, placeholder :String?, fontSize:CGFloat, isEnable:Bool){
        sender.frame.size.height = height
        sender.isEnabled = isEnable
        sender.layer.borderWidth = 1
        sender.layer.borderColor = UIColor.systemGray.cgColor
        sender.layer.cornerRadius = 10
        sender.layer.masksToBounds = true
        sender.backgroundColor = UIColorFromRGB(rgbValue: 0xffffff, alpha: 0.4)
        sender.placeholder = placeholder
        sender.font = UIFont.systemFont(ofSize: fontSize)
        sender.setLeftPaddingPoints(10)
    }
    
    //MARK: -Funciones relacionadas con botones
    
    /// Configuración del botón básico común (azul)
    /// - Parameters:
    ///   - btn: Botón
    ///   - title: Texto del título
    func setButtonUI(btn:UIButton, title:String) {
        btn.setTitle(title, for: .normal)
        btn.layer.borderColor = UIColor.white.cgColor
        btn.layer.borderWidth = 1
        btn.layer.cornerRadius = 10
        btn.layer.masksToBounds = true
        btn.setBackgroundColor(color: UIColorFromRGB(rgbValue: 0x0078D7), forState: .normal)
        btn.setBackgroundColor(color: UIColorFromRGB(rgbValue: 0xD0D0D0), forState: .disabled)
        btn.tintColor = UIColor.blue
        btn.setTitleColor(UIColor.white, for: .normal)
    }
    
    /// Configuración del botón básico común (blanco)
    /// - Parameters:
    ///   - btn: Botón
    ///   - title: Texto del título
    func setButtonUI_White(btn:UIButton, title:String) {
        btn.setTitle(title, for: .normal)
        btn.layer.borderColor = UIColor.black.cgColor
        btn.layer.borderWidth = 1
        btn.layer.cornerRadius = 10
        btn.layer.masksToBounds = true
        btn.setBackgroundColor(color: UIColorFromRGB(rgbValue: 0xFFFFFF), forState: .normal)
        btn.setBackgroundColor(color: UIColorFromRGB(rgbValue: 0xE0E0E0), forState: .disabled)
        btn.tintColor = UIColor.blue
        btn.setTitleColor(UIColor.systemBlue, for: .normal)
    }
    
    
    /// Función diseño de botón dinámico
    /// - Parameters:
    ///   - btn: Botón
    ///   - title: Texto del título
    func setButtonUI_dynamic(btn:UIButton, title:String) {
        btn.setTitle("\(title)", for: .normal)
        btn.titleLabel?.font = UIFont.systemFont(ofSize: 15)
        btn.titleLabel?.numberOfLines = 0
        btn.titleEdgeInsets = UIEdgeInsets(top: 0, left: 5, bottom: 0, right: 5)
        btn.layer.borderColor = UIColorFromRGB(rgbValue: 0xd0d0d0).cgColor
        btn.layer.borderWidth = 1
        btn.layer.cornerRadius = 10
        btn.layer.masksToBounds = true
        btn.setBackgroundColor(color: .white, forState: .normal)
        btn.tintColor = UIColor.blue
        btn.setTitleColor(UIColor.black, for: .normal)
    }
    
    //MARK: -Ajuste RGB
    
    /// Configuración de color
    /// - Parameter rgbValue: Código RGB hexadecimal (16 lanzamientos)
    /// - Returns: Color
    func UIColorFromRGB(rgbValue: UInt) -> UIColor {
        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
    
    /// Configuración de color (transparencia añadida)
    /// - Parameters:
    ///   - rgbValue: Código RGB hexadecimal (16 lanzamientos)
    ///   - alpha: Transparencia
    /// - Returns: Color
    func UIColorFromRGB(rgbValue: UInt, alpha:CGFloat) -> UIColor {
        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(alpha)
        )
    }
    
    //MARK: -Servicio común
    
    /// Servicio de consulta Regional
    /// - Parameters:
    ///   - view: Vista de llamada
    ///   - resp: Función devolución de llamada (call back)
    public func searchDEFT(view:UIView, resp:@escaping (AFDataResponse<Any>) -> Void) {
        
        let url = serverAddress().main
        
        let PARM = Dictionary<String,String>()
        let param = PARM.printJson()
        
        var listService:sendModelEN?
        if let seckey = UserDefaults.standard.string(forKey: "seckey"), let seckeyVector = UserDefaults.standard.string(forKey: "seckeyVector"){
            let paramEN = try! param.aesEncrypt(key: seckey, iv: seckeyVector)
            listService = sendModelEN(IFID: "PERUC0002", PARM: paramEN)
        }else{
            return
        }
        //***************************************************************
        
        //서버호출
        if let listService = listService{
            let req = serviceForm(url: url, parametersEN: listService)
            req.getPostEN(uid: UserDefaults.standard.string(forKey: "MNGR_SN") ?? "", view: view ,response: resp)
        }
    }
    
    /// Servicio de consulta Provincial
    /// - Parameters:
    ///   - view: Vista de llamada
    ///   - DPRTMNT_CODE: Código de Región
    ///   - resp: Función devolución de llamada (call back)
    public func searchProv(view:UIView, DPRTMNT_CODE:String,resp:@escaping (AFDataResponse<Any>) -> Void) {
        
        let url = serverAddress().main
        
        var PARM = Dictionary<String,String>()
        PARM.updateValue(DPRTMNT_CODE, forKey: "DPRTMNT_CODE")
        
        let param = PARM.printJson()
        
        var listService:sendModelEN?
        if let seckey = UserDefaults.standard.string(forKey: "seckey"), let seckeyVector = UserDefaults.standard.string(forKey: "seckeyVector"){
            let paramEN = try! param.aesEncrypt(key: seckey, iv: seckeyVector)
            listService = sendModelEN(IFID: "PERUC0003", PARM: paramEN)
        }else{
            return
        }
        //***************************************************************
        
        //서버호출
        if let listService = listService{
            let req = serviceForm(url: url, parametersEN: listService)
            req.getPostEN(uid: UserDefaults.standard.string(forKey: "MNGR_SN") ?? "", view: view ,response: resp)
        }
    }
    
    /// Servicio de consulta Distrital
    /// - Parameters:
    ///   - view: Vista de llamada
    ///   - DPRTMNT_CODE: Código de Región
    ///   - PRVNCA_CODE: Código de Provincia
    ///   - resp: Función devolución de llamada (call back)
    public func searchDIST(view:UIView, DPRTMNT_CODE:String, PRVNCA_CODE:String,resp:@escaping (AFDataResponse<Any>) -> Void) {
        
        let url = serverAddress().main
        
        var PARM = Dictionary<String,String>()
        PARM.updateValue(DPRTMNT_CODE, forKey: "DPRTMNT_CODE")
        PARM.updateValue(PRVNCA_CODE, forKey: "PRVNCA_CODE")
        
        let param = PARM.printJson()
        var listService:sendModelEN?
        if let seckey = UserDefaults.standard.string(forKey: "seckey"), let seckeyVector = UserDefaults.standard.string(forKey: "seckeyVector"){
            let paramEN = try! param.aesEncrypt(key: seckey, iv: seckeyVector)
            listService = sendModelEN(IFID: "PERUC0004", PARM: paramEN)
        }else{
            return
        }
        //***************************************************************
        
        //서버호출
        if let listService = listService{
            let req = serviceForm(url: url, parametersEN: listService)
            req.getPostEN(uid: UserDefaults.standard.string(forKey: "MNGR_SN") ?? "", view: view ,response: resp)
        }
    }
    
    /// Servicio de consulta de estado
    /// - Parameters:
    ///   - view: Vista de llamada
    ///   - resp: Función devolución de llamada (call back)
    public func searchState(view:UIView, resp:@escaping (AFDataResponse<Any>) -> Void) {
        
        let url = serverAddress().main
        
        let PARM = Dictionary<String,String>()
        
        let param = PARM.printJson()
        var listService:sendModelEN?
        if let seckey = UserDefaults.standard.string(forKey: "seckey"), let seckeyVector = UserDefaults.standard.string(forKey: "seckeyVector"){
            let paramEN = try! param.aesEncrypt(key: seckey, iv: seckeyVector)
            listService = sendModelEN(IFID: "PERUC0005", PARM: paramEN)
        }else{
            return
        }
        //***************************************************************
        
        //서버호출
        if let listService = listService{
            let req = serviceForm(url: url, parametersEN: listService)
            req.getPostEN(uid: UserDefaults.standard.string(forKey: "MNGR_SN") ?? "", view: view ,response: resp)
        }
    }
    
    /// Servicio de consulta encargado oficial
    /// - Parameters:
    ///   - view: Vista de llamada
    ///   - ECSHG_MNGR_SN: Número de serie del encargado oficial
    ///   - MNGR_LOGIN_ID: ID del encargado oficial
    ///   - ISLPRSN_SN: Número de serie de la persona en autocuarentena
    ///   - resp: Función devolución de llamada (call back)
    public func searchManager(view:UIView, ECSHG_MNGR_SN:String, MNGR_LOGIN_ID:String, ISLPRSN_SN:String, resp:@escaping (AFDataResponse<Any>) -> Void) {
        
        let url = serverAddress().main
        
        var PARM = Dictionary<String,String>()
        
        //Como se ocupa solo uno de los tres valores, se transmite el valor que se tiene
        if ISLPRSN_SN == ""{
            PARM.updateValue(MNGR_LOGIN_ID, forKey: "MNGR_LOGIN_ID")
            PARM.updateValue(ECSHG_MNGR_SN, forKey: "ECSHG_MNGR_SN")
            PARM.updateValue("", forKey: "ISLPRSN_SN")
        }else{
            PARM.updateValue("", forKey: "MNGR_LOGIN_ID")
            PARM.updateValue("", forKey: "ECSHG_MNGR_SN")
            PARM.updateValue(ISLPRSN_SN, forKey: "ISLPRSN_SN")
        }
        
        
        let param = PARM.printJson()
        
        var listService:sendModelEN?
        if let seckey = UserDefaults.standard.string(forKey: "seckey"), let seckeyVector = UserDefaults.standard.string(forKey: "seckeyVector"){
            let paramEN = try! param.aesEncrypt(key: seckey, iv: seckeyVector)
            listService = sendModelEN(IFID: "PERU0011", PARM: paramEN)
        }else{
            return
        }
        //***************************************************************
        
        //서버호출
        if let listService = listService{
            let req = serviceForm(url: url, parametersEN: listService)
            req.getPostEN(uid: UserDefaults.standard.string(forKey: "MNGR_SN") ?? "", view: view ,response: resp)
        }
    }
    
    /// Servicio de cambio encargado oficial
    /// - Parameters:
    ///   - view: Vista de llamada
    ///   - MNGR_SN: Número de serie del encargado oficial
    ///   - ISLPRSN_SN: Número de serie de la persona en autocuarentena
    ///   - resp: Función devolución de llamada (call back)
    public func changeManager(view:UIView, MNGR_SN:String, ISLPRSN_SN:String, resp:@escaping (AFDataResponse<Any>) -> Void) {
        
        let url = serverAddress().main
        
        var PARM = Dictionary<String,String>()
        PARM.updateValue(MNGR_SN, forKey: "MNGR_SN")
        PARM.updateValue(ISLPRSN_SN, forKey: "ISLPRSN_SN")
        
        let param = PARM.printJson()
        var listService:sendModelEN?
        if let seckey = UserDefaults.standard.string(forKey: "seckey"), let seckeyVector = UserDefaults.standard.string(forKey: "seckeyVector"){
            let paramEN = try! param.aesEncrypt(key: seckey, iv: seckeyVector)
            listService = sendModelEN(IFID: "PERUO0007", PARM: paramEN)
        }else{
            return
        }
        //***************************************************************
        
        //서버호출
        if let listService = listService{
            let req = serviceForm(url: url, parametersEN: listService)
            req.getPostEN(uid: UserDefaults.standard.string(forKey: "MNGR_SN") ?? "", view: view ,response: resp)
        }
    }
    
    //MARK: - Servicio común
    
    /// Cambiar tamaño de imagen
    /// - Parameters:
    ///   - image: Imagen
    ///   - targetSize: Tamaño a cambiar
    /// - Returns: Imagen cambiada
    func ResizeImage(image: UIImage, targetSize: CGSize) -> UIImage {
        
        let size = image.size
        
        let widthRatio  = targetSize.width  / image.size.width
        let heightRatio = targetSize.height / image.size.height
        
        var newSize: CGSize
        if(widthRatio > heightRatio) {
            newSize = CGSize(width: size.width * heightRatio, height: size.height * heightRatio)
        } else {
            newSize = CGSize(width: size.width * widthRatio, height: size.height * widthRatio)
        }
        
        let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)
        
        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        image.draw(in: rect)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return newImage!
    }
    
    
    /// Ventana de notificación que dura 1 segundo
    /// - Parameters:
    ///   - controller: Controlador de llamadas
    ///   - view: Vista para flotar
    ///   - message: Mensaje para imprimir
    ///   - result: Call back de resultados
    func makeToast(controller: UIViewController, view: UIView, message: String, result : (() -> Void)?) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .actionSheet)
        controller.present(alert, animated: false) {
            sleep(1)
            alert.dismiss(animated: false, completion: result)
        }
    }
    
    /// Verificar si es la última versión
    /// - Returns: Ultima versión Si o No
    func isUpdateAvailable() -> Bool {
        guard
            let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String,
            
            let url = URL(string: "https://"),
            
            let data = try? Data(contentsOf: url),
            let json = try? JSONSerialization.jsonObject(with: data, options: .allowFragments) as? [String: Any],
            
            let results = json["results"] as? [[String: Any]?],
            results.count > 0,
            
            let appStoreVersion = results[0]?["version"] as? String
            else {return false}
        
        print("version: \(version), store: \(appStoreVersion)")
        
        if version.compare(appStoreVersion, options: .numeric) == .orderedAscending {
            return true
        }else{
            return false
        }
        
    }
    
    /// Diálogo de confirmación de lanzamiento de la última versión
    /// - Parameter vc: Llamar al controlador de vista (view controller)
    func makeVersionUpdateDialog(vc: UIViewController) {
        let dialog = UIAlertController(title: "Need update".localized, message: "Please update to the latest version. Press OK to go to the download page.".localized, preferredStyle: .alert)
        
        let okBtn = UIAlertAction(title: "Confirm".localized, style: .default) { (okBtn) in
            let url = URL(string: "https://")
            UIApplication.shared.open(url ?? URL(string:"https://apps.apple.com/kr")!, options: [:], completionHandler: nil)
        }
        
        dialog.addAction(okBtn)
        vc.present(dialog, animated: false, completion: nil)
    }
    
    //공개키 요청 서비스
    public func requestPublic(resp:@escaping (AFDataResponse<Any>) -> Void) {
        let url = serverAddress().main
        let PARM = Dictionary<String,String>()
        let param = PARM.printJson()
        let listService = sendModelEN(IFID: "PERUK0001", PARM: param)
        //서버호출
        let req = serviceForm(url: url, parametersEN: listService)
        req.getPost(response: resp)
    }
    
    //MARK:- 토스트 출력 및 종료
    public func showExitAlert(vc: UIViewController) {
        let alert = UIAlertController(title: nil, message: "키를 받는 도중 에러가 발생하였습니다.\n다시 시도해 주세요.", preferredStyle: .alert)
        
        let cancel = UIAlertAction(title: "확인", style: .default) { (cancel) in
            exit(2)
        }
        
        alert.addAction(cancel)
        
        vc.present(alert, animated: true, completion: nil)
    }
    
    //MARK: - 코드값이 보안키 관련 에러인가?
    public func isCodeAboutSecureKey(code : String) -> Bool {
        if code == "EI0002" {
            return true
        } else {
            return false
        }
    }
    
    //MARK: - 비밀키 틀릴 경우 격리자 등록 화면으로 돌아가기
    private func resetPreference() {
        
        for key in preference.dictionaryRepresentation().keys {
            preference.removeObject(forKey: key)
        }
        preference.set(true, forKey: "notiPermission")
        preference.synchronize()
    }
    
    //MARK:- 보안키가 다를 경우 앱 종료 안내 다이얼로그
    public func showPreferClearDialog(vc : UIViewController ) {
        let alert = UIAlertController(title: nil, message: "키가 만료되었습니다.\n정보를 다시 등록해 주세요.", preferredStyle: .alert)
        let okBtn = UIAlertAction(title: "확인", style: .default) { (ok) in
            self.resetPreference()
            exit(1)
        }
        alert.addAction(okBtn)
        DispatchQueue.main.async {
            if UIApplication.shared.applicationState == .background {
                self.makeLocalNotification(title: nil, body: "키가 만료되었습니다.\n정보를 다시 등록해 주세요.", sound: nil, timer: 1, identifier: "17")
            } else {
                vc.present(alert, animated: true, completion: nil)
            }
        }
    }
    
    //MARK:- 로컬 알림
    func makeLocalNotification(title: String?, body: String?, sound: UNNotificationSound?, timer: TimeInterval, identifier: String) {
        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current().getNotificationSettings { (setting) in
                guard setting.alertSetting != .disabled else {
                    return
                }
                
                let nContent = UNMutableNotificationContent()
                nContent.badge = 0
                nContent.title = title ?? ""
                nContent.body = body ?? ""
                nContent.sound = sound
                
                let trigger = UNTimeIntervalNotificationTrigger(timeInterval: timer, repeats: false)
                let request = UNNotificationRequest(identifier: identifier, content: nContent, trigger: trigger)
                
                UNUserNotificationCenter.current().add(request, withCompletionHandler: nil)
            }
        }
    }
}
