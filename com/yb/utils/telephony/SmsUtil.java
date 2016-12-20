package com.yb.utils.telephony;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * 短信工具类
 * @author YangBin
 *
 */
public class SmsUtil {


	/**
	 * 查询所有的短信内容</br>
	 * 需要权限:</br>
	 * 	uses-permission android:name="android.permission.READ_SMS"
	 *	
	 * @param context
	 * @return List<SmsInfo>
	 */
	public static List<SmsInfo> querySmss(Context context){
		
		List<SmsInfo> list = new ArrayList<SmsInfo>();
		//[5]构造uri
		Uri uri = Uri.parse("content://sms/");
		//[6]由于短信的数据库已经通过内容提供者暴露出来 所以我们直接通过内容解析者查询
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"address","date","body","read"}, null, null, null);
		if(cursor != null && cursor.getCount() > 0){
			
			while(cursor.moveToNext()){
				SmsInfo smsInfo = new SmsInfo();
				
				String address = cursor.getString(0);
				Long date = cursor.getLong(1);
				String body = cursor.getString(2);
				int read = cursor.getInt(3);
				
				smsInfo.adderss = address;
				smsInfo.date = new Date(date);
				smsInfo.body = body;
				smsInfo.read = read;
				list.add(smsInfo);
			}
		}
		return list;
	}
	

	/**
	 * 查询所有的短信内容 </br>
	 * 需要权限:uses-permission android:name="android.permission.READ_SMS"</br>
	 * Key信息：</br>
	 * 	adderss	短信来源</br>
	 * 	body	短信内容</br>
	 * 	date	短信的时间</br>
	 * 	read	短信的状态；	短信是否已读,已读为1, 未读为0</br>
	 * @param context 上下文环境	
	 * @return List集合
	 */
	public static List<Map<String,Object>> querySmsCollection (Context context){
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		//[5]构造uri
		Uri uri = Uri.parse("content://sms/");
		//[6]由于短信的数据库已经通过内容提供者暴露出来 所以我们直接通过内容解析者查询
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"address","date","body","read"}, null, null, null);
		if(cursor != null && cursor.getCount() > 0){
			
			while(cursor.moveToNext()){
				Map<String,Object> map = new HashMap<String,Object>();
				
				String address = cursor.getString(0);
				Long date = cursor.getLong(1);
				String body = cursor.getString(2);
				int read = cursor.getInt(3);
				
				map.put("adderss", address);
				map.put("date", new Date(date));
				map.put("body", body);
				map.put("read", read);
				
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 插入一条短信 数据,时间是当前时间</br>
	 * 需要权限：uses-permission android:name="android.permission.WRITE_SMS"
	 * @param context 上下文环境
	 * @param smsInfo 短信的信息
	 */
	public static void insertSms(Context context, SmsInfo smsInfo){
        //[1]由于短信的数据库已经通过内容提供者暴露出来了 所以我想操作数据库 直接通过内容的解析者
        Uri uri = Uri.parse("content://sms/");
        //[2]创建ContentValues
        ContentValues values = new ContentValues();
        values.put("address", smsInfo.adderss);
        values.put("body", smsInfo.body);
        values.put("date", smsInfo.date.getTime());
        values.put("read", smsInfo.read);
        context.getContentResolver().insert(uri, values);
	}
	

	/**
	 * 插入一条短信 数据,时间是当前时间</br>
	 * 需要权限：uses-permission android:name="android.permission.WRITE_SMS"
	 * @param context	上下文环境
	 * @param adderss	短信来源
	 * @param body	短信内容
	 * @param date	短信的时间
	 * @param read	短信的状态；	短信是否已读,已读为1, 未读为0
	 */
	public static void insertSms(Context context, String adderss, String body, Date date, int read ){
        //[1]由于短信的数据库已经通过内容提供者暴露出来了 所以我想操作数据库 直接通过内容的解析者
        Uri uri = Uri.parse("content://sms/");
        //[2]创建ContentValues
        ContentValues values = new ContentValues();
        values.put("address", adderss);
        values.put("body", body);
        values.put("date", date.getTime());
        values.put("read", read);
        context.getContentResolver().insert(uri, values);
	}	
	
	
	
	
	/**
	 * 短信的信息
	 * @author YangBin
	 */
	public static class SmsInfo{
		/**
		 * 短信来源
		 */
		public String adderss;		
		/**
		 * 短信内容
		 */
		public String body;
		/**
		 * 短信日期
		 */
		public Date date;
		/**
		 * 短信的状态
		 * 	短信是否已读,已读为1, 未读为0
		 */
		public int read;
		
		@Override
		public String toString() {
			return "SmsInfo [adderss=" + adderss + ", body=" + body + ", date="
					+ date + ", read=" + read + "]";
		}		
		
	}
}
