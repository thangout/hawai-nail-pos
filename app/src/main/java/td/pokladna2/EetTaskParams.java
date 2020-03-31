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
    int employeeId;
    String eetRequest;
    int receiptId;

    public EetTaskParams(int employeeId,int receiptId, String eetRequest) {
        this.employeeId = employeeId;
        this.eetRequest = eetRequest;
        this.receiptId = receiptId;
    }

    public EetTaskParams(double totalSum, int employeeId, String terminalId) {
        this.totalSum = totalSum;
        this.employeeId = employeeId;
        this.terminalId = terminalId;
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

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEetRequest() {
        return eetRequest;
    }

    public int getReceiptId() {
        return receiptId;
    }
}
