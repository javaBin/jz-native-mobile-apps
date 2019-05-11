
import UIKit
import XCGLogger
import CoreData
import Swinject
import SwinjectStoryboard
import SVProgressHUD
import FirebaseCore
import UserNotifications
import AudioToolbox
import RealmSwift

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    
    var window: UIWindow?
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.alert, .badge, .sound])
    }
    
    let container = Container() { container in
        // Repositories
        container.register(SpeakerRepository.self, name: "speakerRepository") {
            _ in SpeakerRepository()
        }
        
        container.register(MySessionRepository.self, name: "mySessionRepository") {
            r in MySessionRepository(speakerRepository: r.resolve(SpeakerRepository.self, name: "speakerRepository"))
        }
        
        container.register(SessionRepository.self, name: "sessionRepository") {
            r in SessionRepository(speakerRepository: r.resolve(SpeakerRepository.self, name: "speakerRepository"))
        }
        
        // Views
        container.storyboardInitCompleted(MyScheduleViewController.self) { r, c in
            c.mySessionRepository = r.resolve(MySessionRepository.self, name: "mySessionRepository")
        }
        
        container.storyboardInitCompleted(SessionListViewController.self) { r, c in
            c.sessionRepository = r.resolve(SessionRepository.self, name: "sessionRepository")
            c.speakerRepository = r.resolve(SpeakerRepository.self, name: "speakerRepository")
            c.mySessionRepository = r.resolve(MySessionRepository.self, name: "mySessionRepository")
        }
        
        container.storyboardInitCompleted(SettingsViewController.self) { r, c in
            c.mySessionRepository = r.resolve(MySessionRepository.self, name: "mySessionRepository")
            c.sessionRepository = r.resolve(SessionRepository.self, name: "sessionRepository")
            
        }
        
        
    }
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        let window = UIWindow(frame: UIScreen.main.bounds)
        window.makeKeyAndVisible()
        self.window = window
        FirebaseApp.configure()
        let _ = RemoteConfigValues.sharedInstance
        var migrationVersion: UInt64 = 2
        var oldMigrationVersion: UInt64 = 1
        
        // Set configuration and delete all realm if version is not the same
        let config = Realm.Configuration(
            // Set the new schema version. This must be greater than the previously used
            // version (if you've never set a schema version before, the version is 0).
            schemaVersion: migrationVersion,
            
            migrationBlock: { migration, oldSchemaVersion in
                // We haven’t migrated anything yet, so oldSchemaVersion == 0
                switch oldSchemaVersion {
                default:
                    oldMigrationVersion = oldSchemaVersion
                    break
                }
        }
        )

        if migrationVersion > oldMigrationVersion {
            do {
                let realm = try! Realm(configuration: config)
                try! realm.write {
                    realm.deleteAll()
                }
            } catch let error as NSError {
                // handle error
            }

        }
        
        let storyboard = SwinjectStoryboard.create(name: "Main", bundle: nil, container: container)
        window.rootViewController = storyboard.instantiateInitialViewController()
        SVProgressHUD.setDefaultStyle(SVProgressHUDStyle.dark)
        
        UNUserNotificationCenter.current().delegate = self
        UNUserNotificationCenter.current().requestAuthorization(options: [.badge, .sound, .alert], completionHandler: {(granted, error) in
            if (granted) {
            } else{
                print("Notification permissions not granted")
            }
        })
        
        return true
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
    
    // MARK: - Core Data stack
    
    lazy var persistentContainer: NSPersistentContainer = {
        /*
         The persistent container for the application. This implementation
         creates and returns a container, having loaded the store for the
         application to it. This property is optional since there are legitimate
         error conditions that could cause the creation of the store to fail.
         */
        let container = NSPersistentContainer(name: "Test")
        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
            if let error = error as NSError? {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                
                /*
                 Typical reasons for an error here include:
                 * The parent directory does not exist, cannot be created, or disallows writing.
                 * The persistent store is not accessible, due to permissions or data protection when the device is locked.
                 * The device is out of space.
                 * The store could not be migrated to the current model version.
                 Check the error message to determine what the actual problem was.
                 */
                fatalError("Unresolved error \(error), \(error.userInfo)")
            }
        })
        return container
    }()
    
    // MARK: - Core Data Saving support
    
    func saveContext () {
        let context = persistentContainer.viewContext
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                let nserror = error as NSError
                fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
            }
        }
    }
    
    
}


