//
//  CheckListViewController.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/23.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import Alamofire

//Lista de resultados de autodiagnóstico de personas en autocuarentena
class CheckListViewController: UIViewController,UITableViewDelegate, UITableViewDataSource {
    let pList = UserDefaults.standard
    
    //Información de la página leída hasta ahora
    var page = 0
    
    @IBOutlet var tb_main: UITableView!
    @IBOutlet var more: UIButton!
    
    var sectionRow:[String]!
    var respData:[CheckListModel]!
    @IBOutlet var navTitle: UINavigationItem!
    
    var paramISLPRSN_SN:String!
    
    //Vista de configuración de pantalla inicial
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Configuración de encabezado superior
        let label = UILabel()
        label.backgroundColor = .clear
        label.numberOfLines = 2
        label.font = UIFont.boldSystemFont(ofSize: 16.0)
        label.textAlignment = .center
        label.textColor = .black
        label.text = "Self-diagnosis results".localized
        self.navTitle.titleView = label
        
        self.tb_main.delegate = self
        self.tb_main.dataSource = self
        self.tb_main.allowsMultipleSelection = false
        
        //Dar servicio solo 1 vez al inicio
        self.respData = [CheckListModel]()
        self.sectionRow = [String]()
        self.page = 1
        self.more.isHidden = false
        self.searchList()
    }
    
    //Función búsqueda de agregar lista
    @IBAction func showMore(_ sender: UIButton) {
        self.page += 1
        self.searchList()
    }
    
    //Número de encabezados que se mostrarán en la vista de tabla
    func numberOfSections(in tableView: UITableView) -> Int {
        return self.sectionRow.count
    }
    
    //Altura del encabezado
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return tableView.sectionHeaderHeight * 2
    }
    
    //Establecer texto para encabezado
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let view = UIView()
        view.frame = CGRect(x: 0, y: 0, width: 0, height: 0)
        view.backgroundColor = UIColor.white
        
        guard self.sectionRow != nil else {
            return view
        }
        
        print(tableView.frame.width)
        let titleLB = UILabel(frame: CGRect(x: 0, y: tableView.sectionHeaderHeight / 2 , width: tableView.frame.width, height: 30))
        
        if tableView.frame.width > 350 {
            titleLB.text = "∙∙∙∙∙∙∙  \(self.sectionRow[section])   ∙∙∙∙∙∙∙"
        }else{
            titleLB.text = "∙∙∙∙∙  \(self.sectionRow[section])   ∙∙∙∙∙"
        }
        
        //        titleLB.sizeToFit()
        titleLB.font = UIFont.systemFont(ofSize: 22, weight: .bold)
        titleLB.textColor = UIColor.black
        titleLB.textAlignment = .center
        
        
        view.addSubview(titleLB)
        
        return view
    }
    
    //Cantidad que entrarán en la lista general de sección
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        var count = 0
        
        for row in 0 ..< self.respData.count{
            if self.sectionRow[section] == self.respData[row].ymd{
                count += 1
            }
        }
        return count
    }
    
    //Opera para juntar en la misma hilera de sección las cuales clasifica como la misma sección aquellas que estén en la lista del mismo encabezado
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        //En el indexPath.section llama cantidad de retorno de la sección asignada anteriormente
        let cell = tableView.dequeueReusableCell(withIdentifier: "UserCell") as! UserCell
        
        //Jala las fechas de la lista de autodiagnóstico, después verifica la fecha del índice que corresponde en la sección para registrar en la hilera(row)
        let row :CheckListModel = self.respData.filter{$0.ymd  == self.sectionRow[indexPath.section]}[indexPath.row]
        
        if row.COUGH_AT == "Y" || row.DYSPNEA_AT == "Y" || row.PYRXIA_AT == "Y" || row.SORE_THROAT_AT == "Y"{
            cell.mView.backgroundColor = commFunction().UIColorFromRGB(rgbValue: 0xFF5454)
            cell.mView.layer.borderColor = UIColor.systemRed.cgColor
            cell.dateItem.textColor = UIColor.white
        }else{
            cell.mView.backgroundColor = UIColor.white
            cell.mView.layer.borderColor = UIColor.systemBlue.cgColor
            cell.dateItem.textColor = UIColor.black
        }
        
        cell.mView.layer.borderWidth = 1
        cell.mView.layer.cornerRadius = 8
        cell.mView.clipsToBounds = true
        
        cell.dateItem.text = "\(row.date!)"
        
        cell.dateItem.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: 30)
        
        return cell
    }
    
    //Movimiento cuando selecciona una celda
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        guard let userVC = self.storyboard?.instantiateViewController(withIdentifier: "userVC") as? UserViewController else{
            return
        }
        
        guard self.sectionRow != nil, self.respData != nil else {
            self.makeAlert(title: "No information".localized, message: "There is no information on the selected quarantine. ".localized)
            return
        }
        
        let row = self.respData.filter{$0.ymd  == self.sectionRow[indexPath.section]}[indexPath.row]
        userVC.DATE = row.date
        userVC.COUGH_AT = row.COUGH_AT
        userVC.DYSPNEA_AT = row.DYSPNEA_AT
        userVC.PYRXIA_AT = row.PYRXIA_AT
        userVC.SORE_THROAT_AT = row.SORE_THROAT_AT
        userVC.RM = row.RM
        userVC.BDHEAT = row.BDHEAT
        self.present(userVC, animated: true)
    }
    
    //Ir atrás
    @IBAction func goBack(_ sender: UIBarButtonItem) {
        self.presentingViewController?.dismiss(animated: true)
    }
    
    //Servicio búsqueda de listas
    func searchList() {
        Spinner.start(from: self.view)
        let url = serverAddress().main
        
        var PARM = Dictionary<String,String>()
        PARM.updateValue(self.paramISLPRSN_SN, forKey: "ISLPRSN_SN")
        PARM.updateValue("\(self.page)", forKey: "PAGE")
        
        let param = PARM.printJson()
        
        var listService:sendModelEN?
        if let seckey = self.pList.string(forKey: "seckey"), let seckeyVector = self.pList.string(forKey: "seckeyVector"){
            let paramEN = try! param.aesEncrypt(key: seckey, iv: seckeyVector)
            listService = sendModelEN(IFID: "PERU0007", PARM: paramEN)
        }else{
            self.makeAlert(title: "통신실패", message: "사용자 암호키가 없습니다.")
            return
        }
        
        let headers:HTTPHeaders = [
            "Accept":"application/json",
            "Content-Type":"application/json; charset=utf-8"
        ]
        AF.request(url,method: .post, parameters: listService, encoder: JSONParameterEncoder.default, headers: headers)
            .responseJSON(){
                response in
                
                NSLog(response.debugDescription)
                
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
                        self.makeAlert(title: "Search failed".localized, message: msg ?? "List search failed.".localized)
                        Spinner.stop()
                        return
                    }
                    
                    let msg = decryptMSG.instance.msgDecryptSecure(msg: resp, key: self.pList.string(forKey: "seckey")!,iv: self.pList.string(forKey: "seckeyVector")!)
                    
                    let data :[Dictionary<String,Any>] = decryptMSG.instance.decrypt(enData: msg)
                    
                    let totalCount = (json["TOT_PAGE"] as! NSString).intValue
                    print(totalCount)
                    
                    //Si no hay un número total de historial de autodiagnóstico, la pantalla se cierra.
                    if totalCount == 0{
                        let alert = UIAlertController(title: "Search history".localized,
                                                      message: "There is no history.".localized, preferredStyle: .alert)
                        let confirm = UIAlertAction(title: "Confirm".localized, style: .default){
                            (_)in
                            self.presentingViewController?.dismiss(animated: true, completion: nil)
                        }
                        alert.addAction(confirm)
                        self.present(alert, animated: true, completion: nil)
                        Spinner.stop()
                        return
                    }
                    
                    var tempDate : String = ""
                    for model in data {
                        let dict:Dictionary<String,Any> = model
                        
                        
                        if let date = dict["SLFDGNSS_DT_F"] as? String{
                            
                            //Parte para hacer hileras para secciones que solo contienen fechas
                            let YMD = dict["SLFDGNSS_D_F"] as! String
                            if tempDate == "" {
                                tempDate = YMD
                                self.sectionRow.append(tempDate)
                            }else{
                                if YMD == tempDate{
                                    
                                }else{
                                    tempDate = YMD
                                    self.sectionRow.append(tempDate)
                                }
                            }
                            
                            let val = CheckListModel()
                            val.title = "Confirm".localized
                            val.date = date
                            val.ymd = YMD
                            val.COUGH_AT = dict["COUGH_AT"] as? String
                            val.DYSPNEA_AT = dict["DYSPNEA_AT"] as? String
                            val.PYRXIA_AT = dict["PYRXIA_AT"] as? String
                            val.SORE_THROAT_AT = dict["SORE_THROAT_AT"] as? String
                            val.RM = dict["RM"] as? String
                            val.BDHEAT = dict["BDHEAT"] as? String
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
        }
    }
    
    //Alerta común
    func makeAlert(title:String, message:String){
        let mAlert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let cancle = UIAlertAction(title: "Confirm".localized, style: .cancel, handler: nil)
        mAlert.addAction(cancle)
        self.present(mAlert, animated: true, completion: nil)
    }
}
