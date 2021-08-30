//
//  decryptMSG.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/27.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import CryptoSwift

class decryptMSG {
    static let shared = decryptMSG(key: UserDefaults.standard.string(forKey: StaticString.encryption.PUBLIC_KEY.rawValue) ?? "",
                                   iv: UserDefaults.standard.string(forKey: StaticString.encryption.PUBLIC_VECTOR.rawValue) ?? "")
    
    public var key128:String
    public var iv:String
    
    init(key:String, iv:String) {
        self.key128 = key
        self.iv = iv
    }
    
    //Descifrado de clave pública
    public func msgDecrypt(msg:String) ->String{
        do {
            let data = try msg.aesDecrypt(key: self.key128, vector: self.iv)
            return data
        } catch {
            return ""
        }
    }
    
    //Descifrado de clave privada
    public func msgDecryptSecure(msg:String, key:String, iv:String) ->String{
        do {
            let data = try msg.aesDecrypt(key: key, vector: iv)
            return data
        } catch {
            return ""
        }
    }
    
    public func decrypt(enData:String) -> [Dictionary<String,Any>]{
        
        let en = enData.data(using: .utf8)!
        
        do {
            if let jsonArray = try JSONSerialization.jsonObject(with: en, options : .allowFragments) as? [Dictionary<String,Any>]{
                return jsonArray
            } else {
                return [Dictionary<String,Any>]()
            }
        } catch let error as NSError {
            print(error)
            return [Dictionary<String,Any>]()
        }
    }
}
