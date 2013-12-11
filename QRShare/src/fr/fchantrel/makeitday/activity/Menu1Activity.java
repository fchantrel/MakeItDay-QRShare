/**
 * 
 */
package fr.fchantrel.makeitday.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import fr.fchantrel.makeitday.R;

/**
 * @author FredASI
 * Activité du vérifier id QRShare
 *
 */
public class Menu1Activity extends Activity {
	
	private final String CLE_ID_SHARE = "id_share_utilisateur";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu1);

        // On récupère les données saisies à l'activité AccueilActivity
        Intent intent = getIntent();
        TextView textView2 = (TextView) findViewById(R.id.text_display2);
        
        if (intent != null) {
     	   // on récupère les préférences utilisateur sinon valeur par défaut
        	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        	//String idQRShare = preferences.getString(CLE_ID_SHARE, "d8239a48-edab-4dc0-a081-dd7562896c24");
        	String idQRShare = preferences.getString(CLE_ID_SHARE, "valeurDefaut");
        	
        	//loginDisplay.setText(intent.getStringExtra(EXTRA_LOGIN));
        	//textView1.setText("Accueil du menu 1 !");
        	if((idQRShare != null) && (!"valeurDefaut".equals(idQRShare))){
        		textView2.setText(idQRShare);
        	} else {
        		textView2.setText("Veuillez synchroniser une première fois votre device.");
        	}
        	
        }

    }
	
}
