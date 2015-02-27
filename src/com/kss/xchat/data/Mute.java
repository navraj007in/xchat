package com.kss.xchat.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Mute {
	public int id;
	public String nickname;
	public String user;
	ContentValues contentValues;
	Context context;
	public String TAG="Mute";
	private static final String TABLE_COMPANY= "Mute";

	//private String KEY_ID="_id";
	private String KEY_NICKNAME="nickname";
	private String KEY_USER="user";
	

	public Mute(Context context)
	{
	this.context=context;
	}

	public boolean isStatusMessage()
	{
		return false;
	}
	public void MuteUser()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		CreateContentValues();
	    // Inserting Row
	    db.insert(TABLE_COMPANY,null, contentValues);
	    Log.i(TAG, "Record Inserted successfully");
	    db.close();
	}
	public void unMuteUser()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		   db.delete(TABLE_COMPANY, "nickname=? and user=?", new String[]{this.nickname,this.user});
   
		   db.close();
	}
	public void toggleMute()
	{
		if(isMuted()) unMuteUser();
		else MuteUser();
	}


	public int getCount()
	{
		  	String countQuery = "SELECT  * FROM " + TABLE_COMPANY;
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        return cursor.getCount();
	}
	public boolean isMuted()
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
