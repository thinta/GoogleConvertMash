package net.thinta.android.mashroom.googleconvert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import net.thinta.android.lib.ui.ToggleButtonsPanel;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class SocialImeTransliterateTask extends AsyncTask<String,Integer,TransliterateResultItem> {

	public SocialImeTransliterateTask(Activity owner,ToggleButtonsPanel<String> target) {
		super();
		this.owner = owner;
		this.target = target;
	}

	private Activity owner  = null;
	private ToggleButtonsPanel<String> target  = null;
	private ProgressDialog dialog = null;
	
	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(owner);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("SocialIME変換中…");
		dialog.setTitle("しばらくお待ちください");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.show();
	}
	
	@Override
	protected TransliterateResultItem doInBackground(
			String... params) {
		
        Uri.Builder builder = new Uri.Builder();
        builder.path("http://www.social-ime.com/api");
        builder.appendQueryParameter("string", Uri.encode(params[0]));
        builder.appendQueryParameter("resize[0]", Uri.encode("+" + params[0].length()));
        builder.appendQueryParameter("charset", Uri.encode("UTF-8"));
        String uri = Uri.decode(builder.build().toString());
        Log.d("uri", uri);

        TransliterateResultItem result = new TransliterateResultItem(params[0]);

        try {
            // HTTP GET
            HttpUriRequest httpGet = new HttpGet(uri);
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // HTTP
                String entity = EntityUtils.toString(httpResponse.getEntity());
                // Tab区切りらしいので分解
                StringTokenizer token = new StringTokenizer(entity, "\t");
                while(token.hasMoreTokens()) {
                	String item = token.nextToken().replaceAll("[\r\n]+", "");
                	if(null != item && item.trim().length() > 0){
                    	result.addConvertedWord(item);
                	}
				}
            }
        } catch (IOException e) {
        	e.printStackTrace();
        }
		
		return result;
	}

    @Override
	protected void onPostExecute(TransliterateResultItem result) {

    	ArrayList<String> convertedWords = result.getConvertedWords();
    	
    	if(0 < convertedWords.size()){
    		// target.clear(); // 元のは消す
        	// boolean first = true;
        	for(String word : convertedWords){
        		target.add(word, word);
//        		ToggleButton<String> button = target.add(word, word);
//        		if(first){
//        			button.setSelected(true);
//        			first = false;
//        		}
        	}
    	}
    	dialog.dismiss();
	}

}
