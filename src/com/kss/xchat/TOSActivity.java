package com.kss.xchat;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class TOSActivity extends Activity {
	ListView lstTOS;
	Button cmdContinue;
	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tos);
		lstTOS=(ListView)findViewById(R.id.lstTOS);
		cmdContinue=(Button)findViewById(R.id.cmdContinue);
		cmdContinue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(TOSActivity.this,ConversationsActivity.class);
				startActivity(intent);
				finish();
			}
		});
		ArrayAdapter adapter = ArrayAdapter.createFromResource(
			    this, R.array.listTOS, android.R.layout.simple_list_item_1);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			lstTOS.setAdapter(adapter);

	}



}
