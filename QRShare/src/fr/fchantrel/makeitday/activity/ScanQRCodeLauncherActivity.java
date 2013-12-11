/**
 * 
 */
package fr.fchantrel.makeitday.activity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import fr.fchantrel.makeitday.R;

/**
 * @author FredASI Activité du menu1
 * 
 */
public class ScanQRCodeLauncherActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode_launcher);

		// On récupère les données saisies à l'activité AccueilActivity
		Intent intent = getIntent();
		TextView textView1 = (TextView) findViewById(R.id.text_scan_launcher_1);

		final Button menu2Button = (Button) findViewById(R.id.button_scan);
		menu2Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, 0);
				*/
				IntentIntegrator integrator = new IntentIntegrator(ScanQRCodeLauncherActivity.this);
				integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);

				// Intent intent = new Intent(ScanQRCodeLauncherActivity.this,
				// ScanQRCodeLauncherActivity.class);
				// startActivity(intent);
			}
		});

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

	    	IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
	    	if (result != null) {
	    	  // on récupère une URL contenue dans le QRCode
	  	      String urlRecue = result.getContents();
	  	      if (urlRecue != null) {
	  	    	// on récupère l'id de QRShare et on construit l'URL de l'API 
	  	    	String idShare = urlRecue.substring(urlRecue.indexOf("=") + 1);
	  			String urlAPIJSON = "http://pj.etactic.fr/localstorage/";
	  			
	  			urlAPIJSON = urlAPIJSON.concat(idShare);  
	  	    	
	  	    	TextView textView = (TextView) findViewById(R.id.text_scan_launcher_1);
	        	textView.setText(urlAPIJSON);
	  	    	//textView.setText(idShare);
	  	      } // fin du if contents != null
	    } // fin if result != null
	}

}
