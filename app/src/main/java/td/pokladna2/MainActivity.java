package td.pokladna2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.DataForSendToPrinterPos58;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements CustomPriceFragment.EditCustomPriceDialogListener {

    TextView priceDisplay;

    TextView calcOperationDisplay;

    LinkedList<String> calcOperationList;

    ArrayList<String> decorationPrices;

    //BL shit
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView pairedDevicesListView;

    public static IMyBinder binder;

    //bindService connection
    ServiceConnection conn;


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

        setupBT();

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

        posPrinterName = "Printer001";

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
                showSnackBar("connected too BT printer");
            }

            @Override
            public void onfailed() {
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
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
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

        //quickButtonSetup

        final Button quickButtonA = (Button) findViewById(R.id.quickPrintA);

        quickButtonA.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addNumberToPriceDisplay("280");
                printReceipt();
            }
        });

        final Button quickButtonB = (Button) findViewById(R.id.quickPrintB);

        quickButtonB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addNumberToPriceDisplay("380");
                printReceipt();
            }
        });

        final Button quickButtonC = (Button) findViewById(R.id.quickPrintC);

        quickButtonC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addNumberToPriceDisplay("460");
                printReceipt();
            }
        });

        final Button quickButtonD = (Button) findViewById(R.id.quickPrintD);

        quickButtonD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addNumberToPriceDisplay("480");
                printReceipt();
            }
        });
    }

    private void handleCalcOperation(String operation) {
        String textInCalcOperation = (String) calcOperationDisplay.getText();
        if (textInCalcOperation.equals("0")) return;

        String lastChar = textInCalcOperation.trim().substring(textInCalcOperation.trim().length()-1);

        if (!lastChar.equals("+")){
            textInCalcOperation = textInCalcOperation + " + ";
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

    //TODO: print the price to the printer
    private void printReceipt(){

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
                            String companyShop = "Pobocka: Fajtlova 1090/1, 161 00, Praha 6, OC ŠESTKA";

                            //adding current time
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss ");
                            String currentDateTimeString = sdf.format(new Date());
                            String datePrinted = "Datum: " + currentDateTimeString;

                            String divider = "------------------";

                            ArrayList<String> headerList = new ArrayList<String>();

                            headerList.add(companyHeader);
                            headerList.add(companyAddress);
                            headerList.add(companyID);
                            headerList.add(companyShop);
                            headerList.add(datePrinted);
                            headerList.add(divider);

                            for(String str : headerList){
                                list.add(StringUtils.strTobytes(str));
                                list.add(DataForSendToPrinterPos58.printAndFeedLine());
                            }

                            list.add(DataForSendToPrinterPos58.selectAlignment(0)); //left

                            //here we add each price of a service


                            byte[] basePrice = StringUtils.strTobytes("Sluzba " + getBasePrice() + ",- Kc");
                            list.add(basePrice);
                            list.add(DataForSendToPrinterPos58.printAndFeedLine());

                            if (!decorationPrices.isEmpty()){
                                byte[] customPrice = StringUtils.strTobytes("Zdobeni " + getCustomPrice() + ",- Kc");
                                list.add(customPrice);
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
                            list.add(StringUtils.strTobytes("*** Dekujeme za navstevu ***"));
                            list.add(DataForSendToPrinterPos58.printAndFeedForward(6));

                            //cut pager
                            //ist.add(DataForSendToPrinterPos58.selectCutPagerModerAndCutPager(66,1));

                            list.add(DataForSendToPrinterPos58.creatCashboxContorlPulse(1,25,250));

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
        if(inputText.length() > 7) {
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
}



