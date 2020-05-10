package td.pokladna2.employeedbs;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Room;
import td.pokladna2.LocalDatabase;


public class EmployeeDBS {

    private static EmployeeDBS instance;

    private static AppDatabase db;

    private Context context;

    ArrayList<Employee> employeesFlora = new ArrayList<>();
    ArrayList<Employee> employeesSestka = new ArrayList<>();

    public EmployeeDBS(Context context) {
        this.context = context;

        createInCodeDBS();

        db = LocalDatabase.getInstance(context).DBS;
        saveFloraEmployeeToLocalDBS();
    }

    public static EmployeeDBS getInstance() {
        return instance;
    }

    private void createInCodeDBS(){
        employeesFlora.add(new Employee(201, "Dong","30128","CZ1212121218","EET_CA1_Playground-CZ1212121218.p12","eet","1"));
        employeesFlora.add(new Employee(202, "Quang","20433","CZ683555118", "EET_CA1_Playground-CZ683555118.p12","eet", "1"));
        employeesFlora.add(new Employee(203, "Van","65113","CZ00000019","EET_CA1_Playground-CZ00000019.p12","eet", "1"));
        employeesFlora.add(new Employee(204, "Quynh Anh","10389",""));

        employeesFlora.add(new Employee(205, "Ngan","60182","","EET_CA1_Playground-CZ1212121218.p12","eet","1"));
        employeesFlora.add(new Employee(206, "Mai","29178","","EET_CA1_Playground-CZ1212121218.p12","eet","1"));
        employeesFlora.add(new Employee(207, "Diem","14720","","EET_CA1_Playground-CZ1212121218.p12","eet","1"));
        employeesFlora.add(new Employee(208, "Hong","51179","","EET_CA1_Playground-CZ1212121218.p12","eet","1"));

        employeesFlora.add(new Employee(209, "Thu","22905","","EET_CA1_Playground-CZ1212121218.p12","eet","1"));
        employeesFlora.add(new Employee(210, "Lan","92535","","EET_CA1_Playground-CZ1212121218.p12","eet","1"));
        employeesFlora.add(new Employee(211, "Thuy","59112","","EET_CA1_Playground-CZ1212121218.p12","eet","1"));
        employeesFlora.add(new Employee(212, "Lich","99723","","EET_CA1_Playground-CZ1212121218.p12","eet","1"));

        employeesFlora.add(new Employee(213, "Nhung","26301","","EET_CA1_Playground-CZ1212121218.p12","eet","1"));
        employeesFlora.add(new Employee(214, "Chi","16184","","EET_CA1_Playground-CZ1212121218.p12","eet","1"));
        employeesFlora.add(new Employee(215, "Hang","08984","","EET_CA1_Playground-CZ1212121218.p12","eet","1"));


        employeesSestka.add(new Employee(301, "Ha","22425",""));
        employeesSestka.add(new Employee(302, "Nhan","17797",""));
        employeesSestka.add(new Employee(303, "Tomas","06081",""));
        employeesSestka.add(new Employee(304, "Quyen","29718",""));
        employeesSestka.add(new Employee(305, "Loan","12129",""));
        employeesSestka.add(new Employee(306, "Nhung","23982",""));
        employeesSestka.add(new Employee(307, "Thanh","18802",""));
        employeesSestka.add(new Employee(308, "Nhung2","23599",""));
        //employeesSestka.add(new Employee(999999, "TEST","11111",""));

    }

    void saveFloraEmployeeToLocalDBS(){
        for(Employee emp : employeesFlora){
            saveEmployee(emp);
        }
    }

    public void saveEmployee(Employee employee){
        db.employeeDAO().insertEmployee(employee);
    }

    public ArrayList<Employee> getEmployeesFlora() {
        return employeesFlora;
    }

    public ArrayList<Employee> getEmployeesSestka() {
        return employeesSestka;
    }

    public Employee getEmployeeById(String id){

        /* Employee returnEmp = null;

        int convertedId = Integer.valueOf(id);

        for(Employee emp: getEmployeesFlora()){
            if (emp.getId() == convertedId){
                returnEmp = emp;
            }
        };

        for(Employee emp: getEmployeesSestka()){
            if (emp.getId() == convertedId){
                returnEmp = emp;
            }
        }; */

        return db.employeeDAO().findById(Integer.valueOf(id));
    }

    public static void init(Context context) {
        db = LocalDatabase.getInstance(context).DBS;
    }

    public List<Employee> getAllEmployees() {
        return db.employeeDAO().getAll();
    }

    public void updateEmployee(Employee employee){
        db.employeeDAO().updateEmployee(employee);
    }
}
