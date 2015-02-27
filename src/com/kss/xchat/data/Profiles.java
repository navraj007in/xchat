package com.kss.xchat.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Profiles {
	public int id;
	public String NickName;
	public String Name;
	public String Status;
	public String City;
	public String State;
	public String Zip;
	public String Gender;
	public String DOB;
	public String Country;
	public String ProfileImage;
	ContentValues contentValues;
	Context context;
	public String TAG="Profiles";
	private static final String TABLE_COMPANY= "Friends";

	private String KEY_ID="_id";
	private String KEY_NICKNAME="NickName";
	private String KEY_NAME="Name";
	private String KEY_STATUS="Status";
	private String KEY_CITY="City";
	private String KEY_STATE="State";
	private String KEY_ZIP="Zip";
	private String KEY_COUNTRY="Country";
	private String KEY_PROFILEIMAGE="ProfileImage";
	private String KEY_GENDER="Gender";
	
	
	
	

	public Profiles(Context context)
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
	    Log.i(TAG, "Record Inserted successfully");
	    db.close();
	}
	public ArrayList<Profiles> getProfiles()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ArrayList<Profiles> compList = new ArrayList<Profiles>();
	    // Select All Query
	    String selectQuery = "SELECT  _id,nickname,name,status," +
	    		"city,state,zip,country,ProfileImage,Gender FROM " + TABLE_COMPANY;
	    Cursor cursor = db.rawQuery(selectQuery, null);

	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	Profiles comp= new Profiles(context);
	            comp.id=cursor.getInt(0);
	            comp.NickName=cursor.getString(1);
	            comp.Name=cursor.getString(2);
	            comp.Status=cursor.getString(3);
	            comp.City=cursor.getString(4);
	            comp.State=cursor.getString(5);
	            comp.Zip=cursor.getString(6);
	            comp.Country=cursor.getString(7);
	            comp.ProfileImage=cursor.getString(8);
	            comp.Gender=cursor.getString(9);

	            compList.add(comp);
	            Log.i(TAG,comp.id+"-"+comp.NickName+"-"+comp.Name+"-");
	            
	        } while (cursor.moveToNext());
	    }
	 
	    cursor.close();
	    db.close();
	    // return contact list
	    return compList;
	}

	
	public ArrayList<Conversations> getConversations(String senderId)
	{
		Log.i(TAG, senderId);
		ArrayList<Conversations> compList = new ArrayList<Conversations>();
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_COMPANY, new String[] { KEY_ID,
	            KEY_NICKNAME,
	            KEY_NAME,
	            KEY_CITY,
	            KEY_STATE,
	            KEY_ZIP,
	            KEY_COUNTRY,
	            KEY_PROFILEIMAGE
	            }, 
	            KEY_NICKNAME + "=?",
	            new String[] { String.valueOf(senderId) }, null, null, null, null);
	    if (cursor.moveToFirst()) {
	        do {
	        	Conversations comp= new Conversations(context);
	            comp.id=cursor.getInt(0);
	            comp.Message=cursor.getString(2);
	            comp.sender=cursor.getString(4);
	            comp.timeStamp=cursor.getString(5);
	            comp.fromUser=cursor.getString(6);
	            
	            compList.add(comp);
	            Log.i(TAG,comp.id+"-"+comp.sender+"-"+comp.Message);
	        } while (cursor.moveToNext());
	    }
	    db.close();
	    return compList;
	}

	public Profiles getProfile(int id)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_COMPANY, new String[] { KEY_ID,
	            KEY_NICKNAME,
	            KEY_NAME,
	            KEY_STATUS,
	            KEY_CITY,
	            KEY_STATE,KEY_ZIP,
	            KEY_PROFILEIMAGE,
	            KEY_GENDER
	            }, 
	            KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
		
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    Profiles bp= new Profiles(context);
	    return bp;
	}
	public Profiles getProfile(String nickname)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
/*		Cursor cursor = db.query(TABLE_COMPANY, new String[] { KEY_ID,
	            KEY_NICKNAME,
	            KEY_NAME,
	            KEY_STATUS,
	            KEY_CITY,
	            KEY_STATE,KEY_ZIP,KEY_COUNTRY,
	            KEY_PROFILEIMAGE
	            }, 
	            KEY_NICKNAME + " ='?'",
	            new String[] {nickname}, null, null, null, null);
*/        Cursor cursor=db.rawQuery("select _id,nickname,name,gender,dob,city,state,country,zip,profileimage from Friends where nickname='"+ nickname+"'", null);
Profiles bp= new Profiles(context);

		if (cursor != null)
		{
	        if(cursor.moveToFirst())
	        {
		    bp.NickName=cursor.getString(1);
		    bp.Name=cursor.getString(2);
		    bp.Gender=cursor.getString(3);
		    bp.DOB=cursor.getString(4);
		    bp.City=cursor.getString(5);
		    bp.State=cursor.getString(6);
		    bp.Country=cursor.getString(7);
		    bp.Zip=cursor.getString(8);
		    bp.ProfileImage=cursor.getString(9);
		    cursor.close();
		    return bp;
	        }
	        else 
	        {
	        	cursor.close();
	        	db.close();
	        	return null;
	        }
		}
		else
		{
			//cursor.close();
			db.close();
			return null;
			
		}
	}

	public int getCount()
	{
		  	String countQuery = "SELECT  * FROM " + TABLE_COMPANY;
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        int count=cursor.getCount();
	        cursor.close();
	        db.close();
	        return count;
	}
	public int checkProfile(String nickname)
	{
		  	String countQuery = "SELECT  count(*) as rowcount FROM " + TABLE_COMPANY+" where nickname='"+nickname+"'";
		  	Log.i(TAG, countQuery);
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        if(cursor.moveToFirst())
	        {
		        int count=cursor.getInt(0);
		        db.close();
		        return count;
	        }
	        db.close();
	        return 0;
	}

	public int updateRecord(Profiles bp)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		CreateContentValues();
		    // updating row
		    return db.update(TABLE_COMPANY, contentValues, KEY_ID + " = ?",
		            new String[] { String.valueOf(bp.id) });		
	}
	public void deleteRecord(Profiles bp)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.delete(TABLE_COMPANY, KEY_ID + " = ?",
		            new String[] { String.valueOf(bp.id) });
		    db.close();
	}
	public void removeProfile(String nickname)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.delete(TABLE_COMPANY, KEY_NICKNAME+ " = ?",
		            new String[] { nickname });
		    db.close();
	}

	public void CreateContentValues()
	{
		contentValues = new ContentValues();
		contentValues .put(KEY_NICKNAME, this.NickName); // Contact Name
		contentValues .put(KEY_NAME, this.Name); // Contact Name
		contentValues .put(KEY_CITY, this.City); // Contact Name
		contentValues .put(KEY_STATE, this.State); // Contact Name
		contentValues .put(KEY_STATUS, this.Status); // Contact Name
		contentValues .put(KEY_COUNTRY, this.Country); // Contact Name
		contentValues.put(KEY_ZIP, this.Zip);
		contentValues.put(KEY_PROFILEIMAGE, this.ProfileImage);
		contentValues.put(KEY_GENDER, this.Gender);
		
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
