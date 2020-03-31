package td.pokladna2.eetdatabase;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Receipt {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name="employee_id")
    int employeeId;

    @ColumnInfo(name="price")
    int price;

    @ColumnInfo(name="date_printed")
    Date datePrinted;

    @ColumnInfo(name="eet_request")
    String eetRequest;

    @ColumnInfo(name="is_send")
    boolean isSend;

    public Receipt(int employeeId, int price, Date datePrinted, String eetRequest, boolean isSend) {
        this.employeeId = employeeId;
        this.price = price;
        this.datePrinted = datePrinted;
        this.eetRequest = eetRequest;
        this.isSend = isSend;
    }

    public int getId() {
        return id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public int getPrice() {
        return price;
    }

    public Date getDatePrinted() {
        return datePrinted;
    }

    public String getEetRequest() {
        return eetRequest;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}
