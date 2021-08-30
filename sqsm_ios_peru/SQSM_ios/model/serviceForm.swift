//
//  serviceForm.swift
//  SQSM_ios_peru
//
//  Created by 개발용 맥북 on 2020/07/26.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import Alamofire
import CryptoSwift

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
    

    public func getPost(response:@escaping (AFDataResponse<Any>) -> Void){
        manager.session.configuration.timeoutIntervalForRequest = 15
        manager.request(self.url,method: .post, parameters: self.parametersEN, encoder: JSONParameterEncoder.default, headers: self.headers)
        .responseJSON(completionHandler: response)
    }
    
    public func getPostEN(view:UIView, response:@escaping (AFDataResponse<Any>) -> Void){
        Spinner.start(from: view)
        manager.session.configuration.timeoutIntervalForRequest = 15
        manager.request(self.url,method: .post, parameters: self.parametersEN, encoder: JSONParameterEncoder.default, headers: self.headers)
        .responseJSON(completionHandler: response)
    }
    
    public func getPostEN(uid:String, view:UIView, response: @escaping (AFDataResponse<Any>) -> Void){
        var enHeader = self.headers
        
        let paramEN = try! uid.aesEncrypt(key: decryptMSG.shared.key128, vector: decryptMSG.shared.iv)
        enHeader.add(HTTPHeader(name: "peruuname", value: paramEN)) //격리자 일련번호 담아서 보낼것
        
        print(paramEN)
        
        Spinner.start(from: view)
        manager.session.configuration.timeoutIntervalForRequest = 15
        manager.request(self.url,method: .post, parameters: self.parametersEN, encoder: JSONParameterEncoder.default, headers: enHeader)
        .responseJSON(completionHandler: response)
    }
    
    public func getPostEN(uid:String, response: @escaping (AFDataResponse<Any>) -> Void){
        var enHeader = self.headers
        
        let paramEN = try! uid.aesEncrypt(key: decryptMSG.shared.key128, vector: decryptMSG.shared.iv)
        enHeader.add(HTTPHeader(name: "peruuname", value: paramEN))

        manager.session.configuration.timeoutIntervalForRequest = 15
        manager.request(self.url,method: .post, parameters: self.parametersEN, encoder: JSONParameterEncoder.default, headers: enHeader)
        .responseJSON(completionHandler: response)
    }
}
