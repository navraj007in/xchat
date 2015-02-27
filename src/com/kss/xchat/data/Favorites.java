package com.kss.xchat.data;

import java.util.ArrayList;

import com.kss.xchat.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Favorites {
	public int id;
	public String nickname;
	public String user;
	ContentValues contentValues;
	Context context;
	public String TAG="Blocked";
	private static final String TABLE_COMPANY= "Favorites";

	//private String KEY_ID="_id";
	private String KEY_NICKNAME="nickname";
	private String KEY_USER="user";
	

	public Favorites(Context context)
	{
	this.context=context;
	}

	public void addAsFavorite()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		CreateContentValues();
	    // Inserting Row
	    db.insert(TABLE_COMPANY,null, contentValues);
	    Log.i(TAG, "Favorite Record Inserted successfully");
	    db.close();
	}
	public void removeFromFavorites()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		   db.delete(TABLE_COMPANY, "nickname=? and user=?", new String[]{this.nickname,this.user});
   
		   db.close();
	}
	public void toggleFavorite()
	{
		if(isFavorite()) removeFromFavorites();
		else addAsFavorite();
	}


	public int getCount()
	{
		  	String countQuery = "SELECT  * FROM " + TABLE_COMPANY;
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        return cursor.getCount();
	}
	public ArrayList<Favorites> getFavoritesList()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ArrayList<Favorites> favList = new ArrayList<Favorites>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_COMPANY;
	    Cursor cursor = db.rawQuery(selectQuery, null);

	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	Favorites comp= new Favorites(context);
	            comp.nickname=cursor.getString(1);
	            comp.user=Utils.getLoggedNickName(context);
	            favList.add(comp);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return favList;
	}

	public boolean isFavorite()
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
