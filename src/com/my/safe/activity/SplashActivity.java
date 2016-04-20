package com.my.safe.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.safe.R;
import com.my.safe.utils.StreamUtiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;



public class SplashActivity extends Activity {

    protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int URL_EXCEPTION = 1;
	protected static final int IO_EXCEPTION = 2;
	protected static final int JSON_EXCEPTION = 3;
	protected String mVersionName;
	protected int mVersionCode;
	protected String mVersionDep;
	protected String mDownloadLink;
	protected int mLocalVersionCode;
	Handler mHdl = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case URL_EXCEPTION:
				Toast.makeText(SplashActivity.this, "URL EXCEPTION", 0).show();
				break;
			case IO_EXCEPTION:
				Toast.makeText(SplashActivity.this, "IO EXCEPTION", 0).show();
				break;
			case JSON_EXCEPTION:
				Toast.makeText(SplashActivity.this, "JSON EXCEPTION", 0).show();
				break;
			default:
				break;
			}
		};
	};
	
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
    			Message msg = Message.obtain();
    			HttpURLConnection conn = null;
    			try {
    				url = new URL("http://10.0.2.2:8080/update.json");
    				conn = (HttpURLConnection)url.openConnection();
    				conn.setRequestMethod("GET");
    				conn.setConnectTimeout(5000);
    				conn.setReadTimeout(5000);
    				conn.connect();
    				int responsCode = conn.getResponseCode();
    				if (200 == responsCode) {
    					InputStream in = conn.getInputStream();
    					String result = StreamUtiles.streamToString(in);
    					JSONObject jsn = new JSONObject(result);
    					mVersionName = jsn.getString("versionName");
    					mVersionCode = jsn.getInt("versionCode");
    					mVersionDep = jsn.getString("versionDep");
    					mDownloadLink = jsn.getString("downloadLink");
    					if (mVersionCode > getVersionCode()) {
    						/*update dialog*/
    						msg.what = SHOW_UPDATE_DIALOG;
    					}
    				}
    			} catch (MalformedURLException e) {
    				// TODO Auto-generated catch block
    				msg.what = URL_EXCEPTION;
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				msg.what = IO_EXCEPTION;
    				e.printStackTrace();
    			}
    	    	catch (JSONException e) {
    	    		msg.what = JSON_EXCEPTION;
					e.printStackTrace();
				} finally {
					mHdl.sendMessage(msg);
					if (null != conn) {
						conn.disconnect();
					}
				}
    		}
    	}.start();
    	return;
    }
	protected int getVersionCode() {

    	PackageManager packageManager = getPackageManager();
    	try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			mLocalVersionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("getVersionCode wrong");
			e.printStackTrace();
		}

		return mLocalVersionCode;
	}
	protected void showUpdateDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("update");
		builder.setMessage("update safe version?");
		builder.setPositiveButton("update now", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("will update now");
			}
		});
		builder.setNegativeButton("no later", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("do not update");
			}
		});
		builder.show();
	}
}
