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

import org.w3c.dom.Text;

import java.util.List;

public class EmployeeEetManage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_eet_manage);

        initTable();
    }

    public void initTable() {
        TableLayout ll = (TableLayout) findViewById(R.id.eetReceiptTable);
        ll.removeAllViews();

        final AppDatabase dbs = LocalDatabase.getInstance(getApplicationContext()).DBS;

        //TODO ADD receipt view only for one user and between dates

        List<Receipt> receiptList = dbs.receiptDAO().getAll();

        TextView receiptId = null;
        TextView employeeId = null;
        TextView price = null;
        TextView printedDate = null;
        TextView isSend = null;

        Button sendButton = null;
        Button deleteButton = null;



        String[] headers = new String[]{"ReceiptID","EmployeeID","PrintedDate","Price","Is Send","Action","Action"};
        TableRow tableHead= new TableRow(this);
        for(String header: headers){
            TextView headerText = new TextView(this);
            headerText.setText(header);
            headerText.setPadding(0, 0, 10, 0);
            tableHead.addView(headerText);
        }

        ll.addView(tableHead,0);

        int i = 1;
        for (final Receipt rcp : receiptList){
            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            //ID
            receiptId = new TextView(this);
            receiptId.setPadding(0, 0, 10, 0);
            receiptId.setText("" + rcp.getId());

            employeeId = new TextView(this);
            employeeId.setPadding(0, 0, 10, 0);
            employeeId.setText("" + rcp.getEmployeeId());

            printedDate = new TextView(this);
            printedDate.setPadding(0, 0, 10, 0);
            printedDate.setText(""+ rcp.getDatePrinted());

            price = new TextView(this);
            price.setPadding(0, 0, 10, 0);
            price.setText("" + rcp.getPrice());

            isSend = new TextView(this);
            isSend.setPadding(0, 0, 10, 0);
            isSend.setText("" + rcp.isSend());

            //send button
            sendButton = new Button(this);
            sendButton.setPadding(0, 0, 10, 0);
            sendButton.setText("Send");
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    manualEetSend(rcp.getEmployeeId(),rcp.getId(),rcp.getEetRequest());
                }
            });

            //send button
            deleteButton = new Button(this);
            deleteButton.setText("Del");
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbs.receiptDAO().delete(rcp);
                    initTable();
                }
            });

            row.addView(receiptId);
            row.addView(employeeId);
            row.addView(printedDate);
            row.addView(price);
            row.addView(isSend);

            if (!rcp.isSend()){
                row.addView(deleteButton);
                row.addView(sendButton);
            }

            ll.addView(row,i);
            i++;
        }

    }

    public void manualEetSend(int employeeId,int receiptId, String request){
        EET eetModule = new EET(this);
        EetTaskParams params = new EetTaskParams(employeeId,receiptId,request);
        EetTaskParams[] eetParams = {params};
        eetModule.execute(eetParams);
    }
}
