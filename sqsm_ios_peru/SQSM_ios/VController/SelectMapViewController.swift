//
//  ViewController.swift
//  TestSampleApp
//
//  Created by wini on 2020/02/11.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation
import Alamofire

class SelectMapViewController: UIViewController, CLLocationManagerDelegate, MKMapViewDelegate {
    //MARK: - outlet
    @IBOutlet var mapView: MKMapView!
    @IBOutlet var locationSelect: UIButton!
    @IBOutlet var topView: UIView!
    @IBOutlet var topTextView: UITextView!
    @IBOutlet var buttonView: UIView!
    @IBOutlet var buttonLabel: UILabel!
    
    //MARK: - variable
    var locationManager : CLLocationManager!
    let annotation = MKPointAnnotation()
    var currentMacker:CLLocationCoordinate2D?
    var userLocation:CLLocationCoordinate2D?
    let pUserDate = UserDefaults.standard
    var circle:MKCircle?
    
    var param_x:String = ""
    var param_y:String = ""
    
    //MARK: - lifeCycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if param_x != "" && param_y != ""{
            self.topView.isHidden = true
            self.buttonView.isHidden = true
            self.userLocation = CLLocationCoordinate2D(latitude: Double(param_y)!, longitude: Double(param_x)!)
            self.setMap()
        } else {
            self.setMap()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        if let _ = self.presentingViewController as? UITabBarController {
            self.buttonView.isHidden = true
        }
        
        self.buttonView.layer.cornerRadius = 5
        self.buttonLabel.text = StaticString.mapString.locationRegistration.rawValue.localized
    }
    
    //MARK: - ui action
    
    /// Ir atrás
    @IBAction func goBack(_ sender: UIBarButtonItem) {
        //        self.presentingViewController?.dismiss(animated: true, completion: nil)
        self.navigationController?.popViewController(animated: true)
    }
    
    //Guardar ubicación del usuario
    @IBAction func onSave(_ sender: UIButton) {
        let preVC = self.presentingViewController
        if let tbc = preVC as? UITabBarController {
            let _ = tbc.viewControllers![3] as! SettingViewController
            
            let pList = UserDefaults.standard
            let la : Double = self.currentMacker!.latitude
            let lo : Double = self.currentMacker!.longitude
            
            pList.setValue(lo.string, forKey: StaticString.userInfoPreference.xCoordinate.rawValue)
            pList.setValue(la.string, forKey: StaticString.userInfoPreference.yCoordinate.rawValue)
            pList.synchronize()
            self.navigationController?.popViewController(animated: true)
        } else {
            if self.currentMacker != nil {
                //Registro de ubicación en la primera ejecución
                let pList = UserDefaults.standard
                let la : Double = self.currentMacker!.latitude
                let lo : Double = self.currentMacker!.longitude
                
                pList.setValue(lo.string, forKey: StaticString.userInfoPreference.xCoordinate.rawValue)
                pList.setValue(la.string, forKey: StaticString.userInfoPreference.yCoordinate.rawValue)
                pList.synchronize()
                self.navigationController?.popViewController(animated: true)
            }
        }
    }
    
    //MARK: - function
    
    /// Configuraciones de mapa
    func setMap() {
        self.mapView.delegate = self
        self.locationManager = CLLocationManager()
        self.locationManager.delegate = self
        self.locationManager.allowsBackgroundLocationUpdates = true //항상 사용을 위해 수정 (백그라운드 콜)
        self.locationManager.requestAlwaysAuthorization()
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        
        //Disponer longclick cuando el usuario está en pantalla de registro
        if self.param_x == "" {
            let longTapGesture = UILongPressGestureRecognizer(target: self, action: #selector(longTap(sender:)))
            self.mapView.addGestureRecognizer(longTapGesture)
        }
        
        //Actualización de ubicación
        if let location = self.userLocation{
            self.goPosition(lat: location.latitude, lon: location.longitude)
        }else{
            self.locationManager.startUpdatingLocation()
            if let coor = self.locationManager.location?.coordinate {
                self.goPosition(lat: coor.latitude, lon: coor.longitude)
            }else{
                //Cuando no hay coordenadas
            }
        }
    }
    
    /// Agregar regonocimiento al presionar largo sobre el mapa
    @objc func longTap(sender: UIGestureRecognizer){
        if sender.state == .began {
            //            self.topView.isHidden = true
            let locationInView = sender.location(in: mapView)
            let locationOnMap = mapView.convert(locationInView, toCoordinateFrom: mapView)
            //선택한 위치 저장
            self.currentMacker = locationOnMap
            //선택한 위치 마커등록
            self.addAnnotation(location: locationOnMap)
        }
    }
    
    /// Registro de marcador
    func addAnnotation(location: CLLocationCoordinate2D){
        self.mapView.removeAnnotation(self.annotation)
        if let cic = self.circle {
            self.mapView.removeOverlay(cic)
        }
        annotation.coordinate = location
        if param_x != "" {
            annotation.title = StaticString.mapString.qurantineLocation.rawValue.localized
        } else {
            annotation.title = StaticString.mapString.chooseCoordinate.rawValue.localized
        }
        self.mapView.addAnnotation(annotation)
    }
    
    ///Ocultar información adicional del marcador
    func mapView(_ mapView: MKMapView, didDeselect view: MKAnnotationView) {
        mapView.deselectAnnotation(view.annotation, animated: true)
    }
    
    /// Aplicar imágenes del marcador
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        if annotation is MKUserLocation {
            //return nil so map view draws "blue dot" for standard user location
            return nil
        }
        print(annotation.self)
        
        let reuseId = "pin"
        var pinView:MKPinAnnotationView!
        var _pinView:MKAnnotationView!
        if #available(iOS 13.0, *) {
            pinView = MKPinAnnotationView(annotation: annotation, reuseIdentifier: reuseId)
            pinView!.canShowCallout = true
            pinView!.pinTintColor = UIColor.blue
            pinView!.layer.anchorPoint = CGPoint(x: 0.7, y: 0.5)
        }else{
            _pinView = MKAnnotationView(annotation: annotation, reuseIdentifier: reuseId)
            _pinView!.canShowCallout = true
        }
        
        
        if pinView != nil {
            let xPosition: CGFloat?
            if StaticString.mapString.qurantineAdress.rawValue.count > 7 {
                xPosition = pinView!.frame.minX - 40
            } else {
                xPosition = pinView!.frame.minX - 10
            }
            pinView!.image = UIImage(named: "dot_pin.png")
            let lb = UILabel(frame: CGRect(x: xPosition! + 10, y: pinView!.frame.maxY + 25, width: 0, height: 20))
            lb.text = StaticString.mapString.qurantineAdress.rawValue.localized
            lb.sizeToFit()
            lb.font = UIFont.systemFont(ofSize: 15)
            pinView!.addSubview(lb)
            
            return pinView
        }else{
            let xPosition: CGFloat?
            if StaticString.mapString.qurantineAdress.rawValue.count > 7 {
                xPosition = _pinView!.frame.minX - 60
            } else {
                xPosition = _pinView!.frame.minX - 10
            }
            _pinView!.image = UIImage(named: "dot_pin.png")
            let lb = UILabel(frame: CGRect(x: xPosition!, y: _pinView!.frame.maxY + 25, width: 0, height: 20))
            lb.text = StaticString.mapString.qurantineAdress.rawValue.localized
            lb.sizeToFit()
            lb.font = UIFont.systemFont(ofSize: 15)
            _pinView!.addSubview(lb)
            
            return _pinView
        }
    }
    
    /// Mover el mapa a una ubicación de la coordenada
    func goPosition(lat:CLLocationDegrees, lon:CLLocationDegrees) {
        let location = CLLocationCoordinate2D(latitude: lat, longitude: lon)
        let regionRadius: CLLocationDistance = (Double(pUserDate.string(forKey: "DISTANCE") ?? "100"))! * 3
        let coordinateRegion = MKCoordinateRegion(center: location, latitudinalMeters: regionRadius, longitudinalMeters: regionRadius)
        self.mapView.setRegion(coordinateRegion, animated: true)
        if param_x != "" {
            self.addAnnotation(location: location)
        }
    }
    
    /// Actualizar coordenadas de ubicación actual
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        //Actualización de ubicación
        self.locationManager.startUpdatingLocation()
        guard let coor = self.locationManager.location?.coordinate  else {
            return
        }
        self.userLocation = coor
    }
}

