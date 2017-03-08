package com.thesocialplaylist.user.music.api.declaration;

import com.thesocialplaylist.user.music.dto.SongDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.dto.UserLoginRequestDTO;
import com.thesocialplaylist.user.music.dto.UserProfileRequestDTO;
import com.thesocialplaylist.user.music.dto.UserSearchDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by user on 16-01-2017.
 */


public interface UserApi {

    //search a user
    @POST(value = "users/search")
    Call<List<UserDTO>> searchUser(@Body UserSearchDTO userSearchDTO);

    //register a user
    @POST(value = "users/create")
    Call<UserDTO> registerUser(@Body UserDTO userDTO);

    //endpoint to check if a user has already been registered or not. If the user has not yet been registered, we register him.
    //Either way we update his details and friendslist on service and return back the updated UserDTO to the caller.
    @POST(value = "users/login")
    Call<UserDTO> login(@Query("compress") Boolean compressFlag, @Body UserLoginRequestDTO userLoginRequestDTO);

    @POST(value = "users/{id}/songs/save")
    Call<List<SongDTO>> saveSongs(@Path(value = "id") String id, @Body List<SongDTO> songs);

    @POST(value = "users/profile/{id}")
    Call<UserDTO> getProfile(@Path(value = "id") String id, @Body UserProfileRequestDTO userProfileRequestDTO);
}
