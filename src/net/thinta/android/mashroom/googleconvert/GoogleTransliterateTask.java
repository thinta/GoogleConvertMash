package net.thinta.android.mashroom.googleconvert;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class GoogleTransliterateTask extends AsyncTask<String,Integer,ArrayList<GoogleTransliterateResultItem>> {

	public GoogleTransliterateTask(Activity owner) {
		super();
		this.owner = owner;
	}

	private Activity owner  = null;
	private ProgressDialog dialog = null;
	
	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(owner);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("変換中…");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.show();
	}
	
	@Override
	protected ArrayList<GoogleTransliterateResultItem> doInBackground(
			String... params) {
		
		ArrayList<GoogleTransliterateResultItem> results = new ArrayList<GoogleTransliterateResultItem>();
		
        Uri.Builder builder = new Uri.Builder();
        builder.path("http://www.google.com/transliterate");
        builder.appendQueryParameter("langpair", Uri.encode("ja-Hira|ja"));
        builder.appendQueryParameter("text", Uri.encode(params[0]));
        String uri = Uri.decode(builder.build().toString());
        Log.d("uri", uri);

        try {
            // HTTP GET
            HttpUriRequest httpGet = new HttpGet(uri);
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // HTTP
                String entity = EntityUtils.toString(httpResponse.getEntity());
                // JSONの解析
                JSONArray jsons = new JSONArray(entity);
                for(int i = 0; i < jsons.length() && !jsons.isNull(i);i++){
                	// 文節
                	JSONArray root = jsons.getJSONArray(i);
                	String word = root.getString(0);
            		GoogleTransliterateResultItem item = new GoogleTransliterateResultItem(word);

                	// 変換候補
            		JSONArray items = root.getJSONArray(1);
            		for(int j = 0; j < items.length() && !items.isNull(j); j++){
            			item.addConvertedWord(items.getString(j));
            		}
            		results.add(item);
                }
            }
        } catch (IOException e) {
        	e.printStackTrace();
        } catch (JSONException e) {
        	e.printStackTrace();
        }
		
		return results;
	}

    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int FP = ViewGroup.LayoutParams.FILL_PARENT;
	private static final String REPLACE_KEY = "replace_key";

    @Override
	protected void onPostExecute(ArrayList<GoogleTransliterateResultItem> result) {

    	LinearLayout parent = (LinearLayout)owner.findViewById(R.id.LinearLayout02);
    	final ArrayList<RadioGroup> radioGs = new ArrayList<RadioGroup>();
    	for(GoogleTransliterateResultItem item : result){
    		radioGs.add(addUI(item,parent));
    	}
    	
    	Button buttonDo = (Button)owner.findViewById(R.id.Button01);
    	buttonDo.setVisibility(View.VISIBLE);
    	buttonDo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuffer sb = new StringBuffer();
				for(int i = 0; i < radioGs.size(); i++){
					RadioButton radio = (RadioButton)owner.findViewById(
							radioGs.get(i).getCheckedRadioButtonId());
					sb.append(radio.getText());
				}
				Intent data = new Intent();
				data.putExtra(REPLACE_KEY, sb.toString());
				owner.setResult(Activity.RESULT_OK, data);
				owner.finish();
			}
		});

    	Button buttonCancel = (Button)owner.findViewById(R.id.Button02);
    	buttonCancel.setVisibility(View.VISIBLE);
    	buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				owner.setResult(Activity.RESULT_CANCELED);
				owner.finish();
			}
		});

    	dialog.dismiss();
	}
	
    private RadioGroup addUI(GoogleTransliterateResultItem item,ViewGroup parent){

    	TextView text = new TextView(owner);
    	text.setText(item.getWord());
    	text.setTextColor(Color.BLACK);
    	text.setBackgroundColor(Color.LTGRAY);
    	parent.addView(text,new LayoutParams(FP, WC));

    	LinearLayout base = new LinearLayout(owner);
    	parent.addView(base,new LayoutParams(FP, FP));

    	RadioGroup radioG = new RadioGroup(owner);
    	base.addView(radioG,new LayoutParams(FP, WC));
    	
    	ArrayList<String> words = item.getConvertedWords();
    	
    	boolean first = true;
    	for(String word : words){
    		RadioButton radio = new RadioButton(owner);
    		radio.setText(word);
    		radioG.addView(radio,new LayoutParams(FP, WC));
    		if(first){
    			radio.setChecked(true);
    			first = false;
    		}
    	}
    	return radioG;
    }

}
