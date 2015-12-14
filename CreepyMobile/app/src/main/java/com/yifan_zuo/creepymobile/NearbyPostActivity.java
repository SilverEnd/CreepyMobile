package com.yifan_zuo.creepymobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.squareup.picasso.Picasso;
import com.yifan_zuo.creepymobile.model.UserPost;
import com.yifan_zuo.creepymobile.model.UserProfile;
import com.yifan_zuo.creepymobile.utils.Constants;
import com.yifan_zuo.creepymobile.utils.HttpHelper;

import net.londatiga.android.instagram.Instagram;
import net.londatiga.android.instagram.InstagramRequest;
import net.londatiga.android.instagram.InstagramSession;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;


public class NearbyPostActivity extends ActionBarActivity {

    // Twitter API support
    private Twitter mTwitter;

    // Instagram API support
    private Instagram mInstagram;
    private InstagramSession mInstagramSession;


    private GoogleMap mMap;
    private UserPost mPost;
    private ArrayList<UserPost> mPosts;
    private ArrayList<Marker> mMarkers;
    private HashMap<UserPost, UserProfile> mPostUser;
    private HashMap<Marker, UserPost> mMarkerPost;
    private LatLng mMyLocation;
    private double radius;


    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_post);

        init();
    }


    private void init() {
        radius = 0.05;

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("Progress");
        mProgressDialog.setMessage("Fetching nearby geotagged posts");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id
                .map_nearby)).getMap();
        mMarkerPost = new HashMap<>();
        mPosts = new ArrayList<>();
        mMarkers = new ArrayList<>();
        mPostUser = new HashMap<>();

        mPost = getIntent().getParcelableExtra("Post");

        // Setup map view
        mMap.setMyLocationEnabled(true);

        twInit();
        instInit();

        NearbyPostsTask nearbyPostsTask = new NearbyPostsTask();
        nearbyPostsTask.execute();

    }


    private void twInit() {
        mTwitter = new TwitterFactory().getInstance();
        mTwitter.setOAuthConsumer(Constants.TW_CONSUMER_KEY,
                Constants.TW_CONSUMER_SECRET);
        AccessToken accessToken = new AccessToken(Constants.TW_ACCESS_TOKEN,
                Constants.TW_ACCESS_TOKEN_SECRET);
        mTwitter.setOAuthAccessToken(accessToken);
    }

    private void instInit() {
        mInstagram = new Instagram(this, Constants.INST_CLIENT_ID,
                Constants.INST_CLIENT_SECRET,
                Constants.INST_CALLBACK_URL);
        mInstagramSession = mInstagram.getSession();

    }


    private class NearbyPostsTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mPost != null) {
                mPosts.addAll(getNearByPosts(new LatLng(mPost.getLatitude(),
                        mPost.getLongitude())));
            } else {
                LocationManager locationManager = (LocationManager)
                        NearbyPostActivity.this.getSystemService
                                (LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);

                String provider = locationManager.getBestProvider(criteria,
                        true);

                Location location = locationManager.getLastKnownLocation
                        (provider);

                mMyLocation = new LatLng(location.getLatitude
                        (), location.getLongitude());

                mPosts.addAll(getNearByPosts(mMyLocation));
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e(Constants.DEBUG_TAG, "OnPostExe");

            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }


            if (mPosts.size() > 0) {

                for (UserPost post : mPosts) {
                    Marker marker = mMap.addMarker(new MarkerOptions().position
                            (new LatLng(post.getLatitude(), post.getLongitude
                                    ())).title(post.getTitle()));

                    mMarkers.add(marker);
                    mMarkerPost.put(marker, post);
                }

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View v = getLayoutInflater().inflate(R.layout
                                .info_window, null);

                        UserPost post = mMarkerPost.get(marker);
                        UserProfile user = mPostUser.get(post);


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


                        title.setText(post.getTitle());

                        if (!post.getThumbnail().equals("")) {
                            img.setVisibility(View.VISIBLE);
                            Picasso.with(NearbyPostActivity.this).load(post
                                    .getThumbnail()).into(img);
                        } else {
                            img.setVisibility(View.GONE);
                        }

                        if (user != null) {
                            username.setText(user.getFullName());

                            Picasso.with(NearbyPostActivity.this).load(user
                                    .getProfilePicture()).into(userIcon);

                            switch (user.getPlatform()) {
                                case Constants.FLICKR:
                                    Picasso.with(NearbyPostActivity.this).load(R
                                            .drawable.flickr_icon).into
                                            (platform);
                                    break;
                                case Constants.TWITTER:
                                    Picasso.with(NearbyPostActivity.this).load(R
                                            .drawable.twitter_icon).into
                                            (platform);
                                    break;
                                case Constants.INSTAGRAM:
                                    Picasso.with(NearbyPostActivity.this).load(R
                                            .drawable.instagram_icon).into
                                            (platform);
                                    break;
                                default:
                                    break;
                            }
                        }


                        return v;
                    }
                });

                for (Marker marker : mMarkers) {
                    marker.showInfoWindow();
                    marker.hideInfoWindow();
                }


                if (mPost != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                            LatLng(mPost.getLatitude(), mPost.getLongitude())
                            , 19));
                } else {
                    if (mMyLocation != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom
                                (mMyLocation, 19));
                    }
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap
                        .OnInfoWindowClickListener() {


                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        UserPost post = mMarkerPost.get(marker);
                        UserProfile user = mPostUser.get(post);

                        if (post != null && user != null) {
                            Intent intent = new Intent(NearbyPostActivity
                                    .this, UserPostActivity.class);

                            intent.putExtra("Post", post);
                            intent.putExtra("User", user);

                            startActivity(intent);
                        }
                    }
                });


            } else {

            }

        }
    }

    private ArrayList getNearByPosts(LatLng location) {
        final ArrayList<UserPost> posts = new ArrayList<>();

        // Flickr Posts
        SyncHttpClient flClient = new SyncHttpClient();
        String photosUrl = Constants.FLICKR_QUERY_URL + String.format
                (Constants.FLICKR_SEARCH_PHOTOS, String.valueOf(location
                        .latitude), String.valueOf(location.longitude), radius)
                + Constants.FLICKR_EXTRAS + Constants.FLICKR_QUERY_TAIL +
                Constants.FLICKR_PAGES;

        flClient.get(photosUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {

                try {
                    JSONArray photos = response.getJSONObject
                            ("photos").getJSONArray("photo");

                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject photo = photos.getJSONObject(i);

                        String id = photo.getString("id");
                        String ownerId = photo.getString("owner");
                        String farm = photo.getString("farm");
                        String server = photo.getString("server");
                        String secret = photo.getString("secret");

                        String photoUrl = "https://farm" + farm + "" + "" +
                                ".staticflickr" + ".com/" + server + "/" + id
                                + "_" +
                                secret + "_n.jpg";

                        UserPost p = new UserPost(id, ownerId, photo.getString
                                ("title"), photoUrl, Double.valueOf(photo
                                .getString
                                        ("latitude")), Double.valueOf(photo
                                .getString
                                        ("longitude")), Constants.FLICKR);
                        UserProfile user = getFlUser(ownerId);

                        mPostUser.put(p, user);
                        posts.add(p);
                    }
                } catch (JSONException e) {
                    Log.e(Constants.DEBUG_TAG, e.toString());
                    e.printStackTrace();
                }
            }
        });


        // Twitter Posts
        Query query = new Query().geoCode(new GeoLocation(location.latitude,
                location.longitude), radius, "km").count(10);


//        Log.e(Constants.DEBUG_TAG, query.toString());

        try {
            QueryResult result = mTwitter.search(query);

            List<Status> statuses = result.getTweets();


//            Log.e(Constants.DEBUG_TAG, String.valueOf(statuses.size()));


            if (statuses.size() > 0) {
                for (Status status : statuses) {
                    if (status.getGeoLocation() != null) {
                        User twUser = status.getUser();
                        UserProfile user = new UserProfile(twUser.getName(),
                                twUser.getScreenName(), twUser
                                .getOriginalProfileImageURL(), String.valueOf
                                (twUser.getId()), Constants.TWITTER);

                        UserPost p = new UserPost(String.valueOf(status.getId
                                ()), user.getId(), status.getText(), "",
                                status
                                        .getGeoLocation().getLatitude(),
                                status
                                        .getGeoLocation().getLongitude(),
                                Constants
                                        .TWITTER);

                        mPostUser.put(p, user);
                        posts.add(p);


                        Log.e(Constants.DEBUG_TAG, status.getText());
                    }

                }
            }


//            Log.e(Constants.DEBUG_TAG, result.getTweets())


        } catch (TwitterException e) {
            Log.e(Constants.DEBUG_TAG, e.toString());
            e.printStackTrace();
        }


        // Instagram Posts
        InstagramRequest request = new InstagramRequest
                (mInstagramSession.getAccessToken());
        List<NameValuePair> params = new ArrayList<NameValuePair>
                ();

        params.add(new BasicNameValuePair("lat", String.valueOf(location
                .latitude)));
        params.add(new BasicNameValuePair("lng", String.valueOf(location
                .longitude)));
        params.add(new BasicNameValuePair("distance", String.valueOf
                (radius * 1000)));


        try {
            String response = request.createRequest
                    ("GET", "/media/search", params);

            JSONArray data = new JSONObject(response).getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONObject o = (JSONObject) data.get(i);

                if (o.getString("type").equals("image") && !o.isNull
                        ("location")) {
                    String id = o.getString("id");
                    String ownerId = o.getJSONObject("user").getString("id");
                    String content = o.getJSONObject("caption").getString
                            ("text");
                    String thumbnail = o.getJSONObject("images")
                            .getJSONObject("standard_resolution").getString
                                    ("url");

                    double latitude = o.getJSONObject("location").getDouble
                            ("latitude");
                    double longitude = o.getJSONObject("location").getDouble
                            ("longitude");

                    UserPost p = new UserPost(id, ownerId, content,
                            thumbnail, latitude, longitude, Constants
                            .INSTAGRAM);
                    UserProfile user = getInstUser(mInstagramSession,
                            ownerId);

                    mPostUser.put(p, user);
                    posts.add(p);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Constants.DEBUG_TAG, e.toString());
        }


        return posts;
    }

    private UserProfile getFlUser(String userId) {

        String getInfoUrl = Constants.FLICKR_QUERY_URL + Constants
                .FLICKR_GET_INFO + userId + Constants.FLICKR_QUERY_TAIL;
        String getInfoResponse = HttpHelper.getData(getInfoUrl);

//        Log.e(Constants.DEBUG_TAG, getInfoUrl);

        try {
            JSONObject person = new JSONObject(getInfoResponse).getJSONObject
                    ("person");

            // Get user icon url
            String iconUrl = "";
            String iconServer = person.getString("iconserver");
            String iconFarm = person.getString("iconfarm");


            if (Integer.valueOf(iconFarm) > 0) {
                iconUrl = "https://farm" + iconFarm + ".staticflickr" +
                        ".com/" + iconServer + "/buddyicons/" +
                        userId + ".jpg";
            } else {
                iconUrl = "https://www.flickr.com/images/buddyicon.gif";
            }

            // Get user other info
            String userName = person.getJSONObject("username").getString
                    ("_content");

            String fullName = "";
            if (!person.isNull("realname")) {
                fullName = person.getJSONObject("realname").getString
                        ("_content");
            } else {
                if (fullName.equals(""))
                    fullName = userName;
            }

            return new UserProfile(userName, fullName, iconUrl, userId,
                    Constants.FLICKR);

        } catch (JSONException e) {
            Log.e(Constants.DEBUG_TAG, e.toString());
            e.printStackTrace();
        }

        return null;
    }

    private UserProfile getInstUser(InstagramSession instagramSession, String
            userId) {
        InstagramRequest request = new InstagramRequest
                (instagramSession.getAccessToken());
        List<NameValuePair> params = new ArrayList<NameValuePair>
                ();

        try {
            String response = request.createRequest
                    ("GET", "/users/" + userId, params);

            JSONObject data = new JSONObject(response).getJSONObject("data");

            if (data != null) {
                return new UserProfile(data.getString("username"),
                        data.getString("full_name"), data.getString
                        ("profile_picture"),
                        data.getString("id"), Constants.INSTAGRAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Constants.DEBUG_TAG, e.toString());
        }

        return null;

    }

}
