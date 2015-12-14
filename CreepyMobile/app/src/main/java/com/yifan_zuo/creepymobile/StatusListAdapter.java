package com.yifan_zuo.creepymobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yifan_zuo.creepymobile.model.UserPost;
import com.yifan_zuo.creepymobile.model.UserProfile;
import com.yifan_zuo.creepymobile.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by YifanZuo on 20/03/15.
 */


public class StatusListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> platforms;
    private HashMap<String, ArrayList<UserPost>> posts;
    private HashMap<String, UserProfile> users;


    public StatusListAdapter(Context context, ArrayList<String> platforms,
                             HashMap<String, ArrayList<UserPost>> posts,
                             HashMap<String, UserProfile> users) {
        this.context = context;
        this.platforms = platforms;
        this.posts = posts;
        this.users = users;
    }

    @Override
    public int getGroupCount() {
        return this.platforms.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.posts.get(this.platforms.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.platforms.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.posts.get(this.platforms.get(groupPosition)).get
                (childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View
            convertView, ViewGroup parent) {
        String group = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_parent, null);
        }

        TextView tv = (TextView) convertView
                .findViewById(R.id.group_title);
        tv.setText(group);

        ImageView iv = (ImageView) convertView.findViewById(R.id.group_icon);

        if (group.equals("Flickr")) {
            iv.setImageResource(R.drawable.flickr_icon);
        } else if (group.equals("Twitter")) {
            iv.setImageResource(R.drawable.twitter_icon);
        } else if (group.equals("Instagram")) {
            iv.setImageResource(R.drawable.instagram_icon);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean
            isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.status_list_item, null);
        }


        UserPost post = (UserPost) getChild(groupPosition, childPosition);

        ImageView userIcon = (ImageView) convertView.findViewById(R.id
                .user_icon);
        TextView userName = (TextView) convertView.findViewById(R.id.username);
        TextView content = (TextView) convertView
                .findViewById(R.id.status_content);
        ImageView img = (ImageView) convertView.findViewById(R.id
                .status_img);

        userName.setText(users.get(getGroup(groupPosition)).getFullName());
        content.setText(post.getTitle());

        Picasso.with(context).load(users.get(getGroup(groupPosition))
                .getProfilePicture()).into(userIcon);

        if (!post.getThumbnail().equals("")) {
            img.setVisibility(View.VISIBLE);
            Picasso.with(context).load(post.getThumbnail()).into(img);
        } else {
            img.setVisibility(View.GONE);
        }


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
