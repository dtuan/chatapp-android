package ai.resola.chatapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Created by dotuan on 2017/03/24.
 */

public class User implements IUser, Parcelable {

    private String id;
    private String name;
    private String avatar;

    private String uniqueId;

    public User(String uniqueId, String id, String name, String avatar) {
        this.uniqueId = uniqueId;
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    private User(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.avatar = in.readString();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(avatar);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[0];
        }
    };
}
