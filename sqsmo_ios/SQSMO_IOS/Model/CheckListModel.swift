//
//  CheckListModel.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/25.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation

//Modelo de autodiagnóstico de la persona en autocuarentena
class CheckListModel {
    var title :String?
    var date :String?
    var ymd :String?
    var COUGH_AT:String?        //기침
    var DYSPNEA_AT:String?      //호흡곤란
    var PYRXIA_AT:String?       //발열
    var SORE_THROAT_AT:String?  //인후통
    var RM:String?              //특이사항
    var BDHEAT:String?          //체온
}
