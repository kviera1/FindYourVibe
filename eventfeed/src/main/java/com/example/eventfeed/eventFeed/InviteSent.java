package com.example.eventfeed.eventFeed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.example.eventfeed.R;
//import android.widget.Toolbar;


public class InviteSent extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_sent_layout);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;


        getWindow().setLayout((int)(width*.86),(int)(height*.17));

    }
}
