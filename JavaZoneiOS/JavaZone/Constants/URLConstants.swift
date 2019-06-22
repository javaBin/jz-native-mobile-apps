
import Foundation

struct JZURL {
    private struct ProdUrls {
        static let SleepingPillUrl = "https://sleepingpill.javazone.no/public/allSessions/javazone_2018"
        static let DevNullUrl = "https://devnull.javazone.no"
    }
    
    private struct DevUrls {
        static let SleepingPillUrl = "https://sleepingpill.javazone.no/public/allSessions/javazone_2019"
        static let DevNullUrl = "https://devnull.javazone.no"
    } 
    
    static var GetAllSessions: String {
        #if DEBUG
            return DevUrls.SleepingPillUrl
        #else
            return ProdUrls.SleepingPillUrl
        #endif
    }
    
    static var GetDevNullUrl: String {
        #if DEBUG
            return DevUrls.DevNullUrl
        #else
            return ProdUrls.DevNullUrl
        #endif
    }
}
