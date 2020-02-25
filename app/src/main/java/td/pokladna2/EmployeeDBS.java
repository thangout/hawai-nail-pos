package td.pokladna2;

import java.util.ArrayList;

public class EmployeeDBS {

    ArrayList<Employee> employeesFlora = new ArrayList<>();

    public EmployeeDBS() {

        employeesFlora.add(new Employee(201, "Dong","30128",""));
        employeesFlora.add(new Employee(202, "Quang","20433",""));
        employeesFlora.add(new Employee(203, "Van","65113",""));
        employeesFlora.add(new Employee(204, "Quynh Anh","10389",""));

        employeesFlora.add(new Employee(205, "Ngan","60182",""));
        employeesFlora.add(new Employee(206, "Mai","29178",""));
        employeesFlora.add(new Employee(207, "Diem","14720",""));
        employeesFlora.add(new Employee(208, "Hong","51179",""));

        employeesFlora.add(new Employee(209, "Thu","22905",""));
        employeesFlora.add(new Employee(210, "Lan","92535",""));
        employeesFlora.add(new Employee(211, "Thuy","59112",""));
        employeesFlora.add(new Employee(212, "Lich","99723",""));

        employeesFlora.add(new Employee(213, "Nhung","26301",""));
        employeesFlora.add(new Employee(214, "Chi","16184",""));

        //employeesFlora.add(new Employee(999999, "TEST","11111",""));


    }

    public ArrayList<Employee> getEmployeesFlora() {
        return employeesFlora;
    }
}
