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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothPrinter {

    private static BluetoothPrinter instance;

    int shopID = 1;
    //BL shit
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView pairedDevicesListView;

    public IMyBinder binder;

    //bindService connection
    ServiceConnection conn;

    private boolean isConnectedToPrinter;

    Context ctx;

    String posPrinterName;

    private BluetoothPrinter(Context ctx) {
        this.ctx = ctx;
        setupBT();
    }


    public static void init(Context context) {
        if (instance == null) {
            instance = new BluetoothPrinter(context);
        }
    }

    public static BluetoothPrinter getInstance(Context context) {
        init(context);
        return instance;
    }

    public void setupBT(){
        //bluetooth magic

        BA = BluetoothAdapter.getDefaultAdapter();

        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //ctx.startActivityForResult(turnOn, 0);
            ctx.startActivity(turnOn);
            //Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(getApplicationContext(), "BT Already on", Toast.LENGTH_LONG).show();

            conn  = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    //Bind succesfully
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

            Intent intent= new Intent(ctx, BluetoothPrinter.class);
            ctx.bindService(intent, conn, Context.BIND_AUTO_CREATE);

        }
    }

    public void listDevices(View v){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        //Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        //final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

    }

    void connectToBTPrinter(){

        //now we need to get the name of the bluetooth printer first that is stored in the preferences

        //SharedPreferences sharedPref =
               // PreferenceManager.getDefaultSharedPreferences(this);

        // TODO dat natvrdo jmeno printeru
        //String posPrinterName = sharedPref.getString("pos_printer_name", "-1");


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
        Toast.makeText(ctx, msg,Toast.LENGTH_SHORT).show();
    }

    public IMyBinder getBinder() {
        return binder;
    }

    public boolean isConnectedToPrinter() {
        return isConnectedToPrinter;
    }
}
