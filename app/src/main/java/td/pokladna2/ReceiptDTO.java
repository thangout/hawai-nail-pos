package td.pokladna2;

public class ReceiptDTO {

    //required by EET law
    double totalSum;
    String terminalId;
    String shopId;
    String dateTime;
    String dic;
    String receiptId;
    String FIK;
    String BKP;
    String PKP; //print on receipt if in offline mode;

    String eetRequest;

    public ReceiptDTO(Builder builder){
        this.totalSum = builder.totalSum;
        this.terminalId = builder.terminalId;
        this.shopId = builder.shopId;
        this.dateTime = builder.dateTime;
        this.dic = builder.dic;
        this.receiptId = builder.receiptId;
        this.FIK = builder.FIK;
        this.BKP = builder.BKP;
        this.PKP = builder.PKP;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public String getShopId() {
        return shopId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDic() {
        return dic;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getFIK() {
        return FIK;
    }

    public String getBKP() {
        return BKP;
    }

    public String getPKP() {
        return PKP;
    }

    public String getEetRequest() {
        return eetRequest;
    }

    public static class Builder{
       private double totalSum;
        private String terminalId;
        private String shopId;
        private String dateTime;
        private String dic;
        private String receiptId;
        private String FIK;
        private String BKP;
        private String PKP; //print on receipt if in offline mode;
        private String eetRequest; //print on receipt if in offline mode;

        public static Builder newInstance(){
            return new Builder();
        }

        public Builder setTotalSum(double totalSum) {
            this.totalSum = totalSum;
            return this;
        }

        public Builder setTerminalId(String terminalId) {
            this.terminalId = terminalId;
            return this;
        }

        public Builder setShopId(String shopId) {
            this.shopId = shopId;
            return this;
        }

        public Builder setDateTime(String dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public Builder setDic(String dic) {
            this.dic = dic;
            return this;
        }

        public Builder setReceiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public Builder setFIK(String FIK) {
            this.FIK = FIK;
            return this;
        }

        public Builder setBKP(String BKP) {
            this.BKP = BKP;
            return this;
        }

        public Builder setPKP(String PKP) {
            this.PKP = PKP;
            return this;
        }

        public Builder setEetRequest(String request) {
            this.eetRequest = PKP;
            return this;
        }

        public ReceiptDTO build(){
            return new ReceiptDTO(this);
        }
    }
}
