import UIKit
import Alamofire

class CheckScrollView: UIViewController, UITextViewDelegate, UITextFieldDelegate {
    typealias response = (AFDataResponse<Any>) -> Void
    //MARK: - variable
    let toolBar = UIToolbar().ToolbarPiker(mySelect: #selector(done(_:)))
    
    var param : Int = 0
    var keyboardHeight: CGFloat?
    
    // Vista princial scroll view (desplazamiento)
    var mScrollview = UIScrollView()
    
    // Vista para agregar sub-scroll view (desplazamiento)
    var mView = UIView()
    
    //Registro para la configuración de los botones que oculta la pantalla del teclado
    var keyBoardSelector:Bool = true
    
    // Texto para agregar a la vista
    var mTextLB = UILabel()
    var mCheckBox : CheckBox?
    var mPYRXIA = UILabel()       //Fiebre
    var mCOUGH = UILabel()        //Tos
    var mDYSPNEA = UILabel()      //Disnea
    var mSORE_THROAT = UILabel()  //Dolor de garganta
    var mEtc = UILabel()          //Características especiales
    var sendBtn = UIButton()      //Botón de enviar
    
    //Valor para enviar al servidor
    var PYRXIA_AT : String = "N"
    var COUGH_AT : String = "N"
    var SORE_THROAT_AT : String = "N"
    var DYSPNEA_AT : String = "N"
    var etcTV:UITextView!           //Características especiales
    var tempTF_dpUp:UITextField!    //Temperatura corporal(Mayoría)
    var tempTF_dpDown:UITextField!  //Temperatura corporal(Minoría)
    
    var sPYRXIA = UISegmentedControl(items: [StaticString.diagnoseString.yes.rawValue.localized, StaticString.diagnoseString.no.rawValue.localized])
    var sCOUGH = UISegmentedControl(items: [StaticString.diagnoseString.yes.rawValue.localized, StaticString.diagnoseString.no.rawValue.localized])
    var sDYSPNEA = UISegmentedControl(items: [StaticString.diagnoseString.yes.rawValue.localized, StaticString.diagnoseString.no.rawValue.localized])
    var sSORE_THROAT = UISegmentedControl(items: [StaticString.diagnoseString.yes.rawValue.localized, StaticString.diagnoseString.no.rawValue.localized])
    var mTempView = UIView()
    var mEtcView = UIView()
    
    //Altura del último item
    var lastHeight : CGFloat?
    
    // Medida del ancho de pantalla
    let screenWidth = UIScreen.main.bounds.size.width
    // Medida de la altura de pantalla
    let screenHeight = UIScreen.main.bounds.size.height
    
    private var registerDiagnoseHandler : response!
    
    //MARK: - lifeCycle
    override func viewDidLoad() {
        super.viewDidLoad()
        self.addKeyboardNotification()
        self.hideKeyboardOnTouchOnView()
        var scrH:CGFloat = 0
        //Ajustar la posición de acuerdo con el tamaño de la pantalla
        print(self.view.frame.height)
        if self.view.frame.height < 670 {
            mTextLB.frame = CGRect(x: 20, y: 90, width: self.view.frame.width - 20, height: 40)
            scrH =  screenHeight - (mTextLB.frame.maxY + 30 + 90)
        } else if self.view.frame.height < 740 {
            mTextLB.frame = CGRect(x: 20, y: 90, width: self.view.frame.width - 20, height: 40)
            scrH =  screenHeight - (mTextLB.frame.maxY + 30 + 130)
        } else{
            mTextLB.frame = CGRect(x: 20, y: 110, width: self.view.frame.width - 20, height: 40)
            scrH =  screenHeight - (mTextLB.frame.maxY + 30 + 130)
        }
        
        // Texto principal
        mTextLB.text = StaticString.diagnoseString.selectSymptomsTitle.rawValue.localized
        mTextLB.numberOfLines = 0
        mTextLB.lineBreakMode = .byWordWrapping
        mTextLB.font = UIFont.systemFont(ofSize: 18)
        mTextLB.textColor = UIColor.systemBlue
        mTextLB.sizeToFit()
        mTextLB.center.x = self.view.center.x
        self.view.addSubview(mTextLB)
        
        // Dibujar scroll view
        mScrollview.frame = CGRect(x: 0, y: mTextLB.frame.maxY + 20, width: screenWidth, height: scrH - 40)
        mScrollview.backgroundColor = UIColor.white
        mScrollview.delaysContentTouches = false
        
        // Dibujar la vista que entre dentro del scroll view
        mView.frame = CGRect(x: 0, y: 0, width: screenWidth, height: screenHeight)
        
        //Fiebre
        mPYRXIA.frame = CGRect(x: 20, y: 0 , width: self.view.frame.width - 20, height: 20)
        self.commLB(mPYRXIA, StaticString.diagnoseString.feverLabel.rawValue.localized, 16)
        sPYRXIA.frame = CGRect(x: 0, y: mPYRXIA.frame.maxY + 5, width: mView.frame.width - 40, height: 50)
        self.commSet(sPYRXIA, 0)
        
        mTempView.frame = CGRect(x: 20, y: sPYRXIA.frame.maxY + 5, width: self.view.frame.width - 40, height: 40)
        self.mView.addSubview(mTempView)
        
        //Temperatura corporal
        let temperature = UILabel(frame: CGRect(x: 10, y: 11, width: 0, height: 30))
        temperature.text = StaticString.diagnoseString.temperatureText.rawValue.localized
        temperature.font = UIFont.systemFont(ofSize: 14)
        temperature.sizeToFit()
        
        self.tempTF_dpUp = UITextField(frame: CGRect(x: temperature.frame.maxX + 5, y: 5, width: 50, height: 30))
        let dotLB = UILabel(frame: CGRect(x: self.tempTF_dpUp.frame.maxX + 2, y: 5, width: 5, height: 30))
        self.tempTF_dpDown = UITextField(frame: CGRect(x: dotLB.frame.maxX + 2, y: 5, width: 40, height: 30))
        let tempLB = UILabel(frame: CGRect(x: self.tempTF_dpDown.frame.maxX + 5, y: 5, width: 20, height: 30))
        tempTF_dpUp.tag = 0
        tempTF_dpDown.tag = 1
        dotLB.text = "."
        tempLB.font = UIFont.systemFont(ofSize: 16, weight: .bold)
        tempLB.text = "℃"
        self.commEtcTemp(self.tempTF_dpUp, true)
        self.commEtcTemp(self.tempTF_dpDown, false)
        mTempView.addSubview(temperature)
        mTempView.addSubview(dotLB)
        mTempView.addSubview(tempLB)
        
        //Tos
        mCOUGH.frame = CGRect(x: 20, y: mTempView.frame.maxY + 20 , width: self.view.frame.width - 20, height: 20)
        self.commLB(mCOUGH, StaticString.diagnoseString.coughLabel.rawValue.localized, 16)
        sCOUGH.frame = CGRect(x: 0, y: mCOUGH.frame.maxY + 5, width: mView.frame.width - 40, height: 50)
        self.commSet(sCOUGH, 1)
        
        //Dolor de garganta
        mSORE_THROAT.frame = CGRect(x: 20, y: sCOUGH.frame.maxY + 20 , width: self.view.frame.width - 20, height: 20)
        self.commLB(mSORE_THROAT, StaticString.diagnoseString.soreLabel.rawValue.localized, 16)
        sSORE_THROAT.frame = CGRect(x: 0, y: mSORE_THROAT.frame.maxY + 5, width: mView.frame.width - 40, height: 50)
        self.commSet(sSORE_THROAT, 2)
        
        //Disnea
        mDYSPNEA.frame = CGRect(x: 20, y: sSORE_THROAT.frame.maxY + 20 , width: self.view.frame.width - 20, height: 20)
        self.commLB(mDYSPNEA, StaticString.diagnoseString.dyspneaLabel.rawValue.localized, 16)
        
        sDYSPNEA.frame = CGRect(x: 0, y: mDYSPNEA.frame.maxY + 5, width: mView.frame.width - 40, height: 50)
        self.commSet(sDYSPNEA, 3)
        
        
        //Características especiales
        mEtc.frame = CGRect(x: 20, y: sDYSPNEA.frame.maxY + 20, width: 0, height: 20)
        self.commLB_Image(mEtc, StaticString.diagnoseString.specificLabel.rawValue.localized, 16)
        
        //Características especiales view
        mEtcView.frame = CGRect(x: 20, y: mEtc.frame.maxY + 5, width: self.view.frame.width - 40, height: 90)
        self.mView.addSubview(mEtcView)
        
        self.etcTV = UITextView(frame: CGRect(x: 0, y: 0, width: self.view.frame.width - 40, height: 90))
        self.etcTV.textColor = UIColor.black
        self.etcTV.font = UIFont.systemFont(ofSize: 15)
        self.etcTV.delegate = self
        self.etcTV.autocorrectionType = .no
        self.etcTV.inputAccessoryView = toolBar
        self.etcTV.layer.borderColor = UIColor.gray.cgColor
        self.etcTV.layer.borderWidth = 1
        self.etcTV.layer.cornerRadius = 10
        self.etcTV.layer.masksToBounds = true
        mEtcView.addSubview(etcTV)
        
        mScrollview.addSubview(mView)
        
        // Especificar tamaño del scroll view
        print("\(mEtc.frame.maxY)  \(mScrollview.frame.height)")
        if mEtcView.frame.maxY > mScrollview.frame.height{
            mScrollview.contentSize = CGSize(width: screenWidth , height: mEtcView.frame.maxY + 30)
        }else{
            mScrollview.contentSize = CGSize(width: screenWidth , height: mScrollview.frame.height)
        }
        
        self.view.addSubview(mScrollview)
        
        //Ubicar el botón de registro de autodiagnóstico
        let setPos:CGFloat = screenHeight - mScrollview.frame.maxY
        
        if setPos >= 110{
            sendBtn.frame = CGRect(x: 0, y: self.view.frame.height - 80, width: self.view.frame.width - 40, height: 50)
        }else {sendBtn.frame = CGRect(x: 0, y: self.view.frame.height - 70, width: self.view.frame.width - 40, height: 40)
        }
        
        
        sendBtn.setTitle("FIN".localized, for: .normal)
        sendBtn.titleLabel?.font = UIFont.boldSystemFont(ofSize: 15)
        sendBtn.addTarget(self, action: #selector(onClickBtn(_:)), for: .touchUpInside)
        sendBtn.tag = 1
        sendBtn.center.x = self.view.center.x
        self.setButtonUI(btn: self.sendBtn)
        self.view.addSubview(sendBtn)
        
        //Texto de guía en la parte inferior de la pantalla
        let std = StaticString.diagnoseString.specificText.rawValue.localized
        let setBottomText = UILabel()
        setBottomText.frame = CGRect(x: 20, y: sendBtn.frame.minY - 60, width: self.view.frame.width - 40, height: 35)
        setBottomText.text = std
        setBottomText.font = UIFont.systemFont(ofSize: 14)
        setBottomText.numberOfLines = 4
        setBottomText.lineBreakMode = NSLineBreakMode.byWordWrapping;
        setBottomText.sizeToFit()
        self.lastHeight = setBottomText.frame.minY
        
        self.view.addSubview(setBottomText)
        
        registerDiagnoseHandler = ({
            response in
            Spinner.stop()
            switch response.result {
            case .success(let value):
                print(value)
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
                
                guard let _ = json["RES_DATA"] as? String else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue.localized, result: nil)
                    return
                }
                
                self.showAlert(StaticString.diagnoseString.success.rawValue.localized, StaticString.diagnoseString.registration.rawValue.localized)
                
            case .failure(let error):
                self.showAlert(StaticString.diagnoseString.fail.rawValue.localized, StaticString.diagnoseString.registrationFail.rawValue.localized)
                print(error)
            }
        })
        
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print(param)
        if param != 0 {
            self.tabBarController?.selectedIndex = self.param
            self.param = 0
        }
    }
    
    //MARK: - ui action
    
    /// Ir atrás
    @IBAction func goBack(_ sender: UIBarButtonItem) {
        self.navigationController?.popViewController(animated: true)
    }
    
    //MARK: - function
    
    //Configuración tamaño de etiqueta
    func commLB(_ sender:UILabel, _ text:String, _ size:CGFloat) {
        sender.text = text
        sender.font = UIFont.systemFont(ofSize: size)
        sender.numberOfLines = 0
        sender.lineBreakMode = .byWordWrapping
        sender.textAlignment = .left
        sender.sizeToFit()
        mView.addSubview(sender)
    }
    
    /// Configuraciones imagen de etiqueta
    func commLB_Image(_ sender:UILabel, _ text:String, _ size:CGFloat) {
        let attributedString = NSMutableAttributedString(string: "")
        attributedString.append(NSAttributedString(string: text))
        sender.attributedText = attributedString
        sender.font = UIFont.systemFont(ofSize: size)
        sender.sizeToFit()
        self.mView.addSubview(sender)
    }
    
    /// Configuración de esquema de ventana de entrada
    func commEtcTemp(_ sender:UITextField, _ isUP:Bool){
        sender.textColor = UIColor.black
        sender.keyboardType = .numberPad
        sender.delegate = self
        sender.font = UIFont.systemFont(ofSize: 15)
        sender.setLeftPaddingPoints(5)
        sender.autocorrectionType = .no
        sender.inputAccessoryView = toolBar
        sender.layer.borderColor = UIColor.gray.cgColor
        sender.layer.borderWidth = 1
        sender.layer.cornerRadius = 10
        sender.layer.masksToBounds = true
        sender.addTarget(self, action: #selector(onTouchIsolation(_:)), for: .touchDown)
        mTempView.addSubview(sender)
    }
    
    //Configuración de propiedad común
    func commSet(_ sender:UISegmentedControl, _ tag:Int) {
        let font = UIFont.systemFont(ofSize: 16)
        
        
        if #available(iOS 13.0, *) {
            sender.selectedSegmentTintColor = UIColorFromRGB(rgbValue: 0x0078D7)
        } else {
            sender.tintColor = UIColorFromRGB(rgbValue: 0x0078D7)
        }
        
        sender.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.white], for: .selected)
        sender.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.black], for: .normal)
        sender.setTitleTextAttributes([NSAttributedString.Key.font: font], for: .normal)
        //        sender.addTarget(self, action: #selector(onChange(_:)), for: .valueChanged)
        sender.tag = tag
        sender.center.x = mView.center.x
        mView.addSubview(sender)
    }
    
    //Verifica que el usuario haya seleccionada todas las categorías de la lista de verificación
    @objc func onClickBtn(_ sender:UIButton){
        if sender.tag == 0 {
            let chk = sender as! CheckBox
            
            if chk.isChecked {
                sPYRXIA.selectedSegmentIndex = 1
                sCOUGH.selectedSegmentIndex = 1
                sSORE_THROAT.selectedSegmentIndex = 1
                sDYSPNEA.selectedSegmentIndex = 1
            }
            
        }else if sender.tag == 1 {
            self.setStatusData()
        }
    }
    
    //Limitar cantidad de dígitos en un campo de texto
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        print("shouldChangeCharactersIn")
        guard let std = textField.text else {
            return true
        }
        
        if textField.tag == 0 {
            let len = std.string.count + string.string.count - range.length
            
            return len <= 2
        }else{
            let len = std.string.count + string.string.count - range.length
            return len <= 1
        }
        
    }
    
    //Al ingresar a la ventana de entrada de temperatura (entero), mover automáticamente a la ventana de entrada de temperatura (decimal)
    func textFieldDidChangeSelection(_ textField: UITextField) {
        if textField.tag == 0 && textField.text?.count == 2{
            if let nextField = textField.superview?.viewWithTag(textField.tag + 1) as? UITextField {
                nextField.becomeFirstResponder()
            }
        }
    }
    
    //Limite la cantidad de dígitos que se pueden ingresar en la vista de texto
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        guard let std = textView.text else {
            return true
        }
        let len = std.string.count + text.string.count - range.length
        return len <= 200
    }
    
    //    @objc func onChange(_ sender:UISegmentedControl){
    //
    //        if sPYRXIA.selectedSegmentIndex == 1 &&
    //            sCOUGH.selectedSegmentIndex == 1 &&
    //            sSORE_THROAT.selectedSegmentIndex == 1 &&
    //            sDYSPNEA.selectedSegmentIndex == 1 {
    //        }
    //    }
    
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
    
    /// Crear diálogo
    /// - Parameters:
    ///   - title: Título
    ///   - message: Descripción
    func showAlert(_ title:String, _ message:String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirmar".localized, style: .default){
            (_)in
            if title == "Éxito".localized {
                self.navigationController?.popViewController(animated: true)
            }
        }
        alert.addAction(confirm)
        self.present(alert, animated: true){
            self.sPYRXIA.selectedSegmentIndex = 1
            self.sCOUGH.selectedSegmentIndex = 1
            self.sSORE_THROAT.selectedSegmentIndex = 1
            self.sDYSPNEA.selectedSegmentIndex = 1
            self.PYRXIA_AT = "N"
            self.COUGH_AT = "N"
            self.SORE_THROAT_AT = "N"
            self.DYSPNEA_AT = "N"
        }
    }
    
    @objc func done(_ sender:UITextField){
        let contentInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: 0.0, right: 0.0)
        self.mScrollview.contentInset = contentInsets;
        self.mScrollview.scrollIndicatorInsets = contentInsets;
        
        self.view.endEditing(true)
    }
    
    //Proceso sobre la parte de la pantalla donde tapa el teclado 
    @objc func onTouchIsolation(_ sender:UITextField){
        self.keyBoardSelector = false
    }
    
    private func addKeyboardNotification(){
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
    }
    
    /// 키보드가 나타날때 스크롤뷰의 내부 아이템 위치 변경
    @objc private func keyboardWillShow(_ notification: Notification) {
        if let keyboardFrame: NSValue = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            let keybaordRectangle = keyboardFrame.cgRectValue
            self.keyboardHeight = keybaordRectangle.height
            
            let contentInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: self.keyboardHeight! + self.toolBar.frame.height - (self.view.frame.height - self.lastHeight!) + 20, right: 0.0)
            self.mScrollview.contentInset = contentInsets
            self.mScrollview.scrollIndicatorInsets = contentInsets
            
            if self.keyBoardSelector {
                self.mScrollview.contentOffset.y = contentInsets.bottom
            }
        }
    }
    
    /// Al toque en la pantalla se baja el techado
    func hideKeyboardOnTouchOnView() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(
            target: self,
            action: #selector(self.dismissKeyboardCheck))
        
        self.mScrollview.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboardCheck() {
        self.view.endEditing(true)
        self.mScrollview.contentInset = UIEdgeInsets.zero
        self.mScrollview.scrollIndicatorInsets = UIEdgeInsets.zero
    }
    
    //MARK: - service
    
    //Servicio de registro de autodiagnóstico
    func setStatusData() {
        if self.sPYRXIA.selectedSegmentIndex == -1
            || self.sCOUGH.selectedSegmentIndex == -1
            || self.sSORE_THROAT.selectedSegmentIndex == -1
            || self.sDYSPNEA.selectedSegmentIndex == -1 {
            return
        }

        if self.sPYRXIA.selectedSegmentIndex == 0 {self.PYRXIA_AT = "Y"}
        if self.sCOUGH.selectedSegmentIndex == 0 {self.COUGH_AT = "Y"}
        if self.sSORE_THROAT.selectedSegmentIndex == 0 {self.SORE_THROAT_AT = "Y"}
        if self.sDYSPNEA.selectedSegmentIndex == 0 {self.DYSPNEA_AT = "Y"}
        
        guard UserDefaults.standard.string(forKey: "ISLPRSN_SN") != nil else {
            print("ERROR")
            return
        }
        
        var temp = ""
        if self.tempTF_dpUp.text != "" && self.tempTF_dpDown.text != ""{
            temp = (self.tempTF_dpUp.text ?? "") +  "." + (self.tempTF_dpDown.text ?? "")
        }else{
            commFunction.comm.makeToast(controller: self, message: StaticString.diagnoseString.temperatureNotInput.rawValue.localized, result: nil)
            return
        }
        
        Services.Service.registerDiagnoseService(view: self.view, resp: registerDiagnoseHandler, PYRXIA_AT: self.PYRXIA_AT, COUGH_AT: self.COUGH_AT, SORE_THROAT_AT: self.SORE_THROAT_AT, DYSPNEA_AT: self.DYSPNEA_AT, BDHEAT: temp, RM: self.etcTV.text ?? "")
    }
}
