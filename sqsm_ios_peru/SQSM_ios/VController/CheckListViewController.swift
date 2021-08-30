//
//  CheckListViewController.swift
//  TestSampleApp
//
//  Created by wini on 2020/02/13.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import Alamofire

class CheckListViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    typealias response = (AFDataResponse<Any>) -> Void
    
    @IBOutlet var tb_main: UITableView!
    //MARK: - outlet
    
    @IBOutlet var moreBtn: UIButton!
    @IBOutlet var naviItem: UINavigationItem!
    
    //MARK: - variable
    
    //Información de la página leída hasta ahora
    var page = 0
    var sectionRow:[String]!
    var respData:[CheckListCell]!
    var tempDate : String = ""
    
    private var diagnoseListHandler : response!
    
    //MARK: - lifeCycle
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tb_main.delegate = self
        self.tb_main.dataSource = self
        
        self.respData = [CheckListCell]()
        self.sectionRow = [String]()
        self.page = 1
        self.moreBtn.isHidden = false
        
        diagnoseListHandler = ({
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
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                
                if commFunction.comm.isSecureKeyExist() {
                    let key = commFunction.comm.preference.string(forKey: StaticString.encryption.SECURE_KEY.rawValue)!
                    let vector = commFunction.comm.preference.string(forKey: StaticString.encryption.SECURE_VECTOR.rawValue)!
                    let msg = decryptMSG.shared.msgDecryptSecure(msg: resp, key: key, iv: vector)
                    
                    let data :[Dictionary<String,Any>] = decryptMSG.shared.decrypt(enData: msg)
                    if data.count < 1 {return}
                    
                    let totalCount = (json["TOT_PAGE"] as! NSString).intValue
                    print(totalCount)
                    
                    if totalCount == 0{
                        let alert = UIAlertController(title: StaticString.diagnoseListString.dialogTitle.rawValue.localized,
                                                      message: StaticString.diagnoseListString.dialogContent.rawValue.localized, preferredStyle: .alert)
                        let confirm = UIAlertAction(title: StaticString.dialogText.confirm.rawValue.localized, style: .default){
                            (_)in
                            self.navigationController?.popViewController(animated: true)
                        }
                        alert.addAction(confirm)
                        self.present(alert, animated: true, completion: nil)
                        return
                    }
                    
                    for model in data {
                        let dict:Dictionary<String,Any> = model
                        print(dict)
                        
                        if let date = dict["SLFDGNSS_DT_F"] as? String{
                            
                            let YMD = dict["SLFDGNSS_D_F"] as! String
                            if self.tempDate == "" {
                                self.tempDate = YMD
                                self.sectionRow.append(self.tempDate)
                            }else{
                                if YMD == self.tempDate{
                                    
                                }else{
                                    self.tempDate = YMD
                                    self.sectionRow.append(self.tempDate)
                                }
                            }
                            
                            let val = CheckListCell()
                            val.title = StaticString.dialogText.confirm.rawValue.localized
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
                        self.moreBtn.isHidden = true
                    }
                }
                
            case .failure(let error):
                commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.coommunicationError.rawValue.localized, result: nil)
                print(error)
            }
        })
        
        Services.Service.searchDiagnoseListService(view: self.view, resp: diagnoseListHandler, page: self.page)
    }
    
    //MARK: - ui action
    
    /// Ir atrás
    @IBAction func goBack(_ sender: UIBarButtonItem) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func more(_ sender: UIButton) {
        self.page += 1
        Services.Service.searchDiagnoseListService(view: self.view, resp: diagnoseListHandler, page: self.page)
    }
    
    //MARK: - function
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return self.sectionRow.count
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return tableView.sectionHeaderHeight * 2
    }
    
    /// Configuración de etiqueta de texto de fecha
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        print(self.sectionRow[section])
        let view = UIView()
        view.frame = CGRect(x: 0, y: 0, width: 0, height: 0)
        view.backgroundColor = UIColor.white
        
        print(tableView.frame.width)
        let titleLB = UILabel(frame: CGRect(x: 0, y: tableView.sectionHeaderHeight / 2 , width: tableView.frame.width, height: 30))
        if tableView.frame.width > 350 {
            titleLB.text = "∙∙∙∙∙∙∙  \(self.sectionRow[section])   ∙∙∙∙∙∙∙"
        }else{
            titleLB.text = "∙∙∙∙∙  \(self.sectionRow[section])   ∙∙∙∙∙"
        }
        //        titleLB.sizeToFit()
        titleLB.font = UIFont.systemFont(ofSize: 22)
        titleLB.textColor = UIColor.systemGray
        titleLB.textAlignment = .center
        
        
        view.addSubview(titleLB)
        
        return view
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        var count = 0
        
        for row in 0 ..< self.respData.count {
            if self.sectionRow[section] == self.respData[row].ymd{
                count += 1
            }
        }
        return count
    }
    
    /// Crear items para ingresar a la lista de autodiagnóstico ingresado por el usuario
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        //Llamar por cantidad de items
        guard self.respData.count > 0 else {
            return UITableViewCell()
        }
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "ListCell") as! ListViewCell
        let row :CheckListCell = self.respData.filter{$0.ymd  == self.sectionRow[indexPath.section]}[indexPath.row]
        
        if row.COUGH_AT == "Y" || row.DYSPNEA_AT == "Y" || row.PYRXIA_AT == "Y" || row.SORE_THROAT_AT == "Y"{
            cell.mView.backgroundColor = UIColor.red
            cell.mView.layer.borderColor = UIColor.systemRed.cgColor
            cell.dateItem.textColor = UIColor.white
        }else {
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
    
    /// Mostrar pantalla detallada al hacer clic en el item de autodiagnóstico
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        guard let detailVC = self.storyboard?.instantiateViewController(withIdentifier: "detailView") as? DetailViewController else{
            return
        }
        
        let row = self.respData.filter{$0.ymd  == self.sectionRow[indexPath.section]}[indexPath.row]
        
        detailVC.DATE = row.date
        detailVC.COUGH_AT = row.COUGH_AT
        detailVC.DYSPNEA_AT = row.DYSPNEA_AT
        detailVC.PYRXIA_AT = row.PYRXIA_AT
        detailVC.SORE_THROAT_AT = row.SORE_THROAT_AT
        detailVC.RM = row.RM
        detailVC.BDHEAT = row.BDHEAT
        self.present(detailVC, animated: true)
    }
}
