package com.sjyr.plantalarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sjyr.plantalarm.interfaces.ArduinoService;
import com.sjyr.plantalarm.models.SensorResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 수분값 얻어오기
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ArduinoService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ArduinoService arduinoService = retrofit.create(ArduinoService.class);

        arduinoService.getSensorResult().enqueue(new Callback<SensorResult>() {
            @Override
            public void onResponse(Call<SensorResult> call, Response<SensorResult> response) {
                // 응답
                if (response.isSuccessful()) {
                    // 성공
                    SensorResult body = response.body();
                    Log.d("MainActivity", "onResponse: " + body.getMoisture());

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

        // 뿌려주기
    }
}
