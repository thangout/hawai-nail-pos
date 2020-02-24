package td.pokladna2;

import java.util.ArrayList;

public class EmployeeDBS {

    ArrayList<Employee> employeesFlora = new ArrayList<>();

    public EmployeeDBS() {

        employeesFlora.add(new Employee(1, "Dong","12120",""));
        employeesFlora.add(new Employee(202, "Quang","20433",""));
        employeesFlora.add(new Employee(203, "Van","65113",""));
        employeesFlora.add(new Employee(204, "Quynh nh","19818",""));

        employeesFlora.add(new Employee(205, "Ngan","60182",""));
        employeesFlora.add(new Employee(206, "Mai","91091",""));
        employeesFlora.add(new Employee(207, "Diem","14720",""));
        employeesFlora.add(new Employee(208, "Hong","23104",""));

        employeesFlora.add(new Employee(209, "Thu","45352",""));
        employeesFlora.add(new Employee(210, "Lan","92535",""));
        employeesFlora.add(new Employee(211, "Thuy","23518",""));
        employeesFlora.add(new Employee(212, "Lich","99723",""));

        employeesFlora.add(new Employee(213, "Nhung","26301",""));
        employeesFlora.add(new Employee(214, "Chi","652912",""));


    }

    public ArrayList<Employee> getEmployeesFlora() {
        return employeesFlora;
    }
}
