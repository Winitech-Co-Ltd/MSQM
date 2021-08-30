//
//  commExtension.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/05/06.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import UIKit
import CryptoSwift


//MARK: -VIEW
//Extensión sobre la vista de borde puntual
extension UIView {
    func roundCorners(corners: UIRectCorner, radius: CGFloat) {
        let path = UIBezierPath(roundedRect: bounds, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        let mask = CAShapeLayer()
        mask.path = path.cgPath
        layer.mask = mask
    }
    
    func addDashBorder() {
        let color = UIColor.blue.cgColor
        let shapeLayer:CAShapeLayer = CAShapeLayer()
        let frameSize = self.frame.size
        let shapeRect = CGRect(x: 0, y: 0, width: frameSize.width, height: frameSize.height)
        
        shapeLayer.bounds = shapeRect
        shapeLayer.name = "DashBorder"
        shapeLayer.position = CGPoint(x: frameSize.width/2, y: frameSize.height/2)
        shapeLayer.fillColor = UIColor.clear.cgColor
        shapeLayer.strokeColor = color
        shapeLayer.lineWidth = 1.5
        shapeLayer.lineJoin = .round
        shapeLayer.lineDashPattern = [2,4]
        shapeLayer.path = UIBezierPath(roundedRect: shapeRect, cornerRadius: 10).cgPath
        self.layer.masksToBounds = true
        self.layer.addSublayer(shapeLayer)
    }
}

//Extensión para configurar márgenes de campo de texto
extension UITextField {
    //Dar margen izquierdo
    func setLeftPaddingPoints(_ amount:CGFloat){
        let paddingView = UIView(frame: CGRect(x: 0, y: 0, width: amount, height: self.frame.size.height))
        self.leftView = paddingView
        self.leftViewMode = .always
    }
    
    //Dar margen a la derecha
    func setRightPaddingPoints(_ amount:CGFloat) {
        let paddingView = UIView(frame: CGRect(x: 0, y: 0, width: amount, height: self.frame.size.height))
        self.rightView = paddingView
        self.rightViewMode = .always
    }
}

//Código para aplicar el color presionado cuando se presiona el botón
extension UIButton {
    func setBackgroundColor(color: UIColor, forState: UIControl.State) {
        let size = CGSize(width: 1, height: 1)
        UIGraphicsBeginImageContext(size)
        let context = UIGraphicsGetCurrentContext()
        context?.setFillColor(color.cgColor)
        context?.fill(CGRect(origin: CGPoint.zero, size: size))
        let colorImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        setBackgroundImage(colorImage, for: forState)
    }
    
}

//Extensión para títulos de pantalla y mensajes que aparecen al seleccionar la lista de las personas en autocuarentena
extension UIAlertController {
    //Color de fondo
    func setBackgroundColor(color: UIColor) {
        if let bgView = self.view.subviews.first, let groupView = bgView.subviews.first, let contentView = groupView.subviews.first {
            contentView.backgroundColor = color
        }
    }
    
    //Título
    func setTitlet(color: UIColor?) {
        guard let title = self.title else { return }
        let attributedString = NSAttributedString(string: title, attributes: [NSAttributedString.Key.font: UIFont.boldSystemFont(ofSize: 18), NSAttributedString.Key.foregroundColor: color as Any])
        self.setValue(attributedString, forKey: "attributedTitle")//4
    }
    
    //Mensaje
    func setMessage(color: UIColor?) {
        guard let message = self.message else { return }
        let attributedString = NSAttributedString(string: message, attributes: [NSAttributedString.Key.font: UIFont.boldSystemFont(ofSize: 16), NSAttributedString.Key.foregroundColor: color as Any])
        self.setValue(attributedString, forKey: "attributedMessage")//4
    }
    
    //Color del botón
    func setBottomColor(color: UIColor) {
        self.view.tintColor = color
    }
}

//Configuración en común para aplicar al botón de cierre de teclado
extension UIToolbar {
    func ToolbarPiker(mySelect : Selector) -> UIToolbar {
        let toolBar = UIToolbar()
        toolBar.barStyle = UIBarStyle.default
        toolBar.isTranslucent = true
        toolBar.tintColor = UIColor.black
        toolBar.sizeToFit()
        let doneButton = UIBarButtonItem(title: "Close".localized, style: UIBarButtonItem.Style.plain, target: self, action: mySelect)
        let spaceButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        toolBar.setItems([ spaceButton, doneButton], animated: false)
        toolBar.isUserInteractionEnabled = true
        
        return toolBar
    }
}


//MARK: -FUNCTION
//Extensión que verifica si el diccionario está en formato Josn
extension Dictionary {
    var json: String {
        let invalidJson = "Not a valid JSON".localized
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: self, options: .prettyPrinted)
            return String(bytes: jsonData, encoding: String.Encoding.utf8) ?? invalidJson
        } catch {
            return invalidJson
        }
    }
    
    func printJson() -> String {
        return json
    }
    
}

//Extensión sobre el String
extension String {
    var localized: String {
        return NSLocalizedString(self, tableName: "Locale", value: self, comment: "")
    }
    
    func convertToDictionary() -> [String: Any]? {
        if let data = self.data(using: .utf8) {
            do {
                return try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            } catch {
                print(error.localizedDescription)
            }
        }
        return nil
    }
    
    func stringTrim() -> String{
        return self.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
    }
    
    func fromBase64() -> String?{
        guard let data = Data(base64Encoded: self, options: Data.Base64DecodingOptions(rawValue: 0)) else {
            return nil
        }
        return String(data: data as Data, encoding: String.Encoding.utf8)
    }
    
    func toBase64() -> String?{
        guard let data = self.data(using: String.Encoding.utf8) else{
            return nil
        }
        return data.base64EncodedString(options: Data.Base64EncodingOptions(rawValue: 0))
    }
    
    //Cifrado aes
    func aesEncrypt(key: String, iv:String) throws -> String {
        
        var result = ""
        
        do {
            
            let key: [UInt8] = Array(key.utf8) as [UInt8]
            let iv :[UInt8]      = Array(iv.utf8)
            let aes = try! AES(key: key, blockMode: CBC(iv: iv), padding: .pkcs5)
            let encrypted = try aes.encrypt(Array(self.utf8))
            
            result = encrypted.toBase64()!
            
        } catch {
            
            print("Error: \(error)")
        }
        
        return result
    }
    //Descifrado aes
    func aesDecrypt(key: String, iv:String) throws -> String {
        
        var result = ""
        
        do {
            
            let encrypted = self
            let key: [UInt8] = Array(key.utf8) as [UInt8]
            let iv :[UInt8]      = Array(iv.utf8)
            let aes = try! AES(key: key, blockMode: CBC(iv: iv), padding: .pkcs5)
            let decrypted = try aes.decrypt(Array(base64: encrypted))
            
            result = String(data: Data(decrypted), encoding: .utf8) ?? ""
            
        } catch {
            
            print("Error: \(error)")
        }
        
        return result
    }
}
