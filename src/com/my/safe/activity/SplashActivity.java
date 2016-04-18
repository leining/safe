package com.my.safe.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.safe.R;
import com.my.safe.utils.StreamUtiles;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;



public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_version = (TextView)findViewById(R.id.tv_version);
        tv_version.setText(getVersionName());
        checkVersion();
    }
    private String getVersionName()
    {
    	PackageManager packageManager = getPackageManager();
    	try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return "";
    }
    private void checkVersion()
    {
    	new Thread() {
    		@Override
    		public void run() {
    			URL url;
    			try {
    				url = new URL("http://10.0.2.2:8080/update.json");
    				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    				conn.setRequestMethod("GET");
    				conn.setConnectTimeout(5000);
    				conn.setReadTimeout(5000);
    				int responsCode = conn.getResponseCode();
    				if (200 == responsCode) {
    					InputStream in = conn.getInputStream();
    					String result = StreamUtiles.streamToString(in);
    				}
    		    	conn.connect();
    			} catch (MalformedURLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	    	/*parse json*/
    		}
    	}.start();
    	return;
    }
}
