package com.example.openwithvoice;

import java.util.ArrayList;
import java.util.List;

import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    	public void onClick(View v) {
        
        switch (v.getId()) {
        
            case R.id.button1:
                startVoiceRecognitionActivity();
        
               break;
        
        }
        
        
        
    }
    	void startVoiceRecognitionActivity() {

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                    .getPackage().getName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    	}
    	
    	@Override
        protected
    void onActivityResult(int requestCode, int resultCode, Intent data) {
            String wordStr = null;
            String[] words = null;
            String firstWord = null;
            String secondWord = null;
            if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
                    && resultCode == RESULT_OK) {
                ArrayList<String> matches = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                wordStr = matches.get(0);
                words = wordStr.split(" ");
                firstWord = words[0];
                secondWord = words[1];
                if (firstWord.equals("open")) {
                    PackageManager packageManager = getPackageManager();
                    List<PackageInfo> packs = packageManager
                            .getInstalledPackages(0);
                    int size = packs.size();
                    boolean uninstallApp = false;
                    boolean exceptFlg = false;
                    for (int v = 0; v < size; v++) {
                        PackageInfo p = packs.get(v);
                        String tmpAppName = p.applicationInfo.loadLabel(
                                packageManager).toString();
                        String pname = p.packageName;
                        tmpAppName = tmpAppName.toLowerCase();
                        if (tmpAppName.trim().toLowerCase().
                                equals(secondWord.trim().toLowerCase())) {
                            PackageManager pm = this.getPackageManager();
                            Intent appStartIntent = pm.getLaunchIntentForPackage(pname);
                            if (null != appStartIntent) {
                                try {                            
                                    this.startActivity(appStartIntent);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                  } // end of open app code	
                 } // end of activityOnResult method
            
            }
}
