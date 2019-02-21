package com.example.eventfeed.eventFeed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventfeed.R;
import com.example.eventfeed.login.ActivityLoginEmail;
import com.example.eventfeed.signup.RegisterActivity;
import com.example.eventfeed.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.admin.v1beta1.ListIndexesRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.eventfeed.eventFeed.MyAdapater.MyViewHolder.mCardView;

public class MyAdapater extends RecyclerView.Adapter<MyAdapater.MyViewHolder> {
private LinkedHashMap<String,Post> mPostList;
private  OnItemClickListener mListener;
private static List<String> keys;
private static Post currentItem;
private Context mContext;
private static CardView cardView;
    private static DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/userData");

public interface OnItemClickListener{
    void onItemClick(int position);
}

    public static CardView getCardView() {
        return cardView;
    }

    public void setKeys(ArrayList<String> keys ) {
        this.keys = keys;
    }

    public static List<String> getKeys() {
        return keys;
    }

    public void setCurrentItem(Post currentItem) {
        this.currentItem = currentItem;
    }

    public static Post getCurrentItem() {
        return currentItem;
    }

    public void setOnitemClickListener(OnItemClickListener listener){
    mListener = listener;
}

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row,parent, false);
        MyViewHolder mvh = new MyViewHolder(v, mListener);
       // setKeys(new ArrayList<String>(mPostList.keySet()));
        cardView = MyViewHolder.getmCardView();


        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder,int position) {

    setKeys(new ArrayList<String>(mPostList.keySet()));
        //final MyViewHolder holder1 = holder;
   setCurrentItem(mPostList.get(keys.get(position)));

   // set picture and text of cardview populated with the event posts
   holder.mCardView.setTag(keys.get(position));
        holder.getUsername().setText((CharSequence)((Post)this.mPostList.get(keys.get(position))).getUsername());
        holder.getText().setText((CharSequence)((Post)this.mPostList.get(keys.get(position))).getText());
        Picasso.get().load(((Post)this.mPostList.get(keys.get(position))).getPhoto()).into(holder.getPhoto());




    //start people going activity pass event to the class
        Bundle b = new Bundle();
        b.putString("event",(MyViewHolder.getmCardView().getTag().toString())); //Your i


        final Intent intent = new Intent(mContext,PeopleGoing.class);

        //Toast.makeText(mContext,"button works", Toast.LENGTH_SHORT).show();
        intent.putExtras(b);


        holder.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               mContext.startActivity(intent);
            }
        });

        holder.getGoingBtn().setOnClickListener(new View.OnClickListener() {
            String profile_email = ActivityLoginEmail.getEmailStr();

            @Override
            public void onClick(View v) {
                //need to identify each post by its location
                holder.mCardView.setTag(keys.get(holder.getAdapterPosition()));


            // get all users  going to event in database
                mDocRef = FirebaseFirestore.getInstance().document("events/" + holder.mCardView.getTag()+ "/atendees/" + profile_email );

                mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                           HashMap<String, Object> dataToSave = new HashMap<>();
                            dataToSave.put("yes", profile_email);
                            // dataToSave.put("email", profile_email);
                            mDocRef.update(dataToSave);
                        } else {
                            HashMap<String, Object> dataToSave = new HashMap<>();
                            dataToSave.put("yes", profile_email);
                            // dataToSave.put("email", profile_email);
                            mDocRef.set(dataToSave);
                        }
                    }
                });
            }






    });
    }
    public MyAdapater(LinkedHashMap<String, Post> mPostList){
    this.mPostList = mPostList;
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }
    public MyAdapater(LinkedHashMap postList, Context context){
        mPostList = postList;
        mContext = context;

    };

    public  static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView;
        public TextView mTextView1;
        public Button mButton;
        public Button goingBtn;
        public static CardView mCardView;
        private DocumentReference mDocRef = RegisterActivity.getmDocRef();

        public static CardView getmCardView() {
            return mCardView;
        }


        public void setmCardView(CardView mCardView) {
            this.mCardView = mCardView;
        }

        public final Button getButton() {
            return this.mButton;
        }

        public Button getGoingBtn() {
            return goingBtn;
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
            mTextView1= itemView.findViewById(R.id.text);
            mButton = itemView.findViewById(R.id.friends);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            ArrayList<String> atendees = new ArrayList<String>();
            setmCardView(mCardView);


            goingBtn = (Button) itemView.findViewById(R.id.going_button);

            //mCardView.setTag(getLayoutPosition());

            itemView.findViewById(R.id.friends).setOnClickListener(new View.OnClickListener() {
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
