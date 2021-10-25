//
//  MainViewController.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/22.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import Alamofire
import FirebaseInstanceID


//MARK: -viewController
class MainViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    //Variables de salida
    @IBOutlet var tb_main: UITableView!
    @IBOutlet var more: UIButton!
    @IBOutlet var totalCount: UILabel!
    @IBOutlet var refresh: UIButton!
    @IBOutlet var emptyTitle: UILabel!
    @IBOutlet var manger: UIBarButtonItem!
    @IBOutlet var version: UILabel!
    @IBOutlet var navTitle: UINavigationItem!
    @IBOutlet var mainNavBar: UINavigationBar!
    @IBOutlet var logoutBtn: UIBarButtonItem!
    
    //Variable para almacenar valores
    var pList = UserDefaults.standard
    
    //Ventana de notificación principal común
    var alert : UIAlertController!
    
    //Ventana de notificación de estado
    var stateAL : UIAlertController!
    
    //Modelo de almacenaje de los resultados que se reciben del servidor
    var respData:[UserListModel]!
    
    var page = 1
    var LoginID: String = ""
    var LoginPhoneNumber: String = ""
    
    //Guardar código de estado y botón
    var btnList : [UIButton]!
    var state : [Dictionary<String,String>]!
    var selectStateNum : Int?
    
    //Declarado para enviar la información del usuario de la lista seleccionada por el administrador como alerta
    var selectUserNum:Int = 0
    
    //주정부코드    PSITN_DPRTMNT_CODE
    //시정구코드    PSITN_PRVNCA_CODE
    //구청코드    PSITN_DSTRT_CODE
    var PSITN_DPRTMNT_CODE:String = ""
    var PSITN_PRVNCA_CODE:String = ""
    var PSITN_DSTRT_CODE:String = ""

    //MARK: - tableViewDelegate
    //Establecer el número de secciones en la vista de tabla
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    //Establecer altura para título de sección de vista de tabla
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return tableView.sectionHeaderHeight * 2.5
    }
    
    //Configurar la vista para el encabezado de la sección de la tabla
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        //Crear una vista dedicado al encabezado
        let view = UIView()
        view.frame = CGRect(x: 0, y: 0, width: 0, height: 0)
        view.backgroundColor = UIColor.white

        //Subvista 3 etiquetas en la vista dedicado al encabezado
        let nameLB = UILabel(frame: CGRect(x: 0, y: 0, width: (tableView.frame.width / 4), height: tableView.sectionHeaderHeight * 2.5))
        nameLB.text = "Name".localized
        nameLB.font = UIFont.boldSystemFont(ofSize: 14)
        nameLB.textColor = UIColor.black
        nameLB.textAlignment = .center
        
        let writeLB = UILabel(frame: CGRect(x: nameLB.frame.maxX, y: 0, width: (tableView.frame.width / 4) * 2, height: tableView.sectionHeaderHeight * 2.5))
        writeLB.text = "Self-diagnosis\n(Morning/Afternoon)".localized
        writeLB.numberOfLines = 0
        writeLB.font = UIFont.boldSystemFont(ofSize: 14)
        writeLB.textColor = UIColor.black
        writeLB.textAlignment = .center
        
        let escapeLB = UILabel(frame: CGRect(x: writeLB.frame.maxX, y: 0, width: (tableView.frame.width / 4), height: tableView.sectionHeaderHeight * 2.5))
        escapeLB.text = "Quarantine\narea".localized
        escapeLB.numberOfLines = 0
        escapeLB.font = UIFont.boldSystemFont(ofSize: 14)
        escapeLB.textColor = UIColor.black
        escapeLB.textAlignment = .center
        
        view.addSubview(nameLB)
        view.addSubview(writeLB)
        view.addSubview(escapeLB)
        
        return view
    }
    
    //Número de filas que se incluirán en la sección de la tabla
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        guard let count = self.respData?.count else{
            return 0
        }
        return count
    }
    
    //Establecer datos de cada celda de la tabla
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if self.respData.count == 0 {
            return UITableViewCell()
        }
        
        guard let row = self.respData?[indexPath.row] else{
            return UITableViewCell()
        }
        
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "ListCell") as? ListCell else {
            return UITableViewCell()
        }
        
        cell.tb_escape.numberOfLines = 0
        
        //Cambiar el color de fondo según el estado de la comunicación
        if row.ISLPRSN_NTW_STTUS_CODE! != "4" || row.SLFDGNSS_GUBN_AT == "Y"{
            cell.tb_view.backgroundColor = UIColor.systemRed
            cell.tb_name.textColor = UIColor.white
            cell.tb_write.textColor = UIColor.white
            cell.tb_escape.textColor = UIColor.white
        }else {
            cell.tb_view.backgroundColor = UIColor.white
            cell.tb_name.textColor = UIColor.black
            cell.tb_write.textColor = UIColor.black
            cell.tb_escape.textColor = UIColor.black
        }
        
        //Visualizar en color gris si no es un estado normal
        if row.ISLPRSN_STTUS_CODE != "00301"{
            cell.tb_view.backgroundColor = UIColor.systemGray
            cell.tb_name.textColor = UIColor.white
            cell.tb_write.textColor = UIColor.white
            cell.tb_escape.textColor = UIColor.white
        }
        
        //Aplicar el diseño del borde de cada celda
        cell.tb_view.layer.borderColor = commFunction().UIColorFromRGB(rgbValue: 0x4D4D4D).cgColor
        cell.tb_view.layer.borderWidth = 1.2
        cell.tb_view.layer.cornerRadius = 8
        cell.tb_view.clipsToBounds = true
        
        //Aplicar datos a cada celda
        cell.tb_name.text = "\(row.ISLPRSN_NM ?? "No Name")"
        cell.tb_write.text = "\(row.SLFDGNSS_AM_CODE_NM ?? "") / \(row.SLFDGNSS_PM_CODE_NM ?? "")"
        if row.LC_TRNSMIS_USE_AT == "N" {
            cell.tb_escape.text = "Do not use".localized
        } else {
            cell.tb_escape.text = "\(row.ISLPRSN_NTW_STTUS_CODE_NM ?? "")"
        }
        
        return cell
    }
    
    //Acción de alerta declarada globalmente cuando se selecciona una celda en la vista de tabla
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if self.respData.count == 0 {
            return
        }
        
        guard let row = self.respData?[indexPath.row] else{
            self.makeAlert(title: "No information".localized, message: "There is no information on the selected quarantine.".localized)
            return
        }
        //Llamar a la ventana de notificación de usuario seleccionada
        if row.ITEM_OPEN_AT == "Y"{
            self.alertView(row: indexPath.row)
        }
    }
    
    
    /// Configuración sobre el UILabel común que se muestra al usuario
    /// - Parameters:
    ///   - sender: UILabel
    ///   - title: UILabel set text
    ///   - size: UILabel font size
    func setTextTitle(sender:UILabel, title:String, size:CGFloat) {
        sender.text = title
        sender.font = UIFont.systemFont(ofSize: size)
        sender.sizeToFit()
    }
    
    /// Configuraciones sobre UITextFields común que se muestra al usuario
    /// - Parameters:
    ///   - sender: UITextField
    ///   - text: UITextField set text
    func setTextField(sender:UITextField, text:String) {
        sender.autocapitalizationType = .none
        sender.isEnabled = false
        sender.layer.borderWidth = 1
        sender.layer.borderColor = UIColor.systemBlue.cgColor
        sender.layer.cornerRadius = 10
        sender.text = text
        sender.font = UIFont.systemFont(ofSize: 16)
        sender.setLeftPaddingPoints(10)
        sender.backgroundColor = commFunction().UIColorFromRGB(rgbValue: 0xf0f0f0, alpha: 0.2)
    }
    
    
    /// Cierre de sesión del administrador
    /// - Parameter sender: UIBarButtonItem
    @IBAction func onClickLogOut(_ sender: UIBarButtonItem) {
        self.makeLogOutDialog(title: "log out".localized, message: "Do you want to log out?".localized)
    }
    
    /// Función de Listener(oyente) cuando se hace clic en un número de teléfono
    /// - Parameter sender: 선택한 UIButton
    @objc func callPhone(_ sender:UIButton){
        guard let number = sender.titleLabel?.text else{
            return
        }
        if let phoneCallURL = URL(string: "tel://\(number)") {
            let application:UIApplication = UIApplication.shared
            if (application.canOpenURL(phoneCallURL)) {
                application.open(phoneCallURL, options: [:], completionHandler: nil)
            }
        }
    }
    
    /// Cambio de estado de usuario a Listener (oyente)
    /// - Parameter sender: UIButton
    @objc func stateChange(_ sender:UIButton){
        self.selectStateNum = sender.tag
        
        for btn in self.btnList{
            btn.setBackgroundColor(color: .white, forState: .normal)
            btn.setTitleColor(.black, for: .normal)
        }
        self.btnList[sender.tag].setBackgroundColor(color: commFunction().UIColorFromRGB(rgbValue: 0x0078D7), forState: .normal)
        self.btnList[sender.tag].setTitleColor(.white, for: .normal)
        
    }
    
    /// Registro de estado del usuario
    /// - Registra al servidor el estado seleccionado en stateChange
    func commitState() {
        
        let url = serverAddress().main
        guard let row = self.respData?[self.selectUserNum] else{
            return
        }
        
        guard let selectStateNum = self.selectStateNum else {
            self.makeAlert(title: "Registration failed".localized, message: "After selecting the status, click Apply button".localized)
            return
        }
        
        let code = self.state[selectStateNum]
        
        //관리자일련번호    MNGR_SN
        //격리자일련번호    ISLPRSN_SN
        //격리자상태코드    ISLPRSN_STTUS_CODE
        var PARM = Dictionary<String,String>()
        PARM.updateValue(pList.string(forKey: "MNGR_SN")!, forKey: "MNGR_SN")
        PARM.updateValue(row.ISLPRSN_SN!, forKey: "ISLPRSN_SN")
        PARM.updateValue(code["CODE"]!, forKey: "ISLPRSN_STTUS_CODE")
        
        let param = PARM.printJson()
        
        var listService:sendModelEN?
        if let seckey = self.pList.string(forKey: "seckey"), let seckeyVector = self.pList.string(forKey: "seckeyVector"){
            let paramEN = try! param.aesEncrypt(key: seckey, iv: seckeyVector)
            listService = sendModelEN(IFID: "PERUO0005", PARM: paramEN)
        }else{
            self.makeAlert(title: "통신실패", message: "사용자 암호키가 없습니다.")
            return
        }
        
        let resp : (AFDataResponse<Any>) -> Void = ({
            response in
            Spinner.stop()
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                
                guard let json = value as? [String: Any] else {
                    self.makeAlert(title: "Change failed".localized, message: "Communication and server network is not fluid.".localized)
                    return
                }
                
                guard let code = json["RES_CD"] as? String else {
                    return
                }
                
                if commFunction().isCodeAboutSecureKey(code: code) {
                    commFunction().showPreferClearDialog(vc: self)
                    return
                }
                
                guard let _ = json["RES_DATA"] as? String else {
                    let msg = json["RES_MSG"] as? String
                    self.makeAlert(title: "Change failed".localized, message: msg ?? "Change quarantine status failed.".localized)
                    return
                }
                
                self.stateAL.dismiss(animated: false){
                    self.makeAlert(title: "Change completed".localized, message: "Status of quarantine has changed.".localized)
                    self.page = 1
                    self.searchList()
                }
                
            case .failure(let error):
                print(error)
                self.makeAlert(title: "Communication failure".localized, message: "Communication and server network is not fluid.".localized)
            }
        })
        
        if let listService = listService{
            let req = serviceForm(url: url, parametersEN: listService)
            req.getPostEN(uid: self.pList.string(forKey: "MNGR_SN") ?? "", view: self.view ,response: resp)
        }
    }
    
    /// Función para leer el código de área
    /// - Parameter userCode: País del usuario
    /// - Returns: Código de país al que pertenece
    func searchContry(userCode:String) -> String {
        if userCode == "" {
            return ""
        }
        
        //Obtener la configuración del idioma del teléfono móvil
        let localeID = Locale.preferredLanguages.first
        let deviceLocale = (Locale(identifier: localeID!).languageCode)!
        
        for code in NSLocale.isoCountryCodes  {
            if code == userCode{
                let id = NSLocale.localeIdentifier(fromComponents: [NSLocale.Key.countryCode.rawValue: code])
                if let name = NSLocale(localeIdentifier: deviceLocale).displayName(forKey: NSLocale.Key.identifier, value: id){
                    return name
                }
            }
        }
        return userCode
    }
    
    /// Consulta de lista de personas en autocuarentena
    @objc func searchList() {
        
        let url = serverAddress().main
        //관리자일련번호    MNGR_SN
        //주정부코드    PSITN_DPRTMNT_CODE
        //시정부코드    PSITN_PRVNCA_CODE
        //구청코드    PSITN_DSTRT_CODE
        //격리자이름    ISLPRSN_NM
        //격리자성별코드    SEXDSTN_CODE
        //격리자국적코드    NLTY_CODE
        //페이지(1부터시작)    PAGE
        var PARM = Dictionary<String,String>()
        PARM.updateValue(pList.string(forKey: "MNGR_SN") ?? "", forKey: "MNGR_SN")
        PARM.updateValue(pList.string(forKey: "PSITN_DPRTMNT_CODE") ?? "", forKey: "PSITN_DPRTMNT_CODE")
        PARM.updateValue(pList.string(forKey: "PSITN_PRVNCA_CODE") ?? "", forKey: "PSITN_PRVNCA_CODE")
        PARM.updateValue(pList.string(forKey: "PSITN_DSTRT_CODE") ?? "", forKey: "PSITN_DSTRT_CODE")
        PARM.updateValue("\(self.page)", forKey: "PAGE")
        
        let param = PARM.printJson()
        print(param)
        
        var listService:sendModelEN?
        if let seckey = self.pList.string(forKey: "seckey"), let seckeyVector = self.pList.string(forKey: "seckeyVector"){
            let paramEN = try! param.aesEncrypt(key: seckey, iv: seckeyVector)
            listService = sendModelEN(IFID: "PERUO0002", PARM: paramEN)
        }else{
            self.makeAlert(title: "통신실패", message: "사용자 암호키가 없습니다.")
            return
        }
        
        //Call back de la consulta de lista de personas en autocuarentena
        let resp : (AFDataResponse<Any>) -> Void = ({
            response in
            
            switch response.result {
            case .success(let value):
                
                guard let json = value as? [String: Any] else {
                    self.makeAlert(title: "Search failed".localized, message: "Communication and server network is not fluid.".localized)
                    Spinner.stop()
                    return
                }
                
                guard let code = json["RES_CD"] as? String else {
                    return
                }
                
                if commFunction().isCodeAboutSecureKey(code: code) {
                    commFunction().showPreferClearDialog(vc: self)
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    let msg = json["RES_MSG"] as? String
                    self.makeAlert(title: "Search failed".localized, message: msg ?? "Self-diagnosis search failed.".localized)
                    Spinner.stop()
                    return
                }
                
                //Guardar la cantidad total de las personas en autocuarentena
                let totalCount = (json["TOT_PAGE"] as! NSString).intValue
                
                //En caso de que la cantidad de personas en autocuarentena es cero o nula,
                //notificar un mensaje que no hay persona en autocuarentena
                if totalCount == 0{
                    let alert = UIAlertController(title: "Search result".localized, message: "There is no self-diagnosis list.".localized, preferredStyle: .alert)
                    let cancal = UIAlertAction(title: "Confirm".localized, style: .cancel, handler: nil)
                    alert.addAction(cancal)
                    self.present(alert, animated: true){
                        self.tb_main.reloadData()
                        self.more.isHidden = true
                        self.totalCount.text = "  Self-quarantine numbers: ".localized + "0  "
                        self.emptyTitle.isHidden = false
                    }
                    Spinner.stop()
                    return
                }
                
                //En caso de no Ver más, reinicia la lista
                if self.page == 1 {
                    self.respData = [UserListModel]()
                    self.more.isHidden = false
                    self.emptyTitle.isHidden = true
                }
                
                let msg = decryptMSG.instance.msgDecryptSecure(msg: resp, key: self.pList.string(forKey: "seckey")!,iv: self.pList.string(forKey: "seckeyVector")!)
                
                //Convertir a formato diccionario
                let data :[Dictionary<String,Any>] = decryptMSG.instance.decrypt(enData: msg)
                if data.count < 1 {Spinner.stop();return}
                
                let count = json["TOT_CNT"] as? String
                self.totalCount.text = "  Self-quarantine numbers: ".localized + "\(count ?? "0")  "
                
                for model in data{
                    let dict:Dictionary<String,Any> = model
                    
                    if let _ = dict["ISLPRSN_SN"] as? String{
                        let val = UserListModel()
                        val.ISLPRSN_SN = dict["ISLPRSN_SN"] as? String
                        val.ISLPRSN_NM = dict["ISLPRSN_NM"] as? String
                        val.SEXDSTN_CODE = dict["SEXDSTN_CODE"] as? String
                        val.SEXDSTN_CODE_NM = dict["SEXDSTN_CODE_NM"] as? String
                        val.NLTY_CODE = dict["NLTY_CODE"] as? String
                        val.PSPRNBR = dict["PSPRNBR"] as? String
                        val.INHT_ID = dict["INHT_ID"] as? String
                        val.ISLLC_XCNTS = dict["ISLLC_XCNTS"] as? String
                        val.ISLLC_YDNTS = dict["ISLLC_YDNTS"] as? String
                        val.MNGR_SN = dict["MNGR_SN"] as? String
                        val.TELNO = dict["TELNO"] as? String
                        val.EMGNC_TELNO = dict["EMGNC_TELNO"] as? String
                        val.SLFDGNSS_AM_CODE = dict["SLFDGNSS_AM_CODE"] as? String
                        val.SLFDGNSS_PM_CODE = dict["SLFDGNSS_PM_CODE"] as? String
                        val.SLFDGNSS_AM_CODE_NM = dict["SLFDGNSS_AM_CODE_NM"] as? String
                        val.SLFDGNSS_PM_CODE_NM = dict["SLFDGNSS_PM_CODE_NM"] as? String
                        val.SLFDGNSS_GUBN_AT = dict["SLFDGNSS_GUBN_AT"] as? String
                        val.ISLPRSN_STTUS_CODE = dict["ISLPRSN_STTUS_CODE"] as? String
                        val.ISLPRSN_STTUS_CODE_NM = dict["ISLPRSN_STTUS_CODE_NM"] as? String
                        val.ISLPRSN_XCNTS = dict["ISLPRSN_XCNTS"] as? String
                        val.ISLPRSN_YDNTS = dict["ISLPRSN_YDNTS"] as? String
                        val.CRTFC_NO = dict["CRTFC_NO"] as? String
                        val.DISTANCE = dict["DISTANCE"] as? String
                        val.ISLPRSN_NTW_STTUS_CODE = dict["ISLPRSN_NTW_STTUS_CODE"] as? String
                        val.ISLPRSN_NTW_STTUS_CODE_NM = dict["ISLPRSN_NTW_STTUS_CODE_NM"] as? String
                        val.ITEM_OPEN_AT = dict["ITEM_OPEN_AT"] as? String
                        val.ISLLC_DPRTMNT_CODE = dict["ISLLC_DPRTMNT_CODE"] as? String
                        val.ISLLC_DPRTMNT_CODE_NM = dict["ISLLC_DPRTMNT_CODE_NM"] as? String
                        val.ISLLC_DSTRT_CODE = dict["ISLLC_DSTRT_CODE"] as? String
                        val.ISLLC_DSTRT_CODE_NM = dict["ISLLC_DSTRT_CODE_NM"] as? String
                        val.ISLLC_PRVNCA_CODE = dict["ISLLC_PRVNCA_CODE"] as? String
                        val.ISLLC_PRVNCA_CODE_NM = dict["ISLLC_PRVNCA_CODE_NM"] as? String
                        val.ISLLC_ETC_ADRES = dict["ISLLC_ETC_ADRES"] as? String
                        val.LC_TRNSMIS_USE_AT = dict["LC_TRNSMIS_USE_AT"] as? String
                        val.BRTHDY = dict["BRTHDY"] as? String
                        val.BRTHDY_F = dict["BRTHDY_F"] as? String
                        val.TRMNL_SN = dict["TRMNL_SN"] as? String
                        val.ADDR = dict["ADDR"] as? String
                        val.TRMNL_KND_CODE = dict["TRMNL_KND_CODE"] as? String
                        
                        self.respData.append(val)
                    }
                }
                self.tb_main.reloadData()
                
                
                //Ocultar botón Ver más
                if self.page >= totalCount{
                    self.more.isHidden = true
                }
            case .failure(let error):
                print(error)
                self.makeAlert(title: "Communication failure".localized, message: "Communication and server network is not fluid.".localized)
            }
            Spinner.stop()
        })
        
        if let listService = listService{
            let req = serviceForm(url: url, parametersEN: listService)
            req.getPostEN(uid: self.pList.string(forKey: "MNGR_SN") ?? "", view: self.view ,response: resp)
        }
    }
    
    /// Consulta de información del encargado oficial
    @IBAction func onManager(_ sender: UIBarButtonItem) {
        
        commFunction().searchManager(view: self.view, ECSHG_MNGR_SN: self.pList.string(forKey: "MNGR_SN")!,MNGR_LOGIN_ID: "",ISLPRSN_SN: "" , resp: {
            response in
            
            switch response.result {
            case .success(let value):
                
                guard let json = value as? [String: Any] else {
                    self.makeAlert(title: "Search failed".localized, message: "Communication and server network is not fluid.".localized)
                    Spinner.stop()
                    return
                }
                
                guard let code = json["RES_CD"] as? String else {
                    return
                }
                
                if commFunction().isCodeAboutSecureKey(code: code) {
                    commFunction().showPreferClearDialog(vc: self)
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    let msg = json["RES_MSG"] as? String
                    self.makeAlert(title: "Search failed".localized, message: msg ?? "Information query failed.".localized)
                    Spinner.stop()
                    return
                }
                
                let msg = decryptMSG.instance.msgDecryptSecure(msg: resp, key: self.pList.string(forKey: "seckey")!,iv: self.pList.string(forKey: "seckeyVector")!)
                
                let data :[Dictionary<String,Any>] = decryptMSG.instance.decrypt(enData: msg)
                if data.count < 1 {self.makeAlert(title: "Search failed".localized, message: "There is no information.".localized); return}
                
                let row = data[0]
                
                let alWidth = self.view.frame.width
                
                //Ventana de notificación de información del encargado oficial
                let managerAlert = UIAlertController(title: "\((row["MNGR_NM"] as? String) ?? "")",
                    message: nil , preferredStyle: .actionSheet)
                let cancle = UIAlertAction(title: "Confirm".localized, style: .cancel, handler: nil)
                managerAlert.addAction(cancle)
                
                let container = UIViewController()
                let view = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
                
                managerAlert.setTitlet(color: UIColor.systemBlue)
                managerAlert.setMessage(color: UIColor.systemBlue)
                
                //Configuraciones de mensajes
                let paragraphStyle = NSMutableParagraphStyle()
                paragraphStyle.alignment = NSTextAlignment.left
                
                //Configuración de texto para el mensaje de notificación
                let messageBody = NSMutableAttributedString(string: "Institution : ".localized + "\((row["PSITN_DEPT_NM"] as? String) ?? "")" , attributes: [
                    NSAttributedString.Key.paragraphStyle: paragraphStyle,
                    NSAttributedString.Key.foregroundColor : UIColor.black,
                    NSAttributedString.Key.font : UIFont(name: "Avenir-Roman", size: 16) as Any
                ])
                managerAlert.setValue(messageBody, forKey: "attributedMessage")
                
                container.view = view
                managerAlert.setValue(container, forKey: "contentViewController")
                
                let _lableID = UILabel(frame: CGRect(x: 20, y: 0, width: 120, height: 30))
                let _tfID = UITextField(frame: CGRect(x: _lableID.frame.maxX + 10, y: 0, width: alWidth - 120, height: 30))
                self.managetComm(view: view, lb: _lableID, tf: _tfID, title: "Manager ID".localized, text: ": \((row["LOGIN_ID"] as? String) ?? "")")
                
                let _lablePhone = UILabel(frame: CGRect(x: 20, y: _lableID.frame.maxY + 5, width: _lableID.frame.width, height: 30))
                let _tfPhone = UITextField(frame: CGRect(x: _lablePhone.frame.maxX + 10, y: _lableID.frame.maxY + 5, width: _tfID.frame.width, height: 30))
                self.managetComm(view: view, lb: _lablePhone, tf: _tfPhone, title: "Call No.".localized, text: ": \((row["MBTLNUM"] as? String) ?? "")")

                //Parte para designar el mañano del fondo como la última altura +10 del objeto registrado al final
                container.preferredContentSize = CGSize(width: managerAlert.view.frame.width, height: _lablePhone.frame.maxY + 10)
                self.present(managerAlert, animated: true, completion: nil)
            case .failure(let error):
                print(error)
                self.makeAlert(title: "Communication failure".localized, message: "Communication and server network is not fluid.".localized)
            }
            Spinner.stop()
        })
        
    }
    
    /// Vista común de la consulta de información de encargado oficial
    /// - Parameters:
    ///   - view: Vista principal
    ///   - lb: Etiqueta
    ///   - tf: Campo de texto
    ///   - title: Título de etiqueta
    ///   - text: Título del campo de texto
    func managetComm(view:UIView ,lb:UILabel, tf:UITextField, title:String, text:String) {
        lb.text = title
        lb.font = UIFont.systemFont(ofSize: 17)
        view.addSubview(lb)
        tf.text = text
        tf.font = UIFont.systemFont(ofSize: 17)
        tf.isEnabled = false
        view.addSubview(tf)
    }
    
    // Actualizar
    @IBAction func onRefresh(_ sender: UIButton) {
        self.refresh.isEnabled = false
        self.refresh.backgroundColor = commFunction().UIColorFromRGB(rgbValue: 0xD0D0D0, alpha: 0.5)
        
        //Deja de actualizar durante 5 segundos después de hacer clic en el botón (para evitar consultas frecuentes)
        Timer.scheduledTimer(timeInterval: 5, target: self, selector: #selector(timmerCallBack), userInfo: nil, repeats: false)
        self.page = 1
        self.searchList()
    }
    
    // Configurado para evitar clics repetidos de actualización (retraso de 5 segundos)
    @objc func timmerCallBack(){
        self.refresh.isEnabled = true
        self.refresh.backgroundColor = commFunction().UIColorFromRGB(rgbValue: 0xFFFFFF, alpha: 1)
    }
    
    // Botón de búsqueda (no utilizado actualmente)
    @IBAction func onSearch(_ sender: UIButton) {
        self.page = 1
        self.searchList()
    }
    
    // Si hay más en la listas, buscar lista adicional
    @IBAction func moreSearch(_ sender: Any) {
        self.page += 1
        self.searchList()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
       
        //Establecer el título de navegación en la parte superior
        let label = UILabel()
        label.backgroundColor = .clear
        label.numberOfLines = 2
        label.font = UIFont.boldSystemFont(ofSize: 16.0)
        label.textAlignment = .center
        label.textColor = .black
        label.text = "Self-quarantine management list".localized
        self.navTitle.titleView = label
        
        
        //Configurar la vista de lista
        tb_main.delegate = self
        tb_main.dataSource = self
        tb_main.allowsMultipleSelection = false
        
        //Cambiar dinámicamente la altura de la vista de lista
        tb_main.rowHeight = UITableView.automaticDimension
        tb_main.estimatedRowHeight = 100
        
        //Configuración inicial de la vista inicial
        self.more.isHidden = false
        self.emptyTitle.isHidden = true
        self.searchList()
        
        //Aplicar el borde del botón de actualización
        self.refresh.layer.borderColor = UIColor.black.cgColor
        self.refresh.layer.borderWidth = 1
        self.refresh.layer.cornerRadius = 10
        self.refresh.layer.masksToBounds = true
        self.refresh.setTitleColor(UIColor.white, for: .disabled)
        
        //Configurar la vista de búsqueda total
        self.totalCount.sizeToFit()
        self.totalCount.backgroundColor = UIColor.black
        self.totalCount.textColor = UIColor.white
        self.totalCount.layer.borderColor = UIColor.white.cgColor
        self.totalCount.layer.borderWidth = 1
        self.totalCount.layer.cornerRadius = 10
        self.totalCount.layer.masksToBounds = true
        
        //Configurar la versión de la aplicación en la parte inferior
        let appVersion = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as! String
        self.version.text = "version : ".localized + "\(appVersion)"
        
    }
    
    //Verificar la versión del usuario
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if commFunction().isUpdateAvailable() {
            commFunction().makeVersionUpdateDialog(vc: self)
        }
    }
    
    //Vuelve a abrir el cuadro de diálogo cuando el usuario regresa del diálogo de ubicación u otras acciones
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        //Verifica si el estado de la conexión del usuario en la página es inicial o no
        //Configuración para volver a llamar la alerta de la celda de la tabla seleccionada cuando no es inicial
        if let key = self.pList.string(forKey: "showPage"){
            self.pList.set(nil, forKey: "showPage")
            self.pList.synchronize()
            
            //En el caso de los mapas, siempre se requiere volver a verificar después del registro
            if key == "goMap"{
                //Lista de búsqueda inicial
                self.page = 1
                self.searchList()
            }else if alert != nil{
                //Recuperar la última información seleccionada del usuario (no actualiza la lista completa)
                self.alertView(row: self.selectUserNum)
            }
        }else{
            //Se requiere reiniciar en la parte de volver a consultar la lista
            self.respData = [UserListModel]()
            self.page = 1
        }
        
    }
    
    //Alerta común
    func makeAlert(title:String, message:String){
        let mAlert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let cancle = UIAlertAction(title: "confirm".localized, style: .cancel, handler: nil)
        mAlert.addAction(cancle)
        self.present(mAlert, animated: false, completion: nil)
    }
    
    /// Pantalla que visualiza al seleccionar la lista de usuarios
    /// - Parameter row: Número de secuencia de usuario seleccionado
    func alertView(row:Int) {
        self.selectUserNum = row
        
        //Retorna si no está en la matríz la fila seleccionada
        guard let row = self.respData?[row] else{
            return
        }
        
        //------------------------- Inicia sintaxis de cierre ------------------------------------------
        // Hacer llamada
        let goCallPhone : (UIAlertAction) -> Void = ({
            (_)in
            
            var alWidth:CGFloat!
            if self.view.frame.width < 330 {
                alWidth = self.view.frame.width - 90
            }else{
                alWidth = (self.view.frame.width / 5 ) * 3 - 20
            }
            
            let call = UIAlertController(title: "Make call".localized, message: "Select the number to call.".localized, preferredStyle: .alert)
            let cancle = UIAlertAction(title: "Close".localized, style: .cancel, handler: nil)
            let container = UIViewController()
            let view = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
            container.view = view
            
            //Aplicar vista personalizada a la ventana de notificación
            call.setValue(container, forKey: "contentViewController")
            call.addAction(cancle)
            
            //Crear objetos de pantalla para configurar números de teléfono y números de oficina
            let phoneLB = UILabel()
            let phone = UIButton()
            let image = commFunction().ResizeImage(image: UIImage(named: "phone.png")!, targetSize: CGSize(width: 20, height: 20))
            
            let emergencyLB = UILabel()
            let emergency = UIButton()
            var phoneHeight :CGFloat = view.frame.minY
            
            //Salida de pantalla diferente si tiene número de teléfono
            if let TELNO = row.TELNO{
                phoneLB.frame = CGRect(x: 20, y: phoneHeight, width: 0, height: 20)
                phoneLB.text = "Call No.".localized
                phoneLB.sizeToFit()
                
                phone.frame = CGRect(x: 20, y: phoneLB.frame.maxY + 5, width: alWidth, height: 50)
                commFunction().setButtonUI_White(btn: phone, title: "\(TELNO)")
                phone.tag = 0
                phone.setImage(image, for: .normal)
                phone.imageEdgeInsets = UIEdgeInsets(top: 0, left: phone.frame.size.width - (image.size.width + 15), bottom: 0, right: 0)
                phone.titleEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: image.size.width * 2)
                
                phone.addTarget(self, action: #selector(self.callPhone(_:)), for: .touchUpInside)
                view.addSubview(phoneLB)
                view.addSubview(phone)
                phoneHeight = phone.frame.maxY
            }
            
            if let EMGNC_TELNO = row.EMGNC_TELNO {
                emergencyLB.frame = CGRect(x: 20, y: phoneHeight + 15, width: 0, height: 20)
                emergencyLB.text = "Contact (tutor)".localized
                emergencyLB.sizeToFit()
                
                emergency.frame = CGRect(x: 20, y: emergencyLB.frame.maxY + 5, width: alWidth, height: 50)
                commFunction().setButtonUI_White(btn: emergency, title: "\(EMGNC_TELNO)")
                emergency.tag = 1
                emergency.setImage(image, for: .normal)
                emergency.imageEdgeInsets = UIEdgeInsets(top: 0, left: phone.frame.size.width - (image.size.width + 15), bottom: 0, right: 0)
                emergency.titleEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: image.size.width * 2)
                
                emergency.addTarget(self, action: #selector(self.callPhone(_:)), for: .touchUpInside)
                view.addSubview(emergencyLB)
                view.addSubview(emergency)
                phoneHeight = emergency.frame.maxY
            }
            
            //Ajusta el tamaño de la vista personalizado
            container.preferredContentSize = CGSize(width: self.alert.view.frame.width, height: phoneHeight + 20)
            
            self.present(call, animated: true, completion: nil)
        })
        
        //Mover a ver ubicación
        let goMapView : (UIAlertAction) -> Void = ({
            (_)in
            
            //Parte para especificar la existencia y el valor inicial del controlador de vista de ubicación
            guard let mvc = self.storyboard?.instantiateViewController(withIdentifier: "MapView") as? MapViewController else {
                return
            }
            
            //Verifica si la ubicación del la cuarentena es nula
            if let posX = row.ISLLC_XCNTS, let posY = row.ISLLC_YDNTS{
                mvc.paramISLLC_XCNTS = (posX as NSString).doubleValue
                mvc.paramISLLC_YDNTS = (posY as NSString).doubleValue
            }else{
                mvc.paramISLLC_XCNTS = 0.0
                mvc.paramISLLC_YDNTS = 0.0
            }
            
            //Verifica si la ubicación de la Persona en autocuarentena es nula
            if let userX = row.ISLPRSN_XCNTS, let userY = row.ISLPRSN_YDNTS{
                mvc.paramISLPRSN_XCNTS = (userX as NSString).doubleValue
                mvc.paramISLPRSN_YDNTS = (userY as NSString).doubleValue
            }else{
                mvc.paramISLPRSN_XCNTS = 0.0
                mvc.paramISLPRSN_YDNTS = 0.0
            }
            
            //Radio
            if let radius = row.DISTANCE{
                mvc.paramRaius = (radius as NSString).doubleValue
            }else{
                mvc.paramRaius = 250.0
            }
            
            //Guardar dirección y número de fila seleccionado
            mvc.paramAddress = row.ADDR ?? ""
            mvc.paramAddressETC = row.ISLLC_ETC_ADRES ?? ""
            mvc.paramSelectList = self.selectUserNum
            
            //Diseñado para actualizar la pantalla al volver a la página
            //principal actualizando el almacenamiento local mientras mueve la pantalla
            self.present(mvc, animated: true){
                self.pList.set("goMap", forKey: "showPage")
                self.pList.synchronize()
            }
        })
        
        //Mover a vista de lista de diagnóstico
        //Descripción es la misma que el mapa anterior.
        let goCheckListView : (UIAlertAction) -> Void = ({
            (_)in
            guard let clvc = self.storyboard?.instantiateViewController(withIdentifier: "CheckListView") as? CheckListViewController else {
                return
            }
            
            guard let ISLPRSN_SN = row.ISLPRSN_SN else {
                return
            }
            
            clvc.paramISLPRSN_SN = ISLPRSN_SN
            
            self.present(clvc, animated: true){
                self.pList.set("goList", forKey: "showPage")
                self.pList.synchronize()
            }
        })
        
        //Cambio de estado
        let changeUserState : (UIAlertAction) -> Void = ({
            (_)in
            //Crear una matriz con diccionarios para almacenar el estado dinámico
            self.state = [Dictionary<String,String>]()
            
            //Servicio de consulta de lista de estado
            commFunction().searchState(view: self.view, resp: {
                response in
                
                switch response.result {
                case .success(let value):
                    
                    guard let json = value as? [String: Any] else {
                        self.makeAlert(title: "Change status".localized, message: "Communication and server network is not fluid.".localized)
                        Spinner.stop()
                        return
                    }
                    
                    guard let code = json["RES_CD"] as? String else {
                        return
                    }
                    
                    if commFunction().isCodeAboutSecureKey(code: code) {
                        commFunction().showPreferClearDialog(vc: self)
                        return
                    }
                    
                    guard let resp = json["RES_DATA"] as? String else {
                        let msg = json["RES_MSG"] as? String
                        self.makeAlert(title: "Change status".localized, message: msg ?? "Status list search failed.".localized)
                        Spinner.stop()
                        return
                    }
                    
                    let msg = decryptMSG.instance.msgDecryptSecure(msg: resp, key: self.pList.string(forKey: "seckey")!,iv: self.pList.string(forKey: "seckeyVector")!)
                    //Convertir a formato diccionario
                    let data :[Dictionary<String,Any>] = decryptMSG.instance.decrypt(enData: msg)
                    if data.count < 1 {self.makeAlert(title: "Search failed".localized, message: "There is no status list.".localized); Spinner.stop();return}
                    
                    //Datos recibidos del servidor se extraen para cada fila, se almacenan
                    //en un diccionario y se almacenan en una matriz.
                    for row in data{
                        var dict = Dictionary<String,String>()
                        
                        if type(of: row["CODE"]!) == NSNull.self{
                            dict.updateValue("", forKey: "CODE")
                        }else{
                            dict.updateValue(row["CODE"]! as! String, forKey: "CODE")
                        }
                        dict.updateValue(row["CODE_NM"]! as! String, forKey: "CODE_NM")
                        self.state.append(dict)
                    }
                    
                    //Especificación amplia de la vista interna de la ventana de notificación para responder a cada resolución
                    var alWidth:CGFloat!
                    if self.view.frame.width < 330 {
                        alWidth = self.view.frame.width - 90
                    }else{
                        alWidth = (self.view.frame.width / 5 ) * 3 - 20
                    }
                    let row = self.respData[self.selectUserNum]
                    
                    //Visualizar lista modificable en la pantalla la lista de estado transmitida por el servidor
                    self.stateAL = UIAlertController(title: "Change status of ".localized + "\"\(row.ISLPRSN_NM!)\"", message: "Select a status \nCurrent status: ".localized +  "\(row.ISLPRSN_STTUS_CODE_NM!)", preferredStyle: .alert)
                    
                    //Una vez aceptado, llama al servicio de cambio de estado.
                    let confirm = UIAlertAction(title: "Apply".localized, style: .default){
                        (_)in
                        self.commitState()
                    }
                    
                    let cancle = UIAlertAction(title: "Close".localized, style: .cancel, handler: nil)
                    let container = UIViewController()
                    let view = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
                    container.view = view
                    self.stateAL.setValue(container, forKey: "contentViewController")
                    self.stateAL.addAction(confirm)
                    self.stateAL.addAction(cancle)
                    
                    self.btnList = [UIButton]()
                    for _ in self.state{
                        self.btnList.append(UIButton())
                    }
                    
                    //Parte para agregar lista de estado al botón (adición dinámica)
                    for (index, row) in self.state.enumerated(){
                        if let name = row["CODE_NM"], let _ = row["CODE"] {
                            if index == 0 {
                                self.btnList[index].frame = CGRect(x: 20, y: view.frame.minY, width: alWidth, height: 50)
                            }else{
                                self.btnList[index].frame = CGRect(x: 20, y: self.btnList[index - 1].frame.maxY + 10, width: alWidth, height: 50)
                            }
                            commFunction().setButtonUI_dynamic(btn: self.btnList[index], title: "\(name)")
                            self.btnList[index].tag = index
                            
                            self.btnList[index].addTarget(self, action: #selector(self.stateChange(_:)), for: .touchUpInside)
                            view.addSubview(self.btnList[index])
                        }
                    }
                    
                    container.preferredContentSize = CGSize(width: self.stateAL.view.frame.width, height: self.btnList[self.state.count - 1].frame.maxY + 10)
                    self.present(self.stateAL, animated: false, completion: nil)
                    
                case .failure(let error):
                    print(error)
                    self.makeAlert(title: "Communication failure".localized, message: "Communication and server network is not fluid.".localized)
                }
                Spinner.stop()
            })
        })
        
        //Cambiar Encargado Oficial
        let setChangManager : (UIAlertAction) -> Void = ({
            (_)in
            var alWidth:CGFloat!
            if self.view.frame.width < 330 {
                alWidth = self.view.frame.width - 90
            }else{
                alWidth = (self.view.frame.width / 5 ) * 3 - 20
            }
            let numberTF = UITextField()
            
            let row = self.respData[self.selectUserNum]
            let call = UIAlertController(title: "Change designated manager".localized, message: " The official manager of ".localized + "\(row.ISLPRSN_NM ?? "")" + " has changed.".localized, preferredStyle: .alert)
            let confirm = UIAlertAction(title: "Search official manager".localized, style: .default){
                (_)in
                
                //Retorno en caso de no ingresar el ID del Encargado Oficial
                if numberTF.text == ""{
                    self.makeAlert(title: "No entry".localized, message: "Enter the official manager ID.".localized)
                    return
                }
                
                //Servicio de búsqueda de Encargado Oficial
                commFunction().searchManager(view: self.view, ECSHG_MNGR_SN: "",MNGR_LOGIN_ID: numberTF.text!, ISLPRSN_SN: "", resp: {
                    response in
                    
                    switch response.result {
                    case .success(let value):
                        guard let json = value as? [String: Any] else {
                            self.makeAlert(title: "Search failed".localized, message: "Communication and server network is not fluid.".localized)
                            Spinner.stop()
                            return
                        }
                        
                        guard let code = json["RES_CD"] as? String else {
                            return
                        }
                        
                        if commFunction().isCodeAboutSecureKey(code: code) {
                            commFunction().showPreferClearDialog(vc: self)
                            return
                        }
                        
                        guard let resp = json["RES_DATA"] as? String else {
                            let msg = json["RES_MSG"] as? String
                            self.makeAlert(title: "Search failed".localized, message: msg ?? "Search of manager information failed.".localized)
                            Spinner.stop()
                            return
                        }
                        
                        let msg = decryptMSG.instance.msgDecryptSecure(msg: resp, key: self.pList.string(forKey: "seckey")!,iv: self.pList.string(forKey: "seckeyVector")!)
                        //Convertir a formato diccionario
                        let data :[Dictionary<String,Any>] = decryptMSG.instance.decrypt(enData: msg)
                        if data.count < 1 {self.makeAlert(title: "Search failed".localized, message: "No official manager information.".localized); Spinner.stop(); return}
                        
                        let manager = data[0]
                        
                        let alWidth = self.view.frame.width
                        
                        //Pantalla para mostrar información sobre el Encargado Oficial
                        //que se buscó en una consulta exitosa del Encargado Oficial
                        let managerAlert = UIAlertController(title: "\((manager["MNGR_NM"] as? String) ?? "")",
                            message: nil, preferredStyle: .actionSheet)
                        
                        //Mensaje configurado
                        let paragraphStyle = NSMutableParagraphStyle()
                        paragraphStyle.alignment = NSTextAlignment.left
                        
                        let messageBody = NSMutableAttributedString(string: "Institution : ".localized + "\((manager["PSITN_DEPT_NM"] as? String) ?? "")" , attributes: [
                            NSAttributedString.Key.paragraphStyle: paragraphStyle,
                            NSAttributedString.Key.foregroundColor : UIColor.black,
                            NSAttributedString.Key.font : UIFont(name: "Avenir-Roman", size: 16) as Any
                        ])
                        managerAlert.setValue(messageBody, forKey: "attributedMessage")
                        
                        managerAlert.setTitlet(color: UIColor.black)
                        managerAlert.setMessage(color: UIColor.black)
                        
                        let confirm = UIAlertAction(title: "Change of manager".localized, style: .default){
                            (_)in
                            //Llamar servicio de cambio de Encargado Oficial
                            self.changeManager(row: self.respData[self.selectUserNum], sn: manager["MNGR_SN"] as! String)
                        }
                        
                        //Función adicional de realizar llamada al Encargado Oficial designado
                        //en caso de tener un número de teléfono del Encargado Oficial
                        let callphone = UIAlertAction(title: "Make call".localized, style: .default){
                            (_)in
                            if let number = manager["MBTLNUM"] as? String {
                                if let phoneCallURL = URL(string: "tel://\(number)") {
                                    let application:UIApplication = UIApplication.shared
                                    if (application.canOpenURL(phoneCallURL)) {
                                        application.open(phoneCallURL, options: [:]){
                                            (Bool) -> Void in
                                            self.present(managerAlert, animated: true, completion: nil)
                                        }
                                    }
                                }
                            }
                        }
                        let reSearch = UIAlertAction(title: "Search again".localized, style: .default){
                            (_)in
                            //Función adicional de nueva búsqueda cuando un Encargado Oficial haya hecho una búsqueda incorrecta
                            self.present(call, animated: true, completion: nil)
                        }
                        let cancle = UIAlertAction(title: "Cancle".localized, style: .cancel){
                            (_)in
                            self.alertView(row: self.selectUserNum)
                        }
                        managerAlert.addAction(confirm)
                        

                        if manager["MBTLNUM"] as? String != "-"{
                            managerAlert.addAction(callphone)
                        }
                        managerAlert.addAction(reSearch)
                        managerAlert.addAction(cancle)
                        
                        
                        let container = UIViewController()
                        let view = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
                        container.view = view
                        managerAlert.setValue(container, forKey: "contentViewController")
                        
                        //Parte donde visualiza en la pantalla información sobre el Encargado Oficial buscado
                        let _lableID = UILabel(frame: CGRect(x: 20, y: 0, width: 120, height: 30))
                        let _tfID = UITextField(frame: CGRect(x: _lableID.frame.maxX + 10, y: 0, width: alWidth - 120, height: 30))
                        self.managetComm(view: view, lb: _lableID, tf: _tfID, title: "Manager ID".localized, text: ": \((manager["LOGIN_ID"] as? String) ?? "")")
                        
                        let _lableOffice = UILabel(frame: CGRect(x: 20, y: _tfID.frame.maxY + 5, width: _lableID.frame.width, height: 30))
                        let _tfOffice = UITextField(frame: CGRect(x: _lableOffice.frame.maxX + 10, y: _tfID.frame.maxY + 5, width: _tfID.frame.width, height: 30))
                        self.managetComm(view: view, lb: _lableOffice, tf: _tfOffice, title: "Call No.".localized, text: ": \((manager["MBTLNUM"] as? String) ?? "")")

                        container.preferredContentSize = CGSize(width: managerAlert.view.frame.width, height: _tfOffice.frame.maxY + 10)
                        self.present(managerAlert, animated: true, completion: nil)
                    case .failure(let error):
                        print(error)
                        self.makeAlert(title: "Communication failure".localized, message: "Communication and server network is not fluid.".localized)
                    }
                    Spinner.stop()
                })
            }
            
            let cancle = UIAlertAction(title: "Close", style: .cancel, handler: nil)
            let container = UIViewController()
            let view = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
            container.view = view
            call.setValue(container, forKey: "contentViewController")
            call.addAction(confirm)
            call.addAction(cancle)
            
            
            //Número de autenticación
            let numberLB = UILabel(frame: CGRect(x: 20, y: view.frame.minY, width: 0, height: 20))
            numberTF.frame = CGRect(x: 20, y: numberLB.frame.maxY, width: alWidth, height: 40)
            self.setTextTitle(sender: numberLB, title: "Manager ID".localized, size: 15)
            self.setTextField(sender: numberTF, text: "")
            numberTF.isEnabled = true
            numberTF.placeholder = "Enter manager ID".localized
            view.addSubview(numberLB)
            view.addSubview(numberTF)
            
            container.preferredContentSize = CGSize(width: self.alert.view.frame.width, height: numberTF.frame.maxY + 20)
            self.present(call, animated: true, completion: nil)
        })
        
        //Copiar última ubicación desde el clipboard
        //Copia en el clipboard las últimas coordenadas de la persona en autocuarentena
        let goSaveLocation : (UIAlertAction) -> Void = ({
            (_)in
            let row = self.respData[self.selectUserNum]
            UIPasteboard.general.string = "\(row.ISLPRSN_YDNTS ?? "0.0"),\(row.ISLPRSN_XCNTS ?? "0.0")"
            commFunction().makeToast(controller: self, view: self.view, message: "Copied to the clipboard.".localized, result: nil)
        })
        
        //------------------------- Fin de la declaración de cierre ------------------------------------------
        
        self.alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        
        //cierre
        let checkList = UIAlertAction(title: "View diagnostic results".localized, style: .default, handler: goCheckListView)
        let callPhone = UIAlertAction(title: "Make call".localized, style: .default, handler: goCallPhone)
        let setStatus = UIAlertAction(title: "Change quarantine status".localized, style: .default, handler: changeUserState)
        let goMap = UIAlertAction(title: "View location of quarantine".localized, style: .default, handler: goMapView)
        let setManager = UIAlertAction(title: "Change of manager".localized, style: .default, handler: setChangManager)
        let savePosition = UIAlertAction(title: "Copy location information".localized, style: .default, handler: goSaveLocation)
        let cancle = UIAlertAction(title: "Close".localized, style: .cancel){
            (_)in
        }
        
        //Color del botón inferior
        self.alert.view.tintColor = #colorLiteral(red: 0.8549019694, green: 0.250980407, blue: 0.4784313738, alpha: 1)
        
        var item = ""
        if row.TRMNL_KND_CODE == "00401"{
            item = "android"
        }else{
            item = "ios"
        }
        
        //Configuración del título
        let messageFontTitle = [kCTFontAttributeName: UIFont.systemFont(ofSize: 18, weight: .bold)]
        let messageAttrStringTitle = NSMutableAttributedString(
            string: "\(row.ISLPRSN_NM ?? "") (\(row.ISLPRSN_STTUS_CODE_NM ?? "") / \(item))",
            attributes: messageFontTitle as [NSAttributedString.Key : Any])
        self.alert.setValue(messageAttrStringTitle, forKey: "attributedTitle")
        
        //Configuración de mensaje
        let messageFont = [kCTFontAttributeName: UIFont(name: "Avenir-Roman", size: 16.0)!]
        let messageAttrString = NSMutableAttributedString(string: "\(row.SEXDSTN_CODE_NM ?? "") / \(self.searchContry(userCode: row.NLTY_CODE ?? ""))", attributes: messageFont as [NSAttributedString.Key : Any])
        self.alert.setValue(messageAttrString, forKey: "attributedMessage")
        
        //Agregar vista
        self.alert.addAction(checkList)
        self.alert.addAction(callPhone)
        self.alert.addAction(setStatus)
        self.alert.addAction(goMap)
        self.alert.addAction(setManager)
        self.alert.addAction(savePosition)
        self.alert.addAction(cancle)

        
        //Informacion del usuario
        let _call = row.TELNO ?? ""
        let _contact = row.EMGNC_TELNO ?? ""
        let _addr = row.ADDR ?? ""
        
        //Visualiza solo la parte con la información de cada usuario
        var messageData = "\(row.SEXDSTN_CODE_NM ?? "") / \(row.BRTHDY ?? "") / \(self.searchContry(userCode: row.NLTY_CODE ?? ""))"
        if _call != "" {
            messageData = messageData + "\n" + "Call No.".localized + " : \(_call)"
        }
        if _contact != ""{
            messageData = messageData + "\n" + "Contact (tutor)".localized + " : \(_contact)"
        }
        
        if _addr != "" {
            messageData = messageData + "\nAddress : ".localized+"\(_addr)"
        }
        
        //Configuración de mensaje
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.alignment = NSTextAlignment.left
        
        let messageBody = NSMutableAttributedString(string: messageData , attributes: [
            NSAttributedString.Key.paragraphStyle: paragraphStyle,
            NSAttributedString.Key.foregroundColor : UIColor.black,
            NSAttributedString.Key.font : UIFont(name: "Avenir-Roman", size: 16) as Any
        ])
        self.alert.setValue(messageBody, forKey: "attributedMessage")
        self.alert.setValue(nil, forKey: "contentViewController")
    
        //Designar ubicación de contenedor
        self.present(self.alert, animated: true, completion: {() -> Void in
            self.alert.view.superview?.subviews[0].isUserInteractionEnabled = false
        })
    }
    
    
    //Sacar afuera servicio de modificación de Encargado Oficial
    func changeManager(row:UserListModel, sn:String){
        //Servicio de cambio de Administrador
        commFunction().changeManager(view: self.view, MNGR_SN: sn, ISLPRSN_SN: row.ISLPRSN_SN!, resp: {
            response in
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                
                guard let json = value as? [String: Any], let message = json["RES_MSG"] as? String,
                    let code = json["RES_CD"] as? String else {
                        self.makeAlert(title: "", message: "Change of official manager failed.".localized)
                        Spinner.stop()
                        return
                }
                
                if commFunction().isCodeAboutSecureKey(code: code) {
                    commFunction().showPreferClearDialog(vc: self)
                    return
                }
                
                if code == "100"{
                    //Volve a buscar lista completa
                    self.page = 1
                    
                    let mAlert = UIAlertController(title: nil , message: message, preferredStyle: .alert)
                    let cancle = UIAlertAction(title: "confirm".localized, style: .cancel){
                        (_)in
                        self.searchList()
                    }
                    mAlert.addAction(cancle)
                    self.present(mAlert, animated: true, completion: nil)
                    
                }else{
                    let mAlert = UIAlertController(title: "Successful registration".localized, message: message, preferredStyle: .alert)
                    let cancle = UIAlertAction(title: "confirm".localized, style: .cancel){
                        (_)in
                        self.alertView(row: self.selectUserNum)
                    }
                    mAlert.addAction(cancle)
                    self.present(mAlert, animated: true, completion: nil)
                }
            case .failure(let error):
                self.makeAlert(title: "Communication failure".localized, message: "Change of official manager failed.".localized)
                print(error)
            }
            Spinner.stop()
        })
    }
    
    /// Cierre de sesión del usuario
    /// - Parameters:
    ///   - title: Título de cierre de sesión
    ///   - message: Mensaje de cierre de sesión
    func makeLogOutDialog(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let ok = UIAlertAction(title: "Confirm".localized, style: .default) { (ok) in
            self.LogOutService()
        }
        let cancel = UIAlertAction(title: "Cancle".localized, style: .cancel, handler: nil)
        
        alert.addAction(ok)
        alert.addAction(cancel)
        self.present(alert, animated: true, completion: nil)
    }
    
    //Servicio de cierre de sesión
    func LogOutService() {
        let url = serverAddress().main
        
        //이름       MNGR_NM
        //로그인ID    LOGIN_ID
        //핸드폰번호  MBTLNUM
        //PUSHID    PUSHID
        var PARM = Dictionary<String,String>()
        PARM.updateValue(self.pList.string(forKey: "MNGR_SN")!, forKey: "MNGR_SN")
        PARM.updateValue(self.pList.string(forKey: "LOGIN_ID")!, forKey: "LOGIN_ID")
        PARM.updateValue("N", forKey: "LOGIN_AT")
        
        //******** Parte que encripta el parámetro existente
        let param = PARM.printJson()
        print(param)
        
        var listService:sendModelEN?
        if let seckey = self.pList.string(forKey: "seckey"), let seckeyVector = self.pList.string(forKey: "seckeyVector"){
            let paramEN = try! param.aesEncrypt(key: seckey, iv: seckeyVector)
            listService = sendModelEN(IFID: "PERUO0006", PARM: paramEN)
        }else{
            self.makeAlert(title: "통신실패", message: "사용자 암호키가 없습니다.")
            return
        }
        //***************************************************************
        
        
        //Call back de cierre de sesión
        let resp : (AFDataResponse<Any>) -> Void = ({
            response in
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                
                guard let json = value as? [String: Any] else {
                    self.makeAlert(title: "log out fail".localized, message: "Communication and server network is not fluid.")
                    Spinner.stop()
                    return
                }
                
                guard let resp = json["RES_CD"] as? String else {
                    let msg = json["RES_MSG"] as? String
                    self.makeAlert(title: "log out fail".localized, message: msg ?? "There is no information.".localized)
                    Spinner.stop()
                    return
                }
                
                if commFunction().isCodeAboutSecureKey(code: resp) {
                    commFunction().showPreferClearDialog(vc: self)
                    return
                }
                
                if resp == "100" {
                    //Para aplicar una función de inicio de sesión, se requiere un separador clasificado de cierre de sesión
                    self.dismiss(animated: true, completion: nil)
                } else {
                    self.makeAlert(title: "log out fail".localized, message: json["RES_MSG"] as? String ?? "There is no information.".localized)
                }
                
            case .failure(let error):
                print(error)
                self.makeAlert(title: "Communication failure".localized, message: "Communication and server network is not fluid.".localized)
            }
            Spinner.stop()
        })
        
        if let listService = listService{
            let req = serviceForm(url: url, parametersEN: listService)
            req.getPostEN(uid: self.pList.string(forKey: "MNGR_SN") ?? "", view: self.view ,response: resp)
        }
    }
    
}
