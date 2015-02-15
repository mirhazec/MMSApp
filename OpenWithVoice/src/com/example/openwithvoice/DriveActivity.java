package com.example.openwithvoice;

import android.app.Activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFile.DownloadProgressListener;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;


public class DriveActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private static final String TAG = "android-drive-quickstart";
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final int REQUEST_CODE_OPENER = 4;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
 
   

    /**
     * File that is selected with the open file activity.
     */
   

    private GoogleApiClient mGoogleApiClient;
    private String words;

    /**
     * Create a new file and save it to Drive.
     */
    

    private void saveFileToDrive() {
       
        Log.i(TAG, "Creating new contents.");
       
        final String extractedWords=words;
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveContentsResult>() {

            @Override
            public void onResult(DriveContentsResult result) {
               
                if (!result.getStatus().isSuccess()) {
                    Log.i(TAG, "Failed to create new contents.");
                    return;
                }
               
                Log.i(TAG, "New contents created.");
              
                OutputStream outputStream = result.getDriveContents().getOutputStream();
                
              
               
               
                try {
                    outputStream.write(extractedWords.getBytes());
                } catch (IOException e1) {
                    Log.i(TAG, "Unable to write file contents.");
                }
               
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setMimeType("text/plain").setTitle("Note.doc").build();
                // Create an intent for the file chooser, and start it.
                IntentSender intentSender = Drive.DriveApi
                        .newCreateFileActivityBuilder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(result.getDriveContents())
                        .build(mGoogleApiClient);
                try {
                	
                    startIntentSenderForResult(
                            intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);   
                    
                } catch (SendIntentException e) {
                    Log.i(TAG, "Failed to launch file chooser.");
                }
                
                
           	 
              
                
            }
        });
        
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	if (mGoogleApiClient == null) {
    	
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
    	}
        // Connect the client. 
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    	 
    	 
         words = null;
         if (requestCode==VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
             ArrayList<String> matches = data
                     .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
             words = matches.get(0);
             
             
             }
    	 
          if( requestCode==REQUEST_CODE_CREATOR && resultCode == RESULT_OK){
         Toast.makeText(getApplicationContext(), "Your file has been uploaded to drive", 
         		   Toast.LENGTH_LONG).show();
         String url = "https://drive.google.com/?authuser=0#recent";
     	 Uri uri = Uri.parse(url);
     	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
     	 startActivity(intent);
         Log.i(TAG, "entered creator.");
         }
         
    	 
         
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
     
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    	if(words==null){
        Log.i(TAG, "API client connected.");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        
        return;
    	}
    	saveFileToDrive();
    	 
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }
    @Override
    public void onBackPressed() {
    	 Log.i(TAG, "BackPressed");
    	 if (mGoogleApiClient != null) {
    		 Log.i(TAG, "BackPressedNull");
             mGoogleApiClient.disconnect();
         }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	 Log.i(TAG, "KeyDownBack");
        	if (mGoogleApiClient != null) {
        		 Log.i(TAG, "KeyDownNotNull");
                mGoogleApiClient.disconnect();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    
    
}