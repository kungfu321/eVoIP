package com.csipsimple.ui.prefs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.csipsimple.api.SipManager;
import com.csipsimple.db.DBProvider;

public class PrefAdministrator {

	public final static int USERS_TYPE_ADMIN = 1;
	public final static int USERS_TYPE_USER = 0;

	public final static String USERS_TABLE_NAME = "users";
	public static final String FIELD_ID = "_id";
	public static final String FIELD_USERNAME = "username";
	public static final String FIELD_PASSWORD = "password";
	public static final String FIELD_USER_TYPE = "user_type";
	public static final String USER_CONTENT_TYPE = SipManager.BASE_DIR_TYPE + ".user";

    public static final String USER_CONTENT_ITEM_TYPE = SipManager.BASE_ITEM_TYPE + ".user";

    public static final Uri USER_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://"
            + SipManager.AUTHORITY + "/" + USERS_TABLE_NAME);

    public static final Uri USER_ID_URI_BASE = Uri.parse(ContentResolver.SCHEME_CONTENT + "://"
            + SipManager.AUTHORITY + "/" + USERS_TABLE_NAME + "/");

    public static String toHexString(byte[] buffer)
    {
    	StringBuilder sb = new StringBuilder();
    	for(byte b:buffer)
    	{
    		int halfbyte = (b>>>4)&0x0F;
    		int two_halfs = 0;
    		do {
    			sb.append((0<=halfbyte)&&(halfbyte<=9)?(char)('0' + halfbyte):(char)('a'+(halfbyte - 10)));
    			halfbyte = b&0x0F;
    		}while(two_halfs++ < 1);
    	}
    	return sb.toString();
    }

    public static String HashPassword(String password) throws NoSuchAlgorithmException
    {
    	MessageDigest md = MessageDigest.getInstance("SHA-1");
    	md.update(password.getBytes(),0,password.length());
    	byte[] h = md.digest();
    	return toHexString(h);
    }

    public interface ResetUserCallback {
        void Resetusers();
    }

    private Context aContext;
    private ContentResolver resolver;

    public ResetUserCallback ResetUser;

    public PrefAdministrator(Context context)
    {
    	aContext = context;
    	resolver =context.getContentResolver();
    }

    public Uri addUser(String username, String password, int userType)throws NoSuchAlgorithmException {
    	ContentValues values = new ContentValues();
		values.put(PrefAdministrator.FIELD_USERNAME, username);
		values.put(PrefAdministrator.FIELD_PASSWORD, PrefAdministrator.HashPassword(password));
		values.put(PrefAdministrator.FIELD_USER_TYPE, Integer.valueOf(userType));

		Uri u=resolver.insert(USER_URI, values);
		return u;
	}

    public int updateUserPassword(String password, int userType) throws NoSuchAlgorithmException {
    	ContentValues values = new ContentValues();
		values.put(PrefAdministrator.FIELD_PASSWORD, PrefAdministrator.HashPassword(password));
		long id = getFirstUserId(userType);
		return resolver.update(USER_URI, values, FIELD_ID + "=?",new String[]{Long.toString(id)});
	}

    public long getFirstUserId(int userType) throws NullPointerException
    {
    	Cursor c = resolver.query(USER_URI, new String[]{ FIELD_ID }, FIELD_USER_TYPE + "=" + Integer.valueOf(userType), null, null);
    	if(c == null) throw new NullPointerException("Bảng users chưa được tạo");
		if(c.moveToFirst())
		{
			return c.getLong(0);
		}
		else
		{
			return -1;
		}
    }

    public long getAdminId() throws NullPointerException
    {
    	Cursor c = resolver.query(USER_URI, new String[]{ FIELD_ID }, FIELD_USER_TYPE + "=" + Integer.valueOf(USERS_TYPE_ADMIN), null, null);
		if(c == null) throw new NullPointerException("Bảng users chưa được tạo");
		if(c.moveToFirst())
			return c.getLong(0);
		else return -1;
    }

    public long getUserId() throws NullPointerException
    {
    	Cursor c = resolver.query(USER_URI, new String[]{ FIELD_ID }, FIELD_USER_TYPE + "=" + Integer.valueOf(USERS_TYPE_USER), null, null);
    	if(c == null) throw new NullPointerException("Bảng users chưa được tạo");
    	if(c.moveToFirst())
			return c.getLong(0);
		else return -1;
    }

    public boolean isCurrentPassword(String password, int userType) throws NoSuchAlgorithmException, NullPointerException
    {
    	Cursor c = resolver.query(USER_URI, new String[]{ FIELD_ID, FIELD_PASSWORD }, FIELD_USER_TYPE + "=" + Integer.valueOf(userType), null, null);
    	if(c == null) throw new NullPointerException("Bảng users chưa được tạo");
		if(c.moveToFirst())
		{
			String h = HashPassword(password);
			String currPass = c.getString(1);
			return h.equalsIgnoreCase(currPass);
		}
		else return false;
    }
}
