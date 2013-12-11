/**
 * 
 */
package fr.fchantrel.makeitday.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.sax.RootElement;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import fr.fchantrel.makeitday.R;
import fr.fchantrel.makeitday.json.JSONParser;
import fr.fchantrel.makeitday.metier.InfosPro;
import fr.fchantrel.makeitday.parser.recherche.XMLRechercheParser;

/**
 * @author FredASI
 * 
 */
public class LRRechercheActivity extends Activity {
	// All static variables
	// keys
	static final String KEY_SONG = "song"; // parent node
	static final String KEY_ID = "id";
	static final String KEY_TITLE = "title";
	static final String KEY_ARTIST = "artist";
	static final String KEY_DURATION = "duration";
	static final String KEY_THUMB_URL = "thumb_url";

	private String url = "www.pagesjaunes.fr";

	private final String URL_WS_REST_RECHERCHE = "http://pj.etactic.fr/ws-rest-recherche-1/rest/epj/";

	// JSON Node Names
	private static final String TAG_LAST_MODIFICATION = "lastmodification";
	private static final String TAG_HISTO_SEARCH = "pjmyhistosearch";
	private static final String TAG_TABLEAU_RECHERCHE = "t";
	private static final String TAG_TABLEAU_PROS = "pro";
	private static final String TAG_OU = "o";
	private static final String TAG_QUOI = "q";
	private static final String TAG_TIMESTAMP = "d";
	private static final String TAG_ID = "id";
	private static final String TAG_URL = "u";

	JSONObject reponseJSONStorage = null;

	private final String CLE_ID_SHARE = "id_share_utilisateur";

	ListView list;
	LazyAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// on récupère l'idShare dans les préférences
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String myIdQRShare = preferences
				.getString(CLE_ID_SHARE, "valeurDefaut");
		// on construit l'URL d'appel à l'API JSON
		url = "http://pj.etactic.fr/localstorage/" + myIdQRShare;

		setContentView(R.layout.liste_recherches);
		// on lance l'AsyncTask qui va interroger le ws-rest
		new JSONParse().execute();
	}

	private class JSONParse extends AsyncTask<String, String, List<InfosPro>> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LRRechercheActivity.this);
			pDialog.setMessage("Chargement en cours ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected List<InfosPro> doInBackground(String... args) {

			List<InfosPro> retour = new ArrayList<InfosPro>();

			JSONParser jParser = new JSONParser();

			// A partir du json on va construire les listes de pros
			try {

				// Getting JSON from URL
				JSONObject json = jParser.getJSONFromUrl(url);
				// Getting JSON Object
				if ((null != json) && (json.has("pjmyhistosearch"))) {
					reponseJSONStorage = json.getJSONObject(TAG_HISTO_SEARCH);
					JSONArray tabRecherche = reponseJSONStorage
							.getJSONArray(TAG_TABLEAU_RECHERCHE);

					// on parcourt les recherches
					for (int i = 0; i < tabRecherche.length(); i++) {
						JSONObject currentLRElement = tabRecherche
								.getJSONObject(i);

						String quoi = currentLRElement.getString(TAG_QUOI);
						String ou = currentLRElement.getString(TAG_OU);
						String currentUrl = currentLRElement.getString(TAG_URL);
						String dateTimestamp = currentLRElement
								.getString(TAG_TIMESTAMP);
						StringBuilder tempChaineQuiQuoi = new StringBuilder(
								quoi);
						tempChaineQuiQuoi.append(" / ");
						tempChaineQuiQuoi.append(ou);

						// on regarde s'il y a des pros associés a cette LR de
						// recherche
						if (currentLRElement.has(TAG_TABLEAU_PROS)) {
							JSONArray tabPro = currentLRElement
									.getJSONArray(TAG_TABLEAU_PROS);

							tempChaineQuiQuoi.append("(");
							tempChaineQuiQuoi.append(tabPro.length());
							tempChaineQuiQuoi.append(")");

							// on parcourt les pros pour une LR
							InfosPro currentPro = null;
							for (int j = 0; j < tabPro.length(); j++) {
								currentPro = new InfosPro();
								// Ajout de l'image correspondant à l'activité
								if (tempChaineQuiQuoi.toString().toLowerCase()
										.indexOf("rest") > -1) {
									currentPro.setNoImage(R.drawable.resto3);
								} else if (tempChaineQuiQuoi.toString()
										.toLowerCase().indexOf("hotel") > -1) {
									currentPro.setNoImage(R.drawable.hotel);
								} else if (tempChaineQuiQuoi.toString()
										.toLowerCase().indexOf("fleur") > -1) {
									currentPro.setNoImage(R.drawable.fleur);
								} else if (tempChaineQuiQuoi.toString()
										.toLowerCase().indexOf("immobil") > -1) {
									currentPro
											.setNoImage(R.drawable.immobilier);
								} else if (tempChaineQuiQuoi.toString()
										.toLowerCase().indexOf("boulang") > -1) {
									currentPro
											.setNoImage(R.drawable.boulangerie);
								} else if (tempChaineQuiQuoi.toString()
										.toLowerCase().indexOf("garage") > -1) {
									currentPro.setNoImage(R.drawable.garage);
								} else if (tempChaineQuiQuoi.toString()
										.toLowerCase().indexOf("simplyfeu") > -1) {
									currentPro.setNoImage(R.drawable.garage2);
								} else {
									currentPro.setNoImage(R.drawable.logo_pj);
								}

								// récupération de l'EPJ du pro
								JSONObject currentProElement = tabPro
										.getJSONObject(j);
								JSONObject currentJSONDataPro = currentProElement
										.getJSONObject(TAG_TIMESTAMP);
								String currentIdPro = currentJSONDataPro
										.getString(TAG_ID);

								InfosPro currentInfosPro = getInfosProJSON(currentIdPro);
								currentPro.setIdentifiantListView(""
										+ (i * 100 + j));
								currentPro
										.setLibelleAdresseComplete(currentInfosPro
												.getLibelleAdresseComplete());
								currentPro.setNoTelephone(currentInfosPro
										.getNoTelephone());
								currentPro.setDenomination(currentInfosPro
										.getDenomination());
								// on ajoute le pro courant à la liste
								retour.add(currentPro);
							} // fin du for tabPro
						}

					} // fin du for tabRecherche
				} else {
					retour.addAll(getMockedInfosPro());
				}

				// TextView textView2 = (TextView)
				// findViewById(R.id.text_display2);
				// textView2.setText(tempChaineAffichee);
			} catch (JSONException e) {
				e.printStackTrace();
				retour.addAll(getMockedInfosPro());
			}

			return retour;
		}

		@Override
		protected void onPostExecute(List<InfosPro> lstInfoPro) {
			pDialog.dismiss();

			final ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map = new HashMap<String, String>();

			// on parcourt la liste des pros
			for (Iterator<InfosPro> iterator = lstInfoPro.iterator(); iterator
					.hasNext();) {
				InfosPro infosPro = (InfosPro) iterator.next();
				map = new HashMap<String, String>();
				map.put(KEY_THUMB_URL, String.valueOf(infosPro.getNoImage()));
				map.put(KEY_ID, "" + infosPro.getIdentifiantListView());
				map.put(KEY_TITLE, infosPro.getDenomination());
				map.put(KEY_ARTIST, infosPro.getLibelleAdresseComplete());
				map.put(KEY_DURATION, infosPro.getNoTelephone());

				songsList.add(map);
			}

			// fin parcours des pros

			list = (ListView) findViewById(R.id.list);

			// Getting adapter by passing xml data ArrayList
			adapter = new LazyAdapter(LRRechercheActivity.this, songsList);
			list.setAdapter(adapter);

			// Click event for single list row
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> a, View v, int position,
						long id) {
					// on récupère la HashMap contenant les infos de notre item
					// (titre, description, img)
					HashMap<String, String> map = (HashMap<String, String>) songsList
							.get(position);
					String tel = map.get(KEY_DURATION);
					// tel = tel.trim();
					String lien = "tel:";
					lien = lien.concat(tel);

					Intent callIntent = new Intent(Intent.ACTION_CALL);
					// callIntent.setData(Uri.parse("tel:0607808767"));
					callIntent.setData(Uri.parse(lien));
					startActivity(callIntent);

					/*
					 * // on créer une boite de dialogue AlertDialog.Builder adb
					 * = new AlertDialog.Builder( LRRechercheActivity.this); //
					 * on attribut un titre à notre boite de dialogue
					 * adb.setTitle("Sélection Item"); // on insère un message à
					 * notre boite de dialogue, et ici on // affiche le titre de
					 * l'item cliqué adb.setMessage("Votre choix : " +
					 * map.get("title")); // on indique que l'on veut le bouton
					 * ok à notre boite de // dialogue
					 * adb.setPositiveButton("Ok", null); // on affiche la boite
					 * de dialogue adb.show();
					 */
				}
			});

		}

		/**
		 * @param currentIdPro
		 * @return
		 */
		private InfosPro getInfosProXML(String currentIdPro) {
			InfosPro retour = new InfosPro();

			try {
				// construction de l'URL
				String urlWSRecherche = URL_WS_REST_RECHERCHE
						.concat(currentIdPro);
				InputStream streamAPIRecherche = downloadUrl(urlWSRecherche);

				XMLRechercheParser parser = new XMLRechercheParser();
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser sp = factory.newSAXParser();
				XMLReader reader = sp.getXMLReader();
				reader.setContentHandler(parser);
				InputSource inSource = new InputSource(streamAPIRecherche);
				reader.parse(inSource);
				/************* Get Parse data in a ArrayList **********/
				retour = parser.getProValues();

				retour.setDenomination("ma dénomination");
				retour.setLibelleAdresseComplete("3 chemin du chene Augue 35630 Vignoc");
				retour.setNoTelephone("02 99 69 82 28");
			} catch (Exception e) {
				retour.setDenomination(currentIdPro);
				retour.setLibelleAdresseComplete("Erreur lors de l'acces a l'API recherche.");
				retour.setNoTelephone("?");
			}
			return retour;
		}

		/**
		 * @param currentIdPro
		 * @return Appel de l'API ws-rest-recherche en JSON
		 */
		private InfosPro getInfosProJSON(String currentIdPro) {
			InfosPro retour = new InfosPro();

			try {
				// construction de l'URL
				String urlWSRecherche = URL_WS_REST_RECHERCHE.concat(
						currentIdPro).concat(".json");

				JSONParser jParser = new JSONParser();
				// Getting JSON from URL
				JSONObject json = jParser.getJSONFromUrl(urlWSRecherche);

				// A partir du json on va construire un InfosPro
				try {
					if (json.has("inscriptions")) {
						JSONArray tabInscription = json
								.getJSONArray("inscriptions");
						if (tabInscription.length() > 0) {
							JSONObject currentInscription = tabInscription
									.getJSONObject(0);
							String currentDenomination = currentInscription
									.getString("denomination");

							JSONObject myAdresse = currentInscription
									.getJSONObject("adresse");
							String currentAdresse = myAdresse
									.getString("libelle_adresse_complete");

							JSONArray tabMoyenComunication = currentInscription
									.getJSONArray("moyens_communication");
							JSONObject myMoyenComunication = tabMoyenComunication
									.getJSONObject(0);
							String currentTelephone = myMoyenComunication
									.getString("numero_formate");

							retour.setDenomination(currentDenomination);
							retour.setLibelleAdresseComplete(currentAdresse);
							retour.setNoTelephone(currentTelephone);
						}
					}
				} catch (Exception e) {
					// on ne fait rien tant pis
					retour.setDenomination(currentIdPro);
					retour.setLibelleAdresseComplete("Erreur lors de l'acces a l'API recherche.");
					retour.setNoTelephone("?");
				}

			} catch (Exception e) {
				retour.setDenomination(currentIdPro);
				retour.setLibelleAdresseComplete("Erreur lors de l'acces a l'API recherche.");
				retour.setNoTelephone("?");
			}
			return retour;
		}

		/**
		 * @param urlString
		 * @return
		 * @throws IOException
		 */
		private InputStream downloadUrl(String urlString) throws IOException {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			return conn.getInputStream();
		}

		private List<InfosPro> getMockedInfosPro() {
			List<InfosPro> retour = new ArrayList<InfosPro>(6);
			InfosPro currentPro = new InfosPro();
			currentPro.setIdentifiantListView("" + 1);
			currentPro.setDenomination("RESTO");
			currentPro.setLibelleAdresseComplete("mon adresse 35000 Rennes");
			currentPro.setNoImage(R.drawable.resto3);
			currentPro.setNoTelephone("EXEMPLE 1");
			retour.add(currentPro);
			currentPro = new InfosPro();
			currentPro.setIdentifiantListView("" + 1);
			currentPro.setDenomination("HOTEL");
			currentPro.setLibelleAdresseComplete("mon adresse 35000 Rennes");
			currentPro.setNoImage(R.drawable.hotel);
			currentPro.setNoTelephone("EXEMPLE 2");
			retour.add(currentPro);
			currentPro = new InfosPro();
			currentPro.setIdentifiantListView("" + 1);
			currentPro.setDenomination("HOTEL");
			currentPro.setLibelleAdresseComplete("mon adresse 35000 Rennes");
			currentPro.setNoImage(R.drawable.hotel2);
			currentPro.setNoTelephone("EXEMPLE 3");
			retour.add(currentPro);
			currentPro = new InfosPro();
			currentPro.setIdentifiantListView("" + 1);
			currentPro.setDenomination("FLEURISTE");
			currentPro.setLibelleAdresseComplete("mon adresse 35000 Rennes");
			currentPro.setNoImage(R.drawable.fleur);
			currentPro.setNoTelephone("EXEMPLE 4");
			retour.add(currentPro);
			currentPro = new InfosPro();
			currentPro.setIdentifiantListView("" + 1);
			currentPro.setDenomination("FLEURISTE");
			currentPro.setLibelleAdresseComplete("mon adresse 35000 Rennes");
			currentPro.setNoImage(R.drawable.fleur2);
			currentPro.setNoTelephone("EXEMPLE 5");
			retour.add(currentPro);
			currentPro = new InfosPro();
			currentPro.setIdentifiantListView("" + 1);
			currentPro.setDenomination("AUTRE");
			currentPro.setLibelleAdresseComplete("mon adresse 35000 Rennes");
			currentPro.setNoImage(R.drawable.logo_pj);
			currentPro.setNoTelephone("EXEMPLE 6");
			retour.add(currentPro);

			return retour;
		}

	}
}
