package com.kss.xchat.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RoomChat {
	public int id;
	public String Message;
	public String sender;
	public String timeStamp;
	public String RoomID;
	public String RoomName;
	
	ContentValues contentValues;
	Context context;
	public String TAG="RoomChat";
	private static final String TABLE_COMPANY= "RoomChat";

	private String KEY_ID="_id";
	private String KEY_RoomID="RoomID";
	private String KEY_RoomName="RoomName";
	private String KEY_Message="Message";
	private String KEY_timeStamp="timeStamp";
	private String KEY_SENDER="sender";
	

	public RoomChat(Context context)
	{
	this.context=context;
	}

	public void addChatRecord()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		CreateContentValues();
	    // Inserting Row
	    db.insert(TABLE_COMPANY,null, contentValues);
	    Log.i(TAG, "Record Inserted successfully");
	    db.close();
	}
	public ArrayList<RoomChat> getChatList()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ArrayList<RoomChat> compList = new ArrayList<RoomChat>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_COMPANY;
	    Cursor cursor = db.rawQuery(selectQuery, null);

	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	RoomChat comp= new RoomChat(context);
	            comp.id=cursor.getInt(0);
	            comp.Message=cursor.getString(1);
	            comp.sender=cursor.getString(2);
	            comp.RoomID=cursor.getString(3);
	            comp.RoomName=cursor.getString(4);

	            compList.add(comp);
	            Log.i(TAG,comp.id+"-"+comp.RoomName+"-"+comp.Message);
	            
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return compList;
	}

	public ArrayList<RoomChat> getChatRoomList(int roomId)
	{
		ArrayList<RoomChat> compList = new ArrayList<RoomChat>();
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_COMPANY, new String[] { KEY_ID,
	            KEY_RoomID,
	            KEY_Message,
	            KEY_RoomName,
	            KEY_SENDER,
	            KEY_timeStamp
	            }, 
	            KEY_RoomID + "=?",
	            new String[] { String.valueOf(roomId) }, null, null, null, null);
	    if (cursor.moveToFirst()) {
	        do {
	        	RoomChat comp= new RoomChat(context);
	            comp.id=cursor.getInt(0);
	            comp.Message=cursor.getString(2);
	            comp.sender=cursor.getString(4);
	            comp.RoomID=cursor.getString(1);
	            comp.RoomName=cursor.getString(3);
	            comp.timeStamp=cursor.getString(5);
	            compList.add(comp);
	            Log.i(TAG,comp.id+"-"+comp.RoomName+"-"+comp.Message);
	            
	        } while (cursor.moveToNext());
	    }
	    return compList;
	}

	public RoomChat getChatLine(int id)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_COMPANY, new String[] { KEY_ID,
	            KEY_RoomID,
	            KEY_Message,
	            KEY_RoomName,
	            KEY_SENDER,
	            KEY_timeStamp
	            }, 
	            KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    RoomChat bp= new RoomChat(context);
	    return bp;
	}
	public int getCount()
	{
		  	String countQuery = "SELECT  * FROM " + TABLE_COMPANY;
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        return cursor.getCount();
	}
	public int updateRecord(RoomChat bp)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		CreateContentValues();
		    // updating row
		    return db.update(TABLE_COMPANY, contentValues, KEY_ID + " = ?",
		            new String[] { String.valueOf(bp.id) });		
	}
	public void deleteRecord(RoomChat bp)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.delete(TABLE_COMPANY, KEY_ID + " = ?",
		            new String[] { String.valueOf(bp.id) });
		    db.close();
	}
	public void CreateContentValues()
	{
		contentValues = new ContentValues();
		contentValues .put(KEY_Message, this.Message); // Contact Name
		contentValues .put(KEY_RoomID, this.RoomID); // Contact Name
		contentValues .put(KEY_RoomName, this.RoomName); // Contact Name
		contentValues .put(KEY_SENDER, this.sender); // Contact Name
		contentValues .put(KEY_timeStamp, this.timeStamp); // Contact Name
	}

}
