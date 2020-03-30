package td.pokladna2;

import androidx.appcompat.app.AppCompatActivity;
import td.pokladna2.eetdatabase.Receipt;
import td.pokladna2.employeedbs.AddEmployee;
import td.pokladna2.employeedbs.AppDatabase;
import td.pokladna2.employeedbs.Employee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

public class EmployeeEetManage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_eet_manage);

        initTable();
    }

    private void initTable() {
        TableLayout ll = (TableLayout) findViewById(R.id.eetReceiptTable);
        ll.removeAllViews();

        AppDatabase dbs = LocalDatabase.getInstance(getApplicationContext()).DBS;

        //TODO ADD receipt view only for one user and between dates

        List<Receipt> receiptList = dbs.receiptDAO().getAll();

        TextView receiptId = null;
        TextView employeeId = null;
        TextView price = null;
        TextView printedDate = null;
        TextView isSend = null;

        Button sendButton = null;
        Button deleteButton = null;

        int i = 0;

        for (Receipt rcp : receiptList){
            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            //ID
            receiptId = new TextView(this);
            receiptId.setPadding(0, 0, 10, 0);
            receiptId.setText("ID: " + rcp.getId());

            employeeId = new TextView(this);
            employeeId.setPadding(0, 0, 10, 0);
            employeeId.setText("EmployeeId: " + rcp.getEmployeeId());

            printedDate = new TextView(this);
            printedDate.setPadding(0, 0, 10, 0);
            printedDate.setText("Date: " + rcp.getDatePrinted());

            price = new TextView(this);
            price.setPadding(0, 0, 10, 0);
            price.setText("Price: " + rcp.getPrice());

            isSend = new TextView(this);
            isSend.setPadding(0, 0, 10, 0);
            isSend.setText("Is send: " + rcp.isSend());

            //send button
            sendButton = new Button(this);
            sendButton.setPadding(0, 0, 10, 0);
            sendButton.setText("Send");
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            //send button
            deleteButton = new Button(this);
            deleteButton.setText("Del");
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            row.addView(receiptId);
            row.addView(employeeId);
            row.addView(printedDate);
            row.addView(price);
            row.addView(isSend);
            row.addView(deleteButton);
            row.addView(sendButton);

            ll.addView(row,i);
            i++;
        }

    }
}
