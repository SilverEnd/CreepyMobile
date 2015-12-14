package com.yifan_zuo.creepymobile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yifan_zuo.creepymobile.model.UserProfile;
import com.yifan_zuo.creepymobile.utils.Constants;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by YifanZuo on 20/03/15.
 */


public class UserListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<UserProfile> users;


    public UserListAdapter(Context context, ArrayList<UserProfile> users) {
        this.context = context;
        this.users = users;
    }


    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public UserProfile getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout
                    .user_list_item, null);
            mViewHolder = new MyViewHolder();

            mViewHolder.usrName = (TextView) convertView.findViewById(R.id
                    .usr_name);
            mViewHolder.usrIcon = (ImageView) convertView.findViewById(R.id
                    .usr_icon);
            mViewHolder.platformIcon = (ImageView) convertView.findViewById(R
                    .id.platform_icon);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.usrName.setText(users.get(position).getUsername());

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadImageFromURL(users.get(position).getProfilePicture(),
                        mViewHolder);
            }
        }).start();

        switch (users.get(position).getPlatform()) {
            case Constants.FLICKR:
                mViewHolder.platformIcon.setImageResource(R.drawable
                        .flickr_icon);
                break;
            case Constants.TWITTER:
                mViewHolder.platformIcon.setImageResource(R.drawable
                        .twitter_icon);
                break;
            case Constants.INSTAGRAM:
                mViewHolder.platformIcon.setImageResource(R.drawable
                        .instagram_icon);
                break;
        }


        return convertView;
    }

    /**
     * A view holder to hold different views in one list item
     *
     * @author Yifan Zuo
     */
    private class MyViewHolder {
        ImageView usrIcon;
        TextView usrName;
        ImageView platformIcon;
    }

    private void loadImageFromURL(String url, MyViewHolder holder) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable draw = Drawable.createFromStream(is, "src");

            holder.usrIcon.setImageDrawable(draw);

        } catch (Exception e) {
            //TODO handle error
            Log.i("loadingImg", e.toString());
        }
    }


}
