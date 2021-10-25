//
//  ClinicViewController.swift
//  SQSM_ios_peru
//
//  Created by 개발용 맥북 on 2020/05/06.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import MapKit
import Alamofire

class ClinicViewController: UIViewController, MKMapViewDelegate {
    typealias response = (AFDataResponse<Any>) -> Void
    
    //MARK: - outlet
    @IBOutlet var naviItem: UINavigationItem!
    @IBOutlet var mapView: MKMapView!
    @IBOutlet var googleMapButton: UIButton!
    
    //MARK: - variable
    var param_x : String?
    var param_y : String?
    let annotation = MKPointAnnotation()
    var qurantineLocation:CLLocationCoordinate2D?
    var circle:MKCircle?
    var mapItem : MKMapItem?
    var minimumHeight : CGFloat?
    var maximumHeight : CGFloat?
    var button : UIButton?
    
    private var clinicInfoHandler : response!
    
    //MARK: - lifeCycle
    override func viewDidLoad() {
        super.viewDidLoad()
        self.mapView.delegate = self
        
        let pList = UserDefaults.standard
        param_x = pList.string(forKey: StaticString.userInfoPreference.xCoordinate.rawValue)
        param_y = pList.string(forKey: StaticString.userInfoPreference.yCoordinate.rawValue)
        
        button = UIButton(frame: CGRect(x: 0, y: 0, width: 24, height: 24))
        button?.setImage(UIImage(named: "phone_black.png"), for: .normal)
        
        clinicInfoHandler = ({
            response in
            Spinner.stop()
            
            switch response.result {
            case .success(let value):
                guard let json = value as? [String: Any] else {
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue, result: nil)
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
                    commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.serviceError.rawValue, result: nil)
                    return
                }
                
                if commFunction.comm.isSecureKeyExist() {
                    let key = commFunction.comm.preference.string(forKey: StaticString.encryption.SECURE_KEY.rawValue)!
                    let vector = commFunction.comm.preference.string(forKey: StaticString.encryption.SECURE_VECTOR.rawValue)!
                    let msg = decryptMSG.shared.msgDecryptSecure(msg: resp, key: key, iv: vector)
                    let data :[Dictionary<String,Any>] = decryptMSG.shared.decrypt(enData: msg)
                    if data.count < 1 {return}
                    print(data)
                    
                    var clinicInfoArray = Array<ClinicInfo>()
                    
                    for value in data {
                        let clinic : Dictionary<String,Any> = value
                        var newClinicInfo = ClinicInfo()
                        for key in clinic.keys {
                            
                            switch key {
                            case "MDLCNST_SN":
                                newClinicInfo.MDLCNST_SN = clinic[key] as? String
                            case "MDLCNST_SE_CODE":
                                newClinicInfo.MDLCNST_SE_CODE = clinic[key] as? String
                            case "MDLCNST_SE_CODE_NM":
                                newClinicInfo.MDLCNST_SE_CODE_NM = clinic[key] as? String
                            case "MDLCNST_NM":
                                newClinicInfo.MDLCNST_NM = clinic[key] as? String
                            case "MDLCNST_XCNTS":
                                newClinicInfo.MDLCNST_XCNTS = clinic[key] as? String
                            case "MDLCNST_YDNTS":
                                newClinicInfo.MDLCNST_YDNTS = clinic[key] as? String
                            case "MDLCNST_ADRES":
                                newClinicInfo.MDLCNST_ADRES = clinic[key] as? String
                            case "CTTPC":
                                newClinicInfo.CTTPC = clinic[key] as? String
                            default:
                                break
                            }
                        }
                        clinicInfoArray.append(newClinicInfo)
                    }
                    
                    if clinicInfoArray.isEmpty {
                        commFunction.comm.makeToast(controller: self, message: StaticString.dialogText.empty.rawValue, result: nil)
                    } else {
                        for info in clinicInfoArray {
                            if info.MDLCNST_XCNTS != nil && info.MDLCNST_YDNTS != nil {
                                let annotation = MKPointAnnotation()
                                let x = Double(info.MDLCNST_XCNTS!)!
                                let y = Double(info.MDLCNST_YDNTS!)!
                                annotation.coordinate = CLLocationCoordinate2D(latitude: y, longitude: x)
                                annotation.title = info.MDLCNST_NM
                                annotation.subtitle = info.CTTPC
                                self.mapView.addAnnotation(annotation)
                            }
                        }
                    }
                }
                
            case .failure(let error):
                commFunction.comm.makeToast(controller: self, message: StaticString.serviceErrorText.coommunicationError.rawValue, result: nil)
                print(error)
            }
        })
        
        if let stringX = param_x, let stringY = param_y {
            if let qurantineX = Double(stringX), let qurantineY = Double(stringY) {
                qurantineLocation = CLLocationCoordinate2D(latitude: qurantineY, longitude: qurantineX)
                goPosition(lat: qurantineY, lon: qurantineX)
                Services.Service.searchClinicService(view: self.view, resp: clinicInfoHandler)
            }
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.googleMapButton.setTitle(StaticString.clinicString.googleMap.rawValue.localized, for: .normal)
    }
    
    //MARK: - ui action
    
    /// Ir atrás
    @IBAction func onClickBack(_ sender: UIBarButtonItem) {
        self.navigationController?.popViewController(animated: true)
    }
    
    /// Ir al mapa web
    @IBAction func onClickWebMap(_ sender: UIButton) {
        let urlString = self.isQurantineCoordinateExist()
        guard let url = URL(string: urlString), UIApplication.shared.canOpenURL(url) else { return }
        UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }
    
    //MARK: - function
    /// registro de marcador
    func addAnnotation(location: CLLocationCoordinate2D){
        self.mapView.removeAnnotation(self.annotation)
        if let cic = self.circle {
            self.mapView.removeOverlay(cic)
        }
        annotation.coordinate = location
        if param_x != "" {
            annotation.title = StaticString.mapString.qurantineAdress.rawValue.localized
        }
        self.mapView.addAnnotation(annotation)
    }
    
    /// Mover el mapa a la ubicación de autocuarentena
    func goPosition(lat:CLLocationDegrees, lon:CLLocationDegrees) {
        let location = CLLocationCoordinate2D(latitude: lat, longitude: lon)
        let regionRadius: CLLocationDistance = (Double(commFunction.comm.preference.string(forKey: "DISTANCE") ?? "100"))! * 3
        let coordinateRegion = MKCoordinateRegion(center: location, latitudinalMeters: regionRadius, longitudinalMeters: regionRadius)
        self.mapView.setRegion(coordinateRegion, animated: true)
        if param_x != "" {
            self.addAnnotation(location: location)
        }
    }
    
    /// Cambiar imagen de pin
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        if annotation is MKUserLocation || (annotation.coordinate.longitude == self.qurantineLocation?.longitude && annotation.coordinate.latitude == self.qurantineLocation?.latitude) {
            //return nil so map view draws "blue dot" for standard user location
            return nil
        }
        
        let reuseId = "hospital"
        var pinView:MKAnnotationView!
        pinView = MKAnnotationView(annotation: annotation, reuseIdentifier: reuseId)
        
        if pinView != nil {
            return pinViewSetting(pinView: pinView)
        } else {
            return nil
        }
    }
    
    /// Configuración inicial de pin
    func pinViewSetting(pinView : MKAnnotationView) -> MKAnnotationView {
        pinView.image = UIImage(named: "hospital.png")
        pinView.canShowCallout = true
        pinView.rightCalloutAccessoryView = self.button
        return pinView
    }
    
    /// Llamar al hospital seleccionado
    func mapView(_ mapView: MKMapView, annotationView view: MKAnnotationView, calloutAccessoryControlTapped control: UIControl) {
        if let phoneCallURL = URL(string: "tel://\(String(describing: view.annotation?.subtitle))") {
            let application:UIApplication = UIApplication.shared
            if (application.canOpenURL(phoneCallURL)) {
                application.open(phoneCallURL, options: [:], completionHandler: nil)
            }
        } else {
            commFunction.comm.makeToast(controller: self, message: "Número de teléfono celular es equivocado.".localized, result: nil)
        }
    }
    
    /// Regresar a la dirección de Google Map que refleja las coordenadas de cuarentena
    /// - Returns: Si hay un valor de coordenadas, si no hay un mapa de Google de la coordenada, solo el mapa de Google
    func isQurantineCoordinateExist() -> String {
        if self.qurantineLocation?.latitude != nil && self.qurantineLocation?.longitude != nil {
            return "https://www.google.com/maps/search/Farmacia/@\(self.qurantineLocation!.latitude),\(self.qurantineLocation!.longitude),15z"
        } else {
            return "https://www.google.com/maps/search/Farmacia"
        }
    }
}
