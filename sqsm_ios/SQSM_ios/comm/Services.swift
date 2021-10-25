//
//  Services.swift
//  SQSM_ios
//
//  Created by 개발용 맥북 on 2020/04/14.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import Alamofire
import CoreLocation

//MARK: -
class Services {
    typealias response = (AFDataResponse<Any>) -> Void
    static let Service = Services()
    private let preference = UserDefaults.standard
    
    private var publicOfficialHandler : response!
    
    let url = serverAddress().main
    let headers:HTTPHeaders = [
        "Accept":"application/json",
        "Content-Type":"application/json; charset=utf-8"
    ]
    
    //Servicio de solicitud de clave pública
    func getPublicKeyService(view : UIView, resp : @escaping response) {
        let PARM = Dictionary<String,String>()
        let param = PARM.printJson()
        
        requestService(param: param, IFID: StaticString.serviceNumber.publicKey.rawValue, resp: resp, view: view)
    }
    
    //MARK: - Officer INFO Search Service
    /// Servicio de importación de información oficial dedicado
    /// - Parameters:
    ///   - view: view
    ///   - MNGR_LOGIN_ID: ID del encargado oficial
    ///   - resp: llamar de vuelta
    func publicOfficialCertificationService(view : UIView ,resp : @escaping response, MNGR_LOGIN_ID: String) {
        var PARM = Dictionary<String, String>()
        PARM.updateValue(MNGR_LOGIN_ID, forKey: "MNGR_LOGIN_ID")
        
        let param = PARM.printJson()
        print(param)
        requestServicePublic(param: param, IFID: StaticString.serviceNumber.officialInfoNoEncrypt.rawValue, resp: resp, view: view)
    }
    
    //MARK: - Officer INFO Search Service
    /// Servicio de importación de información oficial dedicado
    /// - Parameters:
    ///   - view: view
    ///   - MNGR_LOGIN_ID: ID del encargado oficial
    ///   - resp: llamar de vuelta
//    func publicOfficialInfoService(view : UIView ,resp : @escaping response, MNGR_LOGIN_ID: String) {
//        var PARM = Dictionary<String, String>()
//        PARM.updateValue(MNGR_LOGIN_ID, forKey: "MNGR_LOGIN_ID")
//
//        let param = PARM.printJson()
//        print(param)
//        requestServiceSecure(param: param, IFID: StaticString.serviceNumber.officialInfo.rawValue, resp: resp, view: view)
//    }
    
    //MARK: - User Registration Service
    /// Servicio de registro de información del usuario
    /// - Parameters:
    ///   - vc: viewController
    ///   - userInfo: Información ingresada por el usuario
    ///   - resp: llamar de vuelta
    func userRegistrationService(vc: UIViewController, userInfo: UserInfo, resp : @escaping response) {
        var PARM = Dictionary<String,Any>()
        //Valor requerido
        PARM.updateValue(userInfo.INHT_ID ?? "", forKey: "INHT_ID")
        PARM.updateValue(userInfo.ISLPRSN_NM ?? "", forKey: "ISLPRSN_NM")
        PARM.updateValue(userInfo.BRTHDY ?? "", forKey: "BRTHDY")
        PARM.updateValue(userInfo.SEXDSTN_CODE ?? "", forKey: "SEXDSTN_CODE")
        PARM.updateValue(userInfo.NLTY_CODE ?? "", forKey: "NLTY_CODE")
        PARM.updateValue(userInfo.TELNO ?? "", forKey: "TELNO")
        PARM.updateValue(userInfo.TRMNL_KND_CODE ?? "", forKey: "TRMNL_KND_CODE")
        PARM.updateValue(userInfo.TRMNL_NM ?? "", forKey: "TRMNL_NM")
        PARM.updateValue(userInfo.PUSHID ?? "", forKey: "PUSHID")
        PARM.updateValue(userInfo.USE_LANG ?? "", forKey: "USE_LANG")
        PARM.updateValue(userInfo.CRTFC_NO ?? "", forKey: "CRTFC_NO")
        PARM.updateValue(userInfo.BRTHDY ?? "", forKey: "BRTHDY")
        PARM.updateValue(userInfo.ECSHG_MNGR_SN ?? "", forKey: "ECSHG_MNGR_SN")
        
        //Valor opcional
        PARM.updateValue(userInfo.EMGNC_TELNO ?? "", forKey: "EMGNC_TELNO")
        PARM.updateValue(userInfo.PSPRNBR ?? "", forKey: "PSPRNBR")
        
        //Valor relacionado con la dirección
        PARM.updateValue(userInfo.ISL_SE_CODE ?? "", forKey: "ISL_SE_CODE")
        PARM.updateValue(userInfo.ISLLC_DPRTMNT_CODE ?? "", forKey: "ISLLC_DPRTMNT_CODE")
        PARM.updateValue(userInfo.ISLLC_PRVNCA_CODE ?? "", forKey: "ISLLC_PRVNCA_CODE")
        PARM.updateValue(userInfo.ISLLC_DSTRT_CODE ?? "", forKey: "ISLLC_DSTRT_CODE")
        PARM.updateValue(userInfo.ISLLC_ETC_ADRES ?? "", forKey: "ISLLC_ETC_ADRES")
        PARM.updateValue(userInfo.ISLLC_XCNTS ?? "", forKey: "ISLLC_XCNTS")
        PARM.updateValue(userInfo.ISLLC_YDNTS ?? "", forKey: "ISLLC_YDNTS")
        
        let param = PARM.printJson()
        print(param)
        requestServicePublic(param: param, IFID: StaticString.serviceNumber.auth.rawValue, resp: resp, view: vc.view)
    }
    
    /// Servicio de actualización de información del usuario
    /// - Parameters:
    ///   - ISLPRSN_SN: Número de serie del usuario
    ///   - TRMNL_SN: Número de serie del terminal de usuario
    ///   - view: view
    ///   - resp: llamar de vuelta
    func userInfoService(view : UIView, resp : @escaping response, ISLPRSN_SN: String, TRMNL_SN: String) {
        var PARM = Dictionary<String, String>()
        PARM.updateValue(ISLPRSN_SN, forKey: StaticString.userInfoPreference.userSerialCode.rawValue)
        PARM.updateValue(TRMNL_SN, forKey: StaticString.userInfoPreference.userDeviceSerialCode.rawValue)
        
        let param = PARM.printJson()
        print(param)
        requestServiceSecure(param: param, IFID: StaticString.serviceNumber.userInfo.rawValue, resp: resp, view: view)
    }
    
    /// Servicio de actualización de información del usuario
    func userModifyService(view : UIView, resp : @escaping response, userInfo: UserInfo) {
        var PARM = Dictionary<String,String>()
        PARM.updateValue(preference.string(forKey: StaticString.userInfoPreference.cellPhoneNum.rawValue) ?? "", forKey: StaticString.userInfoPreference.cellPhoneNum.rawValue)
        PARM.updateValue(preference.string(forKey: StaticString.userInfoPreference.userSerialCode.rawValue) ?? "", forKey: StaticString.userInfoPreference.userSerialCode.rawValue)
        PARM.updateValue(preference.string(forKey: StaticString.userInfoPreference.userDeviceSerialCode.rawValue) ?? "", forKey: StaticString.userInfoPreference.userDeviceSerialCode.rawValue)
        PARM.updateValue(StaticString.deviceCode.IPhone.rawValue, forKey:
            StaticString.userInfoPreference.deviceCode.rawValue)
        
        PARM.updateValue(preference.string(forKey: StaticString.userInfoPreference.departmentCode.rawValue) ?? "", forKey: StaticString.userInfoPreference.departmentCode.rawValue)
        PARM.updateValue(preference.string(forKey: StaticString.userInfoPreference.provinceCode.rawValue) ?? "", forKey: StaticString.userInfoPreference.provinceCode.rawValue)
        PARM.updateValue(preference.string(forKey: StaticString.userInfoPreference.districtCode.rawValue) ?? "", forKey: StaticString.userInfoPreference.districtCode.rawValue)
        
        PARM.updateValue(userInfo.ISLLC_ETC_ADRES ?? "", forKey: StaticString.userInfoPreference.etcAdress.rawValue)
        PARM.updateValue(userInfo.EMGNC_TELNO ?? "", forKey: StaticString.userInfoPreference.emergencyPhoneNum.rawValue)
        
        let param = PARM.printJson()
        print(param)
        requestServiceSecure(param: param, IFID: StaticString.serviceNumber.userInfoModify.rawValue, resp: resp, view: view)
    }
    
    //MARK: - Servicio para llamar fecha y hora del último autodiagnóstico
    /// Servicio para llamar fecha y hora del último autodiagnóstico
    /// - Parameters:
    ///   - vc: viewController
    ///   - resp: llamar de vuelta
    func lastDiagnosisService(vc : UIViewController, resp : @escaping response) {
        Spinner.start(from: vc.view)
        
        guard let ISLPRSN_SN = self.preference.string(forKey: StaticString.userInfoPreference.userSerialCode.rawValue) else {
            print("ERROR")
            return
        }
        
        var PARM = Dictionary<String,String>()
        //Valor requerido
        PARM.updateValue(ISLPRSN_SN, forKey: StaticString.userInfoPreference.userSerialCode.rawValue)
        PARM.updateValue(self.preference.string(forKey: StaticString.userInfoPreference.userDeviceSerialCode.rawValue) ?? "", forKey: StaticString.userInfoPreference.userDeviceSerialCode.rawValue)
        
        let param = PARM.printJson()
        print(param)
        requestServiceSecure(param: param, IFID: StaticString.serviceNumber.lastDiagnosis.rawValue, resp: resp, view: vc.view)
    }
    
    
    //MARK: - currentLocation send service
    /// Servicio de transferencia de ubicación actual
    /// - Parameters:
    ///   - isTrust: Confiabilidad del valor de la ubicación
    ///   - timerClear: Función de inicialización del timer (temporizador)
    ///   - timmerLoc: Valor de ubicación
    ///   - resp: llamar de vuelta
    func sendPositionService(resp : @escaping response, isTrust:Bool, timerClear :@escaping () -> Void, timmerLoc : CLLocationCoordinate2D?){
        guard let ISLPRSN_SN = self.preference.string(forKey: StaticString.userInfoPreference.userSerialCode.rawValue),
            let TRMNL_SN = self.preference.string(forKey: StaticString.userInfoPreference.userDeviceSerialCode.rawValue) else {
                print("ERROR")
                timerClear()
                return
        }
        
        var PARM = Dictionary<String,String>()
        PARM.updateValue(ISLPRSN_SN, forKey: StaticString.userInfoPreference.userSerialCode.rawValue)
        PARM.updateValue(TRMNL_SN, forKey: StaticString.userInfoPreference.userDeviceSerialCode.rawValue)
        if !isTrust{
            PARM.updateValue("0.0", forKey: StaticString.userInfoPreference.currentXcoordinate.rawValue)
            PARM.updateValue("0.0", forKey: StaticString.userInfoPreference.currentYcoordinate.rawValue)
        }else{
            PARM.updateValue((timmerLoc?.longitude.string)!, forKey: "ISLPRSN_XCNTS")
            PARM.updateValue((timmerLoc?.latitude.string)!, forKey: "ISLPRSN_YDNTS")
        }
        
        if CLLocationManager.authorizationStatus() != .authorizedAlways && CLLocationManager.authorizationStatus() != .authorizedWhenInUse {
            PARM.updateValue("OFF", forKey: StaticString.userInfoPreference.GPSavailable.rawValue)
        } else {
            PARM.updateValue("ON", forKey: StaticString.userInfoPreference.GPSavailable.rawValue)
        }
        
        let param = PARM.printJson()
        print(param)
        requestServiceSecure(param: param, IFID: StaticString.serviceNumber.currentPosition.rawValue, resp: resp)
    }
    
    /// Servicio para obtener valor de Región
    func departmentService(view : UIView, resp : @escaping response) {
        let PARM = Dictionary<String,String>()
        let param = PARM.printJson()
        requestServicePublic(param: param, IFID: StaticString.serviceNumber.regional.rawValue, resp: resp, view: view)
    }
    
    /// Servicio para obtener valor de Provincia
    /// - Parameter DPRTMNT_CODE: Valor del código de Región seleccionado por el usuario
    func provinceService(view : UIView,resp : @escaping response, DPRTMNT_CODE: String) {
        var PARM = Dictionary<String, String>()
        PARM.updateValue(DPRTMNT_CODE, forKey: "DPRTMNT_CODE")
        
        let param = PARM.printJson()
        print(param)
        requestServicePublic(param: param, IFID: StaticString.serviceNumber.provincial.rawValue, resp: resp, view: view)
    }
    
    /// Servicio para obtener valor de Distrito
    /// - Parameters:
    ///   - DEPARTMENT_CODE: Valor de Región seleccionado por el usuario
    ///   - PROVINCE_CODE: Valor de Provincia seleccionado por el usuario
    func districtService(view : UIView, resp : @escaping response, DEPARTMENT_CODE: String, PROVINCE_CODE: String) {
        var PARM = Dictionary<String, String>()
        PARM.updateValue(DEPARTMENT_CODE, forKey: "DPRTMNT_CODE")
        PARM.updateValue(PROVINCE_CODE, forKey: "PRVNCA_CODE")
        
        let param = PARM.printJson()
        print(param)
        requestServicePublic(param: param, IFID: StaticString.serviceNumber.district.rawValue, resp: resp, view: view)
    }
    
    func registerDiagnoseService(view : UIView, resp : @escaping response, PYRXIA_AT : String, COUGH_AT : String, SORE_THROAT_AT : String, DYSPNEA_AT : String, BDHEAT : String, RM : String) {
        var PARM = Dictionary<String,String>()
        PARM.updateValue(preference.string(forKey: "ISLPRSN_SN") ?? "", forKey: "ISLPRSN_SN")
        PARM.updateValue(PYRXIA_AT , forKey: "PYRXIA_AT")
        PARM.updateValue(COUGH_AT, forKey: "COUGH_AT")
        PARM.updateValue(SORE_THROAT_AT, forKey: "SORE_THROAT_AT")
        PARM.updateValue(DYSPNEA_AT, forKey: "DYSPNEA_AT")
        PARM.updateValue(BDHEAT , forKey: "BDHEAT")
        PARM.updateValue(preference.string(forKey: "TRMNL_SN") ?? "", forKey: "TRMNL_SN")
        PARM.updateValue(RM, forKey: "RM")
        print(PARM)
        
        let param = PARM.printJson()
        requestServiceSecure(param: param, IFID: StaticString.serviceNumber.diagnosisRegistration.rawValue, resp: resp, view: view)
    }
    
    /// Servicio de consulta de lista de autodiagnóstico
    func searchDiagnoseListService(view : UIView, resp : @escaping response, page : Int) {
        guard UserDefaults.standard.string(forKey: "ISLPRSN_SN") != nil else {
            print("ERROR")
            return
        }
        
        var PARM = Dictionary<String,String>()
        PARM.updateValue(preference.string(forKey: "ISLPRSN_SN") ?? "", forKey: "ISLPRSN_SN")
        PARM.updateValue(preference.string(forKey: "TRMNL_SN") ?? "", forKey: "TRMNL_SN")
        PARM.updateValue("\(page)", forKey: "PAGE")
        
        let param = PARM.printJson()
        requestServiceSecure(param: param, IFID: StaticString.serviceNumber.diagnosisList.rawValue, resp: resp, view: view)
    }
    
    /// Servicio de consulta Centro de Salud cercano
    func searchClinicService(view : UIView, resp : @escaping response) {
        var PARM = Dictionary<String,String>()
        PARM.updateValue(commFunction.comm.preference.string(forKey: StaticString.userInfoPreference.xCoordinate.rawValue)!, forKey: "CURRENT_X")
        PARM.updateValue(commFunction.comm.preference.string(forKey: StaticString.userInfoPreference.yCoordinate.rawValue)!, forKey: "CURRENT_Y")
        
        let param = PARM.printJson()
        print(param)
        requestServiceSecure(param: param, IFID: StaticString.serviceNumber.nearByClinc.rawValue, resp: resp, view: view)
    }
    
    //MARK:- Petición de servicio (Spinner x, cifrado de clave privada)
    private func requestServiceSecure(param : String, IFID : String, resp : @escaping response) {
        let key =  preference.string(forKey: StaticString.encryption.SECURE_KEY.rawValue)!
        let vector = preference.string(forKey: StaticString.encryption.SECURE_VECTOR.rawValue)!
        let paramEN = try! param.aesEncrypt(key: key, vector: vector)
        let uid = preference.string(forKey: "ISLPRSN_SN") ?? ""
        let listService = sendModelEN(IFID: IFID, PARM: paramEN)
        print(listService)
        serviceForm(url: url, parametersEN: listService).getPostEN(uid: uid, response: resp)
    }
    
    //MARK:- Petición de servicio (Spinner o, cifrado de clave privada)
    private func requestServiceSecure(param : String, IFID : String, resp : @escaping response,
                                      view : UIView) {
        let key =  preference.string(forKey: StaticString.encryption.SECURE_KEY.rawValue)!
        let vector = preference.string(forKey: StaticString.encryption.SECURE_VECTOR.rawValue)!
        let paramEN = try! param.aesEncrypt(key: key, vector: vector)
        let uid = preference.string(forKey: "ISLPRSN_SN") ?? ""
        let listService = sendModelEN(IFID: IFID, PARM: paramEN)
        print(listService)
        serviceForm(url: url, parametersEN: listService).getPostEN(uid: uid, view: view, response: resp)
    }
    
    //MARK:- Petición de servicio (Spinner x, cifrado de clave pública)
    private func requestServicePublic(param : String, IFID : String, resp : @escaping response) {
        let key = decryptMSG.shared.key128
        let vector = decryptMSG.shared.iv
        let paramEN = try! param.aesEncrypt(key: key, vector: vector)
        let listService = sendModelEN(IFID: IFID, PARM: paramEN)
        print(listService)
        serviceForm(url: url, parametersEN: listService).getPost(response: resp)
    }
    
    //MARK:- Petición de servicio (Spinner o, cifrado de clave pública)
    private func requestServicePublic(param : String, IFID : String, resp : @escaping response,
                                      view : UIView) {
        let key = decryptMSG.shared.key128
        let vector = decryptMSG.shared.iv
        let paramEN = try! param.aesEncrypt(key: key, vector: vector)
        let listService = sendModelEN(IFID: IFID, PARM: paramEN)
        print(listService)
        serviceForm(url: url, parametersEN: listService).getPostEN(view: view, response: resp)
    }
    
    //MARK:- Petición de servicio (Spinner o, Obtenga una clave de cifrado)
    private func requestServiceOnlySecureKey(param : String, IFID : String, resp : @escaping response,
                                             view : UIView) {
        let key = decryptMSG.shared.key128
        let vector = decryptMSG.shared.iv
        let paramEN = try! param.aesEncrypt(key: key, vector: vector)
        let listService = sendModelEN(IFID: IFID, PARM: paramEN)
        let uid = preference.string(forKey: "ISLPRSN_SN") ?? ""
        print(listService)
        serviceForm(url: url, parametersEN: listService).getPostEN(uid: uid, view: view, response: resp)
    }
    
    //MARK:- Petición de servicio (Spinner o)
    private func requestService(param : String, IFID : String, resp : @escaping response,
                                view : UIView) {
        let listService = sendModelEN(IFID: IFID, PARM: param)
        print(listService)
        serviceForm(url: url, parametersEN: listService).getPostEN(view: view, response: resp)
    }
    
    //MARK:- Petición de servicio (Spinner x)
    private func requestService(param : String, IFID : String, resp : @escaping response) {
        let listService = sendModelEN(IFID: IFID, PARM: param)
        print(listService)
        serviceForm(url: url, parametersEN: listService).getPost(response: resp)
    }
}
