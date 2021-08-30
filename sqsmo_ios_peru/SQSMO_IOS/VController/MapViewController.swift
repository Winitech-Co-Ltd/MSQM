//
//  MapViewController.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/22.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import MapKit
import Alamofire

//Página de verificar ubicación de persona en autocuarentena
class MapViewController: UIViewController, MKMapViewDelegate, UIPickerViewDataSource, UIPickerViewDelegate, UITextFieldDelegate {
    let pList = UserDefaults.standard
    
    //MARK: -TextField delegate
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        //Inicializar Provincia y Distrito al seleccionar campos de texto Región
        if textField.tag == 0 {
            self.Provincial_CODE = [Dictionary<String,String>]()
            self.Distrital_CODE = [Dictionary<String,String>]()
        }
        
        //Inicializar Distrito al seleccionar campos de texto Provincia
        if textField.tag == 1 {
            self.Distrital_CODE = [Dictionary<String,String>]()
            
            //Verificar si existe en la lista de códigos la Provincia que ha buscado
            if self.Provincial_CODE.count > 0{
                return true
            }else{
                //Servicio de búsqueda de código de Provincia
                commFunction().searchProv(view: self.view,DPRTMNT_CODE: self.selectRegionalCD , resp: {
                    response in
                    
                    switch response.result {
                    case .success(let value):
                        
                        guard let json = value as? [String: Any] else {
                            self.makeAlert(title: "Gobierno Provincial".localized, message: "Communication and server network is not fluid.".localized)
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
                            self.makeAlert(title: "Gobierno Provincial".localized, message: msg ?? "Falló búsqueda de la Provincia.".localized)
                            Spinner.stop()
                            return
                        }
                        
                        let msg = decryptMSG.instance.msgDecryptSecure(msg: resp, key: self.pList.string(forKey: "seckey")!,iv: self.pList.string(forKey: "seckeyVector")!)
                        //Convertir a formato diccionario
                        let data :[Dictionary<String,Any>] = decryptMSG.instance.decrypt(enData: msg)
                        if data.count < 1 {self.makeAlert(title: "Search failed".localized, message: "No hay lista de la Provincia.".localized); Spinner.stop();return}
                        
                        for row in data{
                            var dict = Dictionary<String,String>()
                            
                            if type(of: row["CODE"]!) == NSNull.self{
                                dict.updateValue("", forKey: "CODE")
                            }else{
                                dict.updateValue(row["CODE"]! as! String, forKey: "CODE")
                            }
                            dict.updateValue(row["CODE_NM"]! as! String, forKey: "CODE_NM")
                            self.Provincial_CODE.append(dict)
                        }
                        //Según características del Swift, si no se mueve no puede seleccionarse por lo tanto el primer valor se designa en blanco
                        self.Provincial_CODE.remove(at: 0)
                        self.Provincial_CODE.insert(self.commonDict, at: 0)
                        self.pickerView_provincial.reloadAllComponents()
                        self.pickerView_provincial.selectedRow(inComponent: 0)
                        self.provincialTF.becomeFirstResponder()
                        
                    case .failure(let error):
                        print(error)
                        self.makeAlert(title: "Communication failure".localized, message: "Communication and server network is not fluid.".localized)
                    }
                    Spinner.stop()
                })
            }
        }else if textField.tag == 3 {
            //Si al seleccionar un Distrito en el campo de texto y existe el Distrito buscado en la lista
            if self.Distrital_CODE.count > 0{
                return true
            }else{
                //Servicio de búsqueda de Distrito en la lista
                commFunction().searchDIST(view: self.view, DPRTMNT_CODE: self.selectRegionalCD,PRVNCA_CODE: self.selectProvincialCD, resp: {
                    response in
                    
                    switch response.result {
                    case .success(let value):
                        
                        guard let json = value as? [String: Any] else {
                            self.makeAlert(title: "Gobierno Distrital".localized, message: "Communication and server network is not fluid.".localized)
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
                            self.makeAlert(title: "Gobierno Distrital".localized, message: msg ?? "Falló búsqueda del Distrito.".localized)
                            Spinner.stop()
                            return
                        }
                        
                        let msg = decryptMSG.instance.msgDecryptSecure(msg: resp, key: self.pList.string(forKey: "seckey")!,iv: self.pList.string(forKey: "seckeyVector")!)
                        //딕셔너리 형태로 변환
                        let data :[Dictionary<String,Any>] = decryptMSG.instance.decrypt(enData: msg)
                        if data.count < 1 {self.makeAlert(title: "Search failed".localized, message: "No hay lista del Distrito.".localized); Spinner.stop();return}
                        
                        for row in data{
                            var dict = Dictionary<String,String>()
                            
                            if type(of: row["CODE"]!) == NSNull.self{
                                dict.updateValue("", forKey: "CODE")
                            }else{
                                dict.updateValue(row["CODE"]! as! String, forKey: "CODE")
                            }
                            dict.updateValue(row["CODE_NM"]! as! String, forKey: "CODE_NM")
                            self.Distrital_CODE.append(dict)
                        }
                        self.Distrital_CODE.remove(at: 0)
                        self.Distrital_CODE.insert(self.commonDict, at: 0)
                        self.pickerView_distrital.reloadAllComponents()
                        self.pickerView_distrital.selectedRow(inComponent: 0)
                        self.distritalTF.becomeFirstResponder()
                        
                    case .failure(let error):
                        print(error)
                        self.makeAlert(title: "Communication failure".localized, message: "Communication and server network is not fluid.".localized)
                    }
                    Spinner.stop()
                })
            }
        }
        return true
    }
    
    //MARK: -PickerView delegate
    //Número de columnas de selector
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    //Solicitud de selección para cada uno en Región, Provincia, Distrito
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if pickerView == self.pickerView_regional{
            return Regional_CODE.count
        }else if pickerView == self.pickerView_provincial {
            return Provincial_CODE.count
        }else if pickerView == self.pickerView_distrital{
            return Distrital_CODE.count
        }
        return 0
    }
    
    //Texto que se muestra en el selector
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        
        if pickerView == self.pickerView_regional{
            let value:Dictionary<String,String> = Regional_CODE[row]
            return value["CODE_NM"]
        }else if pickerView == self.pickerView_provincial {
            let value:Dictionary<String,String> = Provincial_CODE[row]
            return value["CODE_NM"]
        }else if pickerView == self.pickerView_distrital{
            let value:Dictionary<String,String> = Distrital_CODE[row]
            return value["CODE_NM"]
        }
        return ""
    }
    
    //Parte que almacena el valor de acuerdo va seleccionando el selector
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        
        if row != 0{
            if pickerView == self.pickerView_regional{
                let value:Dictionary<String,String> = Regional_CODE[row]
                self.regionalTF.text = value["CODE_NM"]
                self.selectRegionalCD = value["CODE"]!
                self.changeAreaTFSetting(Provincial: true, Distrital: false)
                
                self.provincialTF.text = ""
                self.selectProvincialCD = ""
                self.distritalTF.text = ""
                self.selectDistritalCD = ""
                
            }else if pickerView == self.pickerView_provincial {
                let value:Dictionary<String,String> = Provincial_CODE[row]
                self.provincialTF.text = value["CODE_NM"]
                self.selectProvincialCD = value["CODE"]!
                self.changeAreaTFSetting(Provincial: true, Distrital: true)
                
                self.distritalTF.text = ""
                self.selectDistritalCD = ""
                
            }else if pickerView == self.pickerView_distrital{
                let value:Dictionary<String,String> = Distrital_CODE[row]
                self.distritalTF.text = value["CODE_NM"]
                self.selectDistritalCD = value["CODE"]!
            }
        }
    }
    
    /// Función de cambio en pantalla y valor según la selección de categoría
    /// - Parameters:
    ///   - Provincial: Disponibilidad de entrada de Provincia
    ///   - Distrital: Disponibilidad de entrada de Distrito
    func changeAreaTFSetting(Provincial:Bool, Distrital:Bool){
        self.provincialTF.isEnabled = Provincial
        self.distritalTF.isEnabled = Distrital
        
        if Provincial{
            self.provincialTF.backgroundColor = commFunction().UIColorFromRGB(rgbValue: 0xFFFFFF, alpha: 0.4)
        }else{
            self.provincialTF.backgroundColor = commFunction().UIColorFromRGB(rgbValue: 0x5d5d5d, alpha: 0.6)
        }
        
        if Distrital{
            self.distritalTF.backgroundColor = commFunction().UIColorFromRGB(rgbValue: 0xFFFFFF, alpha: 0.4)
        }else{
            self.distritalTF.backgroundColor = commFunction().UIColorFromRGB(rgbValue: 0x5d5d5d, alpha: 0.6)
        }
    }
    
    @IBOutlet var mapView: MKMapView!
    @IBOutlet var changeBtn: UIButton!
    @IBOutlet var hint: UITextView!
    
    var paramISLLC_XCNTS:Double? //Ubicación de cuarentena X
    var paramISLLC_YDNTS:Double? //Ubicación de cuarentena Y
    var paramRaius:Double?       //Radio
    var paramAddress:String?     //Dirección
    var paramAddressETC:String?  //Otra dirección
    var paramSelectList:Int?
    
    var paramISLPRSN_XCNTS:Double? //Ubicación actual de la persona en cuarentena X
    var paramISLPRSN_YDNTS:Double? //Ubicación actual de la persona en cuarentena Y
    
    var locationUser : CLLocationCoordinate2D?
    let annotation = MKPointAnnotation()
    let annotationUser = MKPointAnnotation()
    var cycle:MKCircle!
    var currentMacker:CLLocationCoordinate2D?
    var distanceLine : [CLLocationCoordinate2D]!
    
    //Variable para dirección almacenada
    var ISLLC_XCNTS:String?
    var ISLLC_YDNTS:String?
    
    //Región, Provincia, Distrito
    //Almacenamiento para la comunicación del servidor
    var commonDict = Dictionary<String,String>()
    
    var Regional_CODE = [Dictionary<String,String>]()
    var Provincial_CODE : [Dictionary<String,String>]!
    var Distrital_CODE : [Dictionary<String,String>]!
    
    //Valor para la configuración de pantalla
    var regionalTF:UITextField!
    var provincialTF:UITextField!
    var distritalTF:UITextField!
    
    //Variable de almacenamiento temporal
    var selectRegionalCD:String = ""
    var selectProvincialCD:String = ""
    var selectDistritalCD:String = ""
    
    //Ir atrás
    @IBAction func goBack(_ sender: UIBarButtonItem) {
        self.presentingViewController?.dismiss(animated: true)
    }
    
    //Guardar ubicación
    //Al guardar la ubicación, se debe seleccionar el código de la Región,
    //por lo que se llama al servicio de código de Región por adelantado.
    @IBAction func onSave(_ sender: UIButton) {
        if self.Regional_CODE.count > 0{
            self.makeDetailAdressAlert(title: "Enter address".localized, message: "Enter a detailed address (optional)".localized)
        }else{
            //Servicio de búsqueda de Región en la lista
            commFunction().searchDEFT(view: self.view, resp: {
                response in
                
                switch response.result {
                case .success(let value):
                    
                    guard let json = value as? [String: Any] else {
                        self.makeAlert(title: "Gobierno Regional".localized, message: "Communication and server network is not fluid.".localized)
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
                        self.makeAlert(title: "Gobierno Regional".localized, message: msg ?? "Falló búsqueda de la Región.".localized)
                        Spinner.stop()
                        return
                    }
                    
                    let msg = decryptMSG.instance.msgDecryptSecure(msg: resp, key: self.pList.string(forKey: "seckey")!,iv: self.pList.string(forKey: "seckeyVector")!)
                    //Convertir a formato diccionario
                    let data :[Dictionary<String,Any>] = decryptMSG.instance.decrypt(enData: msg)
                    if data.count < 1 {self.makeAlert(title: "Search failed".localized, message: "No hay lista de la Región. ".localized); Spinner.stop();return}
                    
                    for row in data{
                        var dict = Dictionary<String,String>()
                        
                        if type(of: row["CODE"]!) == NSNull.self{
                            dict.updateValue("", forKey: "CODE")
                        }else{
                            dict.updateValue(row["CODE"]! as! String, forKey: "CODE")
                        }
                        dict.updateValue(row["CODE_NM"]! as! String, forKey: "CODE_NM")
                        self.Regional_CODE.append(dict)
                    }
                    self.Regional_CODE.remove(at: 0)
                    self.Regional_CODE.insert(self.commonDict, at: 0)
                    
                    //Ventana emergente para introducir dirección detallada
                    self.makeDetailAdressAlert(title: "Enter address".localized, message: "Enter a detailed address (optional)".localized)
                    
                case .failure(let error):
                    print(error)
                    self.makeAlert(title: "Communication failure".localized, message: "Communication and server network is not fluid.".localized)
                }
                Spinner.stop()
            })
        }
    }
    
    //Configuración de pantalla inicial
    override func viewDidLoad() {
        super.viewDidLoad()
        //Configuración del mapa (registro de marcador)
        self.setMap()

        //Aplicar diseño de botón
        let localeID = Locale.preferredLanguages.first
        let deviceLocale = (Locale(identifier: localeID!).languageCode)!
        
        //Registrar diseño de botón para guardar ubicación
        commFunction().setButtonUI(btn: self.changeBtn, title: "")
        if deviceLocale == "es"{
            self.changeBtn.setBackgroundImage(UIImage(named: "btn_location_spa.png"), for: .normal)
        }else{
            self.changeBtn.setBackgroundImage(UIImage(named: "btn_location_eng.png"), for: .normal)
        }
        
        //Registrar la parte superior para sugerencias
        self.hint.text = "Move the map and press the desired location for more than a second.".localized
        self.hint.textContainerInset = UIEdgeInsets(top: 5, left: 0, bottom: 0, right: 0)
        
        //Partes comunes generados anticipadamente que van al selector común
        self.commonDict.updateValue("", forKey: "CODE")
        self.commonDict.updateValue("Por favor seleccione.".localized, forKey: "CODE_NM")

    }
    
    //Salida emergente al hacer clic en el marcador de superposición
    func mapView(_ mapView: MKMapView, didDeselect view: MKAnnotationView) {
        mapView.deselectAnnotation(view.annotation, animated: true)
    }
    
    
    //Registro de imagen pin
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        if annotation is MKUserLocation {
            //return nil so map view draws "blue dot" for standard user location
            return nil
        }
        
        let reuseId = "pin"
        var pinView:MKPinAnnotationView!
        var _pinView:MKAnnotationView!
        
        //Registro de marcador según versión IOS
        if #available(iOS 13.0, *) {
            pinView = MKPinAnnotationView(annotation: annotation, reuseIdentifier: reuseId)
            pinView!.canShowCallout = true
            pinView!.pinTintColor = UIColor.blue
            pinView!.layer.anchorPoint = CGPoint(x: 0.7, y: 0.5)
        }else{
            _pinView = MKAnnotationView(annotation: annotation, reuseIdentifier: reuseId)
            _pinView!.canShowCallout = true
        }
        
        //Configuración de pantalla según versión
        if #available(iOS 13.0, *) {
            if annotation.title == "Quarantine location".localized{
                pinView!.image = UIImage(named: "dot_pin.png")
                let lb = UILabel(frame: CGRect(x: pinView!.frame.minX - 15, y: pinView!.frame.maxY + 25, width: 150, height: 20))
                lb.text = "Quarantine location".localized
                lb.font = UIFont.systemFont(ofSize: 15)
                pinView!.addSubview(lb)
            } else {
                pinView!.image = UIImage(named: "pin_man.png")
                let lb = UILabel(frame: CGRect(x: pinView!.frame.minX - 50, y: pinView!.frame.maxY + 15, width: 200, height: 20))
                lb.text = "Location of the Quarantine".localized
                lb.font = UIFont.systemFont(ofSize: 15)
                pinView!.addSubview(lb)
            }
            return pinView
        }else{
            if annotation.title == "Quarantine location".localized{
                _pinView!.image = UIImage(named: "dot_pin.png")
                let lb = UILabel(frame: CGRect(x: _pinView!.frame.minX - 10, y: _pinView!.frame.maxY + 25, width: 150, height: 20))
                lb.text = "Quarantine location".localized
                lb.font = UIFont.systemFont(ofSize: 15)
                _pinView!.addSubview(lb)
            } else {
                _pinView!.image = UIImage(named: "pin_man.png")
                let lb = UILabel(frame: CGRect(x: _pinView!.frame.minX - 50, y: _pinView!.frame.maxY + 15, width: 200, height: 20))
                lb.text = "Location of the Quarantine".localized
                lb.font = UIFont.systemFont(ofSize: 15)
                _pinView!.addSubview(lb)
            }
            return _pinView
        }
    }
    
    
    //Delegado de superposición
    //Si desea mostrar el radio de la ubicación en cuarentena, simplemente elimine la nota
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
        
        if let overlay = overlay as? MKCircle {
            var circleRenderer = MKCircleRenderer()
            //            circleRenderer = MKCircleRenderer(circle: overlay)
            //            circleRenderer.fillColor = UIColor.blue
            //            circleRenderer.strokeColor = .black
            //            circleRenderer.lineWidth = 1.0
            //            circleRenderer.alpha = 0.3
            return circleRenderer
        }else {
            let polyLine = MKPolylineRenderer(overlay: overlay)
            polyLine.strokeColor = UIColor.systemBlue.withAlphaComponent(0.5)
            polyLine.lineWidth = 3
            return polyLine
        }
    }
    
    //Configuración de mapa
    func setMap() {
        //Registrar delegado
        self.mapView.delegate = self
        
        //En caso de haber una ubicación de la persona en autocuarentena
        if self.paramISLPRSN_YDNTS != 0.0{
            self.locationUser = CLLocationCoordinate2D(latitude: self.paramISLPRSN_YDNTS!, longitude: self.paramISLPRSN_XCNTS!)
        }else{
            self.locationUser = nil
        }
        
        //En caso de haber una ubicación de cuarentena
        if self.paramISLLC_YDNTS != 0.0 {
            self.goPosition(lat: self.paramISLLC_YDNTS, lon: self.paramISLLC_XCNTS)
        }else{
            self.makeAlert(title: "Designated quarantine location is required.".localized, message: "")
            self.goPosition(lat: 0.0, lon: 0.0)
        }
        
        //Información sobre el clic largo del usuario
        let longTapGesture = UILongPressGestureRecognizer(target: self, action: #selector(longTap(sender:)))
        self.mapView.addGestureRecognizer(longTapGesture)
    }
    
    //Cuando el usuario hace clic largo, mueve el marcado y almacena las coordenadas para el selector
    @objc func longTap(sender: UIGestureRecognizer){
        if sender.state == .began {
            
            let locationInView = sender.location(in: mapView)
            let locationOnMap = mapView.convert(locationInView, toCoordinateFrom: mapView)
            
            //Guardar ubicación seleccionada
            self.currentMacker = locationOnMap
            self.paramAddressETC = ""
            //Registrar el marcador de ubicación seleccionado (eliminar primero)
            if let cycle = self.cycle{
                self.mapView.removeOverlay(cycle)
            }
            
            self.addAnnotation(location: locationOnMap, isChange: true)
            
        }
    }
    
    //Mapa visualizando la latitud y longitud actual
    func goPosition(lat:CLLocationDegrees?, lon:CLLocationDegrees?) {
        //Ubicación de autocuarentena
        let location:CLLocationCoordinate2D?
        if lat != 0.0 && lon != 0.0{
            location = CLLocationCoordinate2D(latitude: lat!, longitude: lon!)
        }else{
            location = nil
        }
        
        self.addAnnotation(location: location, isChange: false)
    }
    
    //Regitrar marcador
    func addAnnotation(location: CLLocationCoordinate2D?, isChange:Bool){
        self.mapView.removeAnnotation(self.annotation)
        self.mapView.removeAnnotation(self.annotationUser)
        
        //Registrar ubicación de autocuarentena
        if let loc = location{
            annotation.coordinate = loc
            annotation.title = "Quarantine location".localized
            self.cycle = MKCircle(center: loc, radius: self.paramRaius!)
            self.mapView.addOverlay(self.cycle)
            self.mapView.addAnnotation(annotation)
        }
        
        //Regitrar a la persona en autocuarentena
        if let user = self.locationUser{
            annotationUser.coordinate = user
            annotationUser.title = "Location of the Quarantine".localized
            self.mapView.addAnnotation(annotationUser)
        }
        
        
        var bound = self.paramRaius
        
        //Mostrar la ubicación de la persona en cuarentena y para visualizar las dos coordenadas (X, Y) en la pantalla
        //Vuelva a crear los límites utilizando la distancia entre dos puntos
        if let user = self.locationUser, let loc = location{
            
            
            //Medir la distancia entre dos puntos
            let IsolationPos = CLLocation(latitude: loc.latitude, longitude: loc.longitude)
            
            let UserPos = CLLocation(latitude: user.latitude, longitude: user.longitude)
            let distance = IsolationPos.distance(from: UserPos)
            bound = distance
        }
        
        //En caso de que la persona en autocuarentena tenga una ubicación designar enfocado a la persona,
        //pero si la persona en autocuarentena no tiene ubicación se le cambia esta ubicación en base al punto de cuarentena
        if !isChange, let user = self.locationUser{
            var regionRadius: CLLocationDistance = bound!
            if regionRadius < 3000.0{
                regionRadius = bound! * 3
            }
            let coordinateRegion = MKCoordinateRegion(center: user, latitudinalMeters: regionRadius, longitudinalMeters: regionRadius)
            self.mapView.setRegion(coordinateRegion, animated: false)
        }else if !isChange, let loc = location{
            let regionRadius: CLLocationDistance = bound ?? 100
            let coordinateRegion = MKCoordinateRegion(center: loc, latitudinalMeters: regionRadius, longitudinalMeters: regionRadius)
            self.mapView.setRegion(coordinateRegion, animated: false)
        }
        
    }
    
    //Alerta común
    func makeAlert(title:String, message:String){
        let mAlert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let cancle = UIAlertAction(title: "Confirm".localized, style: .cancel, handler: nil)
        mAlert.addAction(cancle)
        self.present(mAlert, animated: true, completion: nil)
    }
    
    //Ventana emergente para registrar detalles de dirección
    var alert:UIAlertController!
    func makeDetailAdressAlert(title: String, message: String) {
        alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let okButton = UIAlertAction(title: "Change".localized, style: .default) { (okBtn) in
            let pvc = self.presentingViewController
            guard let mvc = pvc as? MainViewController else {
                return
            }
            
            guard let model = mvc.respData?[self.paramSelectList!] else {
                return
            }
            
            guard self.selectRegionalCD != "", self.selectProvincialCD != "", self.selectDistritalCD != "" else{
                self.makeAlert(title: "Necesita cambiar".localized, message: "Seleccione su región, provincia y distrito.".localized)
                return
            }

            let url = serverAddress().main
            
            //Si no ha movido ningún punto, se enviará la ubicación como lo recibió
            self.ISLLC_XCNTS = "\(self.currentMacker?.longitude ?? self.paramISLLC_XCNTS ?? 0.0)"
            self.ISLLC_YDNTS = "\(self.currentMacker?.latitude ?? self.paramISLLC_YDNTS ?? 0.0)"
            
            var PARM = Dictionary<String,String>()
            PARM.updateValue(model.ISLPRSN_SN!, forKey: "ISLPRSN_SN")
            PARM.updateValue(model.TRMNL_SN!, forKey: "TRMNL_SN")
            PARM.updateValue(model.TELNO!, forKey: "TELNO")
            PARM.updateValue(self.ISLLC_XCNTS ?? "", forKey: "ISLLC_XCNTS")
            PARM.updateValue(self.ISLLC_YDNTS ?? "", forKey: "ISLLC_YDNTS")
            //지역코드
            PARM.updateValue(self.selectRegionalCD , forKey: "ISLLC_DPRTMNT_CODE")
            PARM.updateValue(self.selectProvincialCD, forKey: "ISLLC_PRVNCA_CODE")
            PARM.updateValue(self.selectDistritalCD, forKey: "ISLLC_DSTRT_CODE")

            if self.alert.textFields?[0].text != "" {
                self.paramAddressETC = self.alert.textFields?[0].text ?? ""
                PARM.updateValue(self.paramAddressETC ?? "", forKey: "ISLPRSN_ETC_ADRES")
            } else {
                PARM.updateValue("", forKey: "ISLPRSN_ETC_ADRES")
            }
            
            let param = PARM.printJson()
            print(param)
            
            let paramEN = try! param.aesEncrypt(key: decryptMSG.instance.key128, iv: decryptMSG.instance.iv)
            let listService = sendModelEN(IFID: "PERU0005", PARM: paramEN)
            
            let headers:HTTPHeaders = [
                "Accept":"application/json",
                "Content-Type":"application/json; charset=utf-8"
            ]
            Spinner.start(from: self.view)
            AF.request(url,method: .post, parameters: listService, encoder: JSONParameterEncoder.default, headers: headers)
                .responseJSON(){
                    response in
                    
                    print(response.debugDescription)
                    switch response.result {
                    case .success(let value):
                        
                        guard let json = value as? [String: Any], let code = json["RES_CD"] as? String else {
                            self.makeAlert(title: "", message: "Location could not be changed.".localized)
                            Spinner.stop()
                            return
                        }
                        
                        if commFunction().isCodeAboutSecureKey(code: code) {
                            commFunction().showPreferClearDialog(vc: self)
                            return
                        }
                        
                        guard let _ = json["RES_DATA"] as? String else {
                            let msg = json["RES_MSG"] as? String
                            self.makeAlert(title: "Location change failed".localized, message: msg ?? "Location could not be changed.".localized)
                            Spinner.stop()
                            return
                        }
                        
                        if code == "100" {
                            model.ISLLC_XCNTS = self.ISLLC_XCNTS
                            model.ISLLC_YDNTS = self.ISLLC_YDNTS
                            model.ISLLC_ETC_ADRES = self.paramAddressETC
                            
                            self.makeAlert(title: "Successful registration".localized, message: "Location of the quarantine has changed.".localized)
                        }else{
                            self.makeAlert(title: "Location change failed".localized, message: "Location could not be changed.".localized)
                        }
                        
                    case .failure(let error):
                        print(error)
                        self.makeAlert(title: "Communication failure".localized, message: "Communication and server network is not fluid.".localized)
                    }
                    Spinner.stop()
            }
        }
        
        let cancelButton = UIAlertAction(title: "Cancle".localized, style: .cancel, handler: nil)
        
        //Agregar vista de selección de dirección de usuario
        var alWidth:CGFloat!
        if self.view.frame.width < 330 {
            alWidth = self.view.frame.width - 90
        }else{
            alWidth = (self.view.frame.width / 5 ) * 3 - 10
        }
        
        let container = UIViewController()
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
        container.view = view
        self.alert.setValue(container, forKey: "contentViewController")
        
        self.alert.addTextField { (textField) in
            textField.placeholder = "Enter your address.".localized
        }
        
        self.alert.addAction(okButton)
        self.alert.addAction(cancelButton)
        
        var alertHeight:CGFloat = 0
        
        //Crear pantalla para selección de Región, Provincia, Distrito
        self.regionalTF = UITextField()
        self.provincialTF = UITextField()
        self.distritalTF = UITextField()
        
        //Región
        let regionalNM = UILabel()
        regionalNM.frame = CGRect(x: 15, y: self.alert.view.frame.minY, width: 0, height: 15)
        regionalNM.text = "Gobierno Regional".localized
        regionalNM.sizeToFit()
        
        regionalTF.frame = CGRect(x: 15, y: regionalNM.frame.maxY + 2, width: alWidth, height: 0)
        commFunction().setTextFeild(sender: regionalTF, height: 35, placeholder: "Seleccionar Región", fontSize: 15, isEnable: true)
        commFunction().ImgRightView(tf: regionalTF, image: "downArrow.png")
        regionalTF.inputView = pickerView_regional
        setInputViewDatePicker(sender: regionalTF, target: self)
        regionalTF.delegate = self
        regionalTF.tag = 0
        
        view.addSubview(regionalNM)
        view.addSubview(regionalTF)
        
        //Provincia
        let provincialNM = UILabel()
        provincialNM.frame = CGRect(x: 15, y: self.regionalTF.frame.maxY + 5, width: 0, height: 15)
        provincialNM.text = "Gobierno Provincial".localized
        provincialNM.sizeToFit()
        
        provincialTF.frame = CGRect(x: 15, y: provincialNM.frame.maxY + 2, width: alWidth, height: 0)
        commFunction().setTextFeild_dark(sender: provincialTF, height: 35, placeholder: "Seleccionar Provincia", fontSize: 15, isEnable: true)
        commFunction().ImgRightView(tf: provincialTF, image: "downArrow.png")
        provincialTF.inputView = pickerView_provincial
        setInputViewDatePicker(sender: provincialTF, target: self)
        provincialTF.delegate = self
        provincialTF.tag = 1
        
        view.addSubview(provincialNM)
        view.addSubview(provincialTF)
        
        //Distrito
        let distritalNM = UILabel()
        distritalNM.frame = CGRect(x: 15, y: self.provincialTF.frame.maxY + 5, width: 0, height: 15)
        distritalNM.text = "Gobierno Distrital".localized
        distritalNM.sizeToFit()
        
        distritalTF.frame = CGRect(x: 15, y: distritalNM.frame.maxY + 2, width: alWidth, height: 0)
        commFunction().setTextFeild_dark(sender: distritalTF, height: 35, placeholder: "Seleccionar Distrito", fontSize: 15, isEnable: true)
        commFunction().ImgRightView(tf: distritalTF, image: "downArrow.png")
        distritalTF.inputView = pickerView_distrital
        setInputViewDatePicker(sender: distritalTF, target: self)
        distritalTF.delegate = self
        distritalTF.tag = 3
        
        view.addSubview(distritalNM)
        view.addSubview(distritalTF)
        
        //Establecer la altura de la pantalla como la suma de cada valor
        alertHeight = regionalNM.frame.height + regionalTF.frame.height
            + provincialNM.frame.height + provincialTF.frame.height
            + distritalNM.frame.height + distritalTF.frame.height
        
        container.preferredContentSize = CGSize(width: self.alert.view.frame.width, height: alertHeight + 30)
        self.present(self.alert, animated: true, completion: nil)
    }
    
    
    /// Configuración de pantalla común para la vista del selector
    /// - Parameters:
    ///   - sender: Campo de texto
    ///   - target: Vista para mostrar
    func setInputViewDatePicker(sender:UITextField, target: Any) {
        let toolBar = UIToolbar(frame: CGRect(x: 0.0, y: 0.0, width: self.view.frame.width, height: 44.0)) //4
        let flexible = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil) //5
        let barButton = UIBarButtonItem(title: "Close".localized, style: .plain, target: target, action: #selector(tapCancel)) //7
        toolBar.setItems([flexible, barButton], animated: false) //8
        sender.inputAccessoryView = toolBar //9
    }
    
    //Bajar el teclado
    @objc func tapCancel() {
        self.alert.view.endEditing(true)
    }
    
    //Vista de selector de Región
    lazy var pickerView_regional: UIPickerView = {
        // Generate UIPickerView.
        let picker = UIPickerView()
        // Specify the size.
        picker.frame = CGRect(x: 0, y: 150, width: self.view.bounds.width, height: 250.0)
        // Set the backgroundColor.
        picker.backgroundColor = .white
        // Set the delegate.
        picker.delegate = self
        // Set the dataSource.
        picker.dataSource = self
        return picker
    }()
    
    //Vista de selector de Provincia
    lazy var pickerView_provincial: UIPickerView = {
        // Generate UIPickerView.
        let picker = UIPickerView()
        // Specify the size.
        picker.frame = CGRect(x: 0, y: 150, width: self.view.bounds.width, height: 250.0)
        // Set the backgroundColor.
        picker.backgroundColor = .white
        // Set the delegate.
        picker.delegate = self
        // Set the dataSource.
        picker.dataSource = self
        return picker
    }()
    
    //Vista de selector de Distrito
    lazy var pickerView_distrital: UIPickerView = {
        // Generate UIPickerView.
        let picker = UIPickerView()
        // Specify the size.
        picker.frame = CGRect(x: 0, y: 150, width: self.view.bounds.width, height: 250.0)
        // Set the backgroundColor.
        picker.backgroundColor = .white
        // Set the delegate.
        picker.delegate = self
        // Set the dataSource.
        picker.dataSource = self
        return picker
    }()
}
