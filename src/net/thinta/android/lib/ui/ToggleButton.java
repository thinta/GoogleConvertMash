package net.thinta.android.lib.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

public class ToggleButton<T> extends TextView {

	public ToggleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setStyle();
	}

	public ToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setStyle();
	}

	public ToggleButton(Context context) {
		super(context);
		setStyle();
	}

	private void setStyle(){
		this.setTextSize(22.0f);
		this.setBackgroundColor(Color.DKGRAY);
		this.setTextColor(Color.WHITE);
		FlowLayout.LayoutParams params =
			new FlowLayout.LayoutParams(3,3);
		this.setLayoutParams(params);
		this.setPadding(5, 5, 5, 5);
	}
	
	private boolean selected = false;
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		if(selected){
			this.setBackgroundColor(Color.WHITE);
			this.setTextColor(Color.BLACK);
		}else{
			this.setBackgroundColor(Color.DKGRAY);
			this.setTextColor(Color.WHITE);
		}
	}

	public boolean Toggle(){
		this.setSelected(!isSelected());
		return selected;
	}
	
	private T metadata = null;

	public T getMetadata() {
		return this.metadata;
	}

	public void setMetadata(T metadata) {
		this.metadata = metadata;
	}
	
}
