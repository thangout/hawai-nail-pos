package td.pokladna2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.DataForSendToPrinterPos58;
import net.posprinter.utils.DataForSendToPrinterPos80;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements CustomPriceFragment.EditCustomPriceDialogListener {

    //set the ID of a shop that the app is installed for
    // shopID = 2 is for Sestka
    // shopID = 1 is for Flora
    int shopID = 1;


    //use name = nailsfloratest, for test purposes
    //Flora production
    //String DBS_NAME = "nailsfloraprod";

    //Sestka production
    //String DBS_NAME = "nailssestkaprod";
    String DBS_NAME = "nailsfloratest";

    TextView priceDisplay;

    TextView calcOperationDisplay;

    LinkedList<String> calcOperationList;

    ArrayList<String> decorationPrices;

    //index of a price that will be reduced by 50%
    int halfPriceIndex = 0;

    //BL shit
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView pairedDevicesListView;

    public static IMyBinder binder;

    //bindService connection
    ServiceConnection conn;

    FirebaseFirestore db;

    String employeeName;
    String employeeId;

    String[] mobileArray = {"Android"};

    boolean isConnectedToPrinter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        priceDisplay = findViewById(R.id.viewPrice);
        calcOperationDisplay = findViewById(R.id.viewCalcOperation);

        //inits the operation list
        calcOperationList = new LinkedList<>();

        //here we store each custom added price (zdobeni)
        decorationPrices = new ArrayList<>();


        setupButtons();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);


        setupEmployeeInfo();

        //comment for deployment on simulator
        //setupBT();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
            //5minutes
        }, 300000);
    }

    private void setupEmployeeInfo() {
        Intent intent = getIntent();
        employeeName = intent.getStringExtra("name");
        employeeId = intent.getStringExtra("id");

        TextView empNameText = findViewById(R.id.employeeNameText);
        empNameText.setText(employeeName);

        //TODO fetch data about the employee all his transactions

        //set time to query data
        Date date = new Date();
        Calendar cal = Calendar.getInstance();

        int dayOfMonth = cal.get(cal.DAY_OF_MONTH);
        int year = cal.get(cal.YEAR);
        int month = cal.get(cal.MONTH);


        cal.set(year,month,dayOfMonth,1,0);
        Date startDate = cal.getTime();
        System.out.println("datum" + cal.toString());
        final long startTmp = startDate.getTime();

        cal.set(year,month,dayOfMonth,23,0);
        System.out.println("datum2" + cal.toString());
        Date endDate = cal.getTime();
        long endTmp = endDate.getTime();
        //time in miliseconds


        CollectionReference transactionsRef = db.collection(DBS_NAME);
        //Query query = transactionsRef.whereEqualTo("employeeId", employeeId).whereGreaterThan("timestamp",startDate).whereLessThan("timestamp",endDate);
        Query query = transactionsRef.whereEqualTo("employeeId",employeeId).orderBy("timestamp").startAt(startDate).endAt(endDate);
        //Query query = transactionsRef.whereEqualTo("employeeId", employeeId).orderBy("timestamp");

        Log.d("DATABASE", "Setting up employee data ");


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                final ArrayList<String> priceLists = new ArrayList<>();

                int transactionSum = 0;
                Log.d("DATABASE", "Started quering: and found " + queryDocumentSnapshots.size());

                for (Iterator<QueryDocumentSnapshot> it = queryDocumentSnapshots.iterator(); it.hasNext(); ) {
                    DocumentSnapshot snap = it.next();

                    HashMap map = (HashMap) snap.getData();
                    Long price = (Long) map.get("price");
                    Timestamp timestamp = (Timestamp) map.get("timestamp");
                    long convertedTmp = timestamp.getSeconds();
                    priceLists.add(String.valueOf(price));

                    transactionSum += price;
                }

                String[] a = new String[25];

                for (int i = 0; i < a.length; i++) {
                    a[i] = " ";
                }

                if (!priceLists.isEmpty()) {
                    for (int i = 0; i < priceLists.size(); i++) {
                            a[i] = priceLists.get(i);
                    }
                }

                //load data to display transactions
                ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this,
                        R.layout.single_list_view, a);

                ListView listView = (ListView) findViewById(R.id.transactionListView);
                listView.setAdapter(adapter);

                TextView transactionSumText = (TextView) findViewById(R.id.transSumText);
                transactionSumText.setText(String.valueOf(transactionSum));
            }
        });


    }

    public void setupBT(){
        //bluetooth magic

        BA = BluetoothAdapter.getDefaultAdapter();



        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "BT Already on", Toast.LENGTH_LONG).show();

            conn  = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    //Bind successfully
                    binder = (IMyBinder) iBinder;
                    Log.e("binder","connected");
                    listDevices(null);
                    connectToBTPrinter();
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    Log.e("disbinder","disconnected");
                }
            };

            Intent intent= new Intent(this,PosprinterService.class);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);

        }
    }

    public void listDevices(View v){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

    }

    void connectToBTPrinter(){

        //now we need to get the name of the bluetooth printer first that is stored in the preferences

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);

        // TODO dat natvrdo jmeno printeru
        String posPrinterName = sharedPref.getString("pos_printer_name", "-1");


        if (shopID == 1){
            //flora
            posPrinterName = "Printer002";
        }else if (shopID == 2){
            //sestka
            posPrinterName = "Printer001";
        }


        ArrayList<BluetoothDevice> list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt);

        boolean isPrinterNameExist = false;
        BluetoothDevice btDevice = null;

        for(BluetoothDevice bDevice: list){
            if (bDevice.getName().equals(posPrinterName)) {
                btDevice = bDevice;
                isPrinterNameExist = true;
            }

        }

        //this checks if the name of the printer in the settings exist within the list of paired BT devices
        if(!isPrinterNameExist) {
            showSnackBar("Printer with the name "+ posPrinterName + " doesn't exist");
            return;
        }

        String btDeviceAddress = btDevice.getAddress();

        binder.connectBtPort(btDeviceAddress, new UiExecute() {
            @Override
            public void onsucess() {
                isConnectedToPrinter = true;
                showSnackBar("connected too BT printer");
            }

            @Override
            public void onfailed() {
                isConnectedToPrinter = false;
                showSnackBar("didnt connect to BT printer");
            }
        });
    }

    void showSnackBar(String msg){
        Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
    }

    public void setupButtons(){

        Button b0 = findViewById(R.id.noZero);
        Button b1 = findViewById(R.id.noOne);
        Button b2 = findViewById(R.id.noTwo);
        Button b3 = findViewById(R.id.noThree);
        Button b4 = findViewById(R.id.noFour);
        Button b5 = findViewById(R.id.noFive);
        Button b6 = findViewById(R.id.noSix);
        Button b7 = findViewById(R.id.noSeven);
        Button b8 = findViewById(R.id.noEight);
        Button b9 = findViewById(R.id.noNine);

        ArrayList listNumbers = new ArrayList<Button>();

        listNumbers.add(b0);
        listNumbers.add(b1);
        listNumbers.add(b2);
        listNumbers.add(b3);
        listNumbers.add(b4);
        listNumbers.add(b5);
        listNumbers.add(b6);
        listNumbers.add(b7);
        listNumbers.add(b8);
        listNumbers.add(b9);

        //setting up the number of each number
        for(int i= 0; i < 10; i++){

            Button bTmp = (Button) listNumbers.get(i);
            final int finalI = i;

            bTmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNumberToPriceDisplay(String.valueOf(finalI));

                }
            });
        }

        //erase display of number

        final Button setZeroToPriceDisplay = findViewById(R.id.buttonErase);
        setZeroToPriceDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setZeroPriceDisplay();

            }
        });

        Button printReceiptButton = findViewById(R.id.buttonPrintReciept);

        printReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (priceDisplay.getText().equals("0")) return;

                printReceipt();
            }
        });

        //open custom price
        final Button buttonCustomPrice = (Button) findViewById(R.id.customPriceButton);

        buttonCustomPrice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
                CustomPriceFragment dialogFragment = CustomPriceFragment.newInstances(2);
                dialogFragment.show(fm,"asd");

            }
        });

        //plusButton
        final Button buttonPlus = (Button) findViewById(R.id.plusButton);

        buttonPlus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handleCalcOperation("+");
            }
        });

        //minusButton

        final Button minusButton = (Button) findViewById(R.id.minusButton);

        minusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteLastTransaction();
            }
        });


        final Button halfPrice = (Button) findViewById(R.id.halfPriceButton);

        halfPrice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setPriceToHalfPrice();
            }
        });


        Button finishMainActivityButton = findViewById(R.id.finishMainActivityButton);

        finishMainActivityButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void saveTransactionToDbs() {
        Map<String, Object> user = new HashMap<>();
        user.put("price", Integer.valueOf(getCurrentPrice()));
        user.put("timestamp", Timestamp.now());
        user.put("employeeId", employeeId);
        user.put("serverTimestamp", FieldValue.serverTimestamp());
        user.put("shopId", shopID);


        //production collection will be nailsfloraprod
        //use some other name for test such as moneytest
        db.collection(DBS_NAME)
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("x", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("x", "Error adding document", e);
                    }
                });
    }

    private void setPriceToHalfPrice() {
        String[] eachPrice = calcOperationDisplay.getText().toString().split("\\+");

        //if the index of a price that will be reduced by 50%
        if(halfPriceIndex < eachPrice.length){
            int price = Integer.valueOf(eachPrice[halfPriceIndex].trim());
            price = price / 2;
            if (price%10 == 5){
                price += 5;
            }
            eachPrice[halfPriceIndex] = String.valueOf(price);
            halfPriceIndex++;
        }

        String newCalcOperationText = "";
        int totalPrice = 0;
        for (String tmpPrice : eachPrice){
            newCalcOperationText += tmpPrice.trim() + "+";
            totalPrice += Integer.valueOf(tmpPrice.trim());
        }

        calcOperationDisplay.setText(newCalcOperationText);
        priceDisplay.setText(String.valueOf(totalPrice));

    }

    private void handleCalcOperation(String operation) {
        String textInCalcOperation = (String) calcOperationDisplay.getText();
        if (textInCalcOperation.equals("0")) return;

        String lastChar = textInCalcOperation.trim().substring(textInCalcOperation.trim().length()-1);

        if (!lastChar.equals("+")){
            textInCalcOperation = textInCalcOperation + "+";
        }

        calcOperationDisplay.setText(textInCalcOperation);
    }

    private void updateCalcOperationList(String operation) {
        if (calcOperationList.isEmpty()) {
            calcOperationList.add((String) priceDisplay.getText());
            return;
        }

        //list is not empty
        String lastItemInList = calcOperationList.getLast();
        //when there is some operation at the end of the list then subsitute it with the new one
        //otherwise add new operation
        if (lastItemInList.equals("+") || lastItemInList.equals("-") || lastItemInList.equals("/")){
            calcOperationList.removeLast();
            calcOperationList.add(operation);
        }else{
            calcOperationList.add((String) priceDisplay.getText());
            calcOperationList.add(operation);
        }
    }

    private void updateCalcOperationDisplay() {
        String updated = "";
        for(String tmp : calcOperationList){
            updated = updated + tmp + "+";
        }
        calcOperationDisplay.setText(updated);
    }


    private void printReceipt(){

        //if bluetooth printer is not connected
        if (!isConnectedToPrinter) return;

        binder.writeDataByYouself(
                new UiExecute() {
                    @Override
                    public void onsucess() {
                        // when printing is successful callback

                        //Intent intent = new Intent();
                        //clean the transaction history in the main activity by setting the callback to printStatus flag to 1
                        //intent.putExtra("printStatusFlag", 1);
                        //setResult(RESULT_OK, intent);

                        //close pick employee activity
                        //finish();
                        setZeroPriceDisplay();
                    }

                    @Override
                    public void onfailed() {

                    }
                }, new ProcessData() {
                    @Override
                    public List<byte[]> processDataBeforeSend() {


                        List<byte[]> list=new ArrayList<byte[]>();


                        if (false){
                        }else {
                            //initialize the printer
//                          list.add( DataForSendToPrinterPos58.initializePrinter());
                            list.add(DataForSendToPrinterPos58.initializePrinter());
                            list.add(DataForSendToPrinterPos58.selectCharacterCodePage(6));
                            //
                            list.add(DataForSendToPrinterPos58.selectAlignment(1)); //center


                            String companyHeader = "EURO Nails.cz s.r.o.";
                            String companyAddress = "Prazská 3/5, 268 01, Horovice";
                            String companyID = "IC: 27868648, DIC: CZ27868648";

                            String companyShop = "default name";
                            String companyShop2 = "default name 2";

                            if (shopID == 1){
                                //FLORA
                                companyShop = "Pobocka: Vinohradska 151, 130 00, Praha 3";
                                companyShop2 = "OC Atrium Flora";
                            }else if(shopID == 2){
                                //SESTKA
                                companyShop = "Pobocka: Fajtlova 1090/1, 161 00, Praha 6, OC ŠESTKA";
                                companyShop2 = "OC Sestka";
                            }




                            //adding current time
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss ");
                            String currentDateTimeString = sdf.format(new Date());
                            String datePrinted = "Datum: " + currentDateTimeString;

                            //id of logged in employee
                            String employeeIdReciept = "Kod: " + employeeId;

                            String divider = "------------------";

                            ArrayList<String> headerList = new ArrayList<String>();

                            headerList.add(companyHeader);
                            headerList.add(companyAddress);
                            headerList.add(companyID);
                            headerList.add(companyShop);
                            headerList.add(companyShop2);
                            headerList.add(datePrinted);
                            headerList.add(employeeIdReciept);
                            headerList.add(divider);

                            for(String str : headerList){
                                list.add(StringUtils.strTobytes(str));
                                list.add(DataForSendToPrinterPos58.printAndFeedLine());
                            }

                            list.add(DataForSendToPrinterPos58.selectAlignment(0)); //left

                            //here we add each price of a service

                            String[] eachPrice = calcOperationDisplay.getText().toString().split("\\+");

                            for (String tmpPrice : eachPrice){
                                byte[] basePrice = StringUtils.strTobytes("Sluzba " + tmpPrice + ",- Kc");
                                list.add(basePrice);
                                list.add(DataForSendToPrinterPos58.printAndFeedLine());
                            }



                            byte[] hr = StringUtils.strTobytes("===============================");
                            list.add(hr);
                            list.add(DataForSendToPrinterPos58.printAndFeedLine());


                            list.add(DataForSendToPrinterPos58.selectCharacterSize(1));


                            byte[] totalPriceByte = StringUtils.strTobytes("Celková cena: " + getCurrentPrice() + ",- Kc");
                            list.add(DataForSendToPrinterPos58.selectAlignment(0)); //center
                            list.add(totalPriceByte);

                            list.add(DataForSendToPrinterPos58.printAndFeedLine());
                            list.add(DataForSendToPrinterPos58.selectCharacterSize(0));
                            list.add(hr);

                            //bellow margin so the text is above the cut line
                            list.add(DataForSendToPrinterPos58.selectAlignment(1)); //center
                            list.add(DataForSendToPrinterPos58.printAndFeedForward(2));

                            list.add(StringUtils.strTobytes("7 dni zaruka na nase sluzby"));
                            list.add(DataForSendToPrinterPos58.printAndFeedForward(2));
                            list.add(StringUtils.strTobytes("*** Dekujeme za Vasi navstevu ***"));
                            list.add(DataForSendToPrinterPos58.printAndFeedForward(6));

                            //cut pager


                            if (shopID == 1){
                                //flora
                                list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66,1));
                                list.add(DataForSendToPrinterPos80.creatCashboxContorlPulse(0,25,250));

                            }else if(shopID == 2){

                                list.add(DataForSendToPrinterPos58.creatCashboxContorlPulse(1,25,250));
                            }

                            //save the money to dbs
                            saveTransactionToDbs();

                            //finish();
                            MainActivity.this.finish();

                            return list;
                        }
                        return null;
                    }
                });

    }

    private int getCustomPrice() {
        int totalCustomPrice = 0;
        for (String tmpPrice : decorationPrices){
            int convertedPrice = Integer.valueOf(tmpPrice);
            totalCustomPrice += convertedPrice;
        }
        return totalCustomPrice;
    }

    private int getBasePrice() {
        int totalPrice = Integer.valueOf(priceDisplay.getText().toString());

        return totalPrice - getCustomPrice();
    }


    private String getCurrentPrice() {
        return (String) priceDisplay.getText();
    }

    public void setZeroPriceDisplay(){

        priceDisplay.setText("0");
        calcOperationDisplay.setText("0");
        decorationPrices.clear();
        halfPriceIndex = 0;

        eetCall();

    };

    public void addNumberToPriceDisplay(String inputNo){
        String textInCalcDisplay = (String) calcOperationDisplay.getText();

        String textInDisplay = (String) priceDisplay.getText();
        if(textInDisplay.equals("0") && inputNo.equals("0")) return;

        if (textInDisplay.equals("0")) textInDisplay = "";
        if (textInCalcDisplay.equals("0")) textInCalcDisplay = "";

        /* String oldText = textInDisplay;
        if (oldText=="0"){
            oldText = "";
        }else{
            oldText = (String) priceDisplay.getText();
        }

        */

        //TODO check the length of the input
        if (textInDisplay.length() > 4){
            showSnackBar("Number is too high");
            return;
        }

        calcOperationDisplay.setText(textInCalcDisplay+String.valueOf(inputNo));
        priceDisplay.setText(textInDisplay + String.valueOf(inputNo));
        updateMainPriceDisplay();
    }


    private void updateMainPriceDisplay() {
        String[] eachPrice = calcOperationDisplay.getText().toString().split("\\+");
        int totalPrice = 0;
        for (String tmpPrice : eachPrice){
            totalPrice += Integer.valueOf(tmpPrice.trim());
        }

        priceDisplay.setText(String.valueOf(totalPrice));
    }


    //handles the DONE button in customprice fragment
    @Override
    public void onFinishEditCustomPrice(String inputText) {

        //fix when user doesnt enter any number
        if (inputText.equals("")) return;

        //fix overflow of the integer
        if(inputText.length() > 5) {
            showSnackBar("Số quá lớn");
            return;
        }

        int price = Integer.parseInt(inputText);

        //call plus before adding custom price
        handleCalcOperation("+");
        addNumberToPriceDisplay(inputText);
        //and store it to the custom price list
        decorationPrices.add(inputText);

        showSnackBar(inputText);
    }

    //adds the last transaction as a negative number and by that fix the error printing
    void deleteLastTransaction(){

        Date date = new Date();
        Calendar cal = Calendar.getInstance();

        int dayOfMonth = cal.get(cal.DAY_OF_MONTH);
        int year = cal.get(cal.YEAR);
        int month = cal.get(cal.MONTH);


        cal.set(year,month,dayOfMonth,1,0);
        Date startDate = cal.getTime();
        System.out.println("datum" + cal.toString());
        final long startTmp = startDate.getTime();

        cal.set(year,month,dayOfMonth,23,0);
        System.out.println("datum2" + cal.toString());
        Date endDate = cal.getTime();
        long endTmp = endDate.getTime();
        //time in miliseconds


        CollectionReference transactionsRef = db.collection(DBS_NAME);
        //Query query = transactionsRef.whereEqualTo("employeeId", employeeId).whereGreaterThan("timestamp",startDate).whereLessThan("timestamp",endDate);
        Query query = transactionsRef.whereEqualTo("employeeId",employeeId).orderBy("timestamp").startAt(startDate).endAt(endDate);
        //Query query = transactionsRef.whereEqualTo("employeeId", employeeId).orderBy("timestamp");

        Log.d("DATABASE", "Setting up employee data ");




        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                final LinkedList<String> priceLists = new LinkedList<>();



                int transactionSum = 0;
                Log.d("DATABASE", "Started quering: and found " + queryDocumentSnapshots.size());

                for (Iterator<QueryDocumentSnapshot> it = queryDocumentSnapshots.iterator(); it.hasNext(); ) {
                    DocumentSnapshot snap = it.next();

                    HashMap map = (HashMap) snap.getData();
                    Long price = (Long) map.get("price");
                    Timestamp timestamp = (Timestamp) map.get("timestamp");
                    long convertedTmp = timestamp.getSeconds();
                    priceLists.add(String.valueOf(price));

                    transactionSum += price;
                }

                //if there is no transaction yet then cant proceed with fixing the last price
                if (priceLists.isEmpty()) return;

                showSnackBar(priceLists.getLast() + "");

                // create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Xóa giao dịch cuối cùng");
                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.fragment_delete_transaction, null);
                builder.setView(customLayout);

                TextView deleteTextView = customLayout.findViewById(R.id.deleteTextView);

                //deleteTextView.setText("Would you like to delete last transaction. Amount:"  + priceLists.getLast());
                deleteTextView.setText("Bạn có muốn xóa số tiền:"  + priceLists.getLast());
                // add a button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send data from the AlertDialog to the Activity
                        //EditText editText = customLayout.findViewById(R.id.editText);
                        String lastPrice = priceLists.getLast();
                        int lastPriceNumber = Integer.valueOf(lastPrice);

                        //check for the last number if its below zero
                        if (lastPriceNumber < 0){
                            dialog.dismiss();
                            showSnackBar("CANT FIX number below zero");
                        }else{
                            priceDisplay.setText(String.valueOf("-" + priceLists.getLast()));
                            printReceipt();
                        }

                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    private void sendDialogDataToActivity(String data) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }


    private void eetCall(){

        EET eetModule = new EET();

        AssetManager am = getAssets();

        InputStream cert = null;
        InputStream jks = null;

        try {
            cert = am.open("EET_CA1_Playground-CZ1212121218.p12");
            jks = am.open("newbks");

            String message = "nic se nestalo";

            if (cert != null){
                //message = EET.simpleRegistrationProcessTest(cert);
                InputStream[] certs = new InputStream[2];
                certs[0] = cert;
                certs[1] = jks;
                eetModule.execute(certs);
            }

            sendDialogDataToActivity(message);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (conn != null) {
            unbindService(conn);
        }
    }
}



