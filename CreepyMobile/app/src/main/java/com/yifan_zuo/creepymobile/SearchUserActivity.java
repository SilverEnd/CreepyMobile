package com.yifan_zuo.creepymobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.yifan_zuo.creepymobile.model.UserProfile;
import com.yifan_zuo.creepymobile.utils.Constants;
import com.yifan_zuo.creepymobile.utils.HttpHelper;

import net.londatiga.android.instagram.Instagram;
import net.londatiga.android.instagram.InstagramRequest;
import net.londatiga.android.instagram.InstagramSession;
import net.londatiga.android.instagram.InstagramUser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;


public class SearchUserActivity extends ActionBarActivity {


    // Twitter API support
    private Twitter mTwitter;

    // Instagram API support
    private Instagram mInstagram;
    private InstagramSession mInstagramSession;


    // private variables
    private EditText mEditField;
    private CheckBox mFlCheckBox, mTwCheckBox, mInstCheckBox;
    private Button mSearchBtn;
    private boolean[] mPlatforms;
    private InputMethodManager mInputMethodManager;

    private ProgressDialog mProgressDialog;

    private ArrayList<UserProfile> mUserList;

    private ArrayList<AsyncTask> mTasks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_user);

        init();
    }

    private void init() {
        mEditField = (EditText) findViewById(R.id.edit_field);
        mFlCheckBox = (CheckBox) findViewById(R.id.platform_flickr);
        mTwCheckBox = (CheckBox) findViewById(R.id.platform_twitter);
        mInstCheckBox = (CheckBox) findViewById(R.id.platform_instagram);
        mSearchBtn = (Button) findViewById(R.id.search_btn);

        mInputMethodManager = (InputMethodManager) getSystemService
                (Context.INPUT_METHOD_SERVICE);
        mProgressDialog = new ProgressDialog(this);
        mTasks = new ArrayList<>();

        mPlatforms = new boolean[3];
        mUserList = new ArrayList<>();

        twInit();
        instInit();

        mFlCheckBox.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mPlatforms[Constants.FLICKR] = isChecked;
            }
        });

        mTwCheckBox.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mPlatforms[Constants.TWITTER] = isChecked;
            }
        });

        mInstCheckBox.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mPlatforms[Constants.INSTAGRAM] = isChecked;

                if (isChecked && mInstagramSession.isActive() != true) {

                    mInstagram.authorize(new Instagram.InstagramAuthListener() {
                        @Override
                        public void onSuccess(InstagramUser user) {

                        }

                        @Override
                        public void onError(String error) {

                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            }
        });


        mUserList = new ArrayList<>();

        mInputMethodManager.hideSoftInputFromWindow(mEditField.getWindowToken
                (), 0);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditField.getText().length() > 0) {
                    mProgressDialog.setProgressStyle(ProgressDialog
                            .STYLE_SPINNER);
                    mProgressDialog.setTitle("Progress");
                    mProgressDialog.setMessage("Fetching users");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setCancelable(false);

                    mUserList.clear();
                    mInputMethodManager.hideSoftInputFromWindow(mEditField
                            .getWindowToken(), 0);

                    for (int i = 0; i < mPlatforms.length; i++) {
                        if (mPlatforms[i]) {
                            FindUserTask findUserTask = new FindUserTask
                                    (SearchUserActivity.this, i);
                            findUserTask.executeOnExecutor(AsyncTask
                                    .THREAD_POOL_EXECUTOR);
                        }
                    }
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder
                            (SearchUserActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Warning!");
                    builder.setMessage("Please enter the username!");
                    builder.setNeutralButton("OK", new DialogInterface
                            .OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
                    builder.show();
                }
            }
        });
    }


    // Initialization for different platforms


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

    private class FindUserTask extends AsyncTask<String, Integer, Integer> {

        private Context context;
        private int platform;


        public FindUserTask(Context context, int platform) {
            this.context = context;
            this.platform = platform;


            if (mTasks.size() == 0) {
                mProgressDialog.show();
            }
            mTasks.add(this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            UserProfile user = null;

            switch (platform) {
                case Constants.FLICKR:
                    user = getFlUsers(mEditField
                            .getText().toString().replaceAll(" ", "+"));
                    if (user != null)
                        mUserList.add(user);
                    break;
                case Constants.TWITTER:
                    user = getTwUsers(mTwitter,
                            mEditField.getText().toString());
                    if (user != null)
                        mUserList.add(user);
                    break;
                case Constants.INSTAGRAM:
                    user = getInstUsers(mInstagramSession,
                            mEditField.getText().toString());
                    if (user != null)
                        mUserList.add(user);
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
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                if (mUserList.size() <= 0) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder
                            (context);
                    builder.setCancelable(false);
                    builder.setTitle("Warning!");
                    builder.setMessage("The username entered do not match any" +
                            " users! Please try again !");
                    builder.setNeutralButton("OK", new DialogInterface
                            .OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
                    builder.show();
                } else {
                    Intent intent = new Intent(SearchUserActivity.this,
                            UserProfileActivity.class);

                    intent.putParcelableArrayListExtra("Users", mUserList);
                    startActivity(intent);
                }
            }


        }


    }


    private UserProfile getFlUsers(String username) {

        String findUserUrl = Constants.FLICKR_QUERY_URL + Constants
                .FLICKR_SEARCH_USER_BY_NAME + username +
                Constants.FLICKR_QUERY_TAIL;

        String findUserResponse = HttpHelper.getData(findUserUrl);


        Log.e(Constants.DEBUG_TAG, findUserUrl);

        try {
            JSONObject data = new JSONObject(findUserResponse);

            if (data.getString("stat").equals("ok")) {
                String id = data.getJSONObject("user").getString("nsid");

                String getInfoUrl = Constants.FLICKR_QUERY_URL + Constants
                        .FLICKR_GET_INFO + id + Constants.FLICKR_QUERY_TAIL;
                String getInfoResponse = HttpHelper.getData(getInfoUrl);
                JSONObject person = new JSONObject(getInfoResponse)
                        .getJSONObject
                                ("person");

                // Get user icon url
                String iconUrl = "";
                String iconServer = person.getString("iconserver");
                String iconFarm = person.getString("iconfarm");


                if (Integer.valueOf(iconFarm) > 0) {
                    iconUrl = "https://farm" + iconFarm + ".staticflickr" +
                            ".com/" + iconServer + "/buddyicons/" + id + ".jpg";
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

                return new UserProfile(userName, fullName, iconUrl, id,
                        Constants.FLICKR);
            }


        } catch (JSONException e) {
            Log.e(Constants.DEBUG_TAG, e.toString());
            e.printStackTrace();
        }

        return null;
    }

    private UserProfile getTwUsers(Twitter twitter, String username) {

        try {
            ResponseList<User> users = twitter.searchUsers(username, 1);

            for (User user : users) {
                if (user.isGeoEnabled()) {
                    return new UserProfile(user.getScreenName(),
                            user.getName(),
                            user.getOriginalProfileImageURL(),
                            String.valueOf(user.getId()), Constants
                            .TWITTER);
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return null;
    }

    private UserProfile getInstUsers(InstagramSession instagramSession,
                                     String username) {

        InstagramRequest request = new InstagramRequest
                (instagramSession.getAccessToken());

        List<NameValuePair> params = new ArrayList<NameValuePair>
                (2);

        params.add(new BasicNameValuePair("q", username));
        params.add(new BasicNameValuePair("count", "1"));

        try {
            String response = request.createRequest
                    ("GET", "/users/search", params);

            JSONArray data = new JSONObject(response).getJSONArray("data");

            if (data.length() > 0) {
                JSONObject o = data.getJSONObject(0);


                return new UserProfile(o.getString("username"),
                        o.getString("full_name"), o.getString
                        ("profile_picture"),
                        o.getString("id"), Constants.INSTAGRAM);
            }


        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }


}
