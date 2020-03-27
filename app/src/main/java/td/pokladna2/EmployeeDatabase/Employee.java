package td.pokladna2.EmployeeDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Employee {

    @NonNull
    @PrimaryKey
    int id;

    @ColumnInfo(name="name")
    String name;

    @ColumnInfo(name="password")
    String password;

    @ColumnInfo(name="dic")
    String dic;

    @ColumnInfo(name="certificate_name")
    String certificateName;

    @ColumnInfo(name="certificate_password")
    String certificatePassword;

    //In czech Cislo provozovny, its issued by the eet system thus it cant be random
    @ColumnInfo(name="shop_id")
    String shopId;

    @Ignore
    public Employee(int id, String name, String password, String dic) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.dic = dic;
    }

    @Ignore
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

    public void setCertificatePassword(String certificatePassword) {
        this.certificatePassword = certificatePassword;
    }
}
