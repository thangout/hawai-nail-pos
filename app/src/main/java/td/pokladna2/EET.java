package td.pokladna2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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

import openeet.lite.EetRegisterRequest;
import td.pokladna2.EetTaskParams;
import td.pokladna2.MainActivity;
import td.pokladna2.ReceiptDTO;

import static eet.EetRegisterRequest.loadStream;


public class EET extends AsyncTask<EetTaskParams, Void, ReceiptDTO> {

    private View view;

    private MainActivity activity;

    public EET(View view) {
        this.view = view;
    }

    public EET(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected ReceiptDTO doInBackground(EetTaskParams... params){

        //check for internet connection
        if (!isNetworkAvailable()){
            return null;
        }

        //Employee certificate
        InputStream cert = params[0].getCertificate();
        InputStream keyStore = params[0].getKeyStore();
        String dic = params[0].getDic();
        double totalSum = params[0].getTotalSum();
        String pkcsPassworkd = params[0].getPkcsPassword();
        String shopId = params[0].getShopId();
        String terminalId = params[0].getTerminalId();

        String currentTime = getCurrentTime();
        String receiptId = getReceiptId();

        EetRegisterRequest request= null;


        //custom keystore
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            //ks = KeyStore.getInstance("JKS");
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
                    .porad_cis(receiptId)
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
        String requestBody=request.generateSoapRequest();

        //assertNotNull(requestBody);
        String response= null;

        try {
            response = request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3"));
        } catch (Exception e) {
            Log.d("EET","The EET send had failed when sending");
            e.printStackTrace();
        }
        System.out.println(response);
        //extract FIK
        //assertNotNull(response);
        //assertTrue(response.contains("Potvrzeni fik="));

        String message = "";
        String fik = "";

        if (response != null){
            if (response.contains("Potvrzeni fik=")) {
                message = "EET PROBEHLO OK MAME FIK";
                System.out.println("EET PROBEHLO OK MAME FIK");
                //extracting the text
                int startIndex = response.indexOf("fik=");
                fik = response.substring(startIndex+5, startIndex + 39 + 5);
            }else{
                message = "EET NEPROBEHLO";
                System.out.println("EET NEPROBEHLO");
                fik = "EET NEPROBEHLO";
                Log.d("EET","The EET send had failed when sending");
            }
        }


        ReceiptDTO newReceiept = ReceiptDTO.Builder.newInstance()
                .setTotalSum(totalSum)
                .setTerminalId(terminalId)
                .setShopId(shopId)
                .setDateTime(currentTime)
                .setDic(dic)
                .setReceiptId(receiptId)
                .setFIK(fik)
                .setBKP(bkp)
                .setPKP(pkp)
                .build();

        return newReceiept;
        //ready to print online receipt
    }

    protected void onPostExecute(ReceiptDTO result) {
        if (result == null){
            activity.showSnackBar("NO INTERNET CONNECTION");
            return;
        }
        System.out.println(result);
        activity.printEetReceipt(result);
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
