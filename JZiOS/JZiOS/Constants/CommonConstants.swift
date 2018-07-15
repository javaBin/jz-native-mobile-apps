import Foundation
import UserNotifications
import AvatarImageView

public struct Common {
    static let JavaZone2017Dates = ["13.09.2017", "14.09.2017"]
    static let JavaZone2018Dates = ["12.09.2018", "13.09.2018"]
}

public struct CommonDate {
    static func formatDate(dateString: String?, dateFormat: String) -> String? {
        let dateFormatter = defaultDateFormatter()
        
        if let sectionDate = dateFormatter.date(from: dateString!) {
            dateFormatter.dateFormat = dateFormat
            return dateFormatter.string(from: sectionDate)
        }
        
        return nil
    }
    
    static func resetMinutesFromDate(dateString: String?, dateFormat: String) -> String? {
        let dateFormatter = defaultDateFormatter()
        
        if let sectionDate = dateFormatter.date(from: dateString!) {
            let greg = Calendar(identifier: .gregorian)
            dateFormatter.dateFormat = dateFormat
            var components = greg.dateComponents([.year, .month, .day, .hour, .minute, .second], from: sectionDate)
            components.minute = 0
            let date = greg.date(from: components)!

            return dateFormatter.string(from: date)
        }
        
        return nil
    }
    
    public static func defaultDateFormatter()  -> DateFormatter {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = NSLocale.current
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm"
        return dateFormatter
    }
    
    public static func getCurrentDate() -> Date {
        let date = Date()
        let calendar = Calendar.current
        return date
    }
    
    static func conferenceDates() -> [String] {
        #if DEBUG
            return Common.JavaZone2017Dates
        #else
            return Common.JavaZone2018Dates
        #endif
    }
}

public struct CommonImageUtil {
    public static func setDefaultSpeakerAvatarImage(imageView: AvatarImageView, imageName: String) {
        var dataSpeakerDetailSource = SpeakerImageViewDataSource()
        dataSpeakerDetailSource.avatar =  UIImage(named: imageName)!
        imageView.dataSource = dataSpeakerDetailSource
    }
}


struct AvatarImageConfig: AvatarImageViewConfiguration {
    let shape: Shape = .circle
}

public struct CommonNotificationUtil {
    static func createMySessionNotificationContent(session: Session) -> UNMutableNotificationContent {
        let content = UNMutableNotificationContent()
        content.title = "Don't forget"
        content.body = "Buy some milk"
        content.sound = UNNotificationSound.default()
        
        return content
    }
    
    static func createNotificationTrigger(session: Session) -> UNNotificationTrigger {
        let date = Date(timeIntervalSinceNow: 3600)
        let triggerDate = Calendar.current.dateComponents([.year,.month,.day,.hour,.minute,.second,], from: date)
        return UNCalendarNotificationTrigger(dateMatching: triggerDate,
                                             repeats: false)
    }
    
    static func addNotification(selectedSession: Session) {
        
        // TODO, check if notification already exists at the current time interval. If it does, then do not add
        let center = UNUserNotificationCenter.current()
        let identifier = selectedSession.sessionId!
        let request = UNNotificationRequest(identifier: identifier,
                                            content: createMySessionNotificationContent(session: selectedSession),
                                            trigger: createNotificationTrigger(session: selectedSession))
        center.add(request, withCompletionHandler: { (error) in
            if let error = error {
            }
        })
    }
    
    static func removeNotification(selectedSession: Session) {
        let center = UNUserNotificationCenter.current()
        
        center.removePendingNotificationRequests(withIdentifiers: [selectedSession.sessionId!])
    }
    
    static func getAllPendingNotifications() {
        UNUserNotificationCenter.current().getPendingNotificationRequests(completionHandler: {requests -> () in
            print("\(requests.count) requests -------")
            for request in requests{
                print(request.identifier)
            }
        })
    }
}

