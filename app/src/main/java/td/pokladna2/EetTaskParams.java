package td.pokladna2;


import java.io.InputStream;
import java.security.KeyStore;

public class EetTaskParams {

    double totalSum;
    String dic;
    InputStream certificate;
    InputStream keyStore;
    String pkcsPassword;

    //ID provozovny
    String shopId;

    //ID pokladny
    String terminalId;


    public EetTaskParams(double totalSum, String dic, InputStream certificate, InputStream keyStore, String pkcsPassword) {
        this.totalSum = totalSum;
        this.dic = dic;
        this.certificate = certificate;
        this.keyStore = keyStore;
        this.pkcsPassword = pkcsPassword;
    }

    public EetTaskParams(double totalSum, String dic, InputStream certificate, InputStream keyStore, String pkcsPassword, String shopId, String terminalId) {
        this.totalSum = totalSum;
        this.dic = dic;
        this.certificate = certificate;
        this.keyStore = keyStore;
        this.pkcsPassword = pkcsPassword;
        this.shopId = shopId;
        this.terminalId = terminalId;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public String getDic() {
        return dic;
    }

    public InputStream getCertificate() {
        return certificate;
    }

    public InputStream getKeyStore() {
        return keyStore;
    }

    public String getPkcsPassword() {
        return pkcsPassword;
    }

    public String getShopId() {
        return shopId;
    }

    public String getTerminalId() {
        return terminalId;
    }
}
