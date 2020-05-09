package td.pokladna2;

import androidx.appcompat.app.AppCompatActivity;
import td.pokladna2.employeedbs.Employee;
import td.pokladna2.reporting.Dashboard;
import td.pokladna2.reporting.DatePickerInterface;

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

public class AdminLoginActivity extends AppCompatActivity {

    String PASSWORD_ADMIN = "admin2020#";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        initListeners();
    }


    private void initListeners() {
        final EditText passwordText = findViewById(R.id.adminPasswordText);


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

                if (passText.length() == 10) {

                    if (PASSWORD_ADMIN.equals(passText)){
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(),0);

                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                    }else {
                        passwordText.setText("");
                        showToast("Bad Password");
                    };
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

        final EditText passwordText = findViewById(R.id.adminPasswordText);
        passwordText.setText("");
        passwordText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }
}
