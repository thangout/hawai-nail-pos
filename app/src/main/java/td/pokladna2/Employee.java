package td.pokladna2;

public class Employee {
    int id;
    String name;
    String password;
    String ico;

    public Employee(int id, String name, String password, String ico) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.ico = ico;
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

    public String getIco() {
        return ico;
    }
}
