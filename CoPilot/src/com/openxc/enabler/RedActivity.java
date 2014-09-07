package com.openxc.enabler;


import android.app.Activity;
import android.view.View;

public class RedActivity extends Activity {
	
	void onCreate()
	{
	setContentView(R.layout.main);
	View someView = findViewById(10);
	View root = someView.getRootView();
	root.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
	}
}
