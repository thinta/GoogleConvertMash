package net.thinta.android.lib.ui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ToggleButtonsPanel<T> extends FlowLayout {

    public ToggleButtonsPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ToggleButtonsPanel(Context context) {
		super(context);
	}

	private ArrayList<ToggleButton<T>> buttons = new ArrayList<ToggleButton<T>>();
	
    private ToggleClickListener listener = null;
    
    private boolean isMultiSelectable = false;
    
	public boolean isMultiSelectable() {
		return isMultiSelectable;
	}

	public void setMultiSelectable(boolean isMultiSelectable) {
		this.isMultiSelectable = isMultiSelectable;
	}
	
	public String getSelectedText(){
		return getSelectedText(null);
	}
	
	public String getSelectedText(String delimiter){
		StringBuilder builder = new StringBuilder();
		for(ToggleButton<T> button : buttons){
			if(button.isSelected()){
				if(0 < builder.length() && null != delimiter){
					builder.append(delimiter);
				}
				builder.append(button.getText());
			}
		}
		return builder.toString();
	}
	
	public ArrayList<T> getSelectedItems(){
		ArrayList<T> list = new ArrayList<T>();
		for(ToggleButton<T> button : buttons){
			if(button.isSelected()){
				list.add(button.getMetadata());
			}
		}
		return list;
	}
	
	public int getSelectedCount(){
		int count = 0;
		for(ToggleButton<T> button : buttons){
			if(button.isSelected()){
				count++;
			}
		}
		return count;
	}

	public ToggleButton<T> add(CharSequence text,T metadata){
		for(ToggleButton<T> button : buttons){
			if(button.getText().equals(text)){
				return button;
			}
		}
        ToggleButton<T> button = new ToggleButton<T>(this.getContext());
        button.setText(text);
        button.setMetadata(metadata);
        button.setOnClickListener(getListener());
        this.addView(button);
        buttons.add(button);
        return button;
	}
	
	public void clear(){
		for(ToggleButton<T> button : buttons){
			this.removeView(button);
		}
		buttons.clear();
	}
	
	private ToggleClickListener getListener(){
		if(null== this.listener){
			this.listener = new ToggleClickListener(buttons);
		}
		return this.listener;
	}
	
    class ToggleClickListener implements View.OnClickListener{

    	ArrayList<ToggleButton<T>> buttons = null;
    	
    	public ToggleClickListener(ArrayList<ToggleButton<T>> buttons){
    	  super();
    	  this.buttons = buttons;
    	}
    	
		@Override
		public void onClick(View v) {
			@SuppressWarnings("unchecked")
			ToggleButton<T> button = (ToggleButton<T>)v;
			// radioは"選択OFF"不可
			if(!button.isSelected() || isMultiSelectable()){
				boolean value = button.Toggle();
				// radioの場合は他のをOFF
				if(!isMultiSelectable() && value){
					for(ToggleButton<T> b : buttons){
						if(!b.equals(v)){
							b.setSelected(false);
						}
					}
				}
			}
		} // end-of-method
    } // end-of-inner_class
}