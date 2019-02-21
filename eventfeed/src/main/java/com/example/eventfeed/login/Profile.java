package com.example.eventfeed.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.eventfeed.R;
import com.example.eventfeed.R.id;
import com.example.eventfeed.eventFeed.MainActivity;
import com.example.eventfeed.signup.RegisterActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.jvm.internal.Intrinsics;



public final class Profile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView Email;
    private TextView Uid;
    private TextView nameView;
    private TextView interestsView;
    private Button logout;
    protected NavigationView navigationView;
    private HashMap _$_findViewCache;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/"+ ActivityLoginEmail.getEmailStr()+"/profile/profileInfo");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference coverPhotos = storageRef.child("images/"+ ActivityLoginEmail.getEmailStr()+"/cover" );
    private StorageReference profilePics = storageRef.child("images/"+ ActivityLoginEmail.getEmailStr()+"/profile" );
    private ImageView cover_photo;
    private ImageView profile_photo;

    @NotNull
    protected final NavigationView getNavigationView() {
        NavigationView var10000 = this.navigationView;
        if (this.navigationView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("navigationView");
        }

        return var10000;
    }

    protected final void setNavigationView(@NotNull NavigationView var1) {
        Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
        this.navigationView = var1;
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.profile_main);

        nameView =(TextView) findViewById(id.profile_name);
        interestsView =(TextView) findViewById(id.interests);
       // Email = (TextView)findViewById(R.id.profile_email);

        mAuth = FirebaseAuth.getInstance();
        // logout = (Button)findViewById(R.id.button_logout);
        user = mAuth.getCurrentUser();

        if (user != null){
            String email = user.getEmail();
            String uid = user.getUid();
           // Email.setText(email);

        }
    fetchProfile();



        this.setSupportActionBar((Toolbar)this._$_findCachedViewById(id.toolbar));
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar == null) {
            Intrinsics.throwNpe();
        }

        Intrinsics.checkExpressionValueIsNotNull(actionBar, "actionBar!!");
        actionBar.setTitle((CharSequence)"Find Your Vibe");

        drawerLayout = (DrawerLayout) findViewById(id.profile_drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(Profile.this,drawerLayout,R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle = new ActionBarDrawerToggle(Profile.this,drawerLayout,R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.nav_view) ;
        View navView = navigationView.inflateHeaderView(R.layout.nav_header);



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
public void fetchProfile() {
cover_photo = (ImageView) findViewById(R.id.cover_photo);
profile_photo = (ImageView) findViewById(R.id.profile_picture) ;
    mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               if(documentSnapshot.exists()){
                   String name = documentSnapshot.getString("name");

                   String interests = documentSnapshot.getString("interest");
                   nameView.setText(name);
                   interestsView.setText(interests);

               }
            }
        });


    final long ONE_MEGABYTE = 1024 * 1024;
//    coverPhotos.getBytes(3* ONE_MEGABYTE)
//            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//
//                public void onSuccess(byte[] bytes) {
//                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    DisplayMetrics dm = new DisplayMetrics();
//                    getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//                    cover_photo.setMinimumHeight(dm.heightPixels);
//                    cover_photo.setMinimumWidth(dm.widthPixels);
//                    cover_photo.setImageBitmap(bm);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//        @Override
//        public void onFailure(@NonNull Exception exception) {
//            // Handle any errors
//
//        }
//    });




  profilePics.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
      @Override
      public void onSuccess(Uri uri) {
          Picasso.get().load(uri).into(profile_photo);
      }
  });
  coverPhotos.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
      @Override
      public void onSuccess(Uri uri) {
          Picasso.get().load(uri).into(cover_photo);
      }
  });



//    profilePics.getBytes(ONE_MEGABYTE)
//            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//
//                public void onSuccess(byte[] bytes) {
//                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    DisplayMetrics dm = new DisplayMetrics();
//                    getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//                    profile_photo.setMinimumHeight(dm.heightPixels);
//                    profile_photo.setMinimumWidth(dm.widthPixels);
//                    profile_photo.setImageBitmap(bm);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//        @Override
//        public void onFailure(@NonNull Exception exception) {
//            // Handle any errors
//        }
//    });
//
}
    private void UserMenuSelector(MenuItem item){
        switch(item.getItemId())
        {
            case R.id.nav_profile:
                startActivity(new Intent(Profile.this, Profile.class));
                break;
            case R.id.nav_eventfeed:
                startActivity(new Intent(Profile.this, MainActivity.class));
                break;

            case R.id.nav_logout:

                if(FirebaseAuth.getInstance().getCurrentUser() !=null){
                    RegisterActivity.getFirebaseAuth().signOut();
                    Toast.makeText(this,"Logged out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Profile.this, RegisterActivity.class);
                    getApplicationContext().startActivity(intent);
                }
                else{
                    LoginManager.getInstance().logOut();
                    Toast.makeText(this,"Logged out of Facebook", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Profile.this, RegisterActivity.class);
                    getApplicationContext().startActivity(intent);
                }

                break;

        }
    }



    public boolean onCreateOptionsMenu(@NotNull Menu menu) {
        Intrinsics.checkParameterIsNotNull(menu, "menu");
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    public View _$_findCachedViewById(int var1) {
        if (this._$_findViewCache == null) {
            this._$_findViewCache = new HashMap();
        }

        View var2 = (View)this._$_findViewCache.get(var1);
        if (var2 == null) {
            var2 = this.findViewById(var1);
            this._$_findViewCache.put(var1, var2);
        }

        return var2;
    }

    public void _$_clearFindViewByIdCache() {
        if (this._$_findViewCache != null) {
            this._$_findViewCache.clear();
        }

    }
}
