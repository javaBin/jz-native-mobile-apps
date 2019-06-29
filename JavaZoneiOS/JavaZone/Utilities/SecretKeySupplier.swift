import Foundation
import CommonCrypto
import CryptoSwift


class SecretKeySupplier {
    
    public static func generateVerificationKey(value: String) -> String {
        
        let saltValue = RemoteConfigValues.sharedInstance.string(key: "salted_partner_name")
        
        let valueBytes: [UInt8] = Array(value.utf8)
        let saltBytes: [UInt8] = Array(saltValue.utf8)
        
        let derivedKey = PBKDF2SHA512(password: valueBytes, salt: saltBytes)
        
        print("derivedKey \(derivedKey)")
        return derivedKey
        
    }
    
    static func PBKDF2SHA512(password: Array<UInt8>, salt: Array<UInt8>) -> String {
        let value = try! PKCS5.PBKDF2(password: password, salt: salt, iterations: 10000, keyLength: 64, variant: .sha512).calculate()
        
        return value.toHexString()
    }
    
}
