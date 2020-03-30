package td.pokladna2.employeedbs;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import td.pokladna2.eetdatabase.DateConverters;
import td.pokladna2.eetdatabase.Receipt;
import td.pokladna2.eetdatabase.ReceiptDAO;

@Database(entities = {Employee.class, Receipt.class}, version = 1)
@TypeConverters({DateConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract EmployeeDAO employeeDAO();

    public abstract ReceiptDAO receiptDAO();
}
