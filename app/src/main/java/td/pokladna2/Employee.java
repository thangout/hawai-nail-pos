package td.pokladna2;

public class Employee {
    int id;
    String name;
    String password;
    String dic;
    String certificateName;
    String certificatePassword;

    //In czech Cislo provozovny, its issued by the eet system thus it cant be random
    String shopId;

    public Employee(int id, String name, String password, String dic) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.dic = dic;
    }

    public Employee(int id, String name, String password, String dic, String certificateName, String certificatePassword) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.dic = dic;
        this.certificateName = certificateName;
        this.certificatePassword = certificatePassword;
    }

    public Employee(int id, String name, String password, String dic, String certificateName, String certificatePassword, String shopId) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.dic = dic;
        this.certificateName = certificateName;
        this.certificatePassword = certificatePassword;
        this.shopId = shopId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getDic() {
        return dic;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public String getCertificatePassword() {
        return certificatePassword;
    }

    public String getShopId() {
        return shopId;
    }
}
