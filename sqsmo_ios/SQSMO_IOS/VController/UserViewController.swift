//
//  UserViewController.swift
//  TestSampleApp
//
//  Created by wini on 2020/02/16.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit

//Verificar descripción de detalles de diagnóstico de la persona en autocuarentena
class UserViewController: UIViewController {
    
    // Vista de scroll view principal
    var mScrollview = UIScrollView()
    
    // Vista para agregar en el sub-scroll view
    var mView = UIView()
    
    var DATE:String?            //Fecha
    
    var PYRXIA_AT:String?       //Fiebre
    var COUGH_AT:String?        //Tos
    var DYSPNEA_AT:String?      //Dificultad resperatoria
    var SORE_THROAT_AT:String?  //Dolor de garganta
    var RM:String?              //Características especiales
    var BDHEAT:String?          //Temperatura corporal
    
    var mTextLB = UILabel()
    
    
    var mPYRXIA = UILabel()       //Fiebre
    var mCOUGH = UILabel()        //Tos
    var mDYSPNEA = UILabel()      //Dificultad resperatoria
    var mSORE_THROAT = UILabel()  //Dolor de garganta
    var mEtc = UILabel()          //Características especiales
    var sendBtn = UIButton()      //Botón de cierre
    
    var sPYRXIA = UISegmentedControl(items: ["YES".localized,"NO".localized])
    var sCOUGH = UISegmentedControl(items: ["YES".localized,"NO".localized])
    var sDYSPNEA = UISegmentedControl(items: ["YES".localized,"NO".localized])
    var sSORE_THROAT = UISegmentedControl(items: ["YES".localized,"NO".localized])
    var mEtcView = UIView()
    var mTempView = UIView()
    
    // Ver ancho completo de la vista
    let screenWidth = UIScreen.main.bounds.size.width
    // Ver altura completa de la vista
    let screenHeight = UIScreen.main.bounds.size.height
    
    @IBAction func goBack(_ sender: Any) {
        self.presentingViewController?.dismiss(animated: true, completion: nil)
    }
    
    //Configuración de pantalla inicial
    override func viewDidLoad() {
        super.viewDidLoad()
        self.settingView()
    }
    
    //Ajustes de pantalla
    func settingView() {
        
        var scrH:CGFloat = 0
        //Ajuste de ubicación en parte inferior para modelos que no sean +
        print(self.view.frame.height)
        if self.view.frame.height < 670 {
            mTextLB.frame = CGRect(x: 0, y: 90, width: self.view.frame.width - 40, height: 0)
            scrH =  screenHeight - (110 + 30 + 80)
        } else if self.view.frame.height < 740 {
            mTextLB.frame = CGRect(x: 0, y: 90, width: self.view.frame.width - 40, height: 0)
            scrH =  screenHeight - (110 + 30 + 120)
        } else{
            mTextLB.frame = CGRect(x: 0, y: 110, width: self.view.frame.width - 40, height: 0)
            scrH =  screenHeight - (130 + 30 + 120)
        }
        
        // Texto principal
        mTextLB.text = "Diagnostic time:".localized + (self.DATE ?? "-")
        mTextLB.font = UIFont.systemFont(ofSize: 17, weight: .bold)
        mTextLB.numberOfLines = 0
        mTextLB.sizeToFit()
        mTextLB.center.x = self.view.center.x
        mTextLB.textColor = UIColor.systemBlue
        self.view.addSubview(mTextLB)
        
        
        // Dibujar scroll view
        // Debe establecerse en base al punto de dispositivo principal
        mScrollview.frame = CGRect(x: 0, y: mTextLB.frame.maxY + 20, width: screenWidth, height: scrH)
        mScrollview.backgroundColor = UIColor.white
        mScrollview.delaysContentTouches = false
        
        // Dibujar vista a introducir en el scroll
        mView.frame = CGRect(x: 0, y: 0, width: screenWidth, height: screenHeight)
        
        //Fiebre
        mPYRXIA.frame = CGRect(x: 20, y: 0 , width: self.view.frame.width - 20, height: 0)
        self.commLB_Image(mPYRXIA, " Fever (over 37.5 ℃) or feeling warm".localized, 16)
        sPYRXIA.frame = CGRect(x: 0, y: mPYRXIA.frame.maxY + 5, width: self.view.frame.width - 40, height: 50)
        self.commSet(sPYRXIA, PYRXIA_AT)
        
        mTempView.frame = CGRect(x: 20, y: sPYRXIA.frame.maxY + 5, width: self.view.frame.width - 40, height: 40)
        mTempView.addDashBorder()
        self.mView.addSubview(mTempView)
        
        //Temperatura corporal
        let temperature = UILabel(frame: CGRect(x: 10, y: 5, width: 160, height: 30))
        temperature.text = "Body temperature :".localized
        temperature.font = UIFont.systemFont(ofSize: 15)
        
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
        mCOUGH.frame = CGRect(x: 20, y: mTempView.frame.maxY + 20 , width: self.view.frame.width - 20, height: 0)
        self.commLB_Image(mCOUGH, " Cough".localized, 16)
        sCOUGH.frame = CGRect(x: 0, y: mCOUGH.frame.maxY + 5, width: self.view.frame.width - 40, height: 50)
        self.commSet(sCOUGH, COUGH_AT)
        
        //Dolor de garganta
        mSORE_THROAT.frame = CGRect(x: 20, y: sCOUGH.frame.maxY + 20 , width: self.view.frame.width - 20, height: 0)
        self.commLB_Image(mSORE_THROAT, " Sore throat".localized, 16)
        sSORE_THROAT.frame = CGRect(x: 0, y: mSORE_THROAT.frame.maxY + 5, width: self.view.frame.width - 40, height: 50)
        self.commSet(sSORE_THROAT, SORE_THROAT_AT)
        
        //Dificultad resperatoria
        mDYSPNEA.frame = CGRect(x: 20, y: sSORE_THROAT.frame.maxY + 20 , width: self.view.frame.width - 20, height: 0)
        self.commLB_Image(mDYSPNEA, " Dyspnea (shortness of breath)".localized, 16)
        sDYSPNEA.frame = CGRect(x: 0, y: mDYSPNEA.frame.maxY + 5, width: self.view.frame.width - 40, height: 50)
        self.commSet(sDYSPNEA, DYSPNEA_AT)
        
        //Características especiales
        mEtc.frame = CGRect(x: 20, y: sDYSPNEA.frame.maxY + 20, width: 0, height: 20)
        self.commLB_Image(mEtc, " Observations".localized, 16)

        //Ärea visual de características especiales
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
        
        sendBtn.setTitle("Confirm".localized, for: .normal)
        sendBtn.titleLabel?.font = UIFont.boldSystemFont(ofSize: 15)
        sendBtn.addTarget(self, action: #selector(onClickBtn(_:)), for: .touchUpInside)
        sendBtn.tag = 1
        sendBtn.center.x = self.view.center.x
        self.setButtonUI(btn: self.sendBtn)
        self.view.addSubview(sendBtn)
    }
    
    // Botón oyente al hacer clic en enviar
    @objc func onClickBtn(_ sender:UIButton){
        self.presentingViewController?.dismiss(animated: true, completion: nil)
    }
    
    /// Parte para registrar la imagen en la etiqueta
    /// - Parameters:
    ///   - sender: Etiqueta
    ///   - text: Título de etiqueta
    ///   - size: Tamaño del título de la etiqueta
    func commLB_Image(_ sender:UILabel, _ text:String, _ size:CGFloat) {
        let attributedString = NSMutableAttributedString(string: "")
        let imageAttachment = NSTextAttachment()
        imageAttachment.image = UIImage(named: "etc.png")
        imageAttachment.bounds = CGRect(x: 0, y: 0.5, width: 10, height: 10)
        attributedString.append(NSAttributedString(attachment: imageAttachment))
        attributedString.append(NSAttributedString(string: text))
        sender.attributedText = attributedString
        sender.font = UIFont.systemFont(ofSize: size)
        sender.numberOfLines = 0
        sender.sizeToFit()
        self.mView.addSubview(sender)
    }
    
    /// Configuración de propiedad común
    /// - Parameters:
    ///   - sender: Segmento
    ///   - code: Selección (S / N)
    func commSet(_ sender:UISegmentedControl, _ code:String?) {
        let font = UIFont.systemFont(ofSize: 16)
        
        if #available(iOS 13.0, *) {
            sender.selectedSegmentTintColor = commFunction().UIColorFromRGB(rgbValue: 0x0078D7)
        } else {
            sender.tintColor = commFunction().UIColorFromRGB(rgbValue: 0x0078D7)
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
    
    //Configuración UI del botón
    func setButtonUI(btn:UIButton) {
        btn.layer.borderColor = UIColor.blue.cgColor
        btn.layer.borderWidth = 1
        btn.layer.cornerRadius = 10
        btn.layer.masksToBounds = true
        btn.setBackgroundColor(color: commFunction().UIColorFromRGB(rgbValue: 0x0078D7), forState: .normal)
        btn.tintColor = UIColor.blue
        btn.setTitleColor(UIColor.white, for: .normal)
    }
}
