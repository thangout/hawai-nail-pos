package td.pokladna2;

import android.content.Context;
import android.os.LocaleList;

import androidx.room.Room;
import td.pokladna2.employeedbs.AppDatabase;
import td.pokladna2.employeedbs.EmployeeDBS;


public class LocalDatabase {

    private static LocalDatabase instance;
    public static AppDatabase DBS;

    private  LocalDatabase(AppDatabase db) {
        this.DBS = db;
    }

    private static void init(Context context) {
        if (instance == null) {
            Context newContext = context.getApplicationContext();
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "appdbs").allowMainThreadQueries().build();
            instance = new LocalDatabase(db);
        }
    }

    public static LocalDatabase getInstance(Context context) {
        init(context);
        return instance;
    }
}
