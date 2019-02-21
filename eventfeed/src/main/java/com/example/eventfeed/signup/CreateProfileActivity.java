package com.example.eventfeed.signup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eventfeed.login.Profile;
import com.example.eventfeed.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreateProfileActivity extends AppCompatActivity {


    public static final String INTEREST_KEY = "interest";
    public static final String NAME_KEY = "name";
    private DocumentReference mDocRef = RegisterActivity.getmDocRef();
    private Button createBtn;
    private static final int RESULT_LOAD_IMAGE = 1;
    private  ImageView profilePic;
    private  ImageView coverPic;
    private  int decideImage;
    private Uri uploadFile;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference imagesRef = storageRef.child("images");
    private StorageReference profilesRef = storageRef.child("images/"+RegisterActivity.getEmail());
    private StorageReference coverPhotos = storageRef.child("images/"+ RegisterActivity.getEmail()+"/cover" );
    private StorageReference profilePics = storageRef.child("images/"+ RegisterActivity.getEmail()+"/profile" );

    private UploadTask uploadTask;

    public void setUploadFile(Uri uploadFile) {
        this.uploadFile = uploadFile;
    }

    public Uri getUploadFile() {
        return uploadFile;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.create_profile_layout);
       // Toast.makeText(this, "Create Activity", Toast.LENGTH_SHORT).show();


//initalize image variables
        profilePic =  (ImageView) findViewById(R.id.profile_picture);
        coverPic = (ImageView) findViewById(R.id.cover_photo);

        // allow user to select profile pic and cover pic from their local storage
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               decideImage =0;
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });

        coverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decideImage = 1;
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });


        createBtn = (Button) findViewById(R.id.createProfileBtn);
       //saves user profile information to firebase
        createBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               EditText interestsView = (EditText) findViewById(R.id.interests);
               EditText nameView = (EditText) findViewById(R.id.profile_name);
               String interests = interestsView.getText().toString();
               String name= nameView.getText().toString();


               Map<String, Object> dataToSave = new HashMap<>();
               mDocRef = FirebaseFirestore.getInstance().document("users/" + RegisterActivity.getEmail() + "/profile" +"/profileInfo");
               dataToSave.put(INTEREST_KEY, interests);
               dataToSave.put(NAME_KEY, name);
               mDocRef.set(dataToSave);

               Intent intent = new Intent(CreateProfileActivity.this, Profile.class);
               startActivity(intent);
           }
       });




        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //loads users selected photos into profile template before they upload to database
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && decideImage == 0){
            Uri selectedImage = data.getData();
            uploadFile= data.getData();
            uploadTask = profilePics.putFile(uploadFile);
            profilePic.setImageURI(selectedImage);

        }
        else if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && decideImage == 1){
            Uri selectedImage = data.getData();
            uploadFile= data.getData();
            uploadTask = coverPhotos.putFile(uploadFile);
            coverPic.setImageURI(selectedImage);
        }

    }
}
