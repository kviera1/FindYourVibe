package com.example.eventfeed.eventFeed;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;


import com.example.eventfeed.login.ActivityLoginEmail;
import com.example.eventfeed.login.Profile;
import com.example.eventfeed.R;
import com.example.eventfeed.signup.RegisterActivity;
import com.example.eventfeed.model.Post;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.ticketmaster.api.discovery.DiscoveryApi;
import com.ticketmaster.api.discovery.operation.SearchEventsOperation;
import com.ticketmaster.api.discovery.response.PagedResponse;
import com.ticketmaster.discovery.model.Event;
import com.ticketmaster.discovery.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.jvm.internal.Intrinsics;

public class MainActivity extends AppCompatActivity{

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private MyAdapater mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar mToolbar;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference profilePics = storageRef.child("images/"+ ActivityLoginEmail.getEmailStr()+"/profile" );
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/"+ ActivityLoginEmail.getEmailStr()+"/profile/profileInfo");
    private ImageView photo;
    private  final String apiKey =  "tcCsEcBaUIc5IdAEcnDSymaSAYvF3Jaq7";
   private  static ArrayList<Post> posts = new ArrayList<Post>();
   private  List<Event> events = new ArrayList<Event>();

    public static ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);










//new TicketMaster().execute("hey");
//        String eventImage = events.get(0).getImages().get(0).getUrl().toString();
//        String eventDescription= events.get(0).getDescription();
//       String eventName= events.get(0).getName();

       // postMap.put(eventName,new Post(eventName,eventDescription,eventImage));

//set titls of top tool bar
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        this.setSupportActionBar((Toolbar)this.findViewById(R.id.toolbar));
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar == null) {
            Intrinsics.throwNpe();
        }

        Intrinsics.checkExpressionValueIsNotNull(actionBar, "actionBar!!");
        actionBar.setTitle((CharSequence)"Events");

// initialize list of posts and recyler view then set adapter
        postList =(RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        postList.setLayoutManager(mLayoutManager);
        postList.setAdapter(mAdapter);



        //event posts are held in a linked hashmaps
        LinkedHashMap<String,Post> postMap = new LinkedHashMap<String, Post>();

        //postMap.put("empty", new  Post("empty","empty","empty"));
        postMap.put("Drake",new Post("Drake ", "Drake Live in Charlotte, Get Tickets Now","https://www.billboard.com/files/styles/article_main_image/public/media/02-drake-ovo-2017-billboard-1548.jpg" ));
        postMap.put("Fall Out Boy",new Post("Fall Out Boy", "Fall Out Boy Coming to the Spectrum Center","https://ksassets.timeincuk.net/wp/uploads/sites/55/2018/04/Fall-Out-Boy-920x584.jpg" ));
        postMap.put("Ariana Grande",new Post("Ariana Grande", "Come to Get over your ex","https://s.yimg.com/ny/api/res/1.2/osQltM4C86gEnYnIAkavPg--~A/YXBwaWQ9aGlnaGxhbmRlcjtzbT0xO3c9ODAw/http://media.zenfs.com/en-US/homerun/rollingstone.com/17d2b7bc6facbb1ab10251827b6d0776 "));
        postMap.put("Brad Paisley",new Post("Brad Paisley", "Yeehaw","http://www.vipseats.com/blog/image.axd?picture=2015%2F2%2FBrad+Paisley+Tickets+2015.jpg" ));
        postMap.put("Post Malone",new Post("Post Malone", "Come happy, leave sad","https://www.c3concerts.com/wp/wp-content/uploads/post-malone_c3c.jpg" ));
        //for(int i =0; i<100; i++){
         //   postMap.put("artist"+Integer.toString(i) ,new Post("Artist Name", "Event","https://www.telegraph.co.uk/content/dam/business/spark/e-on-energy-efficiency/view-from-the-crowd-towards-stage-at-rock-concert.jpg?imwidth=1400" ));
         //}

        ///pass event map to the mydapter class
        setPosts(posts);
        mAdapter = new MyAdapater(postMap,this);
        postList.setAdapter(mAdapter);

//Button mButton = (Button) findViewById(R.id.button1);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,PeopleGoing.class);
//                Bundle b = new Bundle();
//                b.putString("event",(MyAdapater.getCardView().getTag().toString())); //Your id
//                intent.putExtras(b);
//                startActivity(intent);
//            }
//        });

//set up layout for side nav
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//get user picture and name to put in side nav
               navigationView = (NavigationView) findViewById(R.id.nav_view) ;
        final View navView = navigationView.inflateHeaderView(R.layout.nav_header);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item){

        //logic for side nav
        switch(item.getItemId())
        {
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this, Profile.class));
                break;
            case R.id.nav_eventfeed:
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
                case R.id.nav_logout:
                if(FirebaseAuth.getInstance().getCurrentUser() !=null){
                    RegisterActivity.getFirebaseAuth().signOut();
                    Toast.makeText(this,"Logged out", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(MainActivity.this, RegisterActivity.class);
                    getApplicationContext().startActivity(intent1);
                }
                else{
                    LoginManager.getInstance().logOut();
                    Toast.makeText(this,"Logged out of Facebook", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(MainActivity.this, RegisterActivity.class);
                    getApplicationContext().startActivity(intent2);
                }
                break;

        }
    }

    private class TicketMaster extends AsyncTask<String,Void, List<Event>> {
        private Exception exception;


        protected List<Event> doInBackground(String... s) {
            DiscoveryApi api = new DiscoveryApi(apiKey);

            PagedResponse<Events> page = null;
            try {
                page = api.searchEvents(new SearchEventsOperation().keyword("pop"));
                events= page.getContent().getEvents();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return events;
        }



    }

}
