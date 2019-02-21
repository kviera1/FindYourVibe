package com.example.eventfeed.profiles;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.eventfeed.R;
import com.example.eventfeed.R.id;
import com.example.eventfeed.eventFeed.InviteSent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;


public final class Profile_guy1 extends AppCompatActivity {
    @NotNull
    protected NavigationView navigationView;
    private HashMap _$_findViewCache;

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
        this.setContentView(R.layout.profile_main3);
        this.setSupportActionBar((Toolbar)this._$_findCachedViewById(id.toolbar));
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar == null) {
            Intrinsics.throwNpe();
        }

        Intrinsics.checkExpressionValueIsNotNull(actionBar, "actionBar!!");
        actionBar.setTitle((CharSequence)"Profile");

        Button button = (Button) findViewById(id.invite_button_guy1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile_guy1.this,InviteSent.class));
            }
        });
    }

    public boolean onCreateOptionsMenu(@NotNull Menu menu) {
        Intrinsics.checkParameterIsNotNull(menu, "menu");
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        Intrinsics.checkParameterIsNotNull(item, "item");
        boolean var10000;
        switch(item.getItemId()) {
            case id.action_settings:
                var10000 = true;
                break;
            default:
                var10000 = super.onOptionsItemSelected(item);
        }

        return var10000;
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
