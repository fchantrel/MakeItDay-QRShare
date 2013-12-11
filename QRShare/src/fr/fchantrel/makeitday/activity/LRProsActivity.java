/**
 * 
 */
package fr.fchantrel.makeitday.activity;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import fr.fchantrel.makeitday.R;
import fr.fchantrel.makeitday.json.JSONParser;

/**
 * @author FredASI
 * 
 */
public class LRProsActivity extends Activity {

	private final String EXTRA_DATA = "extra_data";

	private String url = "www.pagesjaunes.fr";

	// JSON Node Names
	private static final String TAG_LAST_MODIFICATION = "lastmodification";
	private static final String TAG_HISTO_SEARCH = "pjmyhistosearch";
	private static final String TAG_TABLEAU_RECHERCHE = "t";
	private static final String TAG_TABLEAU_PROS = "pro";
	private static final String TAG_OU = "o";
	private static final String TAG_QUOI = "q";
	private static final String TAG_TIMESTAMP = "d";
	private static final String TAG_ID = "id";

	JSONObject reponseJSONStorage = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste_reponse);

		// On récupère les données de l'activity précédente
		Intent intent = getIntent();
		TextView textView1 = (TextView) findViewById(R.id.text_display1);

		if (intent != null) {
			String urlAPIJSON = intent.getStringExtra(EXTRA_DATA);
			// loginDisplay.setText(intent.getStringExtra(EXTRA_LOGIN));
			textView1.setText(urlAPIJSON);
			url = urlAPIJSON;
			
			// on lance l'AsyncTask qui va interroger le ws-rest
			new JSONParse().execute();
		}
	}

	private class JSONParse extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LRProsActivity.this);
			pDialog.setMessage("Getting Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			JSONParser jParser = new JSONParser();

			// Getting JSON from URL
			JSONObject json = jParser.getJSONFromUrl(url);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			pDialog.dismiss();
			try {

				StringBuilder tempChaineAffichee = new StringBuilder(
						"Reponse : ");
				// Getting JSON Object
				reponseJSONStorage = json.getJSONObject("pjmyhistosearch");

				JSONArray tabRecherche = reponseJSONStorage.getJSONArray(TAG_TABLEAU_RECHERCHE);

				for (int i = 0; i < tabRecherche.length(); i++) {
					JSONObject currentLRElement = tabRecherche.getJSONObject(i);

					String quoi = currentLRElement.getString(TAG_QUOI);
					String ou = currentLRElement.getString(TAG_OU);

					tempChaineAffichee.append("Quoi:");
					tempChaineAffichee.append(currentLRElement.getString(TAG_QUOI));
					tempChaineAffichee.append(" / Ou:");
					tempChaineAffichee.append(currentLRElement.getString(TAG_OU));
					tempChaineAffichee.append(" / pros:");

					if(currentLRElement.has(TAG_TABLEAU_PROS)){
						JSONArray tabPro = currentLRElement.getJSONArray(TAG_TABLEAU_PROS);
						for (int j = 0; j < tabPro.length(); j++) {
							if(j != 0){
								tempChaineAffichee.append("/");
							}
							JSONObject currentProElement = tabPro.getJSONObject(j);
							JSONObject currentJSONDataPro = currentProElement.getJSONObject(TAG_TIMESTAMP);
							String currentIdPro = currentJSONDataPro.getString(TAG_ID);
							//String currentIdPro = currentProElement.getString(TAG_TIMESTAMP);
							tempChaineAffichee.append(currentIdPro);
						} // fin du for tabPro
					}
					
					tempChaineAffichee.append(" / ");

				} // fin du for tabRecherche

				// On met dans la textView la reponse formatee
				TextView textView2 = (TextView) findViewById(R.id.text_display2);
				textView2.setText(tempChaineAffichee);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}
}
