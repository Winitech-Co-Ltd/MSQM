//
//  BandViewController.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/04/14.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import Alamofire


import UIKit

extension UIView {
    func fadeIn(duration: TimeInterval = 0.5, delay: TimeInterval = 0.0, completion: @escaping ((Bool) -> Void) = {(finished: Bool) -> Void in }) {
        self.alpha = 0.0

        UIView.animate(withDuration: duration, delay: delay, options: UIView.AnimationOptions.curveEaseIn, animations: {
            self.isHidden = false
            self.alpha = 1.0
        }, completion: completion)
    }

    func fadeOut(duration: TimeInterval = 0.5, delay: TimeInterval = 0.0, completion: @escaping (Bool) -> Void = {(finished: Bool) -> Void in }) {
        self.alpha = 1.0

        UIView.animate(withDuration: duration, delay: delay, options: UIView.AnimationOptions.curveEaseOut, animations: {
            self.isHidden = true
            self.alpha = 0.0
        }, completion: completion)
    }
}

//버튼 왼쪽이미지
extension UIButton {
    func moveImageLeftTextCenter(image : UIImage, imagePadding: CGFloat, renderingMode: UIImage.RenderingMode){
        
        self.setImage(image.withRenderingMode(renderingMode), for: .normal)
        guard let imageViewWidth = self.imageView?.frame.width else{return}
        guard let titleLabelWidth = self.titleLabel?.intrinsicContentSize.width else{return}
        self.contentHorizontalAlignment = .left
        let titleLeft = (bounds.width - titleLabelWidth) / 2 - imageViewWidth
        imageEdgeInsets = UIEdgeInsets(top: 15, left: 20, bottom: 15, right: 20)
        titleEdgeInsets = UIEdgeInsets(top: 0.0, left: titleLeft , bottom: 0.0, right: 0.0)
    }
}


class BandViewController: UIViewController,UITableViewDelegate, UITableViewDataSource, UIPickerViewDataSource, UIPickerViewDelegate {
    
    //MARK: -IBOutlet
    @IBOutlet var mTableView: UITableView!
    @IBOutlet var band_main_image: UIImageView!
    @IBOutlet var band_main_title: UILabel!
    @IBOutlet var band_name: UILabel!
    @IBOutlet var btn_bandInit: UIButton!
    @IBOutlet var band_main_view: UIView!
    @IBOutlet var selectMenuBtn: UISegmentedControl!
    @IBOutlet var emptyLB: UILabel!
    
    @IBAction func goBack(_ sender: UIBarButtonItem) {
        self.presentingViewController?.dismiss(animated: true)
    }
    
    @IBAction func initBand(_ sender: UIButton) {
        if paramSMTBND_REGIST_AT == "Y"{
            let mAlert = UIAlertController(title: "밴드초기화", message: "사용자의 안심밴드를 초기화 하시겠습니까?", preferredStyle: .alert)
            let confirm = UIAlertAction(title: "OK", style: .default, handler: {
                (UIAlertAction) -> Void in
                self.initUserBand()
            })
            let cancle = UIAlertAction(title: "Close", style: .cancel, handler: nil)
            mAlert.addAction(confirm)
            mAlert.addAction(cancle)
            self.present(mAlert, animated: true, completion: nil)
        }else{
            self.makeAlert(title: "초기화실패", message: "사용자 안심밴드가 등록되지 않았습니다.")
        }
    }
    
    //상단메뉴 선택에 대한 동작
    @IBAction func selectMenu(_ sender: UISegmentedControl) {
        
        //취소버튼 클로저
        let cancle : (UIAlertAction) -> Void = {_ in
            self.selectMenuBtn.selectedSegmentIndex = self.selectedSegment
        }
        
        switch sender.selectedSegmentIndex {
        case 0:
            self.makeAlertYN(title: "정보수정", message: "안심밴드 사용자로 변경하시겠습니까?", handle: {
                (UIAlertAction) -> Void in
                self.paramSMTBND_USE_AT = "Y"
                self.paramMOTNPRCP_ONOFF = "OFF"
                self.registSetting()
            }, cancle: cancle)
        case 1:
            self.makeAlertYN(title: "정보수정", message: "동작감지 사용자로 변경하시겠습니까?", handle: {
                (UIAlertAction) -> Void in
                self.paramSMTBND_USE_AT = "N"
                self.paramMOTNPRCP_ONOFF = "ON"
                self.registSetting()
            }, cancle: cancle)

        case 2:
            
            self.makeAlertYN(title: "정보수정", message: "정보수집 기능을 사용하지 않음으로 변경하시겠습니까?", handle: {
                (UIAlertAction) -> Void in
                self.paramSMTBND_USE_AT = "N"
                self.paramMOTNPRCP_ONOFF = "OFF"
                self.registSetting()
            }, cancle: cancle)

        default:
            return
        }
    }
    
    //MARK: -Value
    //공통변수
    var sensorView:UIView!
    var headerTitle:UILabel?
    var selectedSegment:Int!
    
    private var refreshControl = UIRefreshControl()
    
    //밴드전용 변수
    var mList:[BandModel]!
    var paramISLPRSN_SN:String!
    var paramTRMNL_SN:String!
    var paramSMTBND_REGIST_AT:String!
    var paramSMTBND_USE_AT:String!
    var paramMOTNPRCP_ONOFF:String!
    
    var paramSMTBND_ESNTL_NM:String?
    var paramSMTBND_SN:String?
    
    var paramMOTNPRCP_TIME_INTRVL:String?
    var paramMOTNPRCP_BEGIN_TIME:String?
    var paramMOTNPRCP_END_TIME:String?
    
    let mainImages = [
        "band_success.png",
        "band_fail.png"
    ]
    
    let listImages = [
        "band_connect.png",
        "band_disconnect.png"
    ]
    
    //센서전용 변수
    var startTimeTF:UITextField?
    var endTimeTF:UITextField?
    var TimeTF:UITextField?
    
    //MARK: -TableView delegate
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let view = UIView()
        view.frame = CGRect(x: 0, y: 0, width: 0, height: 0)
        view.backgroundColor = UIColor.white
        
        guard self.mList != nil else {
            return view
        }
        
        headerTitle = UILabel(frame: CGRect(x: 0, y: tableView.sectionHeaderHeight / 2 , width: tableView.frame.width, height: 30))
        
        var headerStd = ""
        if tableView.tag == 0 {
            headerStd = "안심밴드이력"
        }else{
            headerStd = "동작감지이력"
        }
        
        if self.mTableView.frame.width > 350 {
            self.headerTitle!.text = "∙∙∙∙∙∙∙  \(headerStd)   ∙∙∙∙∙∙∙"
        }else{
            self.headerTitle!.text = "∙∙∙∙∙  \(headerStd)   ∙∙∙∙∙"
        }
        
        headerTitle!.font = UIFont.systemFont(ofSize: 22, weight: .regular)
        headerTitle!.textColor = UIColor.black
        headerTitle!.textAlignment = .center
        
        view.addSubview(headerTitle!)
        
        return view
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return tableView.sectionHeaderHeight * 2
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.mList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if self.mList.count == 0 {
            return UITableViewCell()
        }
        
        guard let row = self.mList?[indexPath.row] else{
            return UITableViewCell()
        }
        
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "BandCell") as? BandCell else {
            return UITableViewCell()
        }
        
        guard let STNG_DT = row.STNG_DT else{
            return UITableViewCell()
        }

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyMMddHHmmss"
        let _StringToDate = dateFormatter.date(from: STNG_DT)
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
    
        cell.date.text = dateFormatter.string(from: _StringToDate!)

        if tableView.tag == 0 {
            if row.SMTBND_STTUS_CODE_NM == nil{
                cell.state.text = "오류"
            }else{
                cell.state.text = row.SMTBND_STTUS_CODE_NM
            }
            if row.SMTBND_STTUS_CODE == "01501"{
                cell.img.image = UIImage(named: listImages[0])
                cell.state.textColor = UIColor.blue
            }else if row.SMTBND_STTUS_CODE == "01504" || row.SMTBND_STTUS_CODE == "01505"{
                cell.img.image = UIImage(named: listImages[1])
                cell.state.textColor = UIColor.darkGray
            }else{
                cell.img.image = UIImage(named: listImages[1])
                cell.state.textColor = UIColor.red
            }
        }else{
            if row.MOTNPRCP_STTUS_CODE_NM == nil{
                cell.state.text = "오류"
            }else{
                cell.state.text = row.MOTNPRCP_STTUS_CODE_NM
            }
            if row.MOTNPRCP_STTUS_CODE == "01601"{
                cell.img.image = UIImage(named: listImages[0])
                cell.state.textColor = UIColor.systemYellow
                
            }else if row.MOTNPRCP_STTUS_CODE == "01603" || row.MOTNPRCP_STTUS_CODE == "01604"{
                cell.img.image = UIImage(named: listImages[0])
                cell.state.textColor = UIColor.green
            }else{
                cell.img.image = UIImage(named: listImages[1])
                cell.state.textColor = UIColor.red
            }
        }
        
        cell.mainView.backgroundColor = UIColor.white
        cell.mainView.layer.borderColor = UIColor.systemGray.cgColor
        cell.mainView.layer.borderWidth = 1
        cell.mainView.layer.cornerRadius = 5
        cell.mainView.layer.masksToBounds = true
        
        return cell
    }
    //MARK: -PickerView delegate
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if pickerView == pickerView_Time{
            return times.count
        }
        return values.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if pickerView == pickerView_Time{
            return times[row]
        }
        return values[row]
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if row == 0 {
            return
        }
        
        if pickerView == pickerView_START{
            self.startTimeTF?.text = self.values[row]
            self.paramMOTNPRCP_BEGIN_TIME = self.values[row]
        }else if pickerView == pickerView_END{
            self.endTimeTF?.text = self.values[row]
            self.paramMOTNPRCP_END_TIME = self.values[row]
        }else{
            self.TimeTF?.text = self.times[row]
            self.paramMOTNPRCP_TIME_INTRVL = String(self.times[row].prefix(1))
        }
    }
    
    //MARK: -Main
    override func viewDidLoad() {
        super.viewDidLoad()
        //상단메뉴 사이즈 설정
        self.selectMenuBtn.frame.size.height = 40
        self.mTableView.refreshControl = refreshControl
        refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
        refreshControl.attributedTitle = NSAttributedString(string: "새로고침")
        
        if #available(iOS 13.0, *) {
            self.selectMenuBtn.selectedSegmentTintColor = commFunction().UIColorFromRGB(rgbValue: 0x0078D7)
        } else {
            self.selectMenuBtn.tintColor = commFunction().UIColorFromRGB(rgbValue: 0x0078D7)
        }
        self.selectMenuBtn.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.white], for: .selected)
        self.selectMenuBtn.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.black], for: .normal)
        self.selectMenuBtn.setTitleTextAttributes([NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16)], for: .normal)
        
        //초기 센서뷰 생성
        self.sensorView = SensorView(frame: self.band_main_view.frame)
        self.view.addSubview(self.sensorView!)
        
        //초기화 버튼디자인
        commFunction().setButtonUI(btn: self.btn_bandInit, title: "밴드 초기화")
        self.btn_bandInit.moveImageLeftTextCenter(image: UIImage(named: "band_init.png")!, imagePadding: 30, renderingMode: .alwaysOriginal)
   
        if paramSMTBND_USE_AT == "Y" {
            self.commMainViewSet(index: 0)
        }else if paramMOTNPRCP_ONOFF == "ON"{
            self.commMainViewSet(index: 1)
        }else{
            self.commMainViewSet(index: 2)
        }
        
    }
    
    
    //MARK: -View Part
    //상단메뉴에 따른 뷰설정
    func commMainViewSet(index:Int){
        var PARM = Dictionary<String,String>()
        
        self.mTableView.tag = index
        self.selectedSegment = index
        self.selectMenuBtn.selectedSegmentIndex = index
        
        //목록초기화
        self.mList = [BandModel]()
        
        if index == 0 {
            self.mTableView.isHidden = false
            self.band_main_view.isHidden = false
            self.sensorView.isHidden = true
            
            if self.paramSMTBND_REGIST_AT == "N"{
                self.initViewBand()
            }else{
                PARM.updateValue(self.paramISLPRSN_SN, forKey: "ISLPRSN_SN")
                PARM.updateValue(self.paramTRMNL_SN, forKey: "TRMNL_SN")
                PARM.updateValue(self.paramSMTBND_SN!, forKey: "SMTBND_SN")
                PARM.updateValue(self.paramSMTBND_ESNTL_NM!, forKey: "SMTBND_ESNTL_NM")
                self.searchList(serviceNM: "SQSMO0008", param: PARM)
            }
        }else if index == 1{
            self.mTableView.isHidden = false
            self.band_main_view.isHidden = true
            self.sensorView.isHidden = false
            
            PARM.updateValue(self.paramISLPRSN_SN, forKey: "ISLPRSN_SN")
            PARM.updateValue(self.paramTRMNL_SN, forKey: "TRMNL_SN")
            self.searchList(serviceNM: "SQSMO0012", param: PARM)
        }else{
            self.selectMenuBtn.selectedSegmentIndex = 2
            self.selectedSegment = 2
            self.mTableView.isHidden = true
            self.band_main_view.isHidden = true
            self.sensorView.isHidden = true
            self.mTableView.reloadData()
        }
    }
    
    //밴드상태 화면 초기화
    func initViewBand(){
        
        if self.mList.count > 0{
            self.emptyLB.isHidden = true
            /*
             01502 : 끊김
             01501 : 연결중
             */
            guard let row = self.mList.first else {
                self.band_main_title.text = "밴드연결이 끊어졌습니다."
                self.band_main_title.textColor = UIColor.systemRed
                self.band_main_image.image = UIImage(named: self.mainImages[1])
                self.bandNameView(sender: self.band_name, title: "연결되지않음")
                return
            }
            
            if row.SMTBND_STTUS_CODE == "01502"{
                self.band_main_title.text = "안심밴드 연결이 끊어졌습니다."
                self.band_main_title.textColor = UIColor.systemRed
                self.band_main_image.image = UIImage(named: self.mainImages[1])
            }else{
                self.band_main_title.text = "안심밴드가 연결되었습니다."
                self.band_main_title.textColor = UIColor.systemBlue
                self.band_main_image.image = UIImage(named: self.mainImages[0])
            }
            
            self.bandNameView(sender: self.band_name, title:row.SMTBND_ESNTL_NM!)
        }else{
            self.band_main_title.text = "연결된 밴드없음"
            self.band_main_title.textColor = UIColor.systemRed
            self.band_main_image.image = UIImage(named: self.mainImages[1])
            self.bandNameView(sender: self.band_name, title: "미등록")
            self.mList = [BandModel]()
            self.mTableView.reloadData()
            self.emptyLB.isHidden = false
        }
    }
    
    //밴드이름 뷰
    func bandNameView(sender:UILabel, title:String){
        sender.backgroundColor = UIColor.systemGray
        sender.layer.borderColor = UIColor.systemGray.cgColor
        sender.layer.borderWidth = 1
        sender.layer.cornerRadius = 5
        sender.layer.masksToBounds = true
        sender.textColor = UIColor.white
        sender.text = title
    }
    
    //동작감지 뷰 설정
    func initMotionView(){
        let setWidth = (self.sensorView.frame.width / 2) - 5
        let setHeight = (self.sensorView.frame.height / 3) * 2 - 20
        
        //시작 및 종료시간
        let leftBoxView = UIView(frame: CGRect(x: 0, y: 0, width: setWidth, height: setHeight))
        let rigthBoxView = UIView(frame: CGRect(x: setWidth + 10, y: 0, width: setWidth, height: setHeight))
        commFunction().motionBox(sender: leftBoxView)
        commFunction().motionBox(sender: rigthBoxView)
        self.sensorView.addSubview(leftBoxView)
        self.sensorView.addSubview(rigthBoxView)
        
        let startLB = UILabel()
        startLB.text = "시작시간"
        startLB.font = UIFont.systemFont(ofSize: 18, weight: .bold)
        leftBoxView.addSubview(startLB)

        let leftImageView = UIImageView()
        commFunction().motionImage(sender: leftImageView)
        leftBoxView.addSubview(leftImageView)
        
        self.startTimeTF = UITextField()
        commFunction().motionValue(sender: self.startTimeTF!, picker: pickerView_START, value: self.paramMOTNPRCP_BEGIN_TIME ?? "08")
        setInputViewDatePicker(sender: startTimeTF!, target: self)
        leftBoxView.addSubview(startTimeTF!)
        commFunction().motionConst(param1: leftImageView, param2: startLB, param3: leftBoxView, param4: startTimeTF!)
        
        let endLB = UILabel()
        endLB.text = "종료시간"
        endLB.font = UIFont.systemFont(ofSize: 18, weight: .bold)
        rigthBoxView.addSubview(endLB)

        let rightImageView = UIImageView()
        commFunction().motionImage(sender: rightImageView)
        rigthBoxView.addSubview(rightImageView)
        
        self.endTimeTF = UITextField()
        commFunction().motionValue(sender: self.endTimeTF!, picker: pickerView_END, value: self.paramMOTNPRCP_END_TIME ?? "21")
        setInputViewDatePicker(sender: endTimeTF!, target: self)
        rigthBoxView.addSubview(endTimeTF!)
        commFunction().motionConst(param1: rightImageView, param2: endLB, param3: rigthBoxView, param4: endTimeTF!)
        
        //동작감지 동작간격 시간
        let bottomLB = UILabel()
        bottomLB.text = "동작감지 동작간격 시간"
        bottomLB.font = UIFont.systemFont(ofSize: 18, weight: .bold)
        self.sensorView.addSubview(bottomLB)
        
        let bottomView = UIView()
        bottomView.backgroundColor = UIColor.white
        bottomView.layer.borderWidth = 1
        bottomView.layer.borderColor = UIColor.systemBlue.cgColor
        bottomView.layer.cornerRadius = 10
        bottomView.layer.masksToBounds = true
        self.sensorView.addSubview(bottomView)

        self.TimeTF = UITextField()
        TimeTF!.text = self.paramMOTNPRCP_TIME_INTRVL ?? "2시간"
        TimeTF!.font = UIFont.systemFont(ofSize: 18, weight: .bold)
        TimeTF!.tintColor = UIColor.clear
        TimeTF!.textAlignment = .center
        TimeTF!.inputView = pickerView_Time
        setInputViewDatePicker(sender: TimeTF!, target: self)
        bottomView.addSubview(TimeTF!)
        
        let bottomBtn = UIButton()
        commFunction().setButtonUI(btn: bottomBtn, title: "저장")
        bottomBtn.addTarget(self, action: #selector(confirmMotionCommit), for: .touchUpInside)
        self.sensorView.addSubview(bottomBtn)
        
        //위치 공통지정
        commFunction().motionBottom(param1: bottomLB, param2: bottomView, param3: TimeTF!, param4: bottomBtn, param5: leftBoxView, param6: self.sensorView, param7: self.view)
         
    }
    
    func setInputViewDatePicker(sender:UITextField, target: Any) {
        let toolBar = UIToolbar(frame: CGRect(x: 0.0, y: 0.0, width: self.view.frame.width, height: 44.0)) //4
        let flexible = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil) //5
        let barButton = UIBarButtonItem(title: "닫기", style: .plain, target: target, action: #selector(tapCancel)) //7
        toolBar.setItems([flexible, barButton], animated: false) //8
        sender.inputAccessoryView = toolBar //9
    }

    
    @objc func tapCancel() {
        self.view.endEditing(true)
    }
    
    //MARK: -Common
    //사용자 밴드 초기화
    func initUserBand() {
        let url = serverAddress().main

        var PARM = Dictionary<String,String>()
        PARM.updateValue(self.paramISLPRSN_SN, forKey: "ISLPRSN_SN")
        PARM.updateValue(self.paramSMTBND_SN!, forKey: "SMTBND_SN")
        PARM.updateValue(self.paramSMTBND_ESNTL_NM!, forKey: "SMTBND_ESNTL_NM")
        
        //******** 기존의 파라메터를 암호화 하는 부분
        let param = PARM.printJson()
        print(param)
        let paramEN = try! param.aesEncrypt(key: decryptMSG().key128)
        let listService = sendModelEN(IFID: "SQSMO0009", PARM: paramEN)
        //***************************************************************
        
        let resp : (AFDataResponse<Any>) -> Void = ({
            response in
            print(response.debugDescription)
            switch response.result {
            case .success(let value):
                Spinner.stop()
                guard let _ = value as? [String: Any] else {
                    self.makeAlert(title: "조회실패", message: "서버와 통신이 원활하지 않습니다")
                    return
                }
                
                let mAlert = UIAlertController(title: "초기화완료", message: "자가격리자의 밴드정보를 초기화 하였습니다.", preferredStyle: .alert)
                let cancle = UIAlertAction(title: "OK", style: .cancel){
                    (UIAlertAction) -> Void in
                    self.presentingViewController?.dismiss(animated: true)
                }
                mAlert.addAction(cancle)
                self.present(mAlert, animated: true, completion: nil)
                
            case .failure(let error):
                print(error)
                Spinner.stop()
                self.makeAlert(title: "통신실패", message: "서버와의 통신이 원활하지 않습니다.")
            }
        })
        
        //서버호출
        let req = serviceForm(url: url, parametersEN: listService)
        req.getPostEN(view: self.view ,response: resp)
    }
    
    //사용여부 설정
    func registSetting() {
        let url = serverAddress().main

        var PARM = Dictionary<String,String>()
        PARM.updateValue(self.paramISLPRSN_SN, forKey: "ISLPRSN_SN")
        PARM.updateValue(self.paramTRMNL_SN, forKey: "TRMNL_SN")
        PARM.updateValue(self.paramSMTBND_USE_AT, forKey: "SMTBND_USE_AT")
        PARM.updateValue(self.paramMOTNPRCP_ONOFF!, forKey: "MOTNPRCP_ONOFF")
        
        //******** 기존의 파라메터를 암호화 하는 부분
        let param = PARM.printJson()
        print(param)
        let paramEN = try! param.aesEncrypt(key: decryptMSG().key128)
        let listService = sendModelEN(IFID: "SQSMO0011", PARM: paramEN)
        //***************************************************************
        
        let resp : (AFDataResponse<Any>) -> Void = ({
            response in
            switch response.result {
            case .success(let value):
                
                guard let json = value as? [String: Any] else {
                    self.makeAlert(title: "조회실패", message: "서버와 통신이 원활하지 않습니다")
                    self.selectMenuBtn.selectedSegmentIndex = self.selectedSegment
                    Spinner.stop()
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    let msg = json["RES_MSG"] as? String
                    self.makeAlert(title: "조회실패", message: msg ?? "조회정보가 없습니다.")
                    self.selectMenuBtn.selectedSegmentIndex = self.selectedSegment
                    Spinner.stop()
                    return
                }
                
                
                //****** 암호화된 부분을  복호화 하여 딕셔너리 타입으로 변경하는 부분
                let msg = decryptMSG().msgDecrypt(msg: resp)
                //딕셔너리 형태로 변환
                let data :[Dictionary<String,Any>] = decryptMSG(enData: msg).decrypt()
                if data.count < 1 {
                    self.makeAlert(title: "조회실패", message: "조회목록이 없습니다")
                    Spinner.stop()
                    return
                    
                }
                //****************************************************************************************
                print(data.debugDescription)
                
                for model in data{
                    let dict:Dictionary<String,Any> = model
                    
                    if let _ = dict["ISLPRSN_SN"] as? String{
                        self.paramMOTNPRCP_ONOFF = dict["MOTNPRCP_ONOFF"] as? String
                        self.paramSMTBND_USE_AT = dict["SMTBND_USE_AT"] as? String

                        self.paramSMTBND_ESNTL_NM = dict["SMTBND_ESNTL_NM"] as? String
                        self.paramSMTBND_REGIST_AT = dict["SMTBND_REGIST_AT"] as? String
                        self.paramSMTBND_SN = dict["SMTBND_SN"] as? String
                        
                        self.paramMOTNPRCP_TIME_INTRVL = dict["MOTNPRCP_TIME_INTRVL"] as? String
                        self.paramMOTNPRCP_BEGIN_TIME = dict["MOTNPRCP_BEGIN_TIME"] as? String
                        self.paramMOTNPRCP_END_TIME = dict["MOTNPRCP_END_TIME"] as? String
                    }
                }
                
                if self.paramSMTBND_USE_AT == "Y"{
                    self.commMainViewSet(index: 0)
                }else if self.paramMOTNPRCP_ONOFF == "ON"{
                    self.commMainViewSet(index: 1)
                }else{
                    self.commMainViewSet(index: 2)
                }
                Spinner.stop()
            case .failure(let error):
                print(error)
                self.makeAlert(title: "통신실패", message: "서버와의 통신이 원활하지 않습니다.")
                self.selectMenuBtn.selectedSegmentIndex = self.selectedSegment
                Spinner.stop()
            }
        })
        
        //서버호출
        let req = serviceForm(url: url, parametersEN: listService)
        req.getPostEN(view: self.view ,response: resp)
    }
    
    //목록조회
    func searchList(serviceNM:String, param:Dictionary<String,String>) {
        self.mTableView.delegate = self
        self.mTableView.dataSource = self
        
        let url = serverAddress().main

        //******** 기존의 파라메터를 암호화 하는 부분
        let param = param.printJson()
        print(param)
        let paramEN = try! param.aesEncrypt(key: decryptMSG().key128)
        let listService = sendModelEN(IFID: serviceNM, PARM: paramEN)
        //***************************************************************
        
        let resp : (AFDataResponse<Any>) -> Void = ({
            response in
            if self.refreshControl.isRefreshing{
                self.refreshControl.endRefreshing()
            }
            
            switch response.result {
            case .success(let value):
                
                guard let json = value as? [String: Any] else {
                    self.makeAlert(title: "조회실패", message: "서버와 통신이 원활하지 않습니다")
                    Spinner.stop()
                    return
                }
                
                guard let resp = json["RES_DATA"] as? String else {
                    let msg = json["RES_MSG"] as? String
                    self.makeAlert(title: "조회실패", message: msg ?? "조회정보가 없습니다.")
                    Spinner.stop()
                    return
                }
                
                
                //****** 암호화된 부분을  복호화 하여 딕셔너리 타입으로 변경하는 부분
                let msg = decryptMSG().msgDecrypt(msg: resp)
                //딕셔너리 형태로 변환
                let data :[Dictionary<String,Any>] = decryptMSG(enData: msg).decrypt()
                if data.count < 1 {
                    self.emptyLB.isHidden = false
                    //0개일경우 끊겨있는상태로 가정한다
                    if serviceNM == "SQSMO0008"{
                        self.band_main_title.text = "밴드연결이 끊어졌습니다."
                        self.band_main_title.textColor = UIColor.systemRed
                        self.band_main_image.image = UIImage(named: self.mainImages[1])
                    }else if serviceNM == "SQSMO0012"{
                        self.initMotionView()
                    }
                    Spinner.stop()
                    return
                    
                }
                self.emptyLB.isHidden = true
                //****************************************************************************************
                print(data.debugDescription)
                
                for model in data{
                    let dict:Dictionary<String,Any> = model
                    let val = BandModel()
                    val.ISLPRSN_SN = dict["ISLPRSN_SN"] as? String
                    val.SMTBND_SN = dict["SMTBND_SN"] as? String
                    val.SMTBND_ESNTL_NM = dict["SMTBND_ESNTL_NM"] as? String
                    val.SMTBND_STTUS_CODE = dict["SMTBND_STTUS_CODE"] as? String
                    val.SMTBND_STTUS_CODE_NM = dict["SMTBND_STTUS_CODE_NM"] as? String
                    val.MOTNPRCP_STTUS_CODE = dict["MOTNPRCP_STTUS_CODE"] as? String
                    val.MOTNPRCP_STTUS_CODE_NM = dict["MOTNPRCP_STTUS_CODE_NM"] as? String
                    val.STNG_DT = dict["STNG_DT"] as? String
                    self.mList.append(val)
                }
                
                self.mTableView.reloadData()
                
                //08 : 밴드
                //12 : 동작감지
                if serviceNM == "SQSMO0008"{
                    self.initViewBand()
                }else if serviceNM == "SQSMO0012"{
                    self.initMotionView()
                }
                Spinner.stop()
                
            case .failure(let error):
                print(error)
                self.makeAlert(title: "통신실패", message: "서버와의 통신이 원활하지 않습니다.")
                Spinner.stop()
            }
        })
        
        //서버호출
        let req = serviceForm(url: url, parametersEN: listService)
        req.getPostEN(view: self.view ,response: resp)
    }
    
    //동작감지 확인팝업
    @objc func confirmMotionCommit(){
        let alert = UIAlertController(title: "설정변경", message: "자가격리자 센서 측정시간을 변경하시겠습니까?", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "변경", style: .default, handler: {
            (UIAlertAction) -> Void in
            self.commitMotion()
        })
        let cancle = UIAlertAction(title: "취소", style: .cancel, handler: nil)
        alert.addAction(confirm)
        alert.addAction(cancle)
        self.present(alert, animated: true, completion: nil)
    }
    
    //동작감지 등록
    func commitMotion() {
        let url = serverAddress().main
        
        var PARM = Dictionary<String,String>()
        PARM.updateValue(self.paramISLPRSN_SN, forKey: "ISLPRSN_SN")
        PARM.updateValue(self.paramTRMNL_SN, forKey: "TRMNL_SN")
        PARM.updateValue(self.paramMOTNPRCP_BEGIN_TIME ?? "08", forKey: "MOTNPRCP_BEGIN_TIME")
        PARM.updateValue(self.paramMOTNPRCP_END_TIME ?? "21", forKey: "MOTNPRCP_END_TIME")
        PARM.updateValue(self.paramMOTNPRCP_TIME_INTRVL ?? "2", forKey: "MOTNPRCP_TIME_INTRVL")
        
        
        //******** 기존의 파라메터를 암호화 하는 부분
        let param = PARM.printJson()
        print(param)
        let paramEN = try! param.aesEncrypt(key: decryptMSG().key128)
        let listService = sendModelEN(IFID: "SQSMO0013", PARM: paramEN)
        
        let resp : (AFDataResponse<Any>) -> Void = ({
            response in
            switch response.result {
            case .success(let value):
                
                guard let json = value as? [String: Any] else {
                    self.makeAlert(title: "등록실패", message: "서버와 통신이 원활하지 않습니다")
                    Spinner.stop()
                    return
                }
                
                guard let CODE = json["RES_CD"] as? String else {
                    self.makeAlert(title: "등록실패", message: "자가격리자 정보변경에 실패하였습니다.")
                    Spinner.stop()
                    return
                }
                
                if CODE == "100"{
                    let msg = json["RES_MSG"] as? String
                    self.makeAlert(title: "등록성공", message: msg ?? "사용자 정보변경에 성공하였습니다.")
                }else{
                    self.makeAlert(title: "등록실패", message: "자가격리자 정보변경에 실패하였습니다.")
                }
                

                Spinner.stop()
                
            case .failure(let error):
                print(error)
                self.makeAlert(title: "통신실패", message: "서버와의 통신이 원활하지 않습니다.")
                Spinner.stop()
            }
        })
        
        //서버호출
        let req = serviceForm(url: url, parametersEN: listService)
        req.getPostEN(view: self.view ,response: resp)
    }
    
    @objc func refresh(){
        guard let paramSMTBND_SN = self.paramSMTBND_SN, let paramSMTBND_ESNTL_NM = self.paramSMTBND_ESNTL_NM else {
            if self.refreshControl.isRefreshing{
                self.refreshControl.endRefreshing()
            }
            return
        }
        
        //목록초기화
        self.mList = [BandModel]()
        
        var PARM = Dictionary<String,String>()
        PARM.updateValue(self.paramISLPRSN_SN, forKey: "ISLPRSN_SN")
        PARM.updateValue(self.paramTRMNL_SN, forKey: "TRMNL_SN")
        if self.selectedSegment == 0{
            PARM.updateValue(paramSMTBND_SN, forKey: "SMTBND_SN")
            PARM.updateValue(paramSMTBND_ESNTL_NM, forKey: "SMTBND_ESNTL_NM")
            self.searchList(serviceNM: "SQSMO0008", param: PARM)
            
        }else if self.selectedSegment == 1{
            self.searchList(serviceNM: "SQSMO0012", param: PARM)
        }
    }
    
    //공통alert
    func makeAlert(title:String, message:String){
        let mAlert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let cancle = UIAlertAction(title: "확인", style: .cancel, handler: nil)
        mAlert.addAction(cancle)
        self.present(mAlert, animated: true, completion: nil)
    }

    //선택alert
    func makeAlertYN(title:String, message:String, handle:((UIAlertAction) -> Void)?, cancle:((UIAlertAction) -> Void)?){
        let mAlert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let confirm = UIAlertAction(title: "변경", style: .default, handler: handle)
        let cancle = UIAlertAction(title: "취소", style: .default, handler: cancle)
        mAlert.addAction(confirm)
        mAlert.addAction(cancle)
        self.present(mAlert, animated: true, completion: nil)
    }
    
    //시간선택용 피커
    lazy var pickerView_START: UIPickerView = {
        // Generate UIPickerView.
        let picker = UIPickerView()
        // Specify the size.
        picker.frame = CGRect(x: 0, y: 150, width: self.view.bounds.width, height: 300.0)
        // Set the backgroundColor.
        picker.backgroundColor = .white
        // Set the delegate.
        picker.delegate = self
        // Set the dataSource.
        picker.dataSource = self
        return picker }()
    
    lazy var pickerView_END: UIPickerView = {
        // Generate UIPickerView.
        let picker = UIPickerView()
        // Specify the size.
        picker.frame = CGRect(x: 0, y: 150, width: self.view.bounds.width, height: 300.0)
        // Set the backgroundColor.
        picker.backgroundColor = .white
        // Set the delegate.
        picker.delegate = self
        // Set the dataSource.
        picker.dataSource = self
        return picker }()
    
    lazy var pickerView_Time: UIPickerView = {
        // Generate UIPickerView.
        let picker = UIPickerView()
        // Specify the size.
        picker.frame = CGRect(x: 0, y: 150, width: self.view.bounds.width, height: 300.0)
        // Set the backgroundColor.
        picker.backgroundColor = .white
        // Set the delegate.
        picker.delegate = self
        // Set the dataSource.
        picker.dataSource = self
        return picker }()
    
    private let values: [String] = [
        "선택하세요","00","02","03","04","05","06","07","08","09","10",
        "11","12","13","14","15","16","17","18","19","20",
        "21","22","23","23"
    ]
    
    private let times: [String] = [
        "선택하세요","1시간","2시간","3시간","4시간"
    ]
}
