package com.elena.listentogether.data.local.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RoomEntity implements Parcelable {
   // @SerializedName("id_room")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("creator")
    private UserEntity author;
  //  @SerializedName("creation_date")
    private String creationDate;
  //  @SerializedName("members_count")
    private int membersCount;
    @SerializedName("icon")
    private String iconPath;
//    @SerializedName()
//    private int tracksCount;
 //   @SerializedName("last_song")
    private String lastSong;
    @SerializedName("source")
    private String source;
    @SerializedName("password")
    private String password;

    private int songsCount;



    protected RoomEntity(Parcel in) {
        id = in.readLong();
        name = in.readString();
        author = in.readParcelable(UserEntity.class.getClassLoader());
        creationDate = in.readString();
        membersCount = in.readInt();
        iconPath = in.readString();
        lastSong = in.readString();
        source = in.readString();
        password = in.readString();
        songsCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeParcelable(author, flags);
        dest.writeString(creationDate);
        dest.writeInt(membersCount);
        dest.writeString(iconPath);
        dest.writeString(lastSong);
        dest.writeString(source);
        dest.writeString(password);
        dest.writeInt(songsCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RoomEntity> CREATOR = new Creator<RoomEntity>() {
        @Override
        public RoomEntity createFromParcel(Parcel in) {
            return new RoomEntity(in);
        }

        @Override
        public RoomEntity[] newArray(int size) {
            return new RoomEntity[size];
        }
    };

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoomEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getLastSong() {
        return lastSong;
    }

    public void setLastSong(String lastSong) {
        this.lastSong = lastSong;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public int getSongsCount() {
        return songsCount;
    }

    public void setSongsCount(int songsCount) {
        this.songsCount = songsCount;
    }
}
