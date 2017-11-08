package com.logitech.lip.profile;

import android.os.Parcel;
import android.os.Parcelable;

public class SocialProfile implements Parcelable{

    /*** Id of  person from chosen social network.*/
    public String id;

    public String firstName;

    public String lastName;

    /*** Name of  person from social network.*/
    public String name;

    /*** Profile picture url of  person*/
    public String imgUrl;

    /*** Profile URL of person */
    public String profileURL;

    /*** Email of person*/
    public String email;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public String getEmail() {
        return email;
    }

    public static final Parcelable.Creator<SocialProfile> CREATOR
            = new Parcelable.Creator<SocialProfile>() {
        public SocialProfile createFromParcel(Parcel in) {
            return new SocialProfile(in);
        }

        public SocialProfile[] newArray(int size) {
            return new SocialProfile[size];
        }
    };


    private SocialProfile(Parcel in) {
        id = in.readString();
        name = in.readString();
        imgUrl = in.readString();
        profileURL = in.readString();
        email = in.readString();
    }

    public SocialProfile(String userId, String userName) {
        id = userId;
        name = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(imgUrl);
        dest.writeString(profileURL);
        dest.writeString(email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SocialProfile that = (SocialProfile) o;

        if (imgUrl != null ? !imgUrl.equals(that.imgUrl) : that.imgUrl != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(profileURL != null ? !profileURL.equals(that.profileURL) : that.profileURL != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (profileURL != null ? profileURL.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (imgUrl != null ? imgUrl.hashCode() : 0);
        return result;
    }
}
