package com.sjyr.plantalarm;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private List<Date> mDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mDates = new ArrayList<>();

        // 올 해 1월
        Calendar thisYear = Calendar.getInstance();
        thisYear.set(Calendar.MONTH, Calendar.JANUARY);

        // 내년 오늘
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);

        // 오늘
        Date today = new Date();    // 현재 날짜 정보

        // 달력 초기화
        calendar.init(thisYear.getTime(), nextYear.getTime())
                .withSelectedDate(today);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                // 날짜 클릭

                // 이미 들어있는지 확인
                if (mDates.contains(date)) {
                    mDates.remove(date);
                } else {
                    // 하이라이트
                    mDates.add(date);
                }

                calendar.clearHighlightedDates();
                calendar.highlightDates(mDates);
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
//        calendar.init(today, nextYear.getTime())
//                .inMode(RANGE);

        try{
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PlantAlarm", "calendar");
            BufferedReader br = new BufferedReader(new FileReader(file));

            String str = br.readLine();
            while(str != null){
                Log.d("CalendarActivity", str);
                Date date = new Date(Long.parseLong(str));
                mDates.add(date);
                str = br.readLine();
            }

            br.close();

            // 색칠
            calendar.highlightDates(mDates);
        }catch(FileNotFoundException e){
            System.out.println(e);
        }catch(IOException e){
            System.out.println(e);
        }
    }

    // 화면이 안 보이기 직전 시점
    @Override
    protected void onPause() {
        super.onPause();

        // 파일에 저장
        if (isExternalStorageWritable()) {
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PlantAlarm");
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PlantAlarm", "calendar");

            if (!dir.exists()) {
                dir.mkdir();
            } else {

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    PrintWriter printWriter = new PrintWriter(fileOutputStream);

                    for (int i = 0; i < mDates.size(); i++) {
                        printWriter.println(mDates.get(i).getTime());
                    }

                    // 반영
                    printWriter.flush();
                    printWriter.close();
                    fileOutputStream.close();

                    Toast.makeText(CalendarActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    Toast.makeText(CalendarActivity.this, "저장이 실패 하였습니다", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(CalendarActivity.this, "저장이 실패 하였습니다", Toast.LENGTH_SHORT).show();
        }

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
