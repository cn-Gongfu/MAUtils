package com.yb.utils.telephony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Android 手机 联系人 的工具类
 * 
 * @author YangBin
 * 
 */
public class ContactUitl {
	
	/**
	 * 获取手机中所有的联系人</br>
	 * 需要添加权限</br>
	 * uses-permission android:name="android.permission.READ_CONTACTS"</br>
	 * @param context
	 */
	public static List<ContactInfo> queryContacts(Context context) {
		// [0] 创建一个集合
		List<ContactInfo> contactList = new ArrayList<ContactInfo>();

		// [1] 先查询 row_contact表的contact_id列, 知道一共有几条联系人

		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");

		Uri dataUri = Uri.parse("content://com.android.contacts/data");

		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { "contact_id" }, null, null, null);

		// ☆ ☆ ☆ ☆ 当我们在查询data表的时候 其实查询的是view_data的视图
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String contact_id = cursor.getString(0);

				if (contact_id != null) {
					ContactInfo contact = new ContactInfo();
					contact.id = contact_id;

					// [2] 根据contact_id 去查询data表 查询data1列和mimetype_id
					Cursor cursor2 = context.getContentResolver().query(
							dataUri, new String[] { "data1", "mimetype" },
							"raw_contact_id=?", new String[] { contact_id },
							null);
					if (cursor2 != null && cursor2.getCount() > 0) {
						while (cursor2.moveToNext()) {
							String data1 = cursor2.getString(0);
							String mimetype = cursor2.getString(1);
							// [3]根据mimetype 区分data1列的数据类型
							if ("vnd.android.cursor.item/name".equals(mimetype)) {
								contact.name = data1;
							} else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
								contact.phone = data1;
							} else if ("vnd.android.cursor.item/email_v2".equals(mimetype)) {
								contact.email = data1;
							}
						}
					}
					contactList.add(contact);
				}
			}
		}

		return contactList;
	}
	
	

	/**
	 * 获取手机中所有的联系人</br>
	 * 需要添加的权限</br>
	 * uses-permission android:name="android.permission.READ_CONTACTS"</br>
	 *  Key:</br>
	 *  name:联系人名称</br>
	 *  phone:电话号码</br>
	 *  email:邮件地址</br>
	 * @param context
	 * @return List中的Map集合
	 */
	public static List<Map<String,String>> queryContactCollection(Context context) {
		// [0] 创建一个集合
		List<Map<String,String>> contactList = new ArrayList<Map<String,String>>();

		// [1] 先查询 row_contact表的contact_id列, 知道一共有几条联系人

		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");

		Uri dataUri = Uri.parse("content://com.android.contacts/data");

		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { "contact_id" }, null, null, null);

		// ☆ ☆ ☆ ☆ 当我们在查询data表的时候 其实查询的是view_data的视图
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				
				String contact_id = cursor.getString(0);
				if (contact_id != null) {
					// [2] 根据contact_id 去查询data表 查询data1列和mimetype_id
					Cursor cursor2 = context.getContentResolver().query(
							dataUri, new String[] { "data1", "mimetype" },
							"raw_contact_id=?", new String[] { contact_id },
							null);
					if (cursor2 != null && cursor2.getCount() > 0) {
						Map<String,String> map = new HashMap<String, String>();
						while (cursor2.moveToNext()) {
							String data1 = cursor2.getString(0);
							String mimetype = cursor2.getString(1);
							// [3]根据mimetype 区分data1列的数据类型
							if ("vnd.android.cursor.item/name".equals(mimetype)) {
								map.put("name", data1);							
							} else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
								map.put("phone", data1);
							} else if ("vnd.android.cursor.item/email_v2".equals(mimetype)) {
								map.put("email", data1);
							}
						}
						contactList.add(map);
					}					
				}
			}
		}

		return contactList;
	}

	
	/**
	 * 插入联系人信息
	 * <uses-permission android:name="android.permission.WRITE_CONTACTS"/>
	 * @param contactInfo
	 */
	public static void inserContact(Context context, ContactInfo contactInfo){
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		
		//[2]获取name phone email Textutils
		String name = contactInfo.name;
		String phone = contactInfo.phone;
		String email = contactInfo.email;
		
		//[2.1]在插入联系人id的时候 先查询一下 row_contact 一共有几条数据    加+1就是联系人的id 
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		int count = cursor.getCount();
		int contact_id = count +1;
		
		//[3] 先往row_contact表  插入联系人的id (contact_id)  
		ContentValues values = new ContentValues();
		values.put("contact_id", contact_id);
		context.getContentResolver().insert(uri,values);
		
		//[4]在把name phone email 插入到data表 
		ContentValues nameValues = new ContentValues();
		nameValues.put("data1", name);
		//☆ ☆ ☆ ☆ ☆ 插入的数据要告诉数据库 属于第几条联系人  和  数据类型 
		nameValues.put("raw_contact_id", contact_id);
		nameValues.put("mimetype", "vnd.android.cursor.item/name");
		context.getContentResolver().insert(dataUri, nameValues);
		
		//[5]把phone号码 插入到data表 
		ContentValues phoneValues = new ContentValues();
		phoneValues.put("data1", phone);
		phoneValues.put("mimetype", "vnd.android.cursor.item/phone_v2");
		phoneValues.put("raw_contact_id", contact_id);
		context.getContentResolver().insert(dataUri, phoneValues);
		
		
		//[5]把phone号码 插入到data表 
		ContentValues emailValues = new ContentValues();
		emailValues.put("data1", email);
		emailValues.put("mimetype", "vnd.android.cursor.item/email_v2");
		emailValues.put("raw_contact_id", contact_id);
		context.getContentResolver().insert(dataUri, emailValues);
		
	}
	

	/**
	 * 插入联系人信息</br>
	 * <uses-permission android:name="android.permission.WRITE_CONTACTS"/>
	 * @param context	上下文环境
	 * @param name	联系人名称
	 * @param phone	联系人电话
	 * @param email	联系人邮件地址
	 */
	public static void inserContact(Context context, String name, String phone, String email){
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		
		//[2.1]在插入联系人id的时候 先查询一下 row_contact 一共有几条数据    加+1就是联系人的id 
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		int count = cursor.getCount();
		int contact_id = count +1;
		
		//[3] 先往row_contact表  插入联系人的id (contact_id)  
		ContentValues values = new ContentValues();
		values.put("contact_id", contact_id);
		context.getContentResolver().insert(uri,values);
		
		//[4]在把name phone email 插入到data表 
		ContentValues nameValues = new ContentValues();
		nameValues.put("data1", name);
		//☆ ☆ ☆ ☆ ☆ 插入的数据要告诉数据库 属于第几条联系人  和  数据类型 
		nameValues.put("raw_contact_id", contact_id);
		nameValues.put("mimetype", "vnd.android.cursor.item/name");
		context.getContentResolver().insert(dataUri, nameValues);
		
		//[5]把phone号码 插入到data表 
		ContentValues phoneValues = new ContentValues();
		phoneValues.put("data1", phone);
		phoneValues.put("mimetype", "vnd.android.cursor.item/phone_v2");
		phoneValues.put("raw_contact_id", contact_id);
		context.getContentResolver().insert(dataUri, phoneValues);
		
		
		//[5]把email 插入到data表 
		ContentValues emailValues = new ContentValues();
		emailValues.put("data1", email);
		emailValues.put("mimetype", "vnd.android.cursor.item/email_v2");
		emailValues.put("raw_contact_id", contact_id);
		context.getContentResolver().insert(dataUri, emailValues);
		
	}
	
	/**
	 * 手机联系人信息
	 * @author YangBin
	 *
	 */
	public static class ContactInfo{
		public String id;
		/**
		 * 联系人姓名
		 */
		public String name;	
		/**
		 * 电话号码
		 */
		public String phone;
		/**
		 * 邮件
		 */
		public String email;
	}
}
