package com.thesocialplaylist.user.music.api.declaration;

import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.dto.UserSearchDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

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
}
