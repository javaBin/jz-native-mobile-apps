import Foundation
import UserNotifications
import AvatarImageView

public struct Common {
    static let JavaZone2017Dates = ["12.09.2018", "13.09.2018"]
    static let JavaZone2019Dates = ["11.09.2019", "12.09.2019"]
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
        _ = Calendar.current
        return date
    }
    
    static func conferenceDates() -> [String] {
        #if DEBUG
        return Common.JavaZone2019Dates
        #else
        return Common.JavaZone2019Dates
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
    var shape: Shape = .circle
}

public struct CommonNotificationUtil {
    static func scheduleNotification(session: Session, withDate date: Date?, sessionRemove: Bool) {
        if(sessionRemove) {
            UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [session.sessionId!])
            return
        }
        
        if Date() > date! {
            return
        }
        
        addNotifications(session: session, date: date)
    }
    
    static func addNotifications(session: Session, date: Date?) {
        createAndAddNotification(sessionId: session.sessionId!, sessionTitle: session.title!, date: date)
    }

    static func createAndAddNotification(sessionId: String, sessionTitle: String, date: Date?) {
        let identifier = sessionId
        let notificationContent = createMySessionNotificationContent(sessionTitle: sessionTitle)
        
        let dateComponents = Calendar.autoupdatingCurrent.dateComponents([.day, .month, .year, .hour, .minute, .second], from: date!)
        
        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: false)
        
        let notificationRequest = UNNotificationRequest(identifier: identifier, content: notificationContent, trigger: trigger)
        
        UNUserNotificationCenter.current().add(notificationRequest) { error in
            if let e = error {
                print("Error \(e.localizedDescription)")
            }
        }
    }
    
    static func createMySessionNotificationContent(sessionTitle: String) -> UNMutableNotificationContent {
        let content = UNMutableNotificationContent()
        content.title = "\(sessionTitle)"
        content.body = "The session is about to start soon!"
        content.sound = UNNotificationSound.default
        
        return content
    }
    
    static func getStartDate(startTime: String) -> Date? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm"
        dateFormatter.timeZone = TimeZone(identifier: "GMT+1")
//        let testDate = "2019-08-20T18:58"
//        
        let date = dateFormatter.date(from: startTime)
        let calendar = Calendar.current
        let dateComponents = calendar.dateComponents([.year, .month, .day, .hour, .minute, .timeZone], from: date!)
        let sessionDate = calendar.date(from: dateComponents)
        let returnDate = calendar.date(byAdding: .minute, value: -10, to: sessionDate!)
        return returnDate
    }
    
    static func resumeStarredSessions(mySessionList: [MySession]) {
        for mySession in mySessionList {
            let sessionStartTime = getStartDate(startTime: mySession.startTime)
            
            if(sessionStartTime! >= Date()) {
                createAndAddNotification(sessionId: mySession.sessionId, sessionTitle: mySession.sessionTitle, date: sessionStartTime)
            }
        }
        
        UNUserNotificationCenter.current().getPendingNotificationRequests(completionHandler: {requests -> () in
            for request in requests{
                print(request.identifier)
            }
        })
    }
}


