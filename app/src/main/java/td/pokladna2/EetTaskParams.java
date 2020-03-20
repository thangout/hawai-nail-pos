package td.pokladna2;


import java.io.InputStream;
import java.security.KeyStore;

public class EetTaskParams {

    double totalSum;
    String dic;
    InputStream certificate;
    InputStream keyStore;
    String pkcsPassword;

    public EetTaskParams(double totalSum, String dic, InputStream certificate, InputStream keyStore, String pkcsPassword) {
        this.totalSum = totalSum;
        this.dic = dic;
        this.certificate = certificate;
        this.keyStore = keyStore;
        this.pkcsPassword = pkcsPassword;
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
}
