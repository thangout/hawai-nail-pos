package td.pokladna2.EmployeeDatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Employee.class}, version = 1)
public abstract class EmployeeDatabase extends RoomDatabase {
    public abstract EmployeeDAO employeeDAO();
}
