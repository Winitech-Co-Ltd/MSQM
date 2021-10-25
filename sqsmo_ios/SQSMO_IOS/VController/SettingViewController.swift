//
//  SettingViewController.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/03/16.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import UserNotifications

class SettingViewController: UIViewController, UNUserNotificationCenterDelegate {
    
    @IBAction func goBack(_ sender: UIBarButtonItem) {
        self.presentingViewController?.dismiss(animated: true)
    }
    
    @IBOutlet var mainView: UIView!
    @IBOutlet var titleLB: UILabel!
    @IBOutlet var pushSwich: UISwitch!
    
    let center = UNUserNotificationCenter.current()
    
    var isSelected = false {
        willSet(newValue){
            DispatchQueue.main.sync {
                self.pushSwich.isOn = newValue
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.pushSwich.onTintColor = UIColor.blue
        self.pushSwich.tintColor = UIColor.red
        self.pushSwich.thumbTintColor = UIColor.white
        
        self.center.delegate = self
        self.center.getNotificationSettings(){
            (setting)in
                if setting.authorizationStatus == .denied {
                    self.isSelected = false
                }else{
                    self.isSelected = true
                }
        }
        
    }
    
    @IBAction func onClick(_ sender: UISwitch) {
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        self.center.requestAuthorization(options: authOptions){
            granted, error in
            if error != nil {
                self.makeAlert(title: "변경실패", message: "설정변경에 실패하였습니다.")
            }
            
            print(granted)
        }
    }
    
    
    //공통alert
    func makeAlert(title:String, message:String){
        let mAlert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let cancle = UIAlertAction(title: "OK", style: .cancel, handler: nil)
        mAlert.addAction(cancle)
        self.present(mAlert, animated: true, completion: nil)
    }
}
