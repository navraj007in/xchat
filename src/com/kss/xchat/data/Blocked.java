package com.kss.xchat.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Blocked {
	public int id;
	public String nickname;
	public String user;
	ContentValues contentValues;
	Context context;
	public String TAG="Blocked";
	private static final String TABLE_COMPANY= "Blocked";

	//private String KEY_ID="_id";
	private String KEY_NICKNAME="nickname";
	private String KEY_USER="user";
	

	public Blocked(Context context)
	{
	this.context=context;
	}

	public void BlockUser()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		CreateContentValues();
	    // Inserting Row
	    db.insert(TABLE_COMPANY,null, contentValues);
	    Log.i(TAG, "Record Inserted successfully");
	    db.close();
	}
	public void unBlockUser()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		   db.delete(TABLE_COMPANY, "nickname=? and user=?", new String[]{this.nickname,this.user});
   
		   db.close();
	}
	public void toggleBlock()
	{
		if(isBlocked()) unBlockUser();
		else BlockUser();
	}


	public int getCount()
	{
		  	String countQuery = "SELECT  * FROM " + TABLE_COMPANY;
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        return cursor.getCount();
	}
	public ArrayList<Blocked> getBlockList()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ArrayList<Blocked> compList = new ArrayList<Blocked>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_COMPANY;
	    Cursor cursor = db.rawQuery(selectQuery, null);

	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	Blocked comp= new Blocked(context);
	            comp.nickname=cursor.getString(1);
	            compList.add(comp);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return compList;
	}

	public boolean isBlocked()
	{
		  	String countQuery = "SELECT  * FROM " + TABLE_COMPANY+" where nickname='"+this.nickname+"' and user='"+
		  	this.user +"'";
		  	
		  	Log.i(TAG, countQuery);
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);

	        if(cursor.getCount()==0)
	        {
	        	
		        db.close();
		        return false;
	        }
	        else
	        {
		        db.close();

	        	return true;
	        }
	}

	public void CreateContentValues()
	{
		contentValues = new ContentValues();
		contentValues .put(KEY_NICKNAME, this.nickname); // Contact Name
		contentValues .put(KEY_USER, this.user); // Contact Name
	}
	public void clearRecords()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.delete(TABLE_COMPANY, null,
		            null);
		    db.close();
	}
}
