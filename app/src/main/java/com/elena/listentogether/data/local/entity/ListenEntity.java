package com.elena.listentogether.data.local.entity;

import com.google.gson.annotations.SerializedName;

public class ListenEntity {
    @SerializedName("id")
    private Long id;
    @SerializedName("user")
    private UserEntity user;
    @SerializedName("room")
    private RoomEntity room;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
