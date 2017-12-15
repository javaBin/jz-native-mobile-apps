import Foundation
import AvatarImageView

public struct Common {
    static let JavaZone2016Dates = ["06.09.2016", "07.09.2016", "08.09.2016"]
    static let JavaZone2017Dates = ["13.09.2016", "14.09.2016"]
}

public struct CommonDate {
    static func formatDate(dateString: String?, dateFormat: String) -> String? {
        let dateFormatter = setDefaultDateFormatter()
        
        if let sectionDate = dateFormatter.date(from: dateString!) {
            dateFormatter.dateFormat = dateFormat
            return dateFormatter.string(from: sectionDate)
        }
        
        return nil
    }
    
    static func resetMinutesFromDate(dateString: String?, dateFormat: String) -> String? {
        let dateFormatter = setDefaultDateFormatter()
        
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
    
    private static func setDefaultDateFormatter()  -> DateFormatter {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = NSLocale.current
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm"
        return dateFormatter
    }
    
    static func conferenceDates() -> [String] {
        #if DEBUG
            return Common.JavaZone2016Dates
        #else
            return Common.JavaZone2017Dates
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

