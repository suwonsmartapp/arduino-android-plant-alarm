package com.sjyr.plantalarm.interfaces;

import com.sjyr.plantalarm.models.SensorResult;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by junsuk on 16. 4. 30..
 */
public interface ArduinoService {
    // 서버의 RootUrl
    String BASE_URL = "http://192.168.0.29";

    @GET("/")
    Call<SensorResult> getSensorResult();
}
