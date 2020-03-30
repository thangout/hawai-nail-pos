package td.pokladna2.employeedbs;

import androidx.appcompat.app.AppCompatActivity;
import td.pokladna2.LocalDatabase;
import td.pokladna2.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class ManageEmployeesDatabaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_employees_database);

        initTable();
        initButtons();
    }

    private void initButtons() {


        Button addEmployeeButton = findViewById(R.id.addEmployeeButton);

        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddEmployee.class);
                startActivity(intent);
            }
        });
    }

    private void initTable() {

        TableLayout ll = (TableLayout) findViewById(R.id.employeesTable);
        ll.removeAllViews();

        TextView name = null;
        TextView id = null;
        TextView password = null;
        Button editButton = null;
        TextView DIC = null;
        TextView certName = null;
        TextView certPass = null;


        AppDatabase dbs = LocalDatabase.getInstance(getApplicationContext()).DBS;
        List<Employee> employeeList = dbs.employeeDAO().getAll();

        int i = 0;
        for (final Employee emp : employeeList){

            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            //ID
            id = new TextView(this);
            id.setPadding(0, 0, 10, 0);
            id.setText("ID: " + emp.getId());
            //name
            name = new TextView(this);
            name.setPadding(0, 0, 10, 0);
            name.setText("Name: " + emp.getName());

            //password
            password = new TextView(this);
            password.setPadding(0, 0, 10, 0);
            password.setText("Password:" + emp.getPassword());

            //password
            DIC = new TextView(this);
            DIC.setPadding(0, 0, 10, 0);
            DIC.setText("DIC: " + emp.getDic());

            //certName
            certName = new TextView(this);
            certName.setPadding(0, 0, 10, 0);
            certName.setText("CertName: " + emp.getCertificateName());

            //certificate password
            certPass = new TextView(this);
            certPass.setPadding(0, 0, 10, 0);
            certPass.setText("CertPass: " + emp.getCertificatePassword());

            //edit button
            editButton = new Button(this);
            editButton.setText("Edit");
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(getApplicationContext(), AddEmployee.class);
                    myIntent.putExtra("EMPLOYEE_ID", emp.getId()); //Optional parameters
                    startActivity(myIntent);
                }
            });

            row.addView(id);
            row.addView(name);
            row.addView(password);
            row.addView(DIC);
            //row.addView(certName);
            row.addView(certPass);
            row.addView(editButton);
            ll.addView(row,i);
            i++;
        }

        List<String> files = Arrays.asList(fileList());

        for (String file: files ){
            TableRow row= new TableRow(this);
            TextView text = new TextView(this);
            text.setText(file);
            row.addView(text);
            ll.addView(row,i);
            i++;
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        initTable();

    }
}
