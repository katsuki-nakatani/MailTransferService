package com.miruker.lib.mailtransferservice.Utils;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;



public class ContactUtils {

	/**
	 * メールアドレスから表示名を取得する
	 * @param con
	 * @param mailAddress
	 * @return
	 */
	public static String getEmlAddressToDisplay(Context con , String mailAddress)
	{
        ContentResolver content = con.getContentResolver();
        final Cursor cursor = content.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
        		(new String[] {ContactsContract.CommonDataKinds.Email.CONTACT_ID})
        		,ContactsContract.CommonDataKinds.Email.DATA1 +"='" + mailAddress + "'" , null, null);

        String displayName = null;
        if(cursor != null && cursor.getCount() > 0)
        while (cursor.moveToNext()) {

            final Cursor cursorCont = content.query(ContactsContract.Contacts.CONTENT_URI,
            		(new String[] {ContactsContract.Contacts.DISPLAY_NAME}),
            		ContactsContract.Contacts._ID +"='" + cursor.getString(0) + "'", null, null);

            if(cursorCont != null && cursorCont.getCount() > 0)
            	while (cursorCont.moveToNext()) {
            		displayName = cursorCont.getString(0);
            		break;
            	}
            if(cursorCont != null)
            	cursorCont.close();
            break;
        }
        if(cursor != null)
        	cursor.close();

        return displayName;
	}


	/**
	 * 電話番号から表示名を取得する
	 * @param con
	 * @param phoneAddress
	 * @return
	 */
	public static String getPhoneAddressToDisplay(Context con , String phoneAddress)
	{
        ContentResolver content = con.getContentResolver();
        final Cursor cursor = content.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        		(new String[] {ContactsContract.CommonDataKinds.Phone.CONTACT_ID})
        		,ContactsContract.CommonDataKinds.Phone.DATA1 +"='" + phoneAddress + "'", null, null);

        String displayName = null;
        if(cursor != null && cursor.getCount() > 0)
        while (cursor.moveToNext()) {

            final Cursor cursorCont = content.query(ContactsContract.Contacts.CONTENT_URI,
            		(new String[] {ContactsContract.Contacts.DISPLAY_NAME}),
            		ContactsContract.Contacts._ID +"='" + cursor.getString(0) + "'", null, null);

            if(cursorCont != null && cursorCont.getCount() > 0)
            	while (cursorCont.moveToNext()) {
            		displayName = cursorCont.getString(0);
            		break;
            	}
            if(cursorCont != null)
            	cursorCont.close();
            break;
        }
        if(cursor != null)
        	cursor.close();

        return displayName;
	}


	/**
	 * 電話帳の表示名の一覧を取得する
	 * @param con コンテキスト
	 * @return 一覧
	 */
	public static List<String> getDisplayNameList(Context con)
	{
        ContentResolver content = con.getContentResolver();

        List<String> retList = new ArrayList<String>();
        final Cursor cursorCont = content.query(ContactsContract.Contacts.CONTENT_URI,
           		(new String[] {ContactsContract.Contacts.DISPLAY_NAME}),
           		null, null, ContactsContract.Contacts.DISPLAY_NAME);

        if(cursorCont.getCount() > 0)
        	while (cursorCont.moveToNext()) {
        		retList.add(cursorCont.getString(0));
        		break;
        	}
        cursorCont.close();

        return retList;
	}




}
