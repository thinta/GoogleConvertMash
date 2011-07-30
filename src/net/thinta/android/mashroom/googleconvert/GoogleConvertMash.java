package net.thinta.android.mashroom.googleconvert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.widget.Toast;

public class GoogleConvertMash extends Activity {

	private static final String ACTION_INTERCEPT = "com.adamrocker.android.simeji.ACTION_INTERCEPT";
	public static final String REPLACE_KEY = "replace_key";
	private static final int REQCD_SHOWSUB = 0;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String action = i.getAction();
        String original = null;
        setContentView(R.layout.main);
        if(null != action && ACTION_INTERCEPT.equals(action)){
        	original = i.getStringExtra(REPLACE_KEY);
        	if(null == original || original.length() == 0){
        		showSubForm();
        	}else{
        		doMain(original);
        	}
        }else{
    		showSubForm();
        }
    }
    
    private void doMain(String org){
    	GoogleTransliterateTask task = new GoogleTransliterateTask(this);
    	try{
        	task.execute(org);
    	}catch(Exception e){
    		Toast.makeText(this, "エラーが発生しました。", Toast.LENGTH_SHORT);
    		setResult(RESULT_CANCELED);
    		finish();
    	}
    }
    
    private void showSubForm(){
    	ClipboardManager cm = 
    	      (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    	
    	Intent intent = new Intent(this,Sub.class);
    	intent.putExtra(Sub.TEXT_PARAM,cm.getText());

    	startActivityForResult(intent,REQCD_SHOWSUB);  
    }
    
    // メイン画面から呼び出した画面から戻る際に呼び出しされるメソッド  
    @Override  
    protected void onActivityResult(int requestCode, int resultCode,Intent data){  
        if (requestCode == REQCD_SHOWSUB) {  
            if (resultCode == RESULT_OK){  
                CharSequence c = data.getCharSequenceExtra(Sub.TEXT_RESULT);
                if(null != c && c.length() > 0){
                	doMain(c.toString());
                }
            }else{
                setResult(RESULT_CANCELED);
                finish();
            }
        }  
    }
}