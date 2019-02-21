package com.example.eventfeed.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventfeed.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;

public class ActivityLoginEmailFB extends AppCompatActivity {

    private static EditText email;
    private static String emailStr;
    private EditText password;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button button;
    private String mCustomToken;
    private static final String TAG ="ActivityLoginEmail";
    FirebaseAuth.AuthStateListener mAuthListener;
    private static Boolean current_user_db= Boolean.FALSE;
    private static ArrayList<String> peopleGoingEmails;
    private static final String APP_ID = "C97A0F99-AF01-452D-87FB-3FA4EAA3614B";

    public static String getEmailStr() {
        return emailStr.replace(".",",");
    }

    public static void setPeopleGoingEmails(ArrayList<String> peopleGoingEmails) {
        ActivityLoginEmailFB.peopleGoingEmails = peopleGoingEmails;
    }

    public static ArrayList<String> getPeopleGoingEmails() {
        return peopleGoingEmails;
    }

    public static void setEmailStr(String emailStr) {
        ActivityLoginEmailFB.emailStr = emailStr;
    }

    public void setCurrent_user_db(Boolean current_user_db) {
        this.current_user_db = current_user_db;
    }

    public static Boolean getCurrent_user_db() {
        return current_user_db;
    }

    public ActivityLoginEmailFB() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout_fb);

        email = (EditText) findViewById(R.id.login_email_input);
        setEmailStr(email.getText().toString().trim());
       // password = (EditText) findViewById(R.id.login_password_input);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        button = (Button) findViewById(R.id.login);

        if(savedInstanceState !=null){
        setEmailStr(savedInstanceState.getString("email"));
        }

        SendBird.init(APP_ID, this.getApplicationContext());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == button){

                    if (TextUtils.isEmpty(email.getText().toString())){
                        Toast.makeText(getApplicationContext(), "A Field is Empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(password.getText().toString())){
                        Toast.makeText(getApplicationContext(), "A Field is Empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String userId = email.getText().toString();
                    // Remove all spaces from userID
                    userId = userId.replaceAll("\\s", "");

                   // String userNickname = userId.getText().toString();

                    connectToSendBird(userId, userId);
                    LoginUser();
                }
            }
        });
    }
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        // Save the user's current game state
        savedInstanceState.putString("email", getEmailStr());;


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void LoginUser(){
        setEmailStr(email.getText().toString().trim());
        String Password = password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email.getText().toString(), Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                            currentUser = mAuth.getCurrentUser();
                            if(task.isSuccessful()){
                                finish();
                            setCurrent_user_db(false);
                            Intent intent= new Intent(ActivityLoginEmailFB.this, Profile.class);

                            startActivity(intent);
                            }else {

                            setCurrent_user_db(true);
                            Toast.makeText(ActivityLoginEmailFB.this, "couldn't login",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void connectToSendBird(final String userId, final String userNickname) {
        button.setEnabled(false);

        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {

                if (e != null) {
                    // Error!
                    Toast.makeText(
                            ActivityLoginEmailFB.this, "" + e.getCode() + ": " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                            .show();

                    // Show login failure snackbar
                    button.setEnabled(true);
                    return;
                }

                // Update the user's nickname
                updateCurrentUserInfo(userNickname);


                finish();
            }
        });
    }

    /**
     * Updates the user's nickname.
     * @param userNickname  The new nickname of the user.
     */
    private void updateCurrentUserInfo(String userNickname) {
        SendBird.updateCurrentUserInfo(userNickname, null, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {
                    // Error!
                    Toast.makeText(
                           ActivityLoginEmailFB.this, "" + e.getCode() + ":" + e.getMessage(),
                            Toast.LENGTH_SHORT)
                            .show();

                    return;
                }

            }
        });
    }

}
