package com.sjyr.plantalarm.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.sjyr.plantalarm.MainActivity;
import com.sjyr.plantalarm.R;
import com.sjyr.plantalarm.events.ArduinoSensorEvent;
import com.sjyr.plantalarm.interfaces.ArduinoService;
import com.sjyr.plantalarm.models.SensorResult;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 수분 값을 모니터링 하는 서비스
 *
 * 특정 조건에서 MainActivity 에 이벤터 버스를 통해 이벤트를 발송한다
 */
public class ArduinoAlarmService extends IntentService {

    // 알림 주기
    public static final int TIME = 2 * 1000;

    // 알림 플래그
    public boolean mIsNotiOn = true;

    public ArduinoAlarmService() {
        // debug 용도
        super("ArduinoAlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ArduinoService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ArduinoService arduinoService = retrofit.create(ArduinoService.class);

        while (true) {
            try {
                // 1초 대기
                Thread.sleep(TIME);

                // 수분 값 모니터링
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

                            // 수분 값 300 이하일 때 알림 표시
                            if (event.moisture <= 300 && mIsNotiOn) {
                                // 알림 표시
                                showNotification();

                                // 알림 플래그 off
                                mIsNotiOn = false;
                            }

                            // 수분 값이 300 미만에서 300 이상으로 변경 되었을 때
                            // 알림 플래그 on
                            if (event.moisture > 300 && !mIsNotiOn) {
                                mIsNotiOn = true;
                            }
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

    private void showNotification() {
        // 알림 바를 터치 했을 때 실행 될 Activity 정의
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 알림 바에 표시될 알림 객체 정의
        Notification notification =
                new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("수분 값")
                        .setContentText("수분이 부족합니다")
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setVibrate(new long[] { 1000, 200, 300})
                        .build();

        // 알림 바에 알림 표시
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);
    }

}
