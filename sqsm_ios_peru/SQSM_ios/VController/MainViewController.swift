//
//  MainViewController.swift
//  TestSampleApp
//
//  Created by wini on 2020/02/13.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import CoreLocation
import Alamofire

class MainViewController: UIViewController, CLLocationManagerDelegate {
    typealias response = (AFDataResponse<Any>) -> Void
    
    //MARK: - outlet
    @IBOutlet var version: UILabel!
    @IBOutlet var info: UIButton!
    @IBOutlet var rule: UIButton!
    @IBOutlet var manager: UIButton!
    @IBOutlet var call: UIButton!
    @IBOutlet var menuButton: UIButton!
    @IBOutlet var selfCheck: UIButton!
    @IBOutlet var selfCheckLabel: UILabel!
    @IBOutlet var firstView: UIView!
    @IBOutlet var naviItem: UINavigationItem!
    @IBOutlet var lastCheck: UILabel!
    
    //MARK: - variable
    var position:String?
    var isFirst:Bool!
    var mTimer:Timer?
    var timmerCount:Int = 30
    var timmerLoc:CLLocationCoordinate2D?
    var locationManager : CLLocationManager!
    var mLocation = Dictionary<String,Double>()
    
    var checkedImage: UIImage?
    var unCheckdImage: UIImage?
    
    let application = UIApplication.shared.delegate as! AppDelegate
    let preference = UserDefaults.standard
    
    private var lastDiagnoseHandler : response!
    private var officialHandler : response!
    private var sendLocationHandler : response!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.selfCheckLabel.font = self.selfCheckLabel.font.withSize(self.selfCheckLabel.frame.height * 0.35)
        
        self.isFirst = true
        self.menuButton.backgroundColor = UIColor.white
        self.menuButton.layer.cornerRadius = 10
        self.menuButton.titleLabel?.textAlignment = .center
        self.menuButton.layer.shadowColor = UIColor.gray.cgColor
        self.menuButton.layer.shadowOffset = CGSize(width: 0.0, height: 1.0)
        self.menuButton.layer.shadowOpacity = 1.0
        
        mLocation.updateValue(0.0, forKey: "lat")
        mLocation.updateValue(0.0, forKey: "lon")
        
        self.setLocationManager()
        
        if let appVersion = Bundle.main.infoDictionary?[StaticString.introString.bundleVersion.rawValue] as? String {
            self.version.text = "Ver : \(String(describing: appVersion))"
        } else {
        }
            self.version.text = "Ver 1.0"
        
        self.unCheckdImage = UIImage(named: StaticString.selfDiagnosisImage.imageRed.rawValue)
        self.checkedImage = UIImage(named: StaticString.selfDiagnosisImage.imageBlue.rawValue)
        NotificationCenter.default.addObserver(self, selector: #selector(showOfficialView), name: NSNotification.Name(rawValue: StaticString.NotificationName.officialContact.rawValue), object: nil)
        
        commFunction.comm.setNaviTitle(navItem: naviItem, titleText: StaticString.mainString.title.rawValue.localized, font: 15)
        
        lastDiagnoseHandler = ({
            response in
            Spinner.stop()
            
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
                    let msg = json["RES_MSG"] as? String
                    commFunction.comm.makeToast(controller: self, message: msg ?? StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                if commFunction.comm.isSecureKeyExist() {
                    let key = self.preference.string(forKey: StaticString.encryption.SECURE_KEY.rawValue)!
                    let vector = self.preference.string(forKey: StaticString.encryption.SECURE_VECTOR.rawValue)!
                    let msg = decryptMSG.shared.msgDecryptSecure(msg: resp, key: key, iv: vector)
                    let data :[Dictionary<String,Any>] = decryptMSG.shared.decrypt(enData: msg)
                    print(data.debugDescription)
                    
                    if data.count > 0 {
                        let value = data[0]
                        if let last = value["SLFDGNSS_DT_F"] as? String {
                            self.lastCheck.text = last
                        }else{
                            self.lastCheck.text = "-"
                        }
                        
                        if let image = value["SLFDGNSS_AT"] as? String{
                            if image == "Y"{
                                self.selfCheckLabel.textColor = commFunction.comm.UIColorFromRGB(rgbValue: 0x0078D7)
                                self.selfCheck.setBackgroundImage(self.checkedImage, for: .normal)
                            }else{
                                self.selfCheck.setBackgroundImage(self.unCheckdImage, for: .normal)
                                self.selfCheckLabel.textColor = UIColor.red
                            }
                        }
                    }else{
                        self.selfCheckLabel.text = "-"
                        self.selfCheck.setBackgroundImage(self.unCheckdImage, for: .normal)
                    }
                }
                
            case .failure(let error):
                Spinner.stop()
                commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.coommunicationError.rawValue.localized, result: nil)
                print(error)
            }
        })
        
        officialHandler = ({
            response in
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                Spinner.stop()
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
                    let _ = json["RES_MSG"] as? String
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                //[Parte que descifra las partes encriptadas y cambiar a forma de diccionario]
                if commFunction.comm.isSecureKeyExist() {
                    let key = self.preference.string(forKey: StaticString.encryption.SECURE_KEY.rawValue)!
                    let vector = self.preference.string(forKey: StaticString.encryption.SECURE_VECTOR.rawValue)!
                    let msg = decryptMSG.shared.msgDecryptSecure(msg: resp, key: key, iv: vector)
                    
                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: StaticString.NotificationName.officialContact.rawValue), object: nil)
                }
                
            case .failure(let error):
                print(error)
                Spinner.stop()
                commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
            }
            
        })
        
        sendLocationHandler = ({
            response in
            
            self.timerClear()
            
            switch response.result {
            case .success(let value):
                guard let json = value as? [String: Any] else {
                    return
                }
                
                guard let code = json["RES_CD"] as? String else {
                    return
                }
                
                if commFunction.comm.isCodeAboutSecureKey(code: code) {
                    commFunction.comm.showPreferClearDialog()
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    return
                }
                
                if commFunction.comm.isSecureKeyExist() {
                    let key = self.preference.string(forKey: StaticString.encryption.SECURE_KEY.rawValue)!
                    let vector = self.preference.string(forKey: StaticString.encryption.SECURE_VECTOR.rawValue)!
                    let msg = decryptMSG.shared.msgDecryptSecure(msg: resp, key: key, iv: vector)
                    let data :[Dictionary<String,Any>] = decryptMSG.shared.decrypt(enData: msg)
                    print(data.debugDescription)
                    
                    if data.count > 0 {
                        _ = data[0]
                    }
                    
                    print("===== user location registration success ======")
                }
                
            case .failure(let error):
                print(error)
            }
        })
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        Services.Service.lastDiagnosisService(vc: self, resp: lastDiagnoseHandler)
        
        //Verifica la última versión de la aplicación
//        if commFunction.comm.isUpdateAvailable() {
//            commFunction.comm.makeVersionUpdateDialog(vc: self)
//        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let mainTab = segue.destination as? UITabBarController else{
            return
        }
        let CVC = mainTab.viewControllers?.first as! CheckScrollView
        
        switch segue.identifier! {
        //Autodiagnóstico
        case "view00":
            CVC.param = 0
        //Lista de autodiagnóstico
        case "view01":
            CVC.param = 1
        //Editar información
        case "view03":
            CVC.param = 2
        default:
            break
        }
    }
    
    //MARK: -  UI ACTION
    /// call 1339
    /// - Parameter sender: Botón de 1339
    @IBAction func call107(_ sender: UIButton) {
        if let phoneCallURL = URL(string: "tel://107") {
            let application:UIApplication = UIApplication.shared
            if (application.canOpenURL(phoneCallURL)) {
                print(Locale.preferredLanguages[0])
                application.open(phoneCallURL, options: [:], completionHandler: nil)
            }
        }
    }
    
    /// Translada a la pantalla de Centros de Salud alrededores
    /// - Parameter sender: Botón de Centro de Salud cercano
    @IBAction func onClickClinic(_ sender: UIButton) {
        self.performSegue(withIdentifier: StaticString.segueString.showClinicSegue.rawValue, sender: nil)
    }
    
    /// Ver información del administrador
    /// - Parameter sender: Botón de Ver información del administrador
    @IBAction func onClickAdmin(_ sender: UIButton) {
        Services.Service.publicOfficialCertificationService(view: self.view, resp: officialHandler, MNGR_LOGIN_ID: commFunction.comm.preference.string(forKey: StaticString.OfficialInfoPreference.officialID.rawValue) ?? "")
    }
    
    @objc func timeCallback(){
        timmerCount += 1
        self.sendCurrentLocation()
    }
    
    //Actualizar coordenadas de ubicación actual
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let coor = manager.location?.coordinate{
            let location = CLLocationCoordinate2D(latitude: coor.latitude, longitude: coor.longitude)
            application.curntCoordinate = location
            
            if self.mLocation[StaticString.location.latitude.rawValue] == 0.0 && self.mLocation[StaticString.location.longitude.rawValue] == 0.0 {
                //Asignar ubicación inicial
                self.setLocationValue(manager: manager, location: location)
            }else{
                if self.mLocation[StaticString.location.latitude.rawValue] != location.latitude || self.mLocation[StaticString.location.longitude.rawValue] != location.longitude {
                    //Agregar nueva ubicación
                    setLocationValue(manager: manager, location: location)
                }else{
                    //Sin cambio de coordenadas
                }
            }
            
            if isFirst{
                self.isFirst = false
                //Enviar la ubicación actual al iniciar la aplicación por primera vez
                self.sendCurrentLocation()
            }
        }
    }
    
    //MARK: - function
    /// Configuración inicial de locationManager
    func setLocationManager() {
        self.locationManager = CLLocationManager()
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
        self.locationManager.allowsBackgroundLocationUpdates = true //항상 사용을 위해 수정 (백그라운드 콜)
        self.locationManager.pausesLocationUpdatesAutomatically = false
        self.locationManager.startUpdatingLocation()
    }
    
    /// Configurar valor ubicación
    func setLocationValue(manager : CLLocationManager, location : CLLocationCoordinate2D) {
        self.mLocation.updateValue(location.latitude, forKey: StaticString.location.latitude.rawValue)
        self.mLocation.updateValue(location.longitude, forKey: StaticString.location.longitude.rawValue)
        self.mLocation.updateValue(manager.location?.speed ?? 0.0, forKey: StaticString.location.speed.rawValue)
        self.mLocation.updateValue(manager.location?.horizontalAccuracy ?? 0, forKey: StaticString.location.horizontalAccuracy.rawValue)
        self.mLocation.updateValue(manager.location?.verticalAccuracy ?? 0, forKey: StaticString.location.verticalAccuracy.rawValue)
    }
    
    /// Verificar fiabilidad de las coordenadas
    /// - Parameters:
    ///   - user: Actual coordenada del usuario
    ///   - speed: Actual velocidad del usuario
    ///   - hAcc: Precisión horizontal de coordenada actual
    ///   - vAcc: Precisión vertical de coordenada actual
    func trustPOS(user:CLLocationCoordinate2D, speed:Double, hAcc:Double, vAcc:Double){
        //Crear time object (objeto temporizador) para verificar ubicación actual cada 30 segundos
        self.mTimer = Timer.scheduledTimer(timeInterval: 30, target: self, selector: #selector(self.timeCallback), userInfo: nil, repeats: false)
        
        if speed < 200 && hAcc < 66 && vAcc > 0{
            print("trust")
            timmerLoc = user
        }
        
        //Enviar ubicación después de 5 minutos
        if timmerCount >= 30{
            print("===== user coordinate send ======")
            let now = Date()
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
            print("Send Date :  \(dateFormatter.string(from: now as Date))")
            
            if let _ = timmerLoc{
                Services.Service.sendPositionService(resp: sendLocationHandler, isTrust: true, timerClear: timerClear, timmerLoc: self.timmerLoc)
            }else{
                Services.Service.sendPositionService(resp: sendLocationHandler, isTrust: false, timerClear: timerClear, timmerLoc: self.timmerLoc)
            }
        }
    }
    
    /// Actualización del valor de ubicación cada ciclo
    func sendCurrentLocation() {
        print("===== user coordinate send Start \(self.timmerCount) ======")
        
        //Ubicación actual de las personas en auto cuarentena
        let userPoint = CLLocationCoordinate2D(latitude: self.mLocation["lat"]!,
                                               longitude: self.mLocation["lon"]!)
        let speed = self.mLocation["speed"] ?? 0
        let hAcc = self.mLocation["hAcc"] ?? 0
        let vAcc = self.mLocation["vAcc"] ?? 0
        
        self.trustPOS(user: userPoint, speed: speed, hAcc: hAcc, vAcc: vAcc)
    }
    
    /// Reinicio del timer (temporizador)
    func timerClear() {
        self.timmerLoc = nil
        self.timmerCount = 0
        self.mTimer?.invalidate()
        self.timeCallback()
    }
    
    /// Crear pantalla de visualización de información de encargado oficial
    @objc func showOfficialView () {
        let view = CustomAlertView(frame: CGRect(x: 0, y: 0,width: self.view.bounds.maxX, height: self.view.bounds.maxY))
        self.view.addSubview(view)
    }
}

