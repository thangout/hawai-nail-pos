package td.pokladna2;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


import eet.EetRegisterRequest;
import eet.EetTrustManager;

import static eet.EetRegisterRequest.loadStream;


public class EET extends AsyncTask<InputStream, Void, String> {

    public static void main(String[] args) {
        try {
            // TODO code application logic here


        } catch (Exception ex) {
            //Logger.getLogger(EetFirstTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static String simpleRegistrationProcessTest(InputStream cert) throws MalformedURLException, IOException, Exception{
        //set minimal business data & certificate with key loaded from pkcs12 file
        EetRegisterRequest request=EetRegisterRequest.builder()
                .dic_popl("CZ1212121218")
                .id_provoz("1")
                .id_pokl("POKLADNA01")
                .porad_cis("1")
                .dat_trzby("2020-02-19T19:43:28+02:00")
                .celk_trzba(-20000.0)
                .rezim(0)
                //.pkcs12(loadStream(EET.class.getResourceAsStream( "/EET_CA1_Playground-CZ1212121218.p12")))
                .pkcs12(loadStream(cert))
                .pkcs12password("eet")
                .build();

        //for receipt printing in online mode
        String bkp=request.formatBkp();
        //assertNotNull(bkp);

        //for receipt printing in offline mode
        String pkp=request.formatPkp();
        //assertNotNull(pkp);
        //the receipt can be now stored for offline processing
        //System.out.println(pkp);

        //try send
        String requestBody=request.generateSoapRequest();
        //assertNotNull(requestBody);

        String response=request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3"));
        System.out.println(response);
        //extract FIK
        //assertNotNull(response);
        //assertTrue(response.contains("Potvrzeni fik="));

        String message = "";

        if (response.contains("Potvrzeni fik=")) {
            message = "EET PROBEHLO OK MAME FIK";
            System.out.println("EET PROBEHLO OK MAME FIK");
        }else{
            message = "EET NEPROBEHLO";
            System.out.println("EET NEPROBEHLO");
        }

        return message;
        //ready to print online receipt
    }

    @Override
    protected String doInBackground(InputStream... inputStreams){

        InputStream cert = inputStreams[0];

        EetRegisterRequest request= null;

        //custom keystore
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            //ks = KeyStore.getInstance("JKS");
            ks.load(inputStreams[1], "eeteet".toCharArray());
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
                    .dic_popl("CZ1212121218")
                    .id_provoz("1")
                    .id_pokl("POKLADNA01")
                    .porad_cis("1")
                    .dat_trzby("2020-02-19T19:43:28+02:00")
                    .celk_trzba(-20000.0)
                    .rezim(0)
                    //.pkcs12(loadStream(EET.class.getResourceAsStream( "/EET_CA1_Playground-CZ1212121218.p12")))
                    .pkcs12(loadStream(cert))
                    .trustKeyStore(ks)
                    .pkcs12password("eet")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //for receipt printing in online mode
        String bkp=request.formatBkp();
        //assertNotNull(bkp);

        //for receipt printing in offline mode
        String pkp=request.formatPkp();
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

        if (response != null){
            if (response.contains("Potvrzeni fik=")) {
                message = "EET PROBEHLO OK MAME FIK";
                System.out.println("EET PROBEHLO OK MAME FIK");
            }else{
                message = "EET NEPROBEHLO";
                System.out.println("EET NEPROBEHLO");
            }
        }


        return message;
        //ready to print online receipt
    }

    protected void onPostExecute(String result) {
        System.out.println(result);
    }
}
