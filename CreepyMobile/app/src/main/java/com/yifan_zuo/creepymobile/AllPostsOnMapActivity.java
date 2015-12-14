package com.yifan_zuo.creepymobile;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.yifan_zuo.creepymobile.model.UserPost;
import com.yifan_zuo.creepymobile.model.UserProfile;
import com.yifan_zuo.creepymobile.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;


public class AllPostsOnMapActivity extends ActionBarActivity {

    private GoogleMap mMap;
    private ArrayList<UserPost> mPosts;
    private ArrayList<UserProfile> mUsers;
    private ArrayList<Marker> mMarkers;
    private UserProfile mFlUser, mTwUser, mInstUser;
    private HashMap<Marker, UserPost> mMarkerPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_post_on_map);

        init();

    }

    private void init() {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id
                .map_all_posts)).getMap();
        mMarkerPost = new HashMap<>();
        mMarkers = new ArrayList<>();

        mPosts = getIntent().getParcelableArrayListExtra("AllPosts");
        mUsers = getIntent().getParcelableArrayListExtra("AllUsers");

        for (UserProfile user : mUsers) {
            switch (user.getPlatform()) {
                case Constants.FLICKR:
                    mFlUser = user;
                    break;
                case Constants.TWITTER:
                    mTwUser = user;
                    break;
                case Constants.INSTAGRAM:
                    mInstUser = user;
                    break;
                default:
                    break;
            }
        }

        // Setup markers
        if (mPosts != null) {
            for (int i = 0; i < mPosts.size(); i++) {
                UserPost post = mPosts.get(i);

                Marker marker = mMap.addMarker(new MarkerOptions().position(new
                        LatLng(post.getLatitude(), post.getLongitude()))
                        .title(post.getTitle()));

                mMarkers.add(marker);
                mMarkerPost.put(marker, post);
            }
        }


        // Setup map view
        mMap.setMyLocationEnabled(true);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window,
                        null);
                UserPost post = mMarkerPost.get(marker);

                TextView username = (TextView) v.findViewById(R.id
                        .info_username);
                ImageView userIcon = (ImageView) v.findViewById(R.id
                        .info_user_icon);
                ImageView platform = (ImageView) v.findViewById(R.id
                        .info_platform);
                TextView title = (TextView) v.findViewById(R.id
                        .info_title);
                ImageView img = (ImageView) v.findViewById(R.id
                        .info_img);


                // setup posts

                title.setText(post.getTitle());

                if (!post.getThumbnail().equals("")) {
                    img.setVisibility(View.VISIBLE);
                    Picasso.with(AllPostsOnMapActivity.this).load(post
                            .getThumbnail()).into(img);
                } else {
                    img.setVisibility(View.GONE);
                }

                // setup user profile

                UserProfile user = null;

                switch (post.getPlatform()) {
                    case Constants.FLICKR:
                        Picasso.with(AllPostsOnMapActivity.this).load(R
                                .drawable.flickr_icon).into
                                (platform);
                        user = mFlUser;
                        break;
                    case Constants.TWITTER:
                        Picasso.with(AllPostsOnMapActivity.this).load(R
                                .drawable.twitter_icon).into
                                (platform);
                        user = mTwUser;
                        break;
                    case Constants.INSTAGRAM:
                        Picasso.with(AllPostsOnMapActivity.this).load(R
                                .drawable.instagram_icon).into
                                (platform);
                        user = mInstUser;
                        break;
                    default:
                        break;
                }


                if (user != null) {
                    username.setText(user.getFullName());

                    Picasso.with(AllPostsOnMapActivity.this).load(user
                            .getProfilePicture()).into(userIcon);
                }


                return v;
            }


        });


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMarkers.get(0)
                .getPosition(), 19f));


    }


}
