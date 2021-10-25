//
//  DetailViewController.swift
//  TestSampleApp
//
//  Created by wini on 2020/02/16.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit

class DetailViewController: UIViewController {
    //MARK: - variable
    // Vista princial scroll view (desplazamiento)
    var mScrollview = UIScrollView()
    
    // Vista para agregar sub-scroll view (desplazamiento)
    var mView = UIView()
    
    var DATE:String?            //DATE
    var COUGH_AT:String?        //Tos
    var DYSPNEA_AT:String?      //Disnea
    var PYRXIA_AT:String?       //Fiebre
    var SORE_THROAT_AT:String?  //Dolor de garganta
    var RM:String?              //Características especiales
    var BDHEAT:String?          //Temperatura corporal
    
    
    var mTextLB = UILabel()
    var mCheckBox : CheckBox!
    var mPYRXIA = UILabel()       //Fiebre
    var mCOUGH = UILabel()        //Tos
    var mDYSPNEA = UILabel()      //Disnea
    var mSORE_THROAT = UILabel()  //Dolor de garganta
    var mEtc = UILabel()          //Características especiales
    var sendBtn = UIButton()      //Botón para cerrar
    
    var sPYRXIA = UISegmentedControl(items: [StaticString.diagnoseString.yes.rawValue.localized, StaticString.diagnoseString.no.rawValue.localized])
    var sCOUGH = UISegmentedControl(items: [StaticString.diagnoseString.yes.rawValue.localized, StaticString.diagnoseString.no.rawValue.localized])
    var sDYSPNEA = UISegmentedControl(items: [StaticString.diagnoseString.yes.rawValue.localized, StaticString.diagnoseString.no.rawValue.localized])
    var sSORE_THROAT = UISegmentedControl(items: [StaticString.diagnoseString.yes.rawValue.localized, StaticString.diagnoseString.no.rawValue.localized])
    var mEtcView = UIView()
    var mTempView = UIView()
    
    // 뷰 전체 폭 길이
    let screenWidth = UIScreen.main.bounds.size.width
    // 뷰 전체 높이 길이
    let screenHeight = UIScreen.main.bounds.size.height
    
    //MARK: - lifeCycle
    override func viewDidLoad() { 
        super.viewDidLoad()
        self.settingView()
    }
    
    override func viewWillAppear(_ animated: Bool) {
         super.viewWillAppear(animated)
     }
    
    //MARK: - ui action
    
    @IBAction func goBack(_ sender: UIBarButtonItem) {
           self.presentingViewController?.dismiss(animated: true, completion: nil)
       }
    
    //MARK: - function
    
    ///  Dibujar pantalla
    func settingView() {
        var itemHeigth:CGFloat = 0
        
        var scrH:CGFloat = 0
        //Ajuste de ubicación en parte inferior para modelos que no sean +
        print(self.view.frame.height)
        if self.view.frame.height < 670 {
            mTextLB.frame = CGRect(x: 20, y: 90, width: self.view.frame.width - 20, height: 20)
            scrH =  screenHeight - (mTextLB.frame.maxY + 30 + 80)
            itemHeigth = 40
        } else if self.view.frame.height < 740 {
            mTextLB.frame = CGRect(x: 20, y: 90, width: self.view.frame.width - 20, height: 20)
            scrH =  screenHeight - (mTextLB.frame.maxY + 30 + 120)
            itemHeigth = 45
        } else{
            mTextLB.frame = CGRect(x: 20, y: 110, width: self.view.frame.width - 20, height: 20)
            scrH =  screenHeight - (mTextLB.frame.maxY + 30 + 120)
            itemHeigth = 50
        }
        
        // Texto principal
        mTextLB.text = "Hora de diagnótico : ".localized + self.DATE!
        mTextLB.numberOfLines = 0
        mTextLB.lineBreakMode = .byWordWrapping
        mTextLB.font = UIFont.systemFont(ofSize: 18)
        mTextLB.textColor = UIColor.systemBlue
        mTextLB.sizeToFit()
        mTextLB.center.x = self.view.center.x
        self.view.addSubview(mTextLB)
        
        // Dibujar scroll view
        // Debe establecerse en base al punto de dispositivo principal
        mScrollview.frame = CGRect(x: 0, y: mTextLB.frame.maxY + 20, width: screenWidth, height: scrH)
        mScrollview.backgroundColor = UIColor.white
        mScrollview.delaysContentTouches = false
        
        // Dibujar vista a introducir en el scroll
        mView.frame = CGRect(x: 0, y: 0, width: screenWidth, height: screenHeight)
        
        //Fiebre
        mPYRXIA.frame = CGRect(x: 20, y: 0 , width: self.view.frame.width - 20, height: 20)
        self.commLB(mPYRXIA, StaticString.diagnoseString.feverLabel.rawValue.localized, 16)
        sPYRXIA.frame = CGRect(x: 0, y: mPYRXIA.frame.maxY + 5, width: self.view.frame.width - 40, height: itemHeigth)
        self.commSet(sPYRXIA, PYRXIA_AT)
        
        mTempView.frame = CGRect(x: 20, y: sPYRXIA.frame.maxY + 5, width: self.view.frame.width - 40, height: 40)
        mTempView.addDashBorder()
        self.mView.addSubview(mTempView)
        
        //Temperatura corporal
        let temperature = UILabel(frame: CGRect(x: 10, y: 11, width: 0, height: 30))
        temperature.text = StaticString.diagnoseString.temperatureText.rawValue.localized
        temperature.font = UIFont.systemFont(ofSize: 15)
        temperature.sizeToFit()
        
        let tempTF = UITextField(frame: CGRect(x: temperature.frame.maxX + 5, y: 5, width: 150, height: 30))
        if self.BDHEAT != nil && self.BDHEAT != "" {
            tempTF.text = "\(self.BDHEAT!) ℃"
        }else{
            tempTF.text = "-"
        }
        tempTF.isEnabled = false
        tempTF.font = UIFont.systemFont(ofSize: 15)
        mTempView.addSubview(temperature)
        mTempView.addSubview(tempTF)
        
        //Tos
        mCOUGH.frame = CGRect(x: 20, y: mTempView.frame.maxY + 20 , width: self.view.frame.width - 20, height: 20)
        self.commLB(mCOUGH, StaticString.diagnoseString.coughLabel.rawValue.localized, 16)
        sCOUGH.frame = CGRect(x: 0, y: mCOUGH.frame.maxY + 5, width: self.view.frame.width - 40, height: itemHeigth)
        self.commSet(sCOUGH, COUGH_AT)
        
        //Dolor de garganta
        mSORE_THROAT.frame = CGRect(x: 20, y: sCOUGH.frame.maxY + 20 , width: self.view.frame.width - 20, height: 20)
        self.commLB(mSORE_THROAT, StaticString.diagnoseString.soreLabel.rawValue.localized, 16)
        sSORE_THROAT.frame = CGRect(x: 0, y: mSORE_THROAT.frame.maxY + 5, width: self.view.frame.width - 40, height: itemHeigth)
        self.commSet(sSORE_THROAT, SORE_THROAT_AT)
        
        //Disnea
        mDYSPNEA.frame = CGRect(x: 20, y: sSORE_THROAT.frame.maxY + 20 , width: self.view.frame.width - 20, height: 20)
        self.commLB(mDYSPNEA, StaticString.diagnoseString.dyspneaLabel.rawValue.localized, 16)
        sDYSPNEA.frame = CGRect(x: 0, y: mDYSPNEA.frame.maxY + 5, width: self.view.frame.width - 40, height: itemHeigth)
        self.commSet(sDYSPNEA, DYSPNEA_AT)
        
        //Características especiales
        mEtc.frame = CGRect(x: 20, y: sDYSPNEA.frame.maxY + 20, width: 0, height: 20)
        self.commLB_Image(mEtc, StaticString.diagnoseString.specificLabel.rawValue.localized, 16)
        
        //Características especiales 뷰 영역
        mEtcView.frame = CGRect(x: 20, y: mEtc.frame.maxY + 5, width: self.view.frame.width - 40, height: 90)
        mEtcView.addDashBorder()
        self.mView.addSubview(mEtcView)
        
        let etcTV = UITextView(frame: CGRect(x: 5, y: 5, width: self.view.frame.width - 50, height: 80))
        etcTV.textColor = UIColor.black
        etcTV.font = UIFont.systemFont(ofSize: 15)
        etcTV.isEditable = false
        etcTV.text = self.RM ?? "-"
        mEtcView.addSubview(etcTV)
        
        // Pegar a la vista el scroll view
        mScrollview.addSubview(mView)
        
        // Especificar tamaño de scroll view
        print("\(mEtcView.frame.maxY)  \(mScrollview.frame.height)")
        if mEtcView.frame.maxY > mScrollview.frame.height{
            mScrollview.contentSize = CGSize(width: screenWidth , height: mEtcView.frame.maxY + 40)
        }else{
            mScrollview.contentSize = CGSize(width: screenWidth , height: mScrollview.frame.height)
        }
        
        // Poner al principio el scroll view princial
        self.view.addSubview(mScrollview)
        
        //Configuración del botón inferior
        let setPos:CGFloat = screenHeight - mScrollview.frame.maxY
        if setPos >= 110{
            sendBtn.frame = CGRect(x: 0, y: self.view.frame.height - 90, width: self.view.frame.width - 40, height: 50)
        }else {sendBtn.frame = CGRect(x: 0, y: self.view.frame.height - 80, width: self.view.frame.width - 40, height: 40)
        }
        
        sendBtn.setTitle("Confirmar".localized, for: .normal)
        sendBtn.titleLabel?.font = UIFont.boldSystemFont(ofSize: 15)
        sendBtn.addTarget(self, action: #selector(onClickBtn(_:)), for: .touchUpInside)
        sendBtn.tag = 1
        sendBtn.center.x = self.view.center.x
        self.setButtonUI(btn: self.sendBtn)
        self.view.addSubview(sendBtn)
    }
    
    //Botón oyente al hacer clic en enviar
    @objc func onClickBtn(_ sender:UIButton){
        self.presentingViewController?.dismiss(animated: true, completion: nil)
    }
    
    /// Configuración de Etiqueta
    func commLB(_ sender:UILabel, _ text:String, _ size:CGFloat) {
        sender.text = text
        sender.font = UIFont.systemFont(ofSize: size)
        sender.numberOfLines = 0
        sender.lineBreakMode = .byWordWrapping
        sender.textAlignment = .left
        sender.sizeToFit()
        self.mView.addSubview(sender)
    }
    
    /// Configuraciones imagen de etiqueta
    func commLB_Image(_ sender:UILabel, _ text:String, _ size:CGFloat) {
        let attributedString = NSMutableAttributedString(string: "")
        let imageAttachment = NSTextAttachment()
        imageAttachment.image = UIImage(named: "etc.png")
        imageAttachment.bounds = CGRect(x: 0, y: -1, width: 15, height: 15)
        attributedString.append(NSAttributedString(attachment: imageAttachment))
        attributedString.append(NSAttributedString(string: text))
        sender.attributedText = attributedString
        sender.font = UIFont.systemFont(ofSize: size)
        sender.sizeToFit()
        self.mView.addSubview(sender)
    }
    
    //Configuración de propiedad común
    func commSet(_ sender:UISegmentedControl, _ code:String?) {
        let font = UIFont.systemFont(ofSize: 16)
        
        if #available(iOS 13.0, *) {
            sender.selectedSegmentTintColor = UIColorFromRGB(rgbValue: 0x0078D7)
        } else {
            sender.tintColor = UIColorFromRGB(rgbValue: 0x0078D7)
        }
        
        sender.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.white], for: .selected)
        sender.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.black], for: .normal)
        sender.setTitleTextAttributes([NSAttributedString.Key.font: font], for: .normal)
        sender.isEnabled = false
        sender.center.x = self.view.center.x
        
        if code == "Y" {sender.selectedSegmentIndex = 0}
        else {sender.selectedSegmentIndex = 1}
        
        self.mView.addSubview(sender)
    }
    
    //Configuración botón UI (user interface)
    func setButtonUI(btn:UIButton) {
        btn.layer.borderColor = UIColor.blue.cgColor
        btn.layer.borderWidth = 1
        btn.layer.cornerRadius = 10
        btn.backgroundColor = UIColorFromRGB(rgbValue: 0x0078D7)
        btn.tintColor = UIColor.blue
        btn.setTitleColor(UIColor.white, for: .normal)
    }
    
    //Configuración color del botón
    func UIColorFromRGB(rgbValue: UInt) -> UIColor {
        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
}
