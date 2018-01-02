
import Foundation

struct JZURL {
    private struct ProdUrls {
        static let SleepingPillUrl = "https://sleepingpill.javazone.no/public/allSessions/javazone_2017"
        static let DevNullUrl = "https://javazone.no/devnull/server"
    }
    
    private struct DevUrls {
        static let SleepingPillUrl = "https://sleepingpill.javazone.no/public/allSessions/javazone_2016"
        static let DevNullUrl = "https://test.javazone.no/devnull/server"
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
