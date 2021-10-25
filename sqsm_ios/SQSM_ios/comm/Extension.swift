//
//  Extension.swift
//  SQSM_ios_peru
//
//  Created by 개발용 맥북 on 2020/05/20.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import UIKit
import CryptoSwift

extension String {
    /// Importar código de imagen de bandera
    static func emojiFlag(for countryCode: String) -> String! {
        func isLowercaseASCIIScalar(_ scalar: Unicode.Scalar) -> Bool {
            return scalar.value >= 0x61 && scalar.value <= 0x7A
        }
        func regionalIndicatorSymbol(for scalar: Unicode.Scalar) -> Unicode.Scalar {
            precondition(isLowercaseASCIIScalar(scalar))
            
            // 0x1F1E6 marks the start of the Regional Indicator Symbol range and corresponds to 'A'
            // 0x61 marks the start of the lowercase ASCII alphabet: 'a'
            return Unicode.Scalar(scalar.value + (0x1F1E6 - 0x61))!
        }
        
        let lowercasedCode = countryCode.lowercased()
        guard lowercasedCode.count == 2 else { return nil }
        guard lowercasedCode.unicodeScalars.reduce(true, { accum, scalar in accum && isLowercaseASCIIScalar(scalar) }) else { return nil }
        
        let indicatorSymbols = lowercasedCode.unicodeScalars.map({ regionalIndicatorSymbol(for: $0) })
        return String(indicatorSymbols.map({ Character($0) }))
    }
    
    //Localización
    var localized: String {
        return NSLocalizedString(self, tableName: "Localizable", value: self, comment: "")
    }
    
    /// - Parameters:
    ///   - from: Punto de partida
    ///   - to: Punto final
    func subString(from: Int, to: Int) -> String {
        let startIndex = self.index(self.startIndex, offsetBy: from)
        let endIndex = self.index(self.startIndex, offsetBy: to)
        return String(self[startIndex...endIndex])
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
    
    func aesEncrypt(key: String, vector : String) throws -> String {
        
        var result = ""
        
        do {
            let key: [UInt8] = Array(key.utf8) as [UInt8]
            let iv :[UInt8] = Array(vector.utf8)
            let aes = try! AES(key: key, blockMode: CBC(iv: iv), padding: .pkcs5)
            let encrypted = try aes.encrypt(Array(self.utf8))
            
            result = encrypted.toBase64()!
            
        } catch {
            print("Error: \(error)")
        }
        
        return result
    }
    
    func aesDecrypt(key: String, vector : String) throws -> String {
        
        var result = ""
        
        do {
            let encrypted = self
            let key: [UInt8] = Array(key.utf8) as [UInt8]
            let iv :[UInt8] = Array(vector.utf8)
            let aes = try! AES(key: key, blockMode: CBC(iv: iv), padding: .pkcs5)
            let decrypted = try aes.decrypt(Array(base64: encrypted))
            
            result = String(data: Data(decrypted), encoding: .utf8) ?? ""
            
        } catch {
            print("Error: \(error)")
        }
        return result
    }
}

extension Bundle {
    /// Method Swizzling(Reemplazar la función mientras la aplicación se está ejecutando)
    static func swizzleLocalization() {
        let orginalSelector = #selector(localizedString(forKey:value:table:))
        guard let orginalMethod = class_getInstanceMethod(self, orginalSelector) else { return }
        let mySelector = #selector(myLocaLizedString(forKey:value:table:))
        
        guard let myMethod = class_getInstanceMethod(self, mySelector) else { return }
        if class_addMethod(self, orginalSelector, method_getImplementation(myMethod), method_getTypeEncoding(myMethod)) {
            class_replaceMethod(self, mySelector, method_getImplementation(orginalMethod), method_getTypeEncoding(orginalMethod))
        } else {
            method_exchangeImplementations(orginalMethod, myMethod)
        }
    }
    
    @objc private func myLocaLizedString(forKey key: String,value: String?, table: String?) -> String {
        guard let bundlePath = Bundle.main.path(forResource: UserDefaults.standard.string(forKey: StaticString.introString.appLang.rawValue), ofType: "lproj"),
            let bundle = Bundle(path: bundlePath) else {
                return Bundle.main.myLocaLizedString(forKey: key, value: value, table: table)
        }
        return bundle.myLocaLizedString(forKey: key, value: value, table: table)
    }
}

extension UIView {
    /// Dibujo de esquema
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

extension LosslessStringConvertible {
    var string: String { .init(self) }
}

extension UIToolbar {
    /// Crear barra de herramienta con botón de cerrar
    func ToolbarPiker(mySelect : Selector) -> UIToolbar {
        let toolBar = UIToolbar()
        toolBar.barStyle = UIBarStyle.default
        toolBar.isTranslucent = true
        toolBar.tintColor = UIColor.black
        toolBar.sizeToFit()
        
        let doneButton = UIBarButtonItem(title: "completado".localized, style: UIBarButtonItem.Style.plain, target: self, action: mySelect)
        let spaceButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        
        toolBar.setItems([ spaceButton, doneButton], animated: false)
        toolBar.isUserInteractionEnabled = true
        
        return toolBar
    }
}

extension UITextField {
    /// Dar márgenes al lado izquierdo de la vista
    func setLeftPaddingPoints(_ amount:CGFloat){
        let paddingView = UIView(frame: CGRect(x: 0, y: 0, width: amount, height: self.frame.size.height))
        self.leftView = paddingView
        self.leftViewMode = .always
    }
    
    /// Ciclo de relleno a la derecha de la vista
    func setRightPaddingPoints(_ amount:CGFloat) {
        let paddingView = UIView(frame: CGRect(x: 0, y: 0, width: amount, height: self.frame.size.height))
        self.rightView = paddingView
        self.rightViewMode = .always
    }
}

extension Dictionary {
    
    var json: String {
        let invalidJson = "Not a valid JSON"
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

