package td.pokladna2;

import androidx.appcompat.app.AppCompatActivity;
import td.pokladna2.employeedbs.AppDatabase;
import td.pokladna2.employeedbs.Employee;
import td.pokladna2.employeedbs.EmployeeDBS;
import td.pokladna2.employeedbs.ManageEmployeesDatabaseActivity;
import td.pokladna2.reporting.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    List<Employee> employees;
    AppDatabase dbs;
    EmployeeDBS employeeDBS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        dbs = LocalDatabase.getInstance(getApplicationContext()).DBS;

        //employees = employeeDBS.getEmployeesFlora();
        //employees = employeeDBS.getEmployeesSestka();
        employees = dbs.employeeDAO().getAll();

        initListeners();

        setupButtons();
    }

    private void setupButtons() {


        Button dashboardButton = findViewById(R.id.dashboardButton);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initListeners() {
        final EditText passwordText = findViewById(R.id.passwordText);


        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String passText = editable.toString();

                if (passText.length() == 5) {
                    //do attempt to login

                    // if login goes throyugh
                    Employee person = null;

                    for (Employee emp: employees) {

                        //password test if true it opens the main activity
                        if (emp.getPassword().equals(passText)){

                            passwordText.setText("");

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(),0);

                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            myIntent.putExtra("name", emp.getName()); //Optional parameters
                            myIntent.putExtra("id", String.valueOf(emp.getId())); //Optional parameters
                            LoginActivity.this.startActivity(myIntent);
                            person = emp;
                        };
                    }

                    if (person == null){
                        passwordText.setText("");
                        showToast("Bad Password");
                    }
                }
            }
        });
    }


    public void showToast(String text){
        Toast toast=Toast. makeText(getApplicationContext(),text,Toast. LENGTH_SHORT);
        toast. setMargin(50,50);
        toast. show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        employees = dbs.employeeDAO().getAll();

        final EditText passwordText = findViewById(R.id.passwordText);
        passwordText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}

