package td.pokladna2;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


//import eet.EetRegisterRequest;

import openeet.lite.EetRegisterRequest;

import static eet.EetRegisterRequest.loadStream;


public class EET extends AsyncTask<EetTaskParams, Void, String> {

    private View view;

    private MainActivity activity;

    public EET(View view) {
        this.view = view;
    }

    public EET(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(EetTaskParams... params){

        //Employee certificate
        InputStream cert = params[0].getCertificate();
        InputStream keyStore = params[0].getKeyStore();
        String dic = params[0].getDic();
        double totalSum = params[0].getTotalSum();
        String pkcsPassworkd = params[0].getPkcsPassword();


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
                    .id_provoz("1")
                    .id_pokl("POKLADNA01")
                    .porad_cis("1")
                    .dat_trzby("2020-02-19T19:43:28+02:00")
                    .celk_trzba(totalSum)
                    .rezim(0)
                    //.pkcs12(loadStream(EET.class.getResourceAsStream( "/EET_CA1_Playground-CZ1212121218.p12")))
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
        String requestBody=request.generateSoapRequest();
        //assertNotNull(requestBody);

        String response= null;
        try {
            response = request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3"));
        } catch (Exception e) {
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
            }
        }


        return fik;
        //ready to print online receipt
    }

    protected void onPostExecute(String result) {
        System.out.println(result);
        activity.showSnackBar(result);

    }
}
