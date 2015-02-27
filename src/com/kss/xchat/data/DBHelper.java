package com.kss.xchat.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// All Static variables
    // Database Version
//    private String TAG="DBHelper";
	private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "xChat";
 
    // Contacts table name
    private static final String RoomChat_CREATE= "CREATE TABLE RoomChat("
    		+"_id INTEGER PRIMARY KEY," +
    		"RoomID int," +
    		"RoomName text," +
    		"Message text," +
    		"timeStamp text," +
    		"sender text,fromUser text,type text)";
    private static final String PM_CREATE= "CREATE TABLE PersonalMessages("
    		+"_id INTEGER PRIMARY KEY," +
    		"sender text," +
    		"Message text," +
    		"timestamp text," +
    		"Flash int," +
    		"destroyTime text,fromUser text,mid text,unread int,user text,type text," +
    		"sent int,delivered int,read int)";
    private static final String ROSTER_CREATE= "CREATE TABLE Roster("
    		+"_id INTEGER PRIMARY KEY," +
    		"friendid int," +
    		"username text," +
    		"status text," +
    		"avatar blob," +
    		"muted int," +
    		"hotlisted int)";
    private static final String BUDDY_CREATE= "CREATE TABLE BuddyRequests("
    		+"_id INTEGER PRIMARY KEY," +
    		"sender text," +
    		"Message text," +
    		"timestamp text," +
    		"Flash int," +
    		"destroyTime text,fromUser text)";
    private static final String FAVORITES_CREATE= "CREATE TABLE Favorites("
    		+"_id INTEGER PRIMARY KEY," +
    		"nickname text," +
    		"user text," +
    		"avatar text" +
    		")";
    private static final String MUTE_CREATE= "CREATE TABLE Mute("
		+"_id INTEGER PRIMARY KEY," +
		"nickname text," +
		"user text" +
		")";
    private static final String BLOCKED_CREATE= "CREATE TABLE Blocked("
		+"_id INTEGER PRIMARY KEY," +
		"nickname text," +
		"user text" +
		")";

    private static final String FLASH_CREATE= "CREATE TABLE Flash("
    		+"_id INTEGER PRIMARY KEY," +
    		"nickname text," +
    		"user text" +
    		")";
    private static final String SECURE_CREATE= "CREATE TABLE SecureMode("
    		+"_id INTEGER PRIMARY KEY," +
    		"nickname text," +
    		"user text" +
    		")";
    private static final String SETTINGS_CREATE= "CREATE TABLE Favorites("
    		+"_id INTEGER PRIMARY KEY," +
    		"nickname text," +
    		"name text," +
    		"avatar text" +
    		")";

    private static final String GROUPS_CREATE= "CREATE TABLE Groups("
    		+"_id INTEGER PRIMARY KEY," +
    		"gid int," +
    		"name text," +
    		"header text" +
    		")";
    private static final String GROUPMESSAGES_CREATE= "CREATE TABLE GroupMessages("
    		+"_id INTEGER PRIMARY KEY," +
    		"sender text," +
    		"Message text," +
    		"timestamp text," +
    		"Flash int," +
    		"destroyTime text,fromUser text,mid text,unread int,user text," +
    		"sent int,delivered int,read int)";
    private static final String PROFILES_CREATE= "CREATE TABLE Profiles("
		+"_id INTEGER PRIMARY KEY," +
		"nickname text," +
		"name text," +
		"City text," +
		"State text," +
		"Zip text," +
		"Country text," +
		"ProfileImage text," +
		"Status text," +
		"Gender text,dob text" +
		")";
    private static final String ROSTERS_CREATE= "CREATE TABLE Friends("
    		+"_id INTEGER PRIMARY KEY," +
    		"nickname text," +
    		"name text," +
    		"City text," +
    		"State text," +
    		"Zip text," +
    		"Country text," +
    		"ProfileImage text," +
    		"Status text," +
    		"Gender text,dob text" +
    		")";
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(RoomChat_CREATE);
        db.execSQL(PM_CREATE);
        db.execSQL(ROSTER_CREATE);
        db.execSQL(BUDDY_CREATE);
        db.execSQL(FAVORITES_CREATE);
        db.execSQL(MUTE_CREATE);
        db.execSQL(FLASH_CREATE);
        db.execSQL(PROFILES_CREATE);
        db.execSQL(ROSTERS_CREATE);
        db.execSQL(SECURE_CREATE);
        db.execSQL(BLOCKED_CREATE);
        db.execSQL(GROUPS_CREATE);
        db.execSQL(GROUPMESSAGES_CREATE);
        
      
        
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed

    	db.execSQL("DROP TABLE IF EXISTS ROSTER" );
        db.execSQL("DROP TABLE IF EXISTS PersonalMessages" );
        db.execSQL("DROP TABLE IF EXISTS RoomChat" );
        db.execSQL("DROP TABLE IF EXISTS BuddyRequests" );
    	db.execSQL(RoomChat_CREATE);
        db.execSQL(PM_CREATE);
        db.execSQL(ROSTER_CREATE);
        db.execSQL(BUDDY_CREATE);
        db.execSQL(FAVORITES_CREATE);
        db.execSQL(MUTE_CREATE);
        db.execSQL(FLASH_CREATE);

      //  onCreate(db);
    }
}