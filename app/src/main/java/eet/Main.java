package eet;


import java.net.URL;
import java.security.KeyStore;
import java.util.Date;

import openeet.lite.EetRegisterRequest;

public class Main {
	
	public static void main(String[] args) {
		try {
			openeet.lite.EetRegisterRequest request= openeet.lite.EetRegisterRequest.builder()
			   .dic_popl("CZ1212121218")
			   .id_provoz("1")
			   .id_pokl("POKLADNA01")
			   .porad_cis("1")
			   .dat_trzby(openeet.lite.EetRegisterRequest.formatDate(new Date()))
			   .celk_trzba(100.0)
			   .rezim(0)
			   .pkcs12(EetRegisterRequest.loadStream(Main.class.getResourceAsStream("/openeet/lite/EET_CA1_Playground-CZ1212121218.p12")))
			   .pkcs12password("eet")
			   .build();

			//try send
			String requestBody=request.generateSoapRequest();
			System.out.printf("===== BEGIN EET REQUEST =====\n%s\n===== END EET REQUEST =====\n",requestBody);

			String response=request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3"));
			System.out.printf("===== BEGIN EET RESPONSE =====\n%s\n===== END EET RESPONSE =====\n",response);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
