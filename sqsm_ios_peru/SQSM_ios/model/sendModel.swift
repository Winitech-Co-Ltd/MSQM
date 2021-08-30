//
//  loginModel.swift
//  TestSampleApp
//
//  Created by wini on 2020/02/12.
//  Copyright © 2020 wini. All rights reserved.
//

struct sendModel: Encodable {
    let IFID : String
    let PARM : Dictionary<String,String>
}

struct sendModelEN: Encodable {
    let IFID : String
    let PARM : String?
}
