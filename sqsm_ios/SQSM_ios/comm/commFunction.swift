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

class commFunction {
    typealias response = (AFDataResponse<Any>) -> Void
    
    static let comm = commFunction()
    let preference = UserDefaults.standard
    let application = UIApplication.shared.delegate as! AppDelegate
    
    //Configuración del contorno del botón
    func setButtonBorder(sender: UIButton) {
        sender.layer.borderColor = UIColor.black.cgColor
        sender.layer.borderWidth = 1.0
    }
    
    //Configuración de esquema de ventana de entrada
    func setTextFieldBorder(sender: UITextField) {
        sender.layer.borderColor = UIColor.black.cgColor
        sender.layer.borderWidth = 1.0
    }
    
    //Configuración de tamaño de texto en la ventana de selección
    func setSegmentFont(segment: UISegmentedControl, font: UIFont) {
        segment.setTitleTextAttributes([NSAttributedString.Key.font : font], for: .normal)
    }
    
    //Cambiar el contorno de la vista de texto a redondo
    func setTextView(sender : UITextView){
        sender.layer.cornerRadius = 10
    }
    
    //Cambiar el contorno de la vista a redondeado
    func setView(sender : UIView){
        sender.layer.cornerRadius = 10
    }
    
    //Configuración de color
    func UIColorFromRGB(rgbValue: UInt) -> UIColor {
        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
    
    //Configuración botón UI (user interface)
    func setButtonUI(btn:UIButton) {
        if btn.tag == 0 {
            btn.backgroundColor = UIColorFromRGB(rgbValue: 0xFFFFFF)
            btn.setTitleColor(UIColor.blue, for: .normal)
        }else{
            btn.backgroundColor = UIColorFromRGB(rgbValue: 0x0078D7)
            btn.setTitleColor(UIColor.white, for: .normal)
        }
        btn.layer.borderColor = UIColor.blue.cgColor
        btn.layer.borderWidth = 1
        btn.layer.cornerRadius = 10
        btn.layer.masksToBounds = true
    }
    
    //Configuración de color (Agregar transparencia)
    func UIColorFromRGB(rgbValue: UInt, alpha:CGFloat) -> UIColor {
        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(alpha)
        )
    }
    
    /// Determinar si la aplicación actual necesita actualización
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
    
    /// Diálogo de actualización de versión
    func makeVersionUpdateDialog(vc: UIViewController) {
        let dialog = UIAlertController(title: "Actualizar", message: "Por favor, actualice a la última versión. Presione OK para ir a la página de descarga.", preferredStyle: .alert)
        
        let okBtn = UIAlertAction(title: "Confirmar", style: .default) { (okBtn) in
            let url = URL(string: "") // Insert your AppStore app link
            UIApplication.shared.open(url ?? URL(string:"https://apps.apple.com/kr")!, options: [:], completionHandler: nil)
        }
        
        //        let cancelBtn = UIAlertAction(title: "Cancel".localized, style: .cancel)
        
        dialog.addAction(okBtn)
        //        dialog.addAction(cancelBtn)
        
        vc.present(dialog, animated: false, completion: nil)
    }
    
    /// - Parameters:
    ///   - controller: viewController
    ///   - message: Descripción
    ///   - result: resultHanlder
    func makeToast(controller: UIViewController, message: String, result : (() -> Void)?) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .actionSheet)
        controller.present(alert, animated: false) {
            sleep(1)
            alert.dismiss(animated: false, completion: result)
        }
    }
    
    //Verificación de patrón de número de teléfono móvil
    func isPhone(candidate: String, identifier: String) -> Bool {
        if candidate == "" && identifier == StaticString.numberType.emrgencyNumber.rawValue {
            return true
        }
        let regex = "^\\+?[0-9]{10,14}$"
        return NSPredicate(format: "SELF MATCHES %@", regex).evaluate(with: candidate)
    }
    
    /// Diálogo común
    func basicDialog(vc: UIViewController, title: String?, message: String?, positiveTitle: String?, negativeTitle: String?, positiveHandler : ((UIAlertAction) -> Void)?, negativeHandler : ((UIAlertAction) -> Void)?, completionHandler : (() -> Void)?) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        
        if positiveTitle != nil {
            let positiveButton = UIAlertAction(title: positiveTitle, style: .default, handler: positiveHandler)
            alert.addAction(positiveButton)
        }
        
        if negativeTitle != nil {
            let negativeButton = UIAlertAction(title: negativeTitle, style: .cancel, handler: negativeHandler)
            alert.addAction(negativeButton)
        }
        vc.present(alert, animated: true, completion: completionHandler)
    }
    
    /// Ajuste del tamaño del texto de la barra superior
    func setNaviTitle(navItem: UINavigationItem ,titleText: String, font: CGFloat) {
        let titleLabel = UILabel()
        titleLabel.text = titleText
        titleLabel.numberOfLines = 0
        titleLabel.textColor = UIColor.white
        //        titleLabel.font = UIFont.boldSystemFont(ofSize: font)
        titleLabel.textAlignment = .center
        titleLabel.lineBreakMode = .byWordWrapping
        titleLabel.adjustsFontSizeToFitWidth = true
        navItem.titleView = titleLabel
    }
    
    //Finalización de la aplicación
    public func showExitAlert(vc: UIViewController) {
        let alert = UIAlertController(title: nil, message: "An error has occurred while receiving a key.\nPlease try again.".localized, preferredStyle: .alert)
        
        //        let _ = UIAlertAction(title: "재시도", style: .default) { (retry) in
        //            Services.Service.getPublicKeyService(view: vc.view, resp: resp)
        //        }
        
        let cancel = UIAlertAction(title: "Confirm".localized, style: .default) { (cancel) in
            exit(2)
        }
        
        //        alert.addAction(retry)
        alert.addAction(cancel)
        
        vc.present(alert, animated: true, completion: nil)
    }
    
    /// Notificación local
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
    
    //Reiniciar valores almacenados localmente
    private func resetPreference() {
        let language = preference.string(forKey: "AppleLanguage")
        
        for key in preference.dictionaryRepresentation().keys {
            preference.removeObject(forKey: key)
        }
        preference.set(language, forKey: "AppleLanguage")
        UserDefaults.standard.set(true, forKey: "notiPermission")
        preference.set(false, forKey: "isFirst")
        preference.synchronize()
    }
    
    //Diálogo de guía de finalización de la aplicación
    public func showPreferClearDialog() {
        let alert = UIAlertController(title: nil, message: "Your key has expired.\nPlease register your information again.".localized, preferredStyle: .alert)
        let okBtn = UIAlertAction(title: "Confirm".localized, style: .default) { (ok) in
            self.resetPreference()
            exit(1)
        }
        alert.addAction(okBtn)
        DispatchQueue.main.async {
            if UIApplication.shared.applicationState == .background {
                self.makeLocalNotification(title: nil, body: "Your key has expired.\nPlease register your information again.".localized, sound: nil, timer: 1, identifier: "17")
            } else {
                if let vc = self.application.window?.rootViewController {
                    vc.present(alert, animated: true, completion: nil)
                }
            }
        }
    }
    
    //Error de clave de seguridad
    public func isCodeAboutSecureKey(code : String) -> Bool {
        if code == "EI0002" {
            return true
        } else {
            return false
        }
    }
    
    //Verifique si existe clave de seguridad
    public func isSecureKeyExist() -> Bool {
        if preference.string(forKey: StaticString.encryption.SECURE_KEY.rawValue) != nil &&
            preference.string(forKey: StaticString.encryption.SECURE_VECTOR.rawValue) != nil {
            return true
        } else {
            return false
        }
    }
}
