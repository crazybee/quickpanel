package com.crazybee.data;


public class QuickSettingItem {
	
	private String classname;
	private String packagename;
	private int imagetype;
	private int nameid;
	
	
	public QuickSettingItem(){
		
	}
	
	public String getClassName(){
		return classname;
	}
	public void setClassName(String s){
		this.classname = s;
	}
	public int getNameId(){
		return nameid;
	}
	public void setNameId(int n){
		this.nameid = n;
	}

	public String getpackageName(){
		return packagename;
	}
	public void setPackageName(String s){
		this.packagename = s;
	}
	public int getImageType(){
		return imagetype;
	}
	public void setImageType(int i){
		this.imagetype = i;
	}
}
