package com.vatsi.locus;

import com.vatsi.library.DatabaseHandler;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SqlView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dbview);
		TextView tv=(TextView)findViewById(R.id.tvSQLinfo);
		DatabaseHandler info=new DatabaseHandler(this);
		System.out.println("i am here");
		String s=info.getItemData()+"\n\n"+"CurrUser=\n";//+info.getCurrentUser();
			tv.setText("\n"+s);
	}

	/*@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DatabaseHandler info=new DatabaseHandler(this);
		info.updateCurrentUser(0,null);
	}//*/
	
}
