package td.pokladna2.receipt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import td.pokladna2.EET;
import td.pokladna2.EetTaskParams;
import td.pokladna2.LocalDatabase;
import td.pokladna2.R;
import td.pokladna2.eetdatabase.Receipt;
import td.pokladna2.employeedbs.AppDatabase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EmployeeEetManage extends AppCompatActivity {

    int employeeId;
    AppDatabase dbs;

    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_eet_manage);

        dbs = LocalDatabase.getInstance(getApplicationContext()).DBS;

        String empId = getIntent().getExtras().getString("EMPLOYEE_ID");
        employeeId = Integer.valueOf(empId);

        calendar = Calendar.getInstance();

        initTable();
        initButtons();
    }

    private void initButtons() {

        Button pickDateButton = findViewById(R.id.pickDateButton);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String formatedDate = sdf.format(calendar.getTime());
        pickDateButton.setText(formatedDate);

        final EmployeeEetManage activityThis = this;
        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment(activityThis);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    public void initTable() {

        int dayOfMonth = calendar.get(calendar.DAY_OF_MONTH);
        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(calendar.MONTH);

        Date[] dateRange = getTodayDateRange(year, month, dayOfMonth);

        TableLayout ll = (TableLayout) findViewById(R.id.eetReceiptTable);
        ll.removeAllViews();

        //TODO ADD receipt view only for one user and between dates
        //List<Receipt> receiptList = dbs.receiptDAO().getAll();

        List<Receipt> receiptList = dbs.receiptDAO().findEmployeeReceiptPrintedBetweenDates(employeeId,dateRange[0],dateRange[1]);

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

    public void updateTable(int year, int month, int day){

        calendar.set(year, month,day);
        initButtons();
        initTable();
    }

    Date[] getTodayDateRange(int year, int month, int dayOfMonth){

        Calendar cal = Calendar.getInstance();

        cal.set(year,month,dayOfMonth,1,0);
        Date startDate = cal.getTime();
        System.out.println("datum" + cal.toString());
        final long startTmp = startDate.getTime();

        cal.set(year,month,dayOfMonth,23,0);
        System.out.println("datum2" + cal.toString());
        Date endDate = cal.getTime();
        long endTmp = endDate.getTime();

        return new Date[]{startDate,endDate};
    }

    public void manualEetSend(int employeeId,int receiptId, String request){
        EET eetModule = new EET(this);
        EetTaskParams params = new EetTaskParams(employeeId,receiptId,request);
        EetTaskParams[] eetParams = {params};
        eetModule.execute(eetParams);
    }

}
