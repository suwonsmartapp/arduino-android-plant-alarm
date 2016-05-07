package com.sjyr.plantalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.sjyr.plantalarm.events.ArduinoSensorEvent;
import com.sjyr.plantalarm.services.ArduinoAlarmService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    private TextView mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 최초에 호출
        setContentView(R.layout.activity_main);

        // 수분 값 Layout을 코드로 연결
        mResult = (TextView) findViewById(R.id.result_textview);


        // 뿌려주기

        // 서비스 실행
        startService(new Intent(this, ArduinoAlarmService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 화면이 보이기 직전에 호출

        // 이벤트버스에 등록
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 화면이 꺼지기 직전에 호출

        // 이벤트버스에 해제
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onArduinoSensorEvent(ArduinoSensorEvent event) {
        // 이벤트를 받을 곳

        Log.d("ArduinoAlarmService", "getMoisture: " + event.moisture);
        mResult.setText("" + event.moisture);
    }
}
