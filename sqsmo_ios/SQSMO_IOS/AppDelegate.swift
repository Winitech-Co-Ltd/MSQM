//
//  AppDelegate.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/22.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit
import UserNotifications
import Firebase
import BackgroundTasks

//Parte de configuración común de la aplicación
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    
    var window: UIWindow?
    let gcmMessageIDKey = "gcm.message_id"
    
    //Registro de notificación interna para el uso de patrones de observador
     public static let kNotification = Notification.Name("kNotification")
    
    //Operación cuando la pantalla se carga por primera vez
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        FirebaseApp.configure() //firebase 등록
        Messaging.messaging().delegate = self
        
        //Registro de notificación delegado
        UNUserNotificationCenter.current().delegate = self
        
        //Configuración de notificaciones y solicitudes de permisos
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
        options: authOptions,
        completionHandler: {agree, _ in
                NotificationCenter.default.post(name: AppDelegate.kNotification, object: nil)
        })

        application.registerForRemoteNotifications()
        
        //Configuración de procesamiento background
        if #available(iOS 13.0, *) {
            BGTaskScheduler.shared.register(forTaskWithIdentifier: "kr.go.safekorea.sqsmo.ios.fetch", using: nil){
                _ in
            }
            BGTaskScheduler.shared.register(forTaskWithIdentifier: "kr.go.safekorea.sqsmo.ios.noti", using: nil){
                _ in
            }
            BGTaskScheduler.shared.register(forTaskWithIdentifier: "kr.go.safekorea.sqsmo.ios.process", using: nil){
                _ in
            }
        } else {
            // Fallback on earlier versions
        }

        return true
    }
    
    //Recibe notificación de estado foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        NSLog("foregorund notification catch")
        let userInfo = notification.request.content.userInfo
        // Print full message.
        print(userInfo)
        completionHandler([.alert, .sound, .badge])
    }
    
    //Manejar cuando el usuario hace clic en la notificación de estado foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        completionHandler()
        UIApplication.shared.applicationIconBadgeNumber = 0
        NSLog("foreground notification click")
    }
    
    //Call back de registro exitoso de ANPs
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print("Registro exitoso de ANPs")
    }
    
    //Call back de falla de registro de ANPs
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Falla de registro de ANPs")
    }
    
    
    // [START receive_message]
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any]) {
        
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
        print(userInfo)
    }
    
    //Recibir notificación del background
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        NSLog("background notification catch")
        completionHandler(UIBackgroundFetchResult.newData)
    }
    
    func messaging(_ messaging: Messaging, didReceive remoteMessage: MessagingRemoteMessage) {
        print("messaging")
    }
    
    //Renovación de token
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
        if UserDefaults.standard.string(forKey: "token") != fcmToken {
            print("Token cambiado")
            UserDefaults.standard.set(true, forKey: "tokenChange")
        }else{
            UserDefaults.standard.set(false, forKey: "tokenChange")
        }
        print("Firebaseregistration token: \(fcmToken)")
        UserDefaults.standard.set(fcmToken, forKey: "token")
        UserDefaults.standard.synchronize()
        let dateDict:[String: String] = ["token" : fcmToken]
        NotificationCenter.default.post(name: Notification.Name("FCMToken"), object: nil, userInfo: dateDict)
    }
}

