package com.elena.listentogether.utils;

public class Urls {
    public static final String BASE_URL = "http://73a3301b.ngrok.io/";

    public static final String GET_USERS = "groupchat/users";

    public static final String FIND_USER = "groupchat/findUser";

    public static final String ADD_ROOM = "groupchat/insertRoom";
    public static final String GET_ROOMS = "groupchat/rooms";
    public static final String INSERT_LISTEN = "groupchat/insertListen";
    public static final String GET_ROOM_LISTENS = "groupchat/findListens";
    public static final String POST_VIDEO = "groupchat/updateVideo";
    public static final String FIND_ROOM = "groupchat/findRoom";
    public static final String INSERT_MESSAGE = "groupchat/insertMessage";
    public static final String GET_MESSAGES = "groupchat/findMessages";
    public static final String DELETE_LISTEN = "groupchat/deleteListen";
    public static final String GET_USER_ROOMS = "groupchat/getUserRooms";
    public static final String GET_USER_ROOMS_COUNT = "groupchat/getUserRoomsCount";
    public static final String POST_USER_AVATAR = "groupchat/updateUserAvatar";
    public static final String POST_USER_CITY = "groupchat/updateUserCity";
    public static final String POST_USER_COUNTRY = "groupchat/updateUserCountry";
    public static final String POST_USER_PHONE = "groupchat/updateUserPhone";

}
//todo if room date is null in room details activity , set as text current date