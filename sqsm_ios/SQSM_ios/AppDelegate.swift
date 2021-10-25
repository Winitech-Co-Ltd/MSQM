//
//  AppDelegate.swift
//  TestSampleApp
//
//  Created by wini on 2020/02/11.
//  Copyright © 2020 wini. All rights reserved.
//


import Foundation
import UIKit
import CoreLocation
import UserNotifications
import Firebase
import Alamofire
import BackgroundTasks

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    
    var window: UIWindow?
    var curntCoordinate : CLLocationCoordinate2D?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let pList = UserDefaults.standard
        Messaging.messaging().delegate = self
        
        UNUserNotificationCenter.current().delegate = self
        application.registerForRemoteNotifications()
        
        if pList.bool(forKey: StaticString.introString.appRuningFirst.rawValue) == false {
            Bundle.swizzleLocalization()
        }
        
        if #available(iOS 13.0, *) {
            BGTaskScheduler.shared.register(forTaskWithIdentifier: "", using: nil){
                _ in
            }
            BGTaskScheduler.shared.register(forTaskWithIdentifier: "", using: nil){
                _ in
            }
            BGTaskScheduler.shared.register(forTaskWithIdentifier: "", using: nil){
                _ in
            }
            BGTaskScheduler.shared.register(forTaskWithIdentifier: "", using: nil){
                _ in
            }
        }
        return true
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.alert, .sound, .badge])
    }
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler completionHandler:
        @escaping (UIBackgroundFetchResult) -> Void) {
        print(userInfo)
        completionHandler(UIBackgroundFetchResult.newData)
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        completionHandler()
        
        UIApplication.shared.applicationIconBadgeNumber = 0
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        NSLog("APNs registration success")
    }

    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        NSLog("APNs registration fail")
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
        print("Firebaseregistration token: \(fcmToken)")
        let dateDict:[String: String] = [StaticString.preference.token.rawValue : fcmToken]
        NotificationCenter.default.post(name: Notification.Name("FCMToken"), object: nil, userInfo: dateDict)

        let pList = UserDefaults.standard
        pList.set(fcmToken, forKey: StaticString.preference.token.rawValue)
        pList.synchronize()
    }
}
