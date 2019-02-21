package com.example.eventfeed;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.eventfeed.R;
import com.example.eventfeed.login.ActivityLoginEmail;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.chat21.android.core.ChatManager;
import org.chat21.android.core.authentication.ChatAuthentication;
import org.chat21.android.core.contacts.listeners.OnContactCreatedCallback;
import org.chat21.android.core.exception.ChatRuntimeException;
import org.chat21.android.core.users.models.IChatUser;
import org.chat21.android.ui.ChatUI;

public class Chat21 extends Activity {
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/"+ ActivityLoginEmail.getEmailStr()+"/profile/profileInfo");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_21_layout);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               final String name = documentSnapshot.get("name").toString();
                ChatManager.startWithEmailAndPassword(Chat21.this, getString(R.string.google_app_id),
                        ActivityLoginEmail.getEmailStr(), "1234567", new ChatAuthentication.OnChatLoginCallback() {
                            @Override
                            public void onChatLoginSuccess(IChatUser currentUser) {
                                ChatManager.getInstance().createContactFor(currentUser.getId(), currentUser.getEmail(),
                                        name, "", new OnContactCreatedCallback() {
                                            @Override
                                            public void onContactCreatedSuccess(ChatRuntimeException exception) {
                                                if (exception == null) {
//                                            ChatUI.getInstance().openConversationsListActivity();
                                                    ChatUI.getInstance().openConversationMessagesActivity("81gLZhYmpTZM0GGuUI9ovD7RaCZ2", "Chuck Norris");
                                                } else {
                                                    // TODO: handle the exception
                                                }
                                            }
                                        });
                            }

                            @Override
                            public void onChatLoginError(Exception e) {
                                // TODO: 22/02/18
                            }
                        });
            }
        });


    }
}