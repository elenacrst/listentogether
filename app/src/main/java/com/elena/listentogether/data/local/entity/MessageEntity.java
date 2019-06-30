package com.elena.listentogether.data.local.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class MessageEntity implements Parcelable {
    private Long messageId;
    private String content;
    private UserEntity user;
    private RoomEntity room;
    private Long date;

    public MessageEntity() {
    }

    protected MessageEntity(Parcel in) {
        if (in.readByte() == 0) {
            messageId = null;
        } else {
            messageId = in.readLong();
        }
        content = in.readString();
        user = in.readParcelable(UserEntity.class.getClassLoader());
        room = in.readParcelable(RoomEntity.class.getClassLoader());
        date =in.readLong();
    }

    public static final Creator<MessageEntity> CREATOR = new Creator<MessageEntity>() {
        @Override
        public MessageEntity createFromParcel(Parcel in) {
            return new MessageEntity(in);
        }

        @Override
        public MessageEntity[] newArray(int size) {
            return new MessageEntity[size];
        }
    };

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public RoomEntity getRoom() {
        return room;
    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (messageId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(messageId);
        }
        parcel.writeString(content);
        parcel.writeParcelable(user, i);
        parcel.writeParcelable(room, i);
        parcel.writeLong(date);
    }
}
