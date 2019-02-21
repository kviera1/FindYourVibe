package com.example.eventfeed.eventFeed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventfeed.R;
import com.example.eventfeed.model.Person;
import com.example.eventfeed.profiles.UserProfile;
import com.example.eventfeed.signup.RegisterActivity;
import com.example.eventfeed.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.rpc.Help;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PeopleGoingAdapter extends  RecyclerView.Adapter<PeopleGoingAdapter.MyViewHolder> {
    private LinkedHashMap<String,Person> mPostList;
    private  OnItemClickListener mListener;
    private  List<String> keys;
    private static Person currentItem;
    private Context mContext;



    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/"+ PeopleGoing.getEmail()+"/profile/profileInfo");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference coverPhotos = storageRef.child("images/"+ PeopleGoing.getEmail()+"/cover" );
    private StorageReference profilePics;
    private ImageView cover_photo;
    private ImageView profile_photo;


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setKeys(List<String> l) {
        this.keys = l;
    }

    public void setCurrentItem(Person currentItem) {
        this.currentItem = currentItem;
    }

    public static Person getCurrentItem() {
        return currentItem;
    }

    public void setOnitemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_going_row,parent, false);
        MyViewHolder mvh = new MyViewHolder(v, mListener);
        setKeys(new ArrayList<String>(mPostList.keySet()));


        return mvh;
    }


    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.getUsername().setText((CharSequence)((Person)this.mPostList.get(keys.get(position))).getText());;
        setCurrentItem(mPostList.get(position));

       // get user name form database
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = (documentSnapshot.get("name").toString());
                    holder.getUsername().setText(name);

                }
            }
        });

       // mDocRef = FirebaseFirestore.getInstance().document("events/" + mCardView.getTag()+"/atendees/" +profile_email);
        holder.mCardView.setTag(keys.get(holder.getAdapterPosition()));


    // get user pic and set it from databse
        profilePics = storageRef.child("images/"+ keys.get(holder.getAdapterPosition())+"/profile" );
        profilePics.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.getPhoto());
            }
        });

// send to user that is going to events profiel
holder.getButton().setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        b.putString("userEmail",(holder.mCardView.getTag().toString()));
        Intent intent = new Intent(mContext,UserProfile.class);
        intent.putExtras(b);
        mContext.startActivity(intent);
    }
});
    }
    public PeopleGoingAdapter(LinkedHashMap<String,Person> postList, Context context){
        mPostList = postList;
        mContext =context;
        //jeff
    };


    public int getItemCount() {
        return mPostList.size();
    }


    public  static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView;
        public TextView mTextView1;
        public Button mButton;
        public CardView mCardView;
        private DocumentReference mDocRef = RegisterActivity.getmDocRef();
        public final Button getButton() {
            return this.mButton;
        }


        public final TextView getUsername() {
            return this.mTextView;
        }
        public final TextView getText() {
            return this.mTextView1;
        }


        public final ImageView getPhoto() {
            return this.mImageView;
        }

        public MyViewHolder(final View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.photo);
            mTextView= itemView.findViewById(R.id.nav_user_name);


            mButton = itemView.findViewById(R.id.view_profile);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            ArrayList<String> atendees = new ArrayList<String>();
            Button goingBtn = (Button) itemView.findViewById(R.id.going_button);
//            goingBtn.setOnClickListener(new View.OnClickListener() {
//                String profile_email = ActivityLoginEmail.getEmailstr();
//                @Override
//                public void onClick(View v) {
//                    Map<String, Object> dataToSave = new HashMap<>();
//                    mDocRef = FirebaseFirestore.getInstance().document("events/" + mCardView.getTag()+"/atendees/" +profile_email);
//                    dataToSave.put("going", "true");
//                    mDocRef.set(dataToSave);
//                }
//            });

//            Button button= (Button) itemView.findViewById(R.id.friends);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(itemView.getContext(),PeopleGoing.class);
//                    Bundle b = new Bundle();
//
//                    b.putString("email",(mCardView.getTag().toString())); //Your id
//                    intent.putExtras(b);
//                    itemView.getContext().startActivity(intent);
//                }
//            });


            itemView.findViewById(R.id.view_profile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int posistion = getAdapterPosition();
                        if(posistion != RecyclerView.NO_POSITION);
                        listener.onItemClick(posistion);
                    }
                }
            });
        }

        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }





    }
}
