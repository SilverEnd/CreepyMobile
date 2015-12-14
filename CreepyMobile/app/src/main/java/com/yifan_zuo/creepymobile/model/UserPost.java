package com.yifan_zuo.creepymobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by YifanZuo on 21/03/15.
 */
public class UserPost implements Parcelable {

    private String id;
    private String ownerId;
    private String title;
    private String thumbnail;
    private double latitude;
    private double longitude;
    private int platform;

    public UserPost(Parcel source) {
        this.id = source.readString();
        this.ownerId = source.readString();
        this.title = source.readString();
        this.thumbnail = source.readString();
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
        this.platform = source.readInt();
    }

    public UserPost(String id, String ownerId, String title, String
            thumbnail, double latitude, double longitude, int platform) {
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.latitude = latitude;
        this.longitude = longitude;
        this.platform = platform;
    }

    public static final Creator<UserPost> CREATOR = new Creator<UserPost>
            () {


        @Override
        public UserPost createFromParcel(Parcel source) {

            return new UserPost(source);
        }

        @Override
        public UserPost[] newArray(int size) {
            return new UserPost[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ownerId);
        dest.writeString(title);
        dest.writeString(thumbnail);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(platform);
    }


    // Getters


    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }


    public String getThumbnail() {
        return thumbnail;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getPlatform() {
        return platform;
    }
}
