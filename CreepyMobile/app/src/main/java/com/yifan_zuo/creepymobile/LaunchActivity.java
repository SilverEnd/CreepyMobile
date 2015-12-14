package com.yifan_zuo.creepymobile;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class LaunchActivity extends ActionBarActivity {

    private Button mSearchUserBtn, mCurrentNearbyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        init();
    }

    private void init() {
        mSearchUserBtn = (Button) findViewById(R.id.go_to_search_user_btn);
        mCurrentNearbyBtn = (Button) findViewById(R.id
                .go_to_current_nearby_btn);


        mSearchUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LaunchActivity.this,
                        SearchUserActivity.class));
            }
        });


        mCurrentNearbyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LaunchActivity.this,
                        NearbyPostActivity.class));
            }
        });
    }

}
