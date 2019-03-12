import UIKit

class CompanyDataProvider {
    
    func generateFakeCompanies() -> [Company] {
        let company1 = Company(name: "7N", avatar: UIImage(named: "7N")!,
                            hasStamped: false)
        let company2 = Company(name: "Acando", avatar: UIImage(named: "acando")!, hasStamped: false)
        let company3 = Company(name: "Accenture", avatar: UIImage(named: "accenture")!, hasStamped: false)
        let company4 = Company(name: "Ambita", avatar: UIImage(named: "ambita")!, hasStamped: false)
        let company5 = Company(name: "Arktekk", avatar: UIImage(named: "arktekk")!, hasStamped: false)
        let company6 = Company(name: "Atlassian", avatar: UIImage(named: "atlassian")!, hasStamped: false)
        let company7 = Company(name: "Basefarm", avatar: UIImage(named: "basefarm")!, hasStamped: false)
        let company8 = Company(name: "Bekk", avatar: UIImage(named: "bekk")!,
                            hasStamped: false)
        let company9 = Company(name: "Bouvet", avatar: UIImage(named: "bouvet")!,
                            hasStamped: false)
        
        let fakeCompanies = [company1, company2, company3, company4, company5, company6, company7, company8, company9]
        
        return fakeCompanies
    }
    
}

