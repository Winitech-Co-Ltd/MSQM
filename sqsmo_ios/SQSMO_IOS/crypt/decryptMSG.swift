//
//  decryptMSG.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/27.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import CryptoSwift



/*
 #1
 -------------------------------------------------------------------------------------------------------
 [Partes que encriptan los parámetros existentes]
 let param = PARM.printJson()
 let paramEN = try! param.aesEncrypt(key: decryptMSG.decryptMSGInstance.key128)
 let listService = sendModelEN(IFID: "SQSMO0001", PARM: paramEN)
 
 //Llamada al servidor
 let req = serviceForm(url: url, parametersEN: listService)
 req.getPostEN(response: resp)
 -------------------------------------------------------------------------------------------------------
 [Parte que descifra las partes encriptadas y cambiar a forma de diccionario]
 guard let json = value as? [String: Any],
     let resp = json["RES_DATA"] as? String else {
     return
 }

 let msg = decryptMSG.decryptMSGInstance.msgDecrypt(msg: resp)
 
 //Convertir a formato diccionario
 let data :[Dictionary<String,Any>] = decryptMSG(enData: msg).decrypt()
 if data.count < 1 {self.makeAlert(title: "로그인실패", message: "로그인정보가 없습니다"); return}
 -------------------------------------------------------------------------------------------------------
 */
 
///Módulo de cifrado común
class decryptMSG {
    static let instance = decryptMSG(key: "", iv: "")
    
    public var key128:String
    public var iv:String
    
    init(key:String, iv:String) {
        self.key128 = key
        self.iv = iv
    }
    
    //공개키 암호화 풀기
    public func msgDecrypt(msg:String) ->String{
        do {
            let data = try msg.aesDecrypt(key: self.key128, iv: self.iv)
            return data
        } catch {
            return ""
        }
    }
    
    //개인키 암호화 풀기
    public func msgDecryptSecure(msg:String, key:String, iv:String) ->String{
        do {
            let data = try msg.aesDecrypt(key: key, iv: iv)
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
