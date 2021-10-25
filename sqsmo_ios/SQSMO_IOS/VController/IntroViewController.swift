//
//  IntroViewController.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/22.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import Alamofire
import Firebase


/// Pantalla de configuración inicial
class IntroViewController: UIViewController{
    let pList = UserDefaults.standard
    let activityIndicator = UIActivityIndicatorView(style: .whiteLarge)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        activityIndicator.center = self.view.center
        self.view.addSubview(activityIndicator)
    }
    
    //Después de verificar la autoridad del usuario, la pantalla se mueve dependiendo de si el permiso está permitido.
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        if UserDefaults.standard.bool(forKey: "notiPermission") {
            self.notificationPermission()
        }else{
            NotificationCenter.default.addObserver(self, selector: #selector(self.notificationPermissionRequest), name: AppDelegate.kNotification, object: nil)
        }
    }
    
    /// Solicitud de permiso de notificación
    @objc func notificationPermissionRequest() {
        UserDefaults.standard.set(true, forKey: "notiPermission")
        UserDefaults.standard.synchronize()
        
        if #available(iOS 10.0, *) {
            self.notificationPermission()
        }
        NotificationCenter.default.removeObserver(self)
    }
    
    /// Solicitud de permiso de notificación
    func notificationPermission(){
        UNUserNotificationCenter.current().getNotificationSettings(){
            (setting)in
            print(setting.debugDescription)
            switch setting.authorizationStatus{
            case .denied:
                DispatchQueue.main.sync {
                    let dialog = UIAlertController(title: "Permit authorization".localized,
                                                   message: "Configuration in the system to receive quarantine status alarms -> Authorization to receive notification of the App.".localized, preferredStyle: UIAlertController.Style.alert)
                    let okButton = UIAlertAction(title: "Confirm".localized, style: .default, handler: {(UIAlertAction) in
                        if self.pList.value(forKey: "publicKey") != nil {
                            decryptMSG.instance.key128 = self.pList.value(forKey: "publicKey") as! String
                            decryptMSG.instance.iv = self.pList.value(forKey: "publicVector") as! String
                            
                            //Si el token ha sido cambiado
                            if UserDefaults.standard.string(forKey: "LOGIN_ID") != nil && UserDefaults.standard.bool(forKey: "tokenChange") {
                                self.sendToken()
                            }
                            self.performSegue(withIdentifier: "goLogin", sender: self)
                        }else{
                            DispatchQueue.main.async {
                                self.callPublicService()
                            }
                        }
                        
                        
                    })
                    dialog.addAction(okButton)
                    self.present(dialog, animated: false, completion: nil)
                }
                
            default:
                if self.pList.value(forKey: "publicKey") != nil {
                    //키등록
                    decryptMSG.instance.key128 = self.pList.value(forKey: "publicKey") as! String
                    decryptMSG.instance.iv = self.pList.value(forKey: "publicVector") as! String
                    
                    DispatchQueue.main.async {
                        if UserDefaults.standard.string(forKey: "LOGIN_ID") != nil && UserDefaults.standard.bool(forKey: "tokenChange") {
                            self.sendToken()
                        }
                        self.performSegue(withIdentifier: "goLogin", sender: self)
                    }
                }else{
                    DispatchQueue.main.async {
                        self.callPublicService()
                    }
                }
            }
        }
    }
    
    /// Servicio para volver a registrarse cuando se cambia la clave FCM
    func sendToken(){
        let url = serverAddress().main
        var PARM = Dictionary<String,String>()
        
        PARM.updateValue(pList.string(forKey: "MNGR_NM") ?? "" , forKey: "MNGR_NM")
        PARM.updateValue(pList.string(forKey: "LOGIN_ID") ?? "" , forKey: "LOGIN_ID")
        PARM.updateValue(pList.string(forKey: "MBTLNUM") ?? "" , forKey: "MBTLNUM")
        PARM.updateValue("00402", forKey: "TRMNL_KND_CODE")
        PARM.updateValue(pList.string(forKey: "token") ?? "", forKey: "PUSHID")
        
        //******** 기존의 파라메터를 암호화 하는 부분
        let param = PARM.printJson()
        print(param)
        
        var listService:sendModelEN?
        if let seckey = self.pList.string(forKey: "seckey"), let seckeyVector = self.pList.string(forKey: "seckeyVector"){
            let paramEN = try! param.aesEncrypt(key: seckey, iv: seckeyVector)
            listService = sendModelEN(IFID: "PERUO0001", PARM: paramEN)
        }else{
            return
        }
        //***************************************************************
        
        
        //토큰 콜백
        let resp : (AFDataResponse<Any>) -> Void = ({
            response in
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                guard let json = value as? [String: Any], let _ = json["RES_DATA"] as? String else {
                    return
                }
                self.pList.set(false, forKey: "tokenChange")
                self.pList.synchronize()
            case .failure(let error):
                print(error)
            }
        })
        //서버호출
        if let listService = listService{
            let req = serviceForm(url: url, parametersEN: listService)
            req.getPostEN(uid: self.pList.string(forKey: "MNGR_SN") ?? "", response: resp)
        }
    }
    
    //공통키 요청 서비스
    func callPublicService() {
        Spinner.start(from: self.view)
        commFunction().requestPublic(resp: {
            response in
            Spinner.stop()
            switch response.result {
            case .success(let value):
                print(value)
                
                guard let json = value as? [String: Any] else {
                    commFunction().showExitAlert(vc: self)
                    return
                }
                
                guard let res = json["RES"] as? String, let res_cd = json["RES_CD"] as? String else {
                    commFunction().showExitAlert(vc: self)
                    return
                }
                
                if res_cd == "100" {
                    let point = res.index(res.startIndex, offsetBy: 16)
                    let publicKey = res[res.startIndex..<point]
                    let publicVector = res[point..<res.endIndex]
                    //                    print(res[res.startIndex..<point])
                    //                    print(res[point..<res.endIndex])
                    
                    //키등록
                    decryptMSG.instance.key128 = String(publicKey)
                    decryptMSG.instance.iv = String(publicVector)
                    self.pList.set(publicKey, forKey: "publicKey")
                    self.pList.set(publicVector, forKey: "publicVector")
                    self.pList.synchronize()
                    
                    DispatchQueue.main.async {
                        if UserDefaults.standard.string(forKey: "LOGIN_ID") != nil && UserDefaults.standard.bool(forKey: "tokenChange") {
                            self.sendToken()
                        }
                        self.performSegue(withIdentifier: "goLogin", sender: self)
                    }
                }
                
            case .failure(let error):
                commFunction().showExitAlert(vc: self)
                print(error)
            }
        })
    }
    
}
