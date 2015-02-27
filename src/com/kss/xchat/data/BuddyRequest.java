package com.kss.xchat.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BuddyRequest {
	public int id;
	public String Message;
	public String timeStamp;
	public String fromUser;
	ContentValues contentValues;
	Context context;
	public String TAG="BuddyRequests";
	private static final String TABLE_COMPANY= "BuddyRequests";

	private String KEY_ID="_id";
	private String KEY_Message="Message";
	private String KEY_timeStamp="timeStamp";
	private String KEY_FROM="fromUser";
	
	public BuddyRequest(Context context)
	{
	this.context=context;
	}

	public void addRecord()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		CreateContentValues();
	    // Inserting Row
	    db.insert(TABLE_COMPANY,null, contentValues);
	    Log.i(TAG, "Buddy Request Record Inserted successfully");
	    db.close();
	}
	public BuddyRequest getRequest(String from)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_COMPANY, new String[] { KEY_ID,
	            KEY_Message,
	            KEY_FROM,
	            KEY_timeStamp,
	            }, 
	            KEY_FROM + "=?",
	            new String[] { from }, null, null, null, null);
	    if (cursor != null)
	    {
	        cursor.moveToFirst();
	 
	    BuddyRequest bp= new BuddyRequest(context);
	    return bp;
	    }
	    else return null;
	}
	public ArrayList<BuddyRequest> getRequestList()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ArrayList<BuddyRequest> compList = new ArrayList<BuddyRequest>();
	    // Select All Query
	    String selectQuery = "SELECT  _id,message,timeStamp,fromUser FROM " + TABLE_COMPANY;
	    Cursor cursor = db.rawQuery(selectQuery, null);

	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	BuddyRequest comp= new BuddyRequest(context);
	            comp.id=cursor.getInt(0);
	            comp.Message=cursor.getString(1);
	            comp.timeStamp=cursor.getString(2);
	            comp.fromUser=cursor.getString(3);

	            compList.add(comp);
	            Log.i(TAG,comp.id+"-"+comp.timeStamp+"-"+comp.Message+"-"+comp.fromUser);
	            
	        } while (cursor.moveToNext());
	    }
	    cursor.close();db.close();
	    // return contact list
	    return compList;
	}
	public boolean isRequestSent(String sender)
	{
		  	String countQuery = "SELECT  * FROM " + TABLE_COMPANY+" where sender='"+sender+"' ";
		  	
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
	public void clearRecords()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.delete(TABLE_COMPANY, null,
		            null);
		    db.close();
	}


	public int getCount()
	{
		  	String countQuery = "SELECT  * FROM " + TABLE_COMPANY;
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        return cursor.getCount();
	}
	public void deleteRecord(BuddyRequest br)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.delete(TABLE_COMPANY, KEY_ID + " = ?",
		            new String[] { String.valueOf(br.id) });
		    db.close();
	}
	public void deleteRequest(Conversations bp)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.delete(TABLE_COMPANY, KEY_FROM + " = ?",
		            new String[] { bp.fromUser });
		    db.close();
	}
	public void CreateContentValues()
	{
		contentValues = new ContentValues();
		contentValues .put(KEY_Message, this.Message); // Contact Name
		contentValues .put(KEY_timeStamp, this.timeStamp); // Contact Name
		contentValues .put(KEY_FROM, this.fromUser); // Contact Name

	}
}
