//
//  EnvironmentSettingViewController.swift
//  SQSM_ios
//
//  Created by MiRan Kim on 2020/03/17.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import UserNotifications

class EnvironmentSettingViewController: UIViewController {
    @IBOutlet var notiSwitch: UISwitch!
    
    override func viewDidLoad() {
//        super.viewDidLoad()
//
//        let current = UNUserNotificationCenter.current()
//
//        current.getNotificationSettings(completionHandler: { (settings) in
//            if settings.authorizationStatus == .notDetermined {
//                // Notification permission has not been asked yet, go for it!
//            } else if settings.authorizationStatus == .denied {
//                // Notification permission was previously denied, go to settings & privacy to re-enable
//            } else if settings.authorizationStatus == .authorized {
//                // Notification permission was already granted
//            }
//        })
        
        // Do any additional setup after loading the view.
    }
    
    @IBAction func onClickBack(_ sender: UIButton) {
//        self.presentingViewController?.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func onClickSwitch(_ sender: UISwitch) {
    }
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
