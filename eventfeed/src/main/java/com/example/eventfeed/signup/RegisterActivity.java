package com.example.eventfeed.signup;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.eventfeed.FacebookLogin;
import com.example.eventfeed.FacebookLogin;
import com.example.eventfeed.login.Profile;
import com.example.eventfeed.R;
import com.example.eventfeed.login.ActivityLoginEmail;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String EMAIL_KEY = "email";
    public static final String PASSWORD_KEY = "password";
    private static final String TAG ="log" ;
    private EditText txtUserName;
    private EditText txtUserEmail;
    public static String Email;
    private EditText txtUserPassword;
    private TextView fbLogin;
    private LoginButton fbLoginBtn;

    private Button btnRegister;

    private static FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView txtLogin;
    private CallbackManager callbackManager;
    private static DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/userData");

    public static FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public static DocumentReference getmDocRef() {
        return mDocRef;
    }

    public static void setEmail(String email) {
        Email = email;
    }

    public static String getEmail() {
        return Email.replace(".",",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        printhashkey();


        firebaseAuth = FirebaseAuth.getInstance();
//intialize ui variables
        txtUserName = (EditText) findViewById(R.id.etxt_name);
        txtUserEmail = (EditText) findViewById(R.id.etxt_email);
        txtUserPassword = (EditText) findViewById(R.id.etxt_pass);
        btnRegister = (Button) findViewById(R.id.btn_register);
        txtLogin = (TextView) findViewById(R.id.txt_login);

       //on click to link to login if user already has account
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLoginEmail.setEmailStr(txtUserEmail.getText().toString());
                Intent intent = new Intent(RegisterActivity.this, ActivityLoginEmail.class);
                startActivity(intent);
                finish();
            }
        });


        progressDialog = new ProgressDialog(this);

       //sets user email and calls register method
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLoginEmail.setEmailStr(txtUserEmail.getText().toString());
                RegisterUser();
            }
        });
        callbackManager = CallbackManager.Factory.create();

      // intializes fb login button
        fbLoginBtn = (LoginButton) findViewById(R.id.fb_login);
       // LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        //sets requested permsions from user fb profile
        fbLoginBtn.setReadPermissions(
                "public_profile", "email");

        //call back to FB api
      fbLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(LoginResult loginResult) {

              // call method for a facebook login
              handleFacebookAccessToken(loginResult.getAccessToken());




              GraphRequest request = GraphRequest.newMeRequest(
                      loginResult.getAccessToken(),
                      new GraphRequest.GraphJSONObjectCallback() {
                          @Override
                          public void onCompleted(
                                  JSONObject object,
                                  GraphResponse response) {
                              try {
                                  String userEmail= object.get("email").toString();
                                  ActivityLoginEmail.setEmailStr(userEmail);

                              } catch (JSONException e) {
                                  e.printStackTrace();
                              }
                          }
                      });


              FirebaseUser user = getFirebaseAuth().getCurrentUser();

             //ActivityLoginEmail.setEmailStr(user.getEmail());
//             String userEmail = user.getEmail();
//              String userID = user.getUid();
//             Toast.makeText(RegisterActivity.this, userEmail,Toast.LENGTH_SHORT).show();
//              ActivityLoginEmail.setEmailStr(userEmail);
//              setEmail(userEmail);
//              Bundle b = new Bundle();
//              b.putString("emailFB", userEmail);
//
//
//
//              Intent intent = new Intent(RegisterActivity.this, CreateProfileActivity.class);
//
//              startActivity(intent);



          }

          @Override
          public void onCancel() {
              Log.v("LoginActivity", "cancel");
          }

          @Override
          public void onError(FacebookException error) {
              Log.v("LoginActivity", error.getCause().toString());
          }
      });

      //gets login token and checks if user is signed into login
      AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedInFB = accessToken != null && !accessToken.isExpired();


        // need user to login into facebook if they close app for functionality
        if(isLoggedInFB){
            LoginManager.getInstance().logOut();
            accessToken = null;
//            GraphRequest request = GraphRequest.newMeRequest(
//                   AccessToken.getCurrentAccessToken(),
//                    new GraphRequest.GraphJSONObjectCallback() {
//                        @Override
//                        public void onCompleted(
//                                JSONObject object,
//                                GraphResponse response) {
//
//                            try {
//                                String userEmail= object.get("email").toString();
//                                ActivityLoginEmail.setEmailStr(userEmail);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//
//            Intent intent = new Intent(RegisterActivity.this, Profile.class);
//            startActivity(intent);
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        //get facebook credentials and sign into firebase
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            //get user email and set it
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            ActivityLoginEmail.setEmailStr(user.getEmail());
                            setEmail(user.getEmail());
                            //if it is a first time user they are directed to create profile if not their profile is loaded
                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                Intent intent = new Intent(RegisterActivity.this, CreateProfileActivity.class);

                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(RegisterActivity.this, Profile.class);

                                startActivity(intent);
                            }

                            Log.d(TAG, "signInWithCredential:success");
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void RegisterUser(){
    //pull user data that they input and create firebase user
        setEmail(txtUserEmail.getText().toString().trim());
        String Password = txtUserPassword.getText().toString().trim();
       //check if login info is empty
        if (TextUtils.isEmpty(Email)){
            Toast.makeText(this, "A Field is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Password)){
            Toast.makeText(this, "A Field is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,Object> dataToSave = new HashMap<String,Object>();
        mDocRef = FirebaseFirestore.getInstance().document("users/" +Email);
        dataToSave.put(EMAIL_KEY, Email);
        dataToSave.put(PASSWORD_KEY,Password);
        mDocRef.set(dataToSave);
        firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            //check if successful
                            if (task.isSuccessful()) {
                                //User is successfully registered and logged in
                                //start Profile Activity here
                                Toast.makeText(RegisterActivity.this, "registration successful",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), CreateProfileActivity.class));
                            }else{
                                Toast.makeText(RegisterActivity.this, "Couldn't register, try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }





   /* public void googleLogin(View view) {
        startActivity(new Intent(ActivityRegister.this, ActivityGoogleLogin.class));
        finish();
    }

    public void githubLogin(View view) {
        startActivity(new Intent(ActivityRegister.this, ActivityGithub.class));
        finish();
    }

    public void phoneLogin(View view) {
        startActivity(new Intent(ActivityRegister.this, ActivityPhoneAuth.class));
        finish();
    }

    public void twitterLogin(View view) {
        startActivity(new Intent(ActivityRegister.this, ActivityTwitterAuth.class));
        finish();
    }*/

//    public void facebookLogin(View view) {
//        startActivity(new Intent(RegisterActivity.this, FacebookLogin.class));
//        finish();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    public void printhashkey(){
//get sha hash key for linkin purposes
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.eventfeed",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }
}