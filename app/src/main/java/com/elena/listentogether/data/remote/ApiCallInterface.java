package com.elena.listentogether.data.remote;

import com.elena.listentogether.data.local.entity.ListenEntity;
import com.elena.listentogether.data.local.entity.MessageEntity;
import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.UserEntity;
import com.elena.listentogether.utils.Urls;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiCallInterface {

    //@FormUrlEncoded //only when it has request body
    @GET(Urls.GET_USERS)
    Observable<List<UserEntity>> getUsers();

    @GET(Urls.FIND_USER)
    Call<UserEntity> findUser(@Query("username") String username, @Query("password")String pass);

    @POST(Urls.ADD_ROOM)
    Call<String> insertRoom(@Body RoomEntity roomEntity);

    @GET(Urls.GET_ROOMS)
    Call<List<RoomEntity>> getRooms();

    @POST(Urls.INSERT_LISTEN)
    Call<Void> joinRoom(@Body ListenEntity listenEntity);

    @GET(Urls.GET_ROOM_LISTENS)
    Call<List<ListenEntity>> getListeningForRoom(@Query("id_room")Long roomId);

    @POST(Urls.POST_VIDEO)
    Call<Void> updateVideo(@Query("roomId")Long roomId, @Query("videoId")String videoId,
                           @Query("videoTitle")String videoTitle);

    @GET(Urls.FIND_ROOM)
    Call<RoomEntity> findRoom(@Query("idRoom") Long idRoom);

    @POST(Urls.INSERT_MESSAGE)
    Call<Void> insertMessage(@Body MessageEntity messageEntity);

    @GET(Urls.GET_MESSAGES)
    Call<List<MessageEntity>> getMessages(@Query("roomId")Long id);

    @POST(Urls.DELETE_LISTEN)
    Call<String> deleteListen(@Body ListenEntity listenEntity);

    @GET(Urls.GET_USER_ROOMS)
    Call<List<RoomEntity>> loadUserRooms(@Query("id_user")Long userId);

    @GET(Urls.GET_USER_ROOMS_COUNT)
    Call<Integer> getUserRoomsCount(@Query("id_user")Long userId);

    @FormUrlEncoded
    @POST(Urls.POST_USER_AVATAR)
    Call<String> updateUserAvatar(@Query("id_user")Long userId, @Field("avatar")String avatar);

    @POST(Urls.POST_USER_COUNTRY)
    Call<Void> updateUserCountry(@Query("id_user")Long userId, @Query("country")String country);

    @POST(Urls.POST_USER_CITY)
    Call<Void> updateUserCity(@Query("id_user")Long userId, @Query("city")String city);

    @POST(Urls.POST_USER_PHONE)
    Call<Void> updateUserPhone(@Query("id_user")Long userId, @Query("phone")String phone);
}