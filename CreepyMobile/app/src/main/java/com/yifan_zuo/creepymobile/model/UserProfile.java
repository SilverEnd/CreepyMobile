package com.yifan_zuo.creepymobile.model;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by YifanZuo on 20/03/15.
 */

public class UserProfile implements Parcelable {

    private String username;

    private String fullName;

    private String profilePicture;

    private String id;

    private int platform;

    public UserProfile(Parcel source) {
        this.username = source.readString();
        this.fullName = source.readString();
        this.profilePicture = source.readString();
        this.id = source.readString();
        this.platform = source.readInt();
    }


    public UserProfile(String username, String fullName,
                       String profilePicture, String id, int platform) {
        this.username = username;
        this.fullName = fullName;
        this.profilePicture = profilePicture;
        this.id = id;
        this.platform = platform;
    }

    public static final Creator<UserProfile> CREATOR = new
            Creator<UserProfile>() {


                @Override
                public UserProfile createFromParcel(Parcel source) {
                    return new UserProfile(source);
                }

                @Override
                public UserProfile[] newArray(int size) {
                    return new UserProfile[size];
                }
            };

    public UserProfile() {

    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(fullName);
        dest.writeString(profilePicture);
        dest.writeString(id);
        dest.writeInt(platform);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getId() {
        return id;
    }

    public int getPlatform() {
        return platform;
    }
}
