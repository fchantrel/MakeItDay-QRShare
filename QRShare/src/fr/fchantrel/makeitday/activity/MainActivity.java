package fr.fchantrel.makeitday.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import fr.fchantrel.makeitday.R;

/**
 * @author FredASI Activité pour accueil
 * 
 * 
 */
public class MainActivity extends Activity {
	
	private final String CLE_ID_SHARE = "id_share_utilisateur";
	private int REQUEST_CODE_VOICE = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button menu1Button = (Button) findViewById(R.id.menu1);
		menu1Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				/*
				// Affichage de l'idQRShare
				Intent intent = new Intent(MainActivity.this,
						Menu1Activity.class);
				startActivity(intent);
				*/
				startVoiceRecognitionActivity();
			}
		});

		final Button menu3Button = (Button) findViewById(R.id.menu3);
		menu3Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
				integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
			}
		});
		
		final Button menu4Button = (Button) findViewById(R.id.menu4);
		menu4Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lancerActivityHistoriqueRecherche();
			}
		});
		
		final Button menu5Button = (Button) findViewById(R.id.menu5);
		menu5Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// on stocke l'idShare dans les preferences utilisateur
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
				// on supprime une seule entrée
				preferences.edit().remove(CLE_ID_SHARE).commit();
				// pour supprimer toutes les préférences
				//settings.edit().clear().commit();
			}
		});
		
		final Button menu6Button = (Button) findViewById(R.id.menu6);
		menu6Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lancerActivityFavorisRecherche();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(requestCode == REQUEST_CODE_VOICE) {
			// on recoit le résultat de la reconnaissance vocale
			if(resultCode == RESULT_OK){
				// Populate the wordsList with the String values the recognition engine thought it heard
	            List<String> matches = data.getStringArrayListExtra(
	                    RecognizerIntent.EXTRA_RESULTS);
	            
	            String currentWorld = null;
	            String meilleureReponse = matches.get(0);
	            
	            TextView monTextView = (TextView) findViewById(R.id.textSpeech);
				monTextView.setText(meilleureReponse);
				
				if(meilleureReponse.indexOf("favoris") > -1){
					lancerActivityFavorisRecherche();
				} else if(meilleureReponse.indexOf("historique") > -1) {
					lancerActivityHistoriqueRecherche();
				}
				
			} else {
				// result NOK on affiche un message
				TextView monTextView = (TextView) findViewById(R.id.textSpeech);
				monTextView.setText("Je n'ai pas bien compris ...");
			}
			
			super.onActivityResult(requestCode, resultCode, data);
			
			
		} else {
			IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
					resultCode, data);
			if (result != null) {
				// on récupère une URL contenue dans le QRCode
				String urlRecue = result.getContents();
				if (urlRecue != null) {
					// on récupère l'id de QRShare et on construit l'URL de l'API
					String idShare = urlRecue.substring(urlRecue.indexOf("=") + 1);
					
					// on stocke l'idShare dans les preferences utilisateur
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString(CLE_ID_SHARE, idShare);
					editor.commit();
					
					String urlAPIJSON = "http://pj.etactic.fr/localstorage/";

					urlAPIJSON = urlAPIJSON.concat(idShare);
					
					// on créer une boite de dialogue
					AlertDialog.Builder adb = new AlertDialog.Builder(
							MainActivity.this);
					// on attribut un titre à notre boite de dialogue
					adb.setTitle("Synchronisation");
					// on insère un message à notre boite de dialogue, et ici on
					// affiche le titre de l'item cliqué
					adb.setMessage("Félicitations, votre mobile est bien synchronisé !");
					// on indique que l'on veut le bouton ok à notre boite de
					// dialogue
					adb.setPositiveButton("Ok", null);
					// on affiche la boite de dialogue
					adb.show();
				} // fin du if urlRecue != null
			} // fin if result != null
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
     * Déclenche un intent pour démarrer l'activity de reconnaissance vocale.
     */
    private void startVoiceRecognitionActivity()
    {
    	PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        TextView monTextView = (TextView) findViewById(R.id.textSpeech);
        if (activities.size() == 0) {
        	monTextView.setText("Recognizer non présent");
        } else {
        	monTextView.setText("Je vous écoute ...");
        }
    	
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE_VOICE);
    }
    
    /**
     * Affichage de l'Activity permettant de visualiser l'idQRShare
     */
    private void afficherActivityIDQRShare() {
    	// Affichage de l'idQRShare
		Intent intent = new Intent(MainActivity.this,
				Menu1Activity.class);
		startActivity(intent);
    }
	
    private void afficherActivityScannerALancer() {
		
    	/*
		final Button menu2Button = (Button) findViewById(R.id.menu2);
		menu2Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						ScanQRCodeLauncherActivity.class);
				startActivity(intent);
			}
		});*/
    }
    
    /**
     * Déclenche l'Activity HistoriqueRecherche
     */
    private void lancerActivityHistoriqueRecherche() {
    	Intent intent = new Intent(MainActivity.this,
				LRRechercheActivity.class);
		startActivity(intent);
    }
    
    /**
     * Déclenche l'Activity HistoriqueRecherche
     */
    private void lancerActivityFavorisRecherche() {
    	Intent intent = new Intent(MainActivity.this,
				LRFavorisActivity.class);
		startActivity(intent);
    }
}
