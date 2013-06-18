package com.example.facebooktest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;

public class MainActivity extends Activity {

 private String TAG = "MainActivity";
 private TextView lblEmail;
 private TextView Info;
 
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);
  
  lblEmail = (TextView) findViewById(R.id.lblEmail);
  Info = (TextView) findViewById(R.id.Info);
  
  LoginButton authButton = (LoginButton) findViewById(R.id.activity_login_facebook_btn_login);
  authButton.setOnErrorListener(new OnErrorListener() {
   
   @Override
   public void onError(FacebookException error) {
    Log.i(TAG, "Error " + error.getMessage());
   }
  });
  // set permission list, Don't foeget to add email
  authButton.setReadPermissions(Arrays.asList("basic_info","email", "user_location", "friends_location"));
  // session state call back event
  authButton.setSessionStatusCallback(new Session.StatusCallback() {
   
   @Override
   public void call(Session session, SessionState state, Exception exception) {
    
    if (session.isOpened()) {
              Log.i(TAG,"Access Token"+ session.getAccessToken());
              Request.executeMeRequestAsync(session,
                      new Request.GraphUserCallback() {
                          @Override
                          public void onCompleted(GraphUser user,Response response) {
                              if (user != null) { 
                               Log.w(TAG,"User ID: "+ user.getId());
                               Log.w(TAG,"Name: "+ user.asMap().get("name"));
                               Log.w(TAG,"Local: "+ user.getLocation().getProperty("name")); //+ " " + user.getInnerJSONObject());
                              
                               lblEmail.setText("Name: " + user.asMap().get("name") + "\n" + "Email: " + user.asMap().get("email").toString() + "\n" + "Local: "+ user.getLocation().getProperty("name"));
                              } 
                          }
                      });

              Request friendRequest = Request.newMyFriendsRequest(session, 
            		  new Request.GraphUserListCallback() {				
						@Override
						public void onCompleted(List<GraphUser> users, Response response) {
							// TODO Auto-generated method stub
							for(GraphUser u : users){
								Log.i(TAG, "Name: " + u.getName());
								try {
										if(u.getInnerJSONObject().getBoolean("installed") == true){
											Log.i(TAG, u.getName() + " has app intalled.");
											Info.setText(Info.getText() + u.getName() + "\n"); //+ "local: " + u.getInnerJSONObject().optJSONObject("location").get("name") + "\n");
								}
									//Log.i(TAG, "data: " + u.getInnerJSONObject().optJSONObject("location").get("name"));
								} catch (JSONException e) { }
								//u.getLocation());
							}
							//Log.i(TAG, "INFO"+ response.toString());
						}
            	});
              Bundle params = new Bundle();
              params.putString("fields", "id,name,location,picture,birthday,installed");
              
              friendRequest.setParameters(params);
              friendRequest.executeAsync();
              
          }
   }
  });
 }

 @Override
 public void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
     Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
 }
 

}

