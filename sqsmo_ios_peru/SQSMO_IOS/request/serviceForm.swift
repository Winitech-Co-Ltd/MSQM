//
//  serviceForm.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/24.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import Alamofire

/// Formulario de servicio común
class serviceForm {
    
    let headers:HTTPHeaders = [
        "Accept":"application/json",
        "Content-Type":"application/json; charset=utf-8"
    ]
    
    var url:String!
    var parameters:sendModel!
    var parametersEN:sendModelEN!
    let manager = Alamofire.Session.default
    
    init(url:String, parameters:sendModel) {
        self.url = url
        self.parameters = parameters
    }
    
    init(url:String, parametersEN:sendModelEN) {
        self.url = url
        self.parametersEN = parametersEN
    }
    
    //Comunicación general
    public func getPost(response:@escaping (AFDataResponse<Any>) -> Void){
        manager.session.configuration.timeoutIntervalForRequest = 15
        manager.session.configuration.timeoutIntervalForResource = 15
        manager.request(self.url,method: .post, parameters: self.parameters, encoder: JSONParameterEncoder.default, headers: self.headers)
        .responseJSON(completionHandler: response)
    }
    
    //Comunicación de cifrado
    public func getPostEN(view:UIView, response:@escaping (AFDataResponse<Any>) -> Void){
        Spinner.start(from: view)
        manager.session.configuration.timeoutIntervalForRequest = 15
        manager.session.configuration.timeoutIntervalForResource = 15
        manager.request(self.url,method: .post, parameters: self.parametersEN, encoder: JSONParameterEncoder.default, headers: self.headers)
        .responseJSON(completionHandler: response)
    }
    
    //신규통신
    public func getPostEN(uid:String, view:UIView, response:@escaping (AFDataResponse<Any>) -> Void){
        var enHeader = self.headers
        
        let paramEN = try! uid.aesEncrypt(key: decryptMSG.instance.key128, iv: decryptMSG.instance.iv)
        enHeader.add(HTTPHeader(name: "sqsmoname", value: paramEN))
        
        Spinner.start(from: view)
        manager.session.configuration.timeoutIntervalForRequest = 15
        manager.request(self.url,method: .post, parameters: self.parametersEN, encoder: JSONParameterEncoder.default, headers: enHeader)
        .responseJSON(completionHandler: response)
    }
    
    //신규통신 no view
    public func getPostEN(uid:String, response:@escaping (AFDataResponse<Any>) -> Void){
        var enHeader = self.headers
        
        let paramEN = try! uid.aesEncrypt(key: decryptMSG.instance.key128, iv: decryptMSG.instance.iv)
        enHeader.add(HTTPHeader(name: "sqsmoname", value: paramEN))

        manager.session.configuration.timeoutIntervalForRequest = 15
        manager.request(self.url,method: .post, parameters: self.parametersEN, encoder: JSONParameterEncoder.default, headers: enHeader)
        .responseJSON(completionHandler: response)
    }
}
