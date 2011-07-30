package net.thinta.android.mashroom.googleconvert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Sub extends Activity {
	public static final String TEXT_PARAM = "IN_TEXT";
	public static final String TEXT_RESULT = "OUT_TEXT";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub);
        
        Bundle extras = getIntent().getExtras();  
   
        final EditText text = (EditText)findViewById(R.id.EditText01);
        
        text.setText(extras.getCharSequence(TEXT_PARAM));

        Button buttonOK = (Button)findViewById(R.id.Button01);
        buttonOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(TEXT_RESULT, text.getText());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
        
        Button buttonCancel = (Button)findViewById(R.id.Button02);
        buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
        
    }
}
