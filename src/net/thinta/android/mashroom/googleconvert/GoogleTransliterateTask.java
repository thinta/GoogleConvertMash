package net.thinta.android.mashroom.googleconvert;

import java.io.IOException;
import java.util.ArrayList;

import net.thinta.android.lib.ui.ToggleButton;
import net.thinta.android.lib.ui.ToggleButtonsPanel;

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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GoogleTransliterateTask extends AsyncTask<String,Integer,ArrayList<TransliterateResultItem>> {

	public GoogleTransliterateTask(Activity owner) {
		super();
		this.owner = owner;
	}

	private Activity owner  = null;
	private ProgressDialog dialog = null;
	private String sourceString = null;
	
	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(owner);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("Google変換中…");
		dialog.setTitle("しばらくお待ちください");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.show();
	}
	
	@Override
	protected ArrayList<TransliterateResultItem> doInBackground(
			String... params) {
		
		this.sourceString = params[0]; // save
		
		ArrayList<TransliterateResultItem> results = new ArrayList<TransliterateResultItem>();
		
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
            		TransliterateResultItem item = new TransliterateResultItem(word);

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
	protected void onPostExecute(ArrayList<TransliterateResultItem> result) {

    	LinearLayout parent = (LinearLayout)owner.findViewById(R.id.LinearLayout02);
    	final ArrayList<ToggleButtonsPanel<String>> buttonsList = new ArrayList<ToggleButtonsPanel<String>>();
    	for(TransliterateResultItem item : result){
    		buttonsList.add(addUI(item,parent));
    	}
    	
    	// Button OK
    	Button buttonDo = (Button)owner.findViewById(R.id.Button01);
    	buttonDo.setVisibility(View.VISIBLE);
    	buttonDo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuffer sb = new StringBuffer();
				for(ToggleButtonsPanel<String> buttons : buttonsList){
					sb.append(buttons.getSelectedText());
				}
				Intent data = new Intent();
				data.putExtra(REPLACE_KEY, sb.toString());
				owner.setResult(Activity.RESULT_OK, data);
				owner.finish();
			}
		});

    	// Button Cancel
    	Button buttonCancel = (Button)owner.findViewById(R.id.Button02);
    	buttonCancel.setVisibility(View.VISIBLE);
    	buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra(REPLACE_KEY, sourceString);
				owner.setResult(Activity.RESULT_OK, data);
				//owner.setResult(Activity.RESULT_CANCELED); //キャンセル時の動作がIMEによって違うので
				owner.finish();
			}
		});

    	dialog.dismiss();
	}
	
    private ToggleButtonsPanel<String> addUI(TransliterateResultItem item,ViewGroup parent){

    	final TextView text = new TextView(owner);
    	text.setText(item.getWord());
    	text.setTextColor(Color.BLACK);
    	text.setBackgroundColor(Color.LTGRAY);
    	text.setTextSize(14.0f);
    	
    	parent.addView(text,new LayoutParams(FP, WC));

    	LinearLayout base = new LinearLayout(owner);
    	parent.addView(base,new LayoutParams(FP, FP));
    	
    	final ToggleButtonsPanel<String> buttons = new ToggleButtonsPanel<String>(owner);
    	
    	MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(FP, FP);
    	marginLayoutParams.setMargins(3, 3, 3, 3);
    	base.addView(buttons,new LayoutParams(marginLayoutParams));
    	buttons.setLayoutParams(new LinearLayout.LayoutParams(marginLayoutParams));

    	// Call SocialIME when LongPressed
    	final GestureDetector gestureDetector = new GestureDetector(
    													owner.getApplicationContext(),
    													new OnLongTapTranslitateListener(
    															owner,
    															text.getText().toString(),
    															buttons));
    	
    	OnTouchListener onTouchListener = new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gestureDetector.onTouchEvent(event);
				return false;
			}
    	};
    	
    	text.setOnTouchListener(onTouchListener);

    	// buttons.setOnTouchListener(onTouchListener); // too sensitive.

    	ArrayList<String> words = item.getConvertedWords();
    	
    	boolean first = true;
    	for(String word : words){
    		ToggleButton<String> button = buttons.add(word, word);
    		button.setOnTouchListener(onTouchListener);
    		if(first){
    			button.setSelected(true);
    			first = false;
    		}
    	}

    	/* invoke by button
    	final Button button = new Button(owner);
    	button.setText("+");
    	button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SocialImeTransliterateTask task = new SocialImeTransliterateTask(owner, buttons);
				task.execute(text.getText().toString());
			}
		});
    	buttons.addView(button);
    	*/

    	return buttons;
    }

}
