package td.pokladna2.eetdatabase;

import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import td.pokladna2.employeedbs.Employee;

@Dao
public interface ReceiptDAO {

    @Query("SELECT * FROM receipt")
    List<Receipt> getAll();

    @Query("SELECT * FROM receipt WHERE id IN (:receiptId)")
    List<Receipt> loadAllByIds(int[] receiptId);

    @Query("SELECT * FROM receipt WHERE id LIKE :id")
    Receipt findById(int id);

    @Query("SELECT * FROM receipt WHERE date_Printed BETWEEN :from AND :to")
    List<Receipt> findReceiptPrintedBetweenDates(Date from, Date to);

    @Query("SELECT * FROM receipt WHERE employee_id LIKE :employeeId AND date_Printed BETWEEN :from AND :to")
    List<Receipt> findEmployeeReceiptPrintedBetweenDates(int employeeId,Date from, Date to);

    @Insert
    void insertAll(Receipt... receipts);

    @Insert
    void insertEmployee(Receipt receipt);

    @Update
    void updateEmployee(Receipt receipt);

    @Delete
    void delete(Receipt receipt);
}
