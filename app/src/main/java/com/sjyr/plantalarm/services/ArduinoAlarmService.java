package com.sjyr.plantalarm.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.sjyr.plantalarm.events.ArduinoSensorEvent;
import com.sjyr.plantalarm.interfaces.ArduinoService;
import com.sjyr.plantalarm.models.SensorResult;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArduinoAlarmService extends IntentService {

    int i = 0;
    public ArduinoAlarmService() {
        // debug 용도
        super("ArduinoAlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // 서비스가 수행 할 내용

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ArduinoService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ArduinoService arduinoService = retrofit.create(ArduinoService.class);

        while (i < 1000) {
            // 1초 대기
            try {
                Thread.sleep(1000);

                // 수분 값 가져오기
                arduinoService.getSensorResult().enqueue(new Callback<SensorResult>() {
                    @Override
                    public void onResponse(Call<SensorResult> call, Response<SensorResult> response) {
                        // 응답
                        if (response.isSuccessful()) {
                            // 성공
                            SensorResult body = response.body();

                            // 이벤트 발송
                            ArduinoSensorEvent event = new ArduinoSensorEvent();
                            event.moisture = body.getMoisture();
                            EventBus.getDefault().post(event);
                        } else {
                            // 실패
                            Log.d("MainActivity", "onResponse: 실패");
                        }

                    }

                    @Override
                    public void onFailure(Call<SensorResult> call, Throwable t) {
                        // 404 서버 죽었을 때 실패
                        Log.d("MainActivity", "onFailure: 404 서버 다운" + t.getLocalizedMessage());

                    }
                });


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
