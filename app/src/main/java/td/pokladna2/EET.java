package td.pokladna2;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


//import eet.EetRegisterRequest;

import androidx.appcompat.app.AppCompatActivity;
import openeet.lite.EetRegisterRequest;
import td.pokladna2.EetTaskParams;
import td.pokladna2.MainActivity;
import td.pokladna2.ReceiptDTO;
import td.pokladna2.eetdatabase.Receipt;
import td.pokladna2.employeedbs.AppDatabase;
import td.pokladna2.employeedbs.Employee;

import static eet.EetRegisterRequest.loadStream;


public class EET extends AsyncTask<EetTaskParams, Void, ReceiptDTO> {

    private View view;

    private AppCompatActivity activity;

    private boolean isNewEetSend;
    private boolean isEETSuccessSend;

    AppDatabase dbs;


    public EET(AppCompatActivity activity) {
        this.activity =  activity;
        dbs = LocalDatabase.getInstance(activity.getApplicationContext()).DBS;
    }

    @Override
    protected ReceiptDTO doInBackground(EetTaskParams... params){

        isNewEetSend = true;

        if (params[0].getReceiptId() > 0){
            isNewEetSend = false;
        }


        double totalSum = 0;
        String terminalId = "";

        if (isNewEetSend){
            totalSum = params[0].getTotalSum();
            terminalId = params[0].getTerminalId();
        }

        int employeeId = params[0].getEmployeeId();
        int receiptId = params[0].getReceiptId();

        AssetManager am = activity.getApplicationContext().getAssets();
        InputStream cert = null;
        InputStream keyStore = null;

        Employee emp = dbs.employeeDAO().findById(Integer.valueOf(employeeId));

        File file = new File(emp.getCertificateName());

        //this is production line for dynamic load of certificate
        try {
            cert = new FileInputStream(file);
            keyStore = am.open("newbks");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //uncomment for production
        //cert = MainActivity.class.getResourceAsStream( "/EET_CA1_Playground-CZ1212121218.p12");
        //cert = am.open("/EET_CA1_Playground-CZ1212121218.p12");



        //check for internet connection
        /*
        if (!isNetworkAvailable()){
            activity.showSnackBar("No wifi connection");
            //return null;
        }

         */

        //Employee certificate
        String dic = emp.getDic();
        String pkcsPassworkd = emp.getCertificatePassword();
        String shopId = emp.getShopId();
        String currentTime = getCurrentTime();
        String receiptSequenceId = getReceiptId();
        EetRegisterRequest request= null;

        //custom keystore
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(keyStore, "eeteet".toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            request = EetRegisterRequest.builder()
                    .dic_popl(dic)
                    .id_provoz(shopId)
                    .id_pokl(terminalId)
                    .porad_cis(receiptSequenceId)
                    .dat_trzby(currentTime)
                    .celk_trzba(totalSum)
                    .rezim(0)
                    .pkcs12(loadStream(cert))
                    .trustKeyStore(ks)
                    .pkcs12password(pkcsPassworkd)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //for receipt printing in online mode
        String bkp=request.formatBkp();
        System.out.println("BKB je dlouhe " + bkp.length());
        //assertNotNull(bkp);

        //for receipt printing in offline mode
        String pkp=request.formatPkp();
        System.out.println("pkp je dlouhe " + pkp.length());
        //assertNotNull(pkp);
        //the receipt can be now stored for offline processing
        //System.out.println(pkp);

        //try send
        //TODO save requestBody to DBS - local and online

        String requestBody = null;

        if (isNewEetSend){
            requestBody = request.generateSoapRequest();
        }else{
            requestBody = params[0].getEetRequest();
        }


        //assertNotNull(requestBody);
        String response= null;

        try {
            response = request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3"));
        } catch (Exception e) {
            Log.d("EET","The EET send had failed when sending");
            e.printStackTrace();
        }
        //System.out.println(response);
        //extract FIK
        //assertNotNull(response);
        //assertTrue(response.contains("Potvrzeni fik="));

        String message = "";
        String fik = "";

        isEETSuccessSend = false;

        if (response != null){
            if (response.contains("Potvrzeni fik=")) {
                message = "EET PROBEHLO OK MAME FIK";
                System.out.println("EET PROBEHLO OK MAME FIK");
                //extracting the text
                int startIndex = response.indexOf("fik=");
                fik = response.substring(startIndex+5, startIndex + 39 + 5);
                isEETSuccessSend = true;
            }else{
                message = "EET NEPROBEHLO";
                System.out.println("EET NEPROBEHLO");
                fik = "EET NEPROBEHLO";
                Log.d("EET","The EET send had failed when sending");
            }
        }

        int convertedSum = (int) totalSum;

        if(isEETSuccessSend & isNewEetSend){
            //podarilo se poslat novou uctenku
            Receipt recp = new Receipt(1,convertedSum,new Date(),requestBody,isEETSuccessSend);
            dbs.receiptDAO().insertReceipt(recp);
            Log.d("EET", "podarilo se poslat novou uctenku");
        }else if(!isEETSuccessSend & isNewEetSend){
            //nepodarila se poslat nova uctenka
            Receipt recp = new Receipt(1,convertedSum,new Date(),requestBody,isEETSuccessSend);
            dbs.receiptDAO().insertReceipt(recp);
            Log.d("EET", "nepodarilo se poslat novou uctenku");
        }else if(isEETSuccessSend & !isNewEetSend){
            //podarilo se poslat uctenku ktera se prednim neposlala
            Receipt recp = dbs.receiptDAO().findById(receiptId);
            recp.setSend(true);
            dbs.receiptDAO().updateReceipt(recp);
            Log.d("EET", "podarilo se poslat uctenku ktera se prednim neposlala");
        }else{
            //nepodarilo se poslat uctenku ktera nebyla poslana
            Log.d("EET", "nepodarilo se poslat uctenku ktera nebyla poslana");
        }


        ReceiptDTO newReceiept = ReceiptDTO.Builder.newInstance()
                .setTotalSum(totalSum)
                .setTerminalId(terminalId)
                .setShopId(shopId)
                .setDateTime(currentTime)
                .setDic(dic)
                .setReceiptId(receiptSequenceId)
                .setFIK(fik)
                .setBKP(bkp)
                .setPKP(pkp)
                .build();

        return newReceiept;
        //ready to print online receipt
    }

    protected void onPostExecute(ReceiptDTO result) {

        if(isEETSuccessSend & isNewEetSend){
            //podarilo se poslat novou uctenku
            MainActivity mActivity = (MainActivity) activity;
            System.out.println(result);
            mActivity.printEetReceipt(result);

        }else if(!isEETSuccessSend & isNewEetSend){
            //nepodarila se poslat nova uctenka
            MainActivity mActivity = (MainActivity) activity;
            System.out.println(result);
            //TODO call print offline receipt
            //mActivity.printEetReceipt(result);

        }else if(isEETSuccessSend & !isNewEetSend){
            //podarilo se poslat uctenku ktera se prednim neposlala
            EmployeeEetManage mActivity = (EmployeeEetManage) activity;
            mActivity.initTable();

        }else{
            //nepodarilo se poslat uctenku ktera nebyla poslana

        }



    }


    //TODO cross check with internet time
    private String getCurrentTime(){
        // Input
        Date date = new Date(System.currentTimeMillis());

        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        sdf.setTimeZone(TimeZone.getTimeZone("CET"));
        String text = sdf.format(date);
        return text;
    }

    //Generates "poradove cislo", looks like date, in fact it is but its valid sequence number
    private String getReceiptId(){
        // Input
        Date date = new Date(System.currentTimeMillis());
        // Conversion
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("CET"));
        String text = sdf.format(date);

        return text;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
