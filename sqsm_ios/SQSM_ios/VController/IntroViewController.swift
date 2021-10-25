//
//  IntroViewController.swift
//  TestSampleApp
//
//  Created by MiRan Kim on 2020/02/17.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import CoreLocation
import Alamofire
import Foundation
import AdSupport

class IntroViewController: UIViewController, CLLocationManagerDelegate {
    typealias response = (AFDataResponse<Any>) -> Void
    
    @IBOutlet var version: UILabel!
    @IBOutlet var appName: UILabel!
    
    //MARK: - variable
    var locationManager: CLLocationManager!
    private var publicKeyHandler : response!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let appVersion = Bundle.main.infoDictionary?[StaticString.introString.bundleVersion.rawValue] as? String
        if appVersion != nil {
            self.version.text = "Ver \(String(describing: appVersion!))"
        } else {
            self.version.text = "Ver 1.0"
        }
        
        // Se usa para verificar si es un usuario registrado
        if UserDefaults.standard.string(forKey: StaticString.preference.registerationCheck.rawValue) == nil {
            UserDefaults.standard.set(false, forKey: StaticString.preference.registerationCheck.rawValue)
            UserDefaults.standard.synchronize()
        }
        
        // Se usa para verificar si inició por primera vez la aplicación
        if UserDefaults.standard.string(forKey: StaticString.introString.appRuningFirst.rawValue) == nil {
            UserDefaults.standard.set(true, forKey: StaticString.introString.appRuningFirst.rawValue)
            UserDefaults.standard.synchronize()
        }
        
        publicKeyHandler = ({
            response in
            Spinner.stop()
            switch response.result {
            case .success(let value) :
                print(value)
                guard let json = value as? [String: Any] else {
                    commFunction.comm.showExitAlert(vc: self)
                    return
                }
                
                guard let code = json["RES_CD"] as? String else {
                    commFunction.comm.showExitAlert(vc: self)
                    return
                }
                
                if code == "100" {
                    guard let resp = json["RES"] as? String else {
                        commFunction.comm.showExitAlert(vc: self)
                        return
                    }
                    decryptMSG.shared.key128 = resp.subString(from: 0, to: 15)
                    decryptMSG.shared.iv = resp.subString(from: 16, to: 31)
                    print("key : \(decryptMSG.shared.key128), iv : \(decryptMSG.shared.iv)")
                    commFunction.comm.preference.set(decryptMSG.shared.key128, forKey: StaticString.encryption.PUBLIC_KEY.rawValue)
                    commFunction.comm.preference.set(decryptMSG.shared.iv, forKey: StaticString.encryption.PUBLIC_VECTOR.rawValue)
                    commFunction.comm.preference.synchronize()
                    
                    self.goNextScreen()
                } else {
                    commFunction.comm.showExitAlert(vc: self)
                }
                
            case .failure(let error) :
                print(error)
                commFunction.comm.showExitAlert(vc: self)
            }
        })
    }
    
    override func viewDidAppear(_ animated: Bool) {
        self.checkNotificationPermission()
    }
    
    //MARK: - function
    
    /// Función Call Back (devolución de llamada) de solicitud de permiso de alarma
    func notificationPermissionCallBack(){
        UNUserNotificationCenter.current().getNotificationSettings(){
            (setting)in
            switch setting.authorizationStatus {
            case .denied :
                DispatchQueue.main.sync {
                    commFunction.comm.basicDialog(vc: self,
                                                  title: StaticString.dialogText.authAllowTitle.rawValue.localized,
                                                  message: StaticString.dialogText.notificationAllowMessage.rawValue.localized,
                                                  positiveTitle: StaticString.dialogText.confirm.rawValue.localized,
                                                  negativeTitle: nil,
                                                  positiveHandler: {
                                                    (UIAlertAction) in
                                                    self.checkLocationPermission() },
                                                  negativeHandler: nil,
                                                  completionHandler: nil)
                }
                
            default :
                DispatchQueue.main.async {
                    self.checkLocationPermission()
                }
            }
        }
    }
    
    /// Modificación de estado Call back para el permiso de ubicación
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        print(status.rawValue)
        switch status {
        case .denied :
            commFunction.comm.basicDialog(vc: self,
                                          title: StaticString.dialogText.authAllowTitle.rawValue.localized,
                                          message: StaticString.dialogText.locationAllowMessage.rawValue.localized,
                                          positiveTitle: StaticString.dialogText.confirm.rawValue.localized,
                                          negativeTitle: nil,
                                          positiveHandler: {
                                            (UIAlertAction) in
                                            self.permissionCheckEnd() },
                                          negativeHandler: nil,
                                          completionHandler: nil)
            
        case .authorizedAlways, .authorizedWhenInUse :
            self.permissionCheckEnd()
            
        default:
            break
        }
    }
    
    /// Solicitud de permiso de alarma
    func checkNotificationPermission() {
        if #available(iOS 10.0, *) {
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {(isAgree, error) -> Void in
                    if let error = error {
                        print(error.localizedDescription)
                    }
                    self.notificationPermissionCallBack()
            })
        }
    }
    
    /// Función a ejecutar después de que finalice la verificación de permiso
    func permissionCheckEnd() {
        if commFunction.comm.preference.bool(forKey: StaticString.introString.appRuningFirst.rawValue) {
            self.makeLanguageSelectDialog()
        } else {
            if commFunction.comm.preference.string(forKey: StaticString.encryption.PUBLIC_KEY.rawValue) == nil &&
                commFunction.comm.preference.string(forKey: StaticString.encryption.PUBLIC_VECTOR.rawValue) == nil {
                Services.Service.getPublicKeyService(view: self.view, resp: publicKeyHandler)
            } else {
                self.goNextScreen()
            }
        }
    }
    
    /// Solicitar permiso de ubicación
    func checkLocationPermission() {
        self.locationManager = CLLocationManager()
        self.locationManager.allowsBackgroundLocationUpdates = true
        self.locationManager.delegate = self
        self.locationManager.requestAlwaysAuthorization()
    }
    
    /// Diálogo de selección de idioma
    func makeLanguageSelectDialog() {
        let alert = UIAlertController(title: "Select language",
                                      message: "Please select a language to use inside the app", preferredStyle: .actionSheet)
        
        let spanish = UIAlertAction(title: "Español", style: .default) { (UIAlertAction) in
            self.setLanguage(languageCode: "es")
            self.makeAlertDialog(title: "Seleccionar idioma", message: "Salga de la aplicación para aplicar el idioma. Por favor, reinicie la aplicación", okTitle: "Confirmar")
        }
        
        let english = UIAlertAction(title: "English", style: .default) { (UIAlertAction) in
            self.setLanguage(languageCode: "en")
            self.makeAlertDialog(title: "Select language", message: "Exit the app to apply the language. Please relaunch the app", okTitle: "Confirm")
        }
        
        alert.addAction(spanish)
        alert.addAction(english)
        
        self.present(alert, animated: true, completion: nil)
    }
    
    /// Postprocesamiento cuando se selecciona el idioma
    /// - Parameter languageCode: Código de lenguaje
    func setLanguage(languageCode: String) {
        let pList = UserDefaults.standard
        pList.set(false, forKey: StaticString.introString.appRuningFirst.rawValue)
        //        pList.removeObject(forKey: StaticString.introString.appLang.rawValue)
        pList.set(languageCode, forKey: StaticString.introString.appLang.rawValue)
        pList.synchronize()
    }
    
    /// Crear diálogo
    /// - Parameters:
    ///   - title: Título
    ///   - message: Descripción
    func makeAlertDialog(title: String?, message: String?, okTitle: String) {
        let dialog = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
        let okButton = UIAlertAction(title: okTitle, style: .default){
            (_) in
            self.appExit()
        }
        
        dialog.addAction(okButton)
        self.present(dialog, animated: false, completion: nil)
    }
    
    /// Finalizar apliación
    func appExit() {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
            UIApplication.shared.perform(#selector(NSXPCConnection.suspend))
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.7) {
                exit(0)
            }
        }
    }
    
    /// Selección de la pantalla para mostrar a continuación dependiendo de la información del usuario esté o no esté registrado
    func goNextScreen() {
        if UserDefaults.standard.bool(forKey: StaticString.preference.registerationCheck.rawValue) {
            //Ir a pantalla principal
            self.performSegue(withIdentifier: StaticString.segueString.mainSegue.rawValue, sender: self)
        } else {
            //Ir a pantalla de permisos
            self.performSegue(withIdentifier: StaticString.segueString.agreeSegue.rawValue, sender: self)
        }
    }
}

