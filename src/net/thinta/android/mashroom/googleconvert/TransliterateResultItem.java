package net.thinta.android.mashroom.googleconvert;

import java.util.ArrayList;

public class TransliterateResultItem {
	private String word = "";
	private ArrayList<String> converted = null;
	
	public TransliterateResultItem(String word) {
		super();
		this.word = word;
	}
	
	public String getWord(){
		return this.word;
	}
	
	public ArrayList<String> getConvertedWords(){
		if(null == this.converted){
			this.converted = new ArrayList<String>();
		}
		return this.converted;
	}
	
	public void addConvertedWord(String convertedWord){
		getConvertedWords().add(convertedWord);
	}
}
