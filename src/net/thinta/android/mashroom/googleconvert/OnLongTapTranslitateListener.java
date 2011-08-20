package net.thinta.android.mashroom.googleconvert;

import net.thinta.android.lib.ui.ToggleButtonsPanel;
import android.app.Activity;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class OnLongTapTranslitateListener implements OnGestureListener {

	private ToggleButtonsPanel<String> buttons;
	
	private Activity owner;
	private String text;
	
	public OnLongTapTranslitateListener(Activity owner,String text,ToggleButtonsPanel<String> buttons) {
		super();
		this.owner = owner;
		this.text = text;
		this.buttons = buttons;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if(null != getTranslitateTask()){
			getTranslitateTask().execute(text);
			buttons = null; // TODO もうちょっとマシな方法を…
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}

	public ToggleButtonsPanel<String> getToggleButtons() {
		return this.buttons;
	}

	public void setToggleButtons(ToggleButtonsPanel<String> buttons) {
		this.buttons = buttons;
	}

	public SocialImeTransliterateTask getTranslitateTask() {
		if(null != getToggleButtons()){
			return new SocialImeTransliterateTask(this.owner, getToggleButtons());
		}
		return null;
	}

}
