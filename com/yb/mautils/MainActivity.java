package com.yb.mautils;

import java.util.List;
import java.util.Map;

import com.yb.utils.telephony.ContactUitl;
import com.yb.utils.telephony.SmsUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		testSms();
	}

	
	public void testContact(){
		List<Map<String,String>> collection = ContactUitl.queryContactCollection(getApplicationContext());
		System.out.println(collection);
	}
	
	public void testSms(){
		List<Map<String, Object>> collection = SmsUtil.querySmsCollection(getApplicationContext());
		System.out.println(collection);
	}
}
