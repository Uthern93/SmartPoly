package com.jtmk.smartpoly;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetroAPI {
    @GET
    Call<MsgModal>getMessage(@Url String url);
}
