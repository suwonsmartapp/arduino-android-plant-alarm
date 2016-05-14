package com.sjyr.plantalarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // 올 해 1월
        Calendar thisYear = Calendar.getInstance();
        thisYear.set(Calendar.MONTH, Calendar.JANUARY);

        // 내년 오늘
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);

        // 오늘
        Date today = new Date();    // 현재 날짜 정보

        // 달력 초기화
        calendar.init(thisYear.getTime(), nextYear.getTime())
                .withSelectedDate(today);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
//        calendar.init(today, nextYear.getTime())
//                .inMode(RANGE);
    }
}
