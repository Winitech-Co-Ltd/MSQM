//
//  StaticString.swift
//  PERU_ios
//
//  Created by 개발용 맥북 on 2020/04/25.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation

class StaticString {
    static let staticString = StaticString()
    
    enum introString : String {
        case bundleVersion = "CFBundleShortVersionString"
        case appLang = "AppleLanguage"
        case appRuningFirst = "isFirst"
    }
    
    enum agreeString : String {
        case title = "Consentimiento para la recopilación\n y uso de información personal e información de ubicación"
        case pdfName = "Política de Privacidad"
    }
    
    enum authString : String {
        case title = "Registrar a una persona en cuarentena"
        case regionalText = "Seleccione su región"
        case provincialText = "Seleccione su provincia"
        case wrongCellPhone = "Número de teléfono celular es equivocado."
        case wrongTutorCellPhone = "Número de teléfono celular del guardián es incorrecto."
    }
    
    enum mapString : String {
        case chooseCoordinate = "ubicación seleccionada"
        case qurantineLocation = "Ubicación de cuarentena"
        case qurantineAdress = "Lugar de cuarentena"
        case locationRegistration = "Registrar\nUbicación"
    }
    
    enum mainString :String {
        case title = "Protección de seguridad\nde autocuarentena"
    }
    
    enum clinicString : String {
        case title = "Verifique la dirección de cuarentena"
        case googleMap = "Google\nmap"
    }
    
    enum diagnoseString : String {
        case selectSymptomsTitle = "Seleccione los síntomas de hoy"
        case feverLabel = "Fiebre (más de 37.5 ℃) o sensación de calor"
        case temperatureText = "Temperatura :"
        case coughLabel = "Tos"
        case soreLabel = "Dolor de garganta"
        case dyspneaLabel = "Disnea (dificultad para respirar)"
        case specificLabel = "Observaciones"
        case specificText = "❖ Los resultados del autodiagnóstico y las especificaciones se enviarán al funcionario público a cargo."
        case temperatureNotInput = "Ingrese la temperatura de su cuerpo"
        case yes = "Si"
        case no = "No"
        case success = "Éxito"
        case fail = "Falló"
        case registration = "Registro exitoso"
        case registrationFail = "Falló registro"
    }
    
    enum diagnoseListString : String {
        case title = "Lista de los resultados de autodiagnóstico"
        case dialogTitle = "Resultados de autodiagnóstico"
        case dialogContent = "No hay lista de autodiagnóstico"
    }
    
    enum editInfoString : String {
        case title = "Editar información de la persona en cuarentena"
    }
    
    enum segueString : String {
        case mainSegue = "goMain"
        case agreeSegue = "goAgree"
        case authToMap = "selectMap"
        case showClinicSegue = "showClinic"
        case settingToMap = "goMap"
    }
    
    enum dialogText : String {
        case confirm = "Confirmar"
        case authAllowTitle = "Aceptar permiso"
        case notificationAllowMessage = "Configuación en el sistema para recibir alarmas de estado de la cuarentena -> Autorización para recibir noticiaciones de la App"
        case locationAllowMessage = "Autorice permiso de ubicación de la App"
        case empty = "No hay Centro de Salud a su alrededor"
        case departmentEmpty = "Falló búsqueda de la Región"
        case provinceEmpty = "Falló búsqueda de la Provincia"
        case districtEmpty = "Falló búsqueda del Distrito"
    }
    
    enum serviceNumber : String {
        case publicKey = "PERUK0001"
        case officialInfoNoEncrypt = "PERU0011"
        case lastDiagnosis = "PERU0008"
        case currentPosition = "PERU0006"
        case diagnosisRegistration = "PERU0009"
        case userInfo = "PERU0004"
        case userInfoModify = "PERU0005"
        case nearByClinc = "PERUPU0001"
        case regional = "PERUC0002"
        case provincial = "PERUC0003"
        case district = "PERUC0004"
        case auth = "PERU0012"
        case diagnosisList = "PERU0007"
        case silencePush = "PERUPUSH0001"
    }
    
    enum preference : String {
        case registerationCheck = "isRegistration"
        case notificationAgree = "notiAgree"
        case tokenChangeBool = "tokenChange"
        case token = "token"
    }
    
    enum OfficialInfoPreference : String {
        case officialName = "MNGR_NM"
        case officialID = "LOGIN_ID"
        case officialSerialNumber = "MNGR_SN"
        case officialSkypeID = "SKYPE_ID"
        case officialWhatsUpID = "WHATSUP_ID"
        case officialPhoneNumber = "MBTLNUM"
        case officeTelNumber = "OFFM_TELNO"
        case officialDepartment = "PSITN_DEPT_NM"
    }
    
    enum userInfoPreference : String {
        case xCoordinate = "ISLLC_XCNTS"
        case yCoordinate = "ISLLC_YDNTS"
        case genderCode = "SEXDSTN_CODE"
        case userSerialCode = "ISLPRSN_SN"
        case userDeviceSerialCode = "TRMNL_SN"
        case currentXcoordinate = "ISLPRSN_XCNTS"
        case currentYcoordinate = "ISLPRSN_YDNTS"
        case baseAdress = "ADDR"
        case etcAdress = "ISLLC_ETC_ADRES"
        case GPSavailable = "GPS_ONOFF"
        case cellPhoneNum = "TELNO"
        case passPortNum = "PSPRNBR"
        case emergencyPhoneNum = "EMGNC_TELNO"
        case countryCode = "NLTY_CODE"
        case citizenID = "INHT_ID"
        case deviceCode = "TRMNL_KND_CODE"
        case departmentCode = "ISLLC_DPRTMNT_CODE"
        case provinceCode = "ISLLC_PRVNCA_CODE"
        case districtCode = "ISLLC_DSTRT_CODE"
    }
    
    enum displayText : String {
        case PERU = "Perú"
        case selectCoutnry = "Por favor seleccione un Nacionalidad"
        case nonInputText = "Falta o inválido la entrada"
        case choose = "escoger"
    }
    
    enum genderCode : String {
        case male = "00101"
        case female = "00102"
    }
    
    enum deviceCode : String {
        case IPhone = "00402"
    }
    
    enum numberType : String {
        case phoneNumber = "0"
        case emrgencyNumber = "1"
    }
    
    enum NotificationName : String {
        case officialContact = "showOfficialContact"
    }
    
    enum serviceErrorText : String {
        case coommunicationError = "Comunicación de red no es fluida. Vuelva a intentar."
        case serviceError = "Error durante la comunicación del servicio. Vuelva a intentar."
    }
    
    enum selfDiagnosisImage : String {
        case imageRed = "btn_main_red.png"
        case imageBlue = "btn_main_blue.png"
    }
    
    enum location : String {
        case latitude = "lat"
        case longitude = "lon"
        case speed = "speed"
        case horizontalAccuracy = "hAcc"
        case verticalAccuracy = "vAcc"
    }
    
    enum encryption : String {
        case SECURE_KEY = "SECURE_KEY"
        case SECURE_VECTOR = "SECURE_VECTOR"
        case PUBLIC_KEY = "PUBLIC_KEY"
        case PUBLIC_VECTOR = "PUBLIC_VECTOR"
    }
    
    enum observerName : String {
        case department = "onClickDepartment"
        case province = "onClickProvince"
        case district = "onClickDistrict"
    }
}
