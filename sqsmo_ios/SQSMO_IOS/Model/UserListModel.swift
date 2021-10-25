//
//  UserListModel.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/24.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation

//격리자일련번호    ISLPRSN_SN
//격리자이름    ISLPRSN_NM
//성별코드    SEXDSTN_CODE
//성별코드명    SEXDSTN_CODE_NM
//국적코드    NLTY_CODE
//격리자여권번호    PSPRNBR
//격리자주민아이디(주민번호같은개념)    INHT_ID
//격리위치X좌표    ISLLC_XCNTS
//격리위치Y좌표    ISLLC_YDNTS
//관리자일련번호    MNGR_SN
//격리자단말기전화번호    TELNO
//비상연락번호    EMGNC_TELNO
//자가진단여부(오전)코드    SLFDGNSS_AM_CODE
//자가진단여부(오후)코드    SLFDGNSS_PM_CODE
//자가진단여부(오전)코드명    SLFDGNSS_AM_CODE_NM
//자가진단여부(오후)코드명    SLFDGNSS_PM_CODE_NM
//마지막 진단내용 증상있음 여부    SLFDGNSS_GUBN_AT
//격리자관리상태코드    ISLPRSN_STTUS_CODE
//격리자관리상태코드명    ISLPRSN_STTUS_CODE_NM
//격리자현재위치X    ISLPRSN_XCNTS
//격리자현재위치Y    ISLPRSN_YDNTS
//사용자인증번호    CRTFC_NO
//반경    DISTANCE
//격리자에대한통신상태코드    ISLPRSN_NTW_STTUS_CODE
//격리자에대한통신상태코드명    ISLPRSN_NTW_STTUS_CODE_NM
//항목 열림 여부    ITEM_OPEN_AT
//격리자주정부코드    ISLLC_DPRTMNT_CODE
//격리자주정부코드명    ISLLC_DPRTMNT_CODE_NM
//격리자시정구코드    ISLLC_DSTRT_CODE
//격리자시정구코드명    ISLLC_DSTRT_CODE_NM
//격리자구청코드    ISLLC_PRVNCA_CODE
//격리자구청코드명    ISLLC_PRVNCA_CODE_NM
//격리자기타주소    ISLLC_ETC_ADRES
//위치전송사용여부    LC_TRNSMIS_USE_AT
//격리자생년월일    BRTHDY


//Modelo de información de la persona en autocuarentena
class UserListModel {
    var ISLPRSN_SN:String?
    var ISLPRSN_NM:String?
    var SEXDSTN_CODE:String?
    var SEXDSTN_CODE_NM:String?
    var NLTY_CODE:String?
    var PSPRNBR:String?
    var INHT_ID:String?
    var ISLLC_XCNTS:String?
    var ISLLC_YDNTS:String?
    var MNGR_SN:String?
    var TELNO:String?
    var EMGNC_TELNO:String?
    var SLFDGNSS_AM_CODE:String?
    var SLFDGNSS_PM_CODE:String?
    var SLFDGNSS_AM_CODE_NM:String?
    var SLFDGNSS_PM_CODE_NM:String?
    var SLFDGNSS_GUBN_AT:String?
    var ISLPRSN_STTUS_CODE:String?
    var ISLPRSN_STTUS_CODE_NM:String?
    var ISLPRSN_XCNTS:String?
    var ISLPRSN_YDNTS:String?
    var CRTFC_NO:String?
    var DISTANCE:String?
    var ISLPRSN_NTW_STTUS_CODE:String?
    var ISLPRSN_NTW_STTUS_CODE_NM:String?
    var ITEM_OPEN_AT:String?
    var ISLLC_DPRTMNT_CODE:String?
    var ISLLC_DPRTMNT_CODE_NM:String?
    var ISLLC_DSTRT_CODE:String?
    var ISLLC_DSTRT_CODE_NM:String?
    var ISLLC_PRVNCA_CODE:String?
    var ISLLC_PRVNCA_CODE_NM:String?
    var ISLLC_ETC_ADRES:String?
    var LC_TRNSMIS_USE_AT:String?
    var BRTHDY:String?
    var BRTHDY_F:String?
    var TRMNL_SN:String?
    var ADDR:String?
    var TRMNL_KND_CODE:String?
}
