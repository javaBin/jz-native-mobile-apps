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
    
    
    static func pbkdf2SHA512(password: String, salt: Data, rounds: Int) -> Data? {
        return pbkdf2(hash:CCPBKDFAlgorithm(kCCPRFHmacAlgSHA512), password:password, salt:salt, rounds:rounds)
    }
    
    static func pbkdf2(hash :CCPBKDFAlgorithm, password: String, salt: Data, rounds: Int) -> Data? {
        let passwordData = password.data(using:String.Encoding.utf8)!
        var derivedKeyData = Data(repeating:0, count:512)
        
        var derivedKey = derivedKeyData
        let derivationStatus = derivedKey.withUnsafeMutableBytes {derivedKeyBytes in
            salt.withUnsafeBytes { saltBytes in
                
                CCKeyDerivationPBKDF(
                    CCPBKDFAlgorithm(kCCPBKDF2),
                    password, passwordData.count,
                    saltBytes, salt.count,
                    hash,
                    UInt32(rounds),
                    derivedKeyBytes, derivedKeyData.count)
            }
        }
        if (derivationStatus != 0) {
            print("Error: \(derivationStatus)")
            return nil;
        }
        
        return derivedKey
    }
    
}
