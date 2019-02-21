package com.example.eventfeed.eventFeed;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventfeed.login.ActivityLoginEmail;
import com.example.eventfeed.login.Profile;
import com.example.eventfeed.model.Person;
import com.example.eventfeed.model.Post;
import com.example.eventfeed.profiles.Profile_girl;
import com.example.eventfeed.profiles.Profile_guy;
import com.example.eventfeed.profiles.Profile_guy1;
import com.example.eventfeed.R;
import com.example.eventfeed.signup.RegisterActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.security.auth.callback.Callback;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.jvm.internal.Intrinsics;

public class PeopleGoing extends AppCompatActivity {

    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/"+ ActivityLoginEmail.getEmailStr()+"/profile/profileInfo");
    private CollectionReference docRefEventGoers ;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private StorageReference coverPhotos;
    private StorageReference profilePics;
    private ImageView cover_photo;
    private ImageView profile_photo;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private PeopleGoingAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar mToolbar;
    private  static String email;
    private  static String name;
   private LinkedHashMap<String,Person> people = new LinkedHashMap<String, Person>();
   private  ArrayList<String> emails = new ArrayList<String>();
   private EditText emailHolderText;

private static String value;

    public void setName(String name) {

        this.name = name;
    }

    public void setEmails(ArrayList<String> emails) {
       this.emails = emails;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public  static String  getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String getValue() {
        return value;
    }

    public void  setEmail(String email) {
        this.email = email;
    }

    public static String getEmail() {
        return email;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_going_window);
        Bundle b = getIntent().getExtras();
        // or other values
        if(b != null) {
            value = b.getString("event");
        }
        emailHolderText = (EditText) findViewById(R.id.holder_text);



        this.setSupportActionBar((Toolbar)this.findViewById(R.id.toolbar));
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar == null) {
            Intrinsics.throwNpe();
        }

        Intrinsics.checkExpressionValueIsNotNull(actionBar, "actionBar!!");
        actionBar.setTitle((CharSequence)"People Going");


       docRefEventGoers = FirebaseFirestore.getInstance().collection("events/"+ value+"/atendees");
       docRefEventGoers.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

               if(!queryDocumentSnapshots.isEmpty()){
                for (int i=0; i<queryDocumentSnapshots.getDocuments().size(); i++) {
                    final String docId =queryDocumentSnapshots.getDocuments().get(i).getString("yes");
                    getEmails().add(docId);


                    mDocRef = FirebaseFirestore.getInstance().document("users/" + docId + "/profile/profileInfo");
                    mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {


                                name = documentSnapshot.getString("name");
                               // Toast.makeText(PeopleGoing.this, getName(),
                                 //       Toast.LENGTH_SHORT).show();
                            } else {
                                setName("null");
                            }

                            people.put(docId, new Person(docId, name));
                           /// Toast.makeText(PeopleGoing.this, "name",
                              //      Toast.LENGTH_SHORT).show();
                            postList =(RecyclerView) findViewById(R.id.all_users_going_list);
                            postList.setHasFixedSize(true);
                            mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            postList.setLayoutManager(mLayoutManager);
                            postList.setAdapter(mAdapter);


                            mAdapter = new PeopleGoingAdapter(people,getApplicationContext());
                            postList.setAdapter(mAdapter);

                        }
                    });
                }

               }
           }
       });
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(PeopleGoing.this,drawerLayout,R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle = new ActionBarDrawerToggle(PeopleGoing.this,drawerLayout,R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.nav_view) ;
        View navView = navigationView.inflateHeaderView(R.layout.nav_header);


      StorageReference storageRef = storage.getReference();
     profilePics = storageRef.child("images/"+ ActivityLoginEmail.getEmailStr()+"/profile" );

        navigationView = (NavigationView) findViewById(R.id.nav_view) ;

        final CircleImageView profilePicNav = (CircleImageView) navView.findViewById(R.id.nav_profile_pic);
        final TextView profileNameTxt = (TextView) navView.findViewById(R.id.nav_user_name);


        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                profileNameTxt.setText(name);
            }
        });


        profilePics.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(profilePicNav);
            }
        });



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });




//        docRefEventGoers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    QuerySnapshot myResults = task.getResult();
//                    List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
//                    for(int i=0; i<myListOfDocuments.size();i++){
//                       String docId= myListOfDocuments.get(i).getId();
//                        getEmails().add(docId);
//                        Toast.makeText(PeopleGoing.this, docId,
//                               Toast.LENGTH_SHORT).show();
//                    }


//                    for (QueryDocumentSnapshot document : myResults){
//
//                       emailHolderText.setText(document.getId());
//                        ArrayList<String> temp = new ArrayList<String>();
//                        getEmails().add(document.getId().toString());
//                      ActivityLoginEmail.setPeopleGoingEmails(temp);
//                        Toast.makeText(PeopleGoing.this, document.getId(),
//                                Toast.LENGTH_SHORT).show();
//                    }



  //  emails = ActivityLoginEmail.getPeopleGoingEmails();
       for (int i=0; i< emails.size();i++) {
           mDocRef = FirebaseFirestore.getInstance().document("users/" + emails.get(i) + "/profile/profileInfo");
           mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                   if (documentSnapshot.exists()) {


                       name = documentSnapshot.getString("name");
                     //  Toast.makeText(PeopleGoing.this, getName(),
                       //        Toast.LENGTH_SHORT).show();
                   } else {
                       setName("null");
                   }
               }
           });

           //setName("jeff");
           StorageReference coverPhotos = storageRef.child("images/" + getEmail() + "/cover");
           StorageReference profilePics = storageRef.child("images/" + getEmail() + "/profile");

           people.put(emails.get(i), new Person(emails.get(i), getName()));
       }




//
//        Button button_girl = (Button) findViewById(R.id.button_girl);
//        button_girl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PeopleGoing.this, Profile_girl.class);
//                Bundle b = new Bundle();
//                b.putInt("email", 1); //Your id
//                intent.putExtras(b); //Put your id to your next Intent
//                startActivity(intent);
//                finish();
//
//            }
//        });
//        Button button_guy = (Button) findViewById(R.id.button_guy);
//        button_guy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PeopleGoing.this, Profile_guy.class);
//                Bundle b = new Bundle();
//                b.putInt("email", 1); //Your id
//                intent.putExtras(b); //Put your id to your next Intent
//                startActivity(intent);
//                finish();
//
//            }
//        });
//        Button button_guy1 = (Button) findViewById(R.id.button_guy1);
//        button_guy1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PeopleGoing.this, Profile_guy1.class);
//                Bundle b = new Bundle();
//                b.putInt("email", 1); //Your id
//                intent.putExtras(b); //Put your id to your next Intent
//                startActivity(intent);
//                finish();
//
//            }
//        });

     /*   DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;


        getWindow().setLayout((int)(width),(int)(height))*/;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item){
        switch(item.getItemId())
        {
            case R.id.nav_profile:
                startActivity(new Intent(PeopleGoing.this, Profile.class));
                break;
            case R.id.nav_eventfeed:
                startActivity(new Intent(PeopleGoing.this, MainActivity.class));
                break;


            case R.id.nav_logout:
                if(FirebaseAuth.getInstance().getCurrentUser() !=null){
                    RegisterActivity.getFirebaseAuth().signOut();
                    Toast.makeText(this,"Logged out", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(PeopleGoing.this, RegisterActivity.class);
                    getApplicationContext().startActivity(intent1);
                }
                else{
                    LoginManager.getInstance().logOut();
                    Toast.makeText(this,"Logged out of Facebook", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(PeopleGoing.this, RegisterActivity.class);
                    getApplicationContext().startActivity(intent2);
                }
                break;

        }
    }
}
