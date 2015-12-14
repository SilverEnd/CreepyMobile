package com.yifan_zuo.creepymobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yifan_zuo.creepymobile.model.UserPost;
import com.yifan_zuo.creepymobile.model.UserProfile;
import com.yifan_zuo.creepymobile.utils.Constants;
import com.yifan_zuo.creepymobile.utils.HttpHelper;

import net.londatiga.android.instagram.Instagram;
import net.londatiga.android.instagram.InstagramRequest;
import net.londatiga.android.instagram.InstagramSession;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


public class UserProfileActivity extends ActionBarActivity {

    // Twitter API support
    private Twitter mTwitter;

    // Instagram API support
    private Instagram mInstagram;
    private InstagramSession mInstagramSession;


    private ImageView mFlProfileImg, mTwProfileImg, mInstProfileImg;
    private TextView mFlName, mTwName, mInstName;
    private Button mShowMapBtn;
    private ExpandableListView mStatusesListView;
    private ArrayList<UserProfile> mUserList;

    private ArrayList<UserPost> mFlPosts, mTwPosts, mInstPosts;
    private ArrayList<String> mPlatforms;
    private HashMap<String, ArrayList<UserPost>> mPosts;
    private HashMap<String, UserProfile> mUsers;


    private StatusListAdapter mAdapter;

    private ArrayList<AsyncTask> mTasks;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        init();
    }

    private void init() {
        mFlProfileImg = (ImageView) findViewById(R.id.flickr_profile_img);
        mTwProfileImg = (ImageView) findViewById(R.id.twitter_profile_img);
        mInstProfileImg = (ImageView) findViewById(R.id.inst_profile_img);

        mFlName = (TextView) findViewById(R.id.flickr_name);
        mTwName = (TextView) findViewById(R.id.twitter_name);
        mInstName = (TextView) findViewById(R.id.inst_name);

        mShowMapBtn = (Button) findViewById(R.id.show_map_btn);
        mStatusesListView = (ExpandableListView) findViewById(R.id
                .user_posts_list);

        mFlPosts = new ArrayList<>();
        mTwPosts = new ArrayList<>();
        mInstPosts = new ArrayList<>();
        mPlatforms = new ArrayList<>();
        mPosts = new HashMap<>();
        mUsers = new HashMap<>();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("Progress");
        mProgressDialog.setMessage("Fetching user's geotagged posts");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);

        mUserList = getIntent().getParcelableArrayListExtra("Users");


        // Setup group data
        mPlatforms.add("Flickr");
        mPlatforms.add("Twitter");
        mPlatforms.add("Instagram");

        for (int i = 0; i < mUserList.size(); i++) {
            UserProfile user = mUserList.get(i);
            mUsers.put(mPlatforms.get(user.getPlatform()), user);

            switch (user.getPlatform()) {
                case Constants.FLICKR:
                    mFlName.setText(user.getFullName());
                    Picasso.with(this).load(user.getProfilePicture()).into
                            (mFlProfileImg);
                    break;
                case Constants.TWITTER:
                    mTwName.setText(user.getFullName());
                    Picasso.with(this).load(user.getProfilePicture()).into
                            (mTwProfileImg);
                    break;
                case Constants.INSTAGRAM:
                    mInstName.setText(user.getFullName());
                    Picasso.with(this).load(user.getProfilePicture()).into
                            (mInstProfileImg);
                    break;
                default:
                    break;
            }
        }

        mTasks = new ArrayList<>();

        twInit();
        instInit();
        fetchPosts();

        mStatusesListView.setOnChildClickListener(new ExpandableListView
                .OnChildClickListener() {


            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition,
                                        long id) {
                Intent intent = new Intent(UserProfileActivity.this,
                        UserPostActivity.class);
                UserPost post = mPosts.get(mPlatforms.get
                        (groupPosition)).get(childPosition);

                intent.putExtra("Post", post);

                for (UserProfile user : mUserList) {
                    if (user.getPlatform() == post.getPlatform()) {
                        intent.putExtra("User", user);
                    }
                }

                startActivity(intent);

                return true;
            }
        });


        mShowMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this,
                        AllPostsOnMapActivity.class);
                ArrayList<UserPost> allPosts = new ArrayList<>();
                allPosts.addAll(mFlPosts);
                allPosts.addAll(mTwPosts);
                allPosts.addAll(mInstPosts);

                i.putParcelableArrayListExtra("AllPosts", allPosts);
                i.putParcelableArrayListExtra("AllUsers", mUserList);
                startActivity(i);
            }
        });

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

    private void fetchPosts() {
        mFlPosts.clear();
        mTwPosts.clear();
        mInstPosts.clear();

        for (UserProfile user : mUserList) {
            FindStatusTask findStatusTask = new FindStatusTask(this, user);
            findStatusTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    private class FindStatusTask extends AsyncTask<String, Integer, Integer> {

        private Context context;
        private UserProfile user;

        public FindStatusTask(Context context, UserProfile user) {
            this.context = context;
            this.user = user;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mTasks.size() == 0) {
                mProgressDialog.show();
            }
            mTasks.add(this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            switch (user.getPlatform()) {
                case Constants.FLICKR:
                    mFlPosts.addAll(getFlStatus(user));
                    break;
                case Constants.TWITTER:
                    mTwPosts.addAll(getTwStatus(user));
                    break;
                case Constants.INSTAGRAM:
                    mInstPosts.addAll(getInstStatus(user));
                    break;
                default:
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            mTasks.remove(this);

            if (mTasks.size() == 0) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (mFlPosts.size() + mTwPosts.size() + mInstPosts.size() <=
                        0) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder
                            (context);
                    builder.setCancelable(false);
                    builder.setTitle("Warning!");
                    builder.setMessage("The user do not have any geotagged " +
                            "posts");
                    builder.setNeutralButton("OK", new DialogInterface
                            .OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(UserProfileActivity.this,
                                    SearchUserActivity.class));
                        }

                    });
                    builder.show();
                } else {
                    // Setup child data
                    mPosts.put(mPlatforms.get(0), mFlPosts);
                    mPosts.put(mPlatforms.get(1), mTwPosts);
                    mPosts.put(mPlatforms.get(2), mInstPosts);

                    // Setup adapter
                    mAdapter = new StatusListAdapter(UserProfileActivity.this,
                            mPlatforms, mPosts, mUsers);
                    mStatusesListView.setAdapter(mAdapter);
                }
            }


        }


    }

    private ArrayList getFlStatus(UserProfile user) {
        ArrayList<UserPost> flPosts = new ArrayList<>();

        String getPhotosUrl = Constants.FLICKR_QUERY_URL + Constants
                .FLICKR_GET_PHOTOLIST + user.getId() + Constants
                .FLICKR_EXTRAS + Constants.FLICKR_QUERY_TAIL;
        String photosResponse = HttpHelper.getData(getPhotosUrl);

        try {
            JSONArray photoArray = new JSONObject(photosResponse)
                    .getJSONObject
                            ("photos").getJSONArray("photo");

            Log.e(Constants.DEBUG_TAG, getPhotosUrl);


            for (int i = 0; i < photoArray.length(); i++) {
                JSONObject photo = photoArray.getJSONObject(i);

                if (!(photo.getString("latitude").equals("0") && photo
                        .getString("longitude").equals("0") && photo
                        .getString("accuracy").equals("0"))) {

                    String ownerId = photo.getString("owner");
                    String id = photo.getString("id");
                    String farm = photo.getString("farm");
                    String server = photo.getString("server");
                    String secret = photo.getString("secret");

                    String photoUrl = "https://farm" + farm + "" +
                            ".staticflickr" +
                            ".com/" + server + "/" + id +
                            "_" + secret + "_n.jpg";


                    flPosts.add(new UserPost(photo.getString("id"), ownerId,
                            photo.getString("title"), photoUrl,
                            Double.valueOf(photo.getString("latitude")),
                            Double.valueOf(photo.getString("longitude")),
                            Constants.FLICKR));

                }
            }

        } catch (JSONException e) {
            Log.e(Constants.DEBUG_TAG, "Error :   " + e.toString());
            e.printStackTrace();
        }

        return flPosts;
    }

    private ArrayList getTwStatus(UserProfile user) {
        ArrayList<UserPost> twPosts = new ArrayList<>();

        try {
            Paging paging = new Paging(1, 1000);

            ResponseList<twitter4j.Status> statuses = mTwitter
                    .getUserTimeline(Long.parseLong(user
                            .getId
                                    ()), paging);

            for (twitter4j.Status status : statuses) {
                if (status.getGeoLocation() != null) {

                    twPosts.add(new UserPost(String.valueOf(status
                            .getId()), String.valueOf(status.getUser().getId
                            ()), status.getText(), "", status.getGeoLocation
                            ().getLatitude(), status.getGeoLocation()
                            .getLongitude(), Constants.TWITTER));
                }
            }

        } catch (TwitterException e) {
            Log.e(Constants.DEBUG_TAG, e.toString());
            e.printStackTrace();
        }

        return twPosts;
    }

    private ArrayList getInstStatus(UserProfile user) {
        ArrayList<UserPost> instPosts = new ArrayList<>();

        InstagramRequest request = new InstagramRequest
                (mInstagramSession.getAccessToken());

        List<NameValuePair> params = new ArrayList<NameValuePair>
                ();

        params.add(new BasicNameValuePair("count", "200"));


        try {
            String response = request.createRequest
                    ("GET", "/users/" + user.getId() + "/media/recent",
                            params);

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

                    instPosts.add(new UserPost(id, ownerId,
                            content, thumbnail,
                            latitude, longitude, Constants.INSTAGRAM));

                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return instPosts;
    }


}
