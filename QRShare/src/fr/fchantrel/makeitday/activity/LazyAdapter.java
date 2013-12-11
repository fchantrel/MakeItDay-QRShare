/**
 * 
 */
package fr.fchantrel.makeitday.activity;

import java.util.ArrayList;
import java.util.HashMap;

import fr.fchantrel.makeitday.R;
import fr.fchantrel.makeitday.activity.LRRechercheActivity;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author FredASI
 *
 */
public class LazyAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.liste_recherche_row, null);

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
        
        // Setting all values in listview
        title.setText(song.get(LRRechercheActivity.KEY_TITLE));
        artist.setText(song.get(LRRechercheActivity.KEY_ARTIST));
        duration.setText(song.get(LRRechercheActivity.KEY_DURATION));
        int img_int = R.drawable.logo_pj;
        try {
            img_int = Integer.parseInt(song.get(LRRechercheActivity.KEY_THUMB_URL));
        } catch (Exception e) { }

        thumb_image.setImageResource(img_int);
        return vi;
    }
}
