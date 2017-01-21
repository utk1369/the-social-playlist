package com.thesocialplaylist.user.music.activity.facebook;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.thesocialplaylist.user.music.activity.MainActivity;
import com.thesocialplaylist.user.music.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class FacebookLoginActivity extends Activity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void logKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.thesocialplaylist.user.music",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("Package name", "not found");

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        logKeyHash();
        setContentView(R.layout.activity_facebook_login);
        callbackManager = CallbackManager.Factory.create();
        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        fbLogin();
    }

    private void fbLogin() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
               /* info.setText("Welcome: " + loginResult.getAccessToken().getUserId()
                + "\nAuth Token: "+ loginResult.getAccessToken().getToken());*/
                final Profile profile = Profile.getCurrentProfile();
                Log.i("User Name", profile.getName());
                Log.i("Access Token", loginResult.getAccessToken().getToken());
                Log.i("Permissions", String.valueOf(AccessToken.getCurrentAccessToken().getPermissions()));

                new GraphRequest(loginResult.getAccessToken(), "/me/friends", null, HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                Intent intent = new Intent(FacebookLoginActivity.this, MainActivity.class);
                                JSONObject jsonResponse = null;
                                try {
                                    jsonResponse = new JSONObject(response.getRawResponse());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                JSONArray friends = null;
                                try {
                                    friends = jsonResponse.getJSONArray("data");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                intent.putExtra("FRIENDS_LIST",  friends.toString());
                                intent.putExtra("FB_USER_ID", profile.getId());
                                intent.putExtra("FB_USER_NAME", profile.getName());
                                intent.putExtra("accessToken", loginResult.getAccessToken().getToken());
                                startActivity(intent);
                                finish(); //finish the activity so that the user doesnt get back to the login screen
                            }
                        }).executeAsync();
            }

            @Override
            public void onCancel() {
                info.setText("Login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(FacebookLoginActivity.this, "Login Failed! Please try again", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

}
