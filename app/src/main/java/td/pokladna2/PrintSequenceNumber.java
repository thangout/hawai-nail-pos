package td.pokladna2;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
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
import net.posprinter.utils.DataForSendToPrinterPos80;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PrintSequenceNumber extends AppCompatActivity {

    //set the ID of a shop that the app is installed for
    // shopID = 2 is for Sestka
    // shopID = 1 is for Flora
    int shopID = 1;


    //BL shit
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView pairedDevicesListView;

    public IMyBinder binder;

    //bindService connection
    ServiceConnection conn;

    boolean isConnectedToPrinter;

    int sequenceNumber = 1;
    TextView sequenceText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_sequence_number);

        setupBT();
        //binder = BluetoothPrinter.getInstance(getApplicationContext()).getBinder();
        setUpData();
        setUpText();
        initButton();
    }

    private void setUpData() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (getData() < 0){
            editor.putInt("sequenceNumber", sequenceNumber);
            editor.commit();
        }else{
            int defaultValue = -1;
            int tmp = sharedPref.getInt("sequenceNumber", defaultValue);
            sequenceNumber = tmp;
        }

    }

    int getData(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int defaultValue = -1;
        int tmp = sharedPref.getInt("sequenceNumber", defaultValue);
        return tmp;
    }

    void saveData(int number){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("sequenceNumber", number);
        editor.commit();

    }

    private void setUpText() {
        sequenceText = findViewById(R.id.sequenceNumberText);
        updateSeuqenceText();
    }

    private void initButton() {

        Button sequenceButton = findViewById(R.id.sequencePrintButton);
        sequenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSequenceNextNumber();
                printReceipt();
                printReceipt();
                updateSeuqenceText();
            }
        });
    }

    private void updateSeuqenceText() {
        sequenceText.setText(String.valueOf(sequenceNumber));
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

            Intent intent= new Intent(this, PosprinterService.class);
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


    private void printReceipt(){

        //isConnectedToPrinter = BluetoothPrinter.getInstance(getApplicationContext()).isConnectedToPrinter();
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


                            String divider = "------------------";

                            ArrayList<String> headerList = new ArrayList<String>();

                            headerList.add(companyHeader);
                            headerList.add(companyAddress);
                            headerList.add(companyID);
                            headerList.add(companyShop);
                            headerList.add(companyShop2);
                            headerList.add(datePrinted);
                            headerList.add(divider);

                            for(String str : headerList){
                                list.add(StringUtils.strTobytes(str));
                                list.add(DataForSendToPrinterPos58.printAndFeedLine());
                            }

                            list.add(DataForSendToPrinterPos58.selectAlignment(0)); //left

                            //here we add each price of a service

                            byte[] hr = StringUtils.strTobytes("===============================");
                            list.add(hr);
                            list.add(DataForSendToPrinterPos58.printAndFeedLine());

                            list.add(DataForSendToPrinterPos58.selectCharacterSize(1));

                            //tady to je
                            byte[] totalPriceByte = StringUtils.strTobytes("Poradove cislo: " + sequenceNumber);
                            list.add(DataForSendToPrinterPos58.selectAlignment(0)); //center
                            list.add(totalPriceByte);
                            //tady to konci

                            //cut pager
                            list.add(DataForSendToPrinterPos58.printAndFeedForward(2));

                            if (shopID == 1){
                                //flora
                                list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66,1));


                            }else if(shopID == 2){

                            }

                            //save the money to dbs

                            //finish();
                            PrintSequenceNumber.this.finish();

                            return list;
                        }
                        return null;
                    }
                });

    }

    private String getSequenceNextNumber() {

        int currentState = getData();
        if(currentState == 80){
            currentState = 1;
        }

        currentState++;
        sequenceNumber = currentState;

        saveData(currentState);
        return String.valueOf(currentState);
    }
}
