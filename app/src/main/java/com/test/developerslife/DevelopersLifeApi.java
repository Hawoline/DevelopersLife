package com.test.developerslife;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DevelopersLifeApi {
    @GET("/random?json=true")
    Call<Post> getRandomPost();
}
