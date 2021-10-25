//
//  sendModel.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/24.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation


/// Módulo de comunicación
/// IFID : Service ID
/// PARM : Parameter
struct sendModel: Encodable {
    let IFID : String
    let PARM : Dictionary<String,String>
}

/// Módulo de comunicación cifrada
struct sendModelEN: Encodable {
    let IFID : String
    let PARM : String
}
