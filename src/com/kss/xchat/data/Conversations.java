package com.kss.xchat.data;

import java.util.ArrayList;

import com.kss.xchat.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Conversations {
	public int id;
	public String Message;
	public String sender;
	public String timeStamp;
	public String Flash;
	public String destroyTime;
	public String fromUser;
	public String mId;
	public String user;
	public int sent=0;
	public int delivered=0;
	public int read=0;
	public int unread=0;
	ContentValues contentValues;
	Context context;
	public String TAG="Convrsations";
	private static final String TABLE_COMPANY= "PersonalMessages";

	private String KEY_ID="_id";
	private String KEY_destroyTime="destroyTime";
	private String KEY_Flash="Flash";
	private String KEY_Message="Message";
	private String KEY_timeStamp="timeStamp";
	private String KEY_SENDER="sender";
	private String KEY_FROM="fromUser";
	private String KEY_UNREAD="unread";
	private String KEY_MID="mid";
	private String KEY_USER="user";
	private String KEY_SENT="sent";
	private String KEY_DELIVERED="delivered";
	private String KEY_READ="read";
	
	
	
	
	
	

	public Conversations(Context context)
	{
	this.context=context;
	}

	public boolean isStatusMessage()
	{
		return false;
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
	public void markRead(String sender)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues updateConv = new ContentValues();
		   updateConv.put("unread", 0);
		   db.update(TABLE_COMPANY, updateConv, "sender=?", new String[]{sender});
		   db.close();
	}
	public void markSent(String sender,String mid)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues updateConv = new ContentValues();
		   updateConv.put("sent", 1);
		   db.update(TABLE_COMPANY, updateConv, "sender=? and mid=?", new String[]{sender,mid});
		   db.close();
	}

	public ArrayList<Conversations> getChatList()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ArrayList<Conversations> compList = new ArrayList<Conversations>();
	    // Select All Query
	    String selectQuery = "SELECT  _id,message,sender,timeStamp,fromUser,mid FROM " + TABLE_COMPANY;
	    Cursor cursor = db.rawQuery(selectQuery, null);

	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	Conversations comp= new Conversations(context);
	            comp.id=cursor.getInt(0);
	            comp.Message=cursor.getString(1);
	            comp.sender=cursor.getString(2);
	            comp.timeStamp=cursor.getString(3);
	            comp.fromUser=cursor.getString(4);
	//            comp.Flash=cursor.getString(4);
	            comp.mId=cursor.getString(5);

	            compList.add(comp);
	            Log.i(TAG,comp.id+"-"+comp.sender+"-"+comp.timeStamp+"-"+comp.Message+"-"+comp.sender);
	            
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return compList;
	}

	public ArrayList<Conversations> getHeaders()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ArrayList<Conversations> compList = new ArrayList<Conversations>();
	    // Select All Query
	    String selectQuery = "SELECT  _id,message,sender,timeStamp,fromUser,unread FROM " + TABLE_COMPANY +" Group by sender order by _id desc";
	    Cursor cursor = db.rawQuery(selectQuery, null);
	    Log.i(TAG,selectQuery);
	    if (cursor.moveToFirst()) {
	        do {
	        	Conversations comp= new Conversations(context);
	            comp.id=cursor.getInt(0);
	            comp.Message=cursor.getString(1);
	            comp.sender=cursor.getString(2);
	            comp.timeStamp=cursor.getString(3);
	            comp.fromUser=cursor.getString(4);
	            comp.unread=cursor.getInt(5);
	            compList.add(comp);
	            Log.i(TAG,comp.id+"-"+comp.sender+"-"+comp.timeStamp+"-"+comp.Message+"-"+comp.sender);
	            
	        } while (cursor.moveToNext());
	    }
	    db.close();
	    return compList;
	}
	public ArrayList<Conversations> getHeaders1(String nickname)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ArrayList<Conversations> compList = new ArrayList<Conversations>();
	    // Select All Query
	    String selectQuery = "SELECT  _id,message,sender,timeStamp,fromUser,unread FROM " + TABLE_COMPANY +" Group by sender order by _id desc";
	    Cursor cursor = db.rawQuery(selectQuery, null);
	    Log.i(TAG,selectQuery);
	    if (cursor.moveToFirst()) {
	        do {
	        	Conversations comp= new Conversations(context);
	            comp.id=cursor.getInt(0);
	            comp.Message=cursor.getString(1);
	            comp.sender=cursor.getString(2);
	            comp.timeStamp=cursor.getString(3);
	            comp.fromUser=cursor.getString(4);
	            comp.unread=cursor.getInt(5);
	            compList.add(comp);
	            Log.i(TAG,comp.id+"-"+comp.sender+"-"+comp.timeStamp+"-"+comp.Message+"-"+comp.sender);
	            
	        } while (cursor.moveToNext());
	    }
	    db.close();
	    return compList;
	}
	public ArrayList<Conversations> getHeaders(String sender)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ArrayList<Conversations> compList = new ArrayList<Conversations>();
	    // Select All Query
	    String selectQuery = "SELECT  _id,message,sender,timeStamp,fromUser,unread FROM " + TABLE_COMPANY +" where user='"+
	    			sender+"' Group by sender order by _id desc";
	    Cursor cursor = db.rawQuery(selectQuery, null);
	    Log.i(TAG,selectQuery);
	    if (cursor.moveToFirst()) {
	        do {
	        	Conversations comp= new Conversations(context);
	            comp.id=cursor.getInt(0);
	            comp.Message=cursor.getString(1);
	            comp.sender=cursor.getString(2);
	            comp.timeStamp=cursor.getString(3);
	            comp.fromUser=cursor.getString(4);
	            comp.unread=cursor.getInt(5);
	            compList.add(comp);
	            Log.i(TAG,comp.id+"-"+comp.sender+"-"+comp.timeStamp+"-"+comp.Message+"-"+comp.sender);
	            
	        } while (cursor.moveToNext());
	    }
	    db.close();
	    return compList;
	}

	public String getNotificationsString()
	{
		String result="";
		result=getUnreadCount()+" Messages from "+ getHeadersCount() +" Conversations.";
		
	    return result;
	}
	
	public String getConversationText(String senderId)
	{
		ArrayList<Conversations> conversations = getConversations(senderId);
		String backup="";
		for(int i=0;i<conversations.size();i++)
		{
			backup+=senderId+":"+conversations.get(i).timeStamp+":"+conversations.get(i).Message+"\n";
			
		}
			
		return backup;
	}
	public ArrayList<Conversations> getConversations(String senderId)
	{
		Log.i(TAG, senderId);
		ArrayList<Conversations> compList = new ArrayList<Conversations>();
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_COMPANY, new String[] { KEY_ID,
	            KEY_destroyTime,
	            KEY_Message,
	            KEY_Flash,
	            KEY_SENDER,
	            KEY_timeStamp,
	            KEY_FROM,
	            KEY_MID,
	            KEY_SENT
	            }, 
	            KEY_SENDER + "=? and "+ KEY_USER+"=?",
	            new String[] { String.valueOf(senderId),Utils.getLoggedNickName(context) }, null, null, null, null);
	    if (cursor.moveToFirst()) {
	        do {
	        	Conversations comp= new Conversations(context);
	            comp.id=cursor.getInt(0);
	            comp.Message=cursor.getString(2);
	            comp.sender=cursor.getString(4);
	            comp.timeStamp=cursor.getString(5);
	            comp.fromUser=cursor.getString(6);
	            comp.mId=cursor.getString(7);
	            comp.sent=cursor.getInt(8);
	            compList.add(comp);
	            Log.i(TAG,comp.id+"-"+comp.sender+"-"+comp.Message);
	        } while (cursor.moveToNext());
	    }
	    db.close();
	    return compList;
	}

	public RoomChat getChatLine(int id)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_COMPANY, new String[] { KEY_ID,
	            KEY_destroyTime,
	            KEY_Message,
	            KEY_Flash,
	            KEY_SENDER,
	            KEY_timeStamp,
	            KEY_MID
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
	public int getUnreadCount(String sender)
	{
		  	String countQuery = "SELECT  count(*) as rowcount FROM " + TABLE_COMPANY+" where sender='"+sender+"' and unread=1";
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
	public String getUnreadMessages(String sender)
	{
		  	String countQuery = "SELECT  * FROM " + TABLE_COMPANY+" where sender='"+sender+"' and unread=1";
		  	Log.i(TAG, countQuery);
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        String message="";
	        if (cursor.moveToFirst()) {
		        do {
		        	
		        	if(cursor.getString(2).length()>40)
		        		message+=cursor.getString(2).substring(0, 39)+"\n";
		        	else
		        		message+=cursor.getString(2);
		        	
		        } while (cursor.moveToNext());
		    }
	        db.close();
	        return message;
	}

	public ArrayList<Conversations> getUnsentConversations(String sender)
	{
		  	String countQuery = "SELECT  _id,sender,Message,TimeStamp,Flash,DestroyTime,fromuser,mid,unread,user,sent,delivered,read FROM " + TABLE_COMPANY+" where sender='"+sender+"' and unread=1";
		  	Log.i(TAG, countQuery);
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        ArrayList<Conversations> compList=new ArrayList<Conversations>();
		    if (cursor.moveToFirst()) {
		        do {
		        	Conversations comp= new Conversations(context);
		            comp.id=cursor.getInt(0);
		            comp.Message=cursor.getString(2);
		            comp.sender=cursor.getString(1);
		            comp.timeStamp=cursor.getString(3);
		            comp.fromUser=cursor.getString(6);
		            comp.mId=cursor.getString(7);
		            comp.unread=cursor.getInt(8);
		            comp.sent=cursor.getInt(9);
		            compList.add(comp);
		            Log.i(TAG,comp.id+"-"+comp.sender+"-"+comp.Message);
		        } while (cursor.moveToNext());
		    }
		    db.close();
	        return compList;
	}

	public String[] getUnreadMessagesArray()
	{
		  	String countQuery = "SELECT  * FROM " + TABLE_COMPANY+"  where unread=1";
		  	Log.i(TAG, countQuery);
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        String[] response = null;
	        if (cursor.moveToFirst()) {
	        	int count=cursor.getCount();
	        	response=new String[count];
	        	int i=0;
		        do {
		        	
		        	if(cursor.getString(2).length()>40)
		        		response[i]=cursor.getString(2).substring(0, 39);
		        	else
		        		response[i]=cursor.getString(2);
		        	i++;
		        	if(i==10) break;
		        } while (cursor.moveToNext());
		    }
	        db.close();
	        return response;
	}

	public String[] getUnreadMessagesArray(String sender)
	{
		  	String countQuery = "SELECT  * FROM " + TABLE_COMPANY+"  where unread=1 and sender='"+ sender+"'";
		  	Log.i(TAG, countQuery);
			DBHelper dbHelper=new DBHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        String[] response = null;
	        if (cursor.moveToFirst()) {
	        	int count=cursor.getCount();
	        	response=new String[count];
	        	int i=0;
		        do {
		        	
		        	if(cursor.getString(2).length()>40)
		        		response[i]=cursor.getString(2).substring(0, 39);
		        	else
		        		response[i]=cursor.getString(2);
		        	i++;
		        	
		        } while (cursor.moveToNext());
		    }
	        db.close();
	        return response;
	}

	public int getUnreadCount()
	{
		  	String countQuery = "SELECT  count(*) as rowcount FROM " + TABLE_COMPANY+" where unread=1";
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
	public int getHeadersCount()
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_COMPANY +" where unread=1 Group by sender order by _id desc";
	    Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst())
        {
	        int count=cursor.getCount();
	        db.close();
	        return count;
        }
        db.close();
        return 0;
	}

	public int updateRecord(Conversations bp)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		CreateContentValues();
		    // updating row
		    return db.update(TABLE_COMPANY, contentValues, KEY_ID + " = ?",
		            new String[] { String.valueOf(bp.id) });		
	}
	public void deleteRecord(Conversations bp)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.delete(TABLE_COMPANY, KEY_ID + " = ?",
		            new String[] { String.valueOf(bp.id) });
		    db.close();
	}
	public void clearChat(String senderId)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.delete(TABLE_COMPANY, KEY_SENDER + " = ?",
		            new String[] { senderId });
		    db.close();
	}
	public void recallChat(String senderId)
	{
		DBHelper dbHelper=new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.delete(TABLE_COMPANY, KEY_SENDER + " = ? and "+ KEY_FROM
				 +" = ?",
		            new String[] { senderId ,senderId });
		    db.close();
	}

	public void CreateContentValues()
	{
		contentValues = new ContentValues();
		contentValues .put(KEY_Message, this.Message); // Contact Name
		contentValues .put(KEY_destroyTime, this.destroyTime); // Contact Name
		contentValues .put(KEY_Flash, this.Flash); // Contact Name
		contentValues .put(KEY_SENDER, this.sender); // Contact Name
		contentValues .put(KEY_timeStamp, this.timeStamp); // Contact Name
		contentValues .put(KEY_FROM, this.fromUser); // Contact Name
		contentValues.put(KEY_UNREAD, this.unread);
		contentValues.put(KEY_MID, this.mId);
		contentValues.put(KEY_USER, this.user);

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
