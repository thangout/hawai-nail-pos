package td.pokladna2.employeedbs;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface EmployeeDAO {
    @Query("SELECT * FROM employee")
    List<Employee> getAll();

    @Query("SELECT * FROM employee WHERE id IN (:employeesId)")
    List<Employee> loadAllByIds(int[] employeesId);

    @Query("SELECT * FROM employee WHERE id LIKE :id")
    Employee findById(int id);

    @Insert
    void insertAll(Employee... employees);

    @Insert
    void insertEmployee(Employee employee);

    @Update
    void updateEmployee(Employee employee);

    @Delete
    void delete(Employee user);
}

