package com.yifan_zuo.creepymobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yifan_zuo.creepymobile.model.UserPost;
import com.yifan_zuo.creepymobile.model.UserProfile;
import com.yifan_zuo.creepymobile.utils.Constants;
import com.yifan_zuo.creepymobile.utils.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class UserPostActivity extends ActionBarActivity {

    private Button mNearbysBtn;
    private TextView mUsername, mPostTitle;
    private ImageView mUserIcon, mUserPlatform, mPostImg;

    private UserPost mPost;
    private UserProfile mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        init();
    }

    private void init() {

        mUsername = (TextView) findViewById(R.id.username);
        mPostTitle = (TextView) findViewById(R.id.post_title);
        mUserIcon = (ImageView) findViewById(R.id.user_icon);
        mPostImg = (ImageView) findViewById(R.id.post_img);
        mUserPlatform = (ImageView) findViewById(R.id.user_platform);


        mNearbysBtn = (Button) findViewById(R.id.show_nearby_btn);


        mPost = getIntent().getParcelableExtra("Post");
        mUser = getIntent().getParcelableExtra("User");

        if (mPost != null) {
            mUsername.setText(mUser.getFullName());
            Picasso.with(this).load(mUser.getProfilePicture()).into(mUserIcon);

            mPostTitle.setText(mPost.getTitle());


            if (!mPost.getThumbnail().equals("")) {
                mPostImg.setVisibility(View.VISIBLE);
                Picasso.with(this).load(mPost.getThumbnail()).into(mPostImg);
            } else {
                mPostImg.setVisibility(View.GONE);
            }

            switch (mPost.getPlatform()) {
                case Constants.FLICKR:
                    Picasso.with(this).load(R.drawable.flickr_icon).into
                            (mUserPlatform);
                    break;
                case Constants.TWITTER:
                    Picasso.with(this).load(R.drawable.twitter_icon).into
                            (mUserPlatform);
                    break;
                case Constants.INSTAGRAM:
                    Picasso.with(this).load(R.drawable.instagram_icon).into
                            (mUserPlatform);
                    break;
                default:
                    break;
            }
        }


        mNearbysBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserPostActivity.this,
                        NearbyPostActivity.class);
                i.putExtra("Post", mPost);
                startActivity(i);
            }
        });
    }

}
