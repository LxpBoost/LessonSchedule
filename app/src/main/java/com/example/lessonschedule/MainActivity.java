package com.example.lessonschedule;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.color.MaterialColors;

import java.lang.reflect.Array;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    String dataBack;
    private static ArrayList<ClassInfo> allClasses;
    private static int semesterIndex, grade;
    private static String teachWeekStartDate;

    private GridLayout allClassGrid;
    private TextView semesterTextView, weekIndexTextView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    dataBack=data.getStringExtra("data_return");
                }break;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent=new Intent(MainActivity.this,SettingActivity.class);
        intent.putExtra("extra_data",dataBack);
        setContentView(R.layout.activity_main);
        allClassGrid = findViewById(R.id.main_grid_layout_all_class);
        semesterTextView = findViewById(R.id.main_text_view_semester);
        weekIndexTextView = findViewById(R.id.main_text_view_week_index);
        findViewById(R.id.main_float_btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent,1);
            }
        });

        if (allClasses == null) getStoredData();
        addAllClass();
        setSemesterWeek();
    }

    private void setSemesterWeek() {
        final String[] num2ChineseNum = new String[]{null, "一", "二", "三","四"};
        if (grade > 0 && grade <= 4 && semesterIndex >= 0 && semesterIndex <= 1)
            semesterTextView.setText(String.format("大%s%s", num2ChineseNum[grade], (semesterIndex == 0 ? "上" : "下")));

        int dayOfWeek = GregorianCalendar.getInstance().get(Calendar.DAY_OF_WEEK);
        // 周日是第一天
        if (dayOfWeek >= 2 && dayOfWeek <= 6) {
            LinearLayout linearLayout = findViewById(R.id.main_linear_weekdays);
            linearLayout.getChildAt(dayOfWeek - 2).setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
        }
        try {
            Date now = new Date();
            Date start = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(teachWeekStartDate);
            if (start != null) {
                Log.e(TAG, "setSemesterWeek: " + start + ", now: " + now + " dif: " + (now.getTime() - start.getTime()));
                long weekIndex = (now.getTime() - start.getTime()) / (7L * 24 * 60 * 60 * 1000) + 1;
                weekIndexTextView.setText(String.format(Locale.CHINA, "第%d周", weekIndex));
            }
        } catch (Exception e) {
            Log.e(TAG, "getStoredData: incorrect date format");
        }
    }

    private void addAllClass() {
        int k = 0;
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 13; ) {
                if (k < allClasses.size() && allClasses.get(k).getWeekdayIndex() == i && allClasses.get(k).getClassStartIndex() == j) {
                    final ClassInfo tmpClassInfo = allClasses.get(k);
                    TextView textView = new TextView(new ContextThemeWrapper(this, R.style.TextViewClassInfo));
                    textView.setText(tmpClassInfo.toClassInfoString());
                    textView.setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
                    CardView cardView = new CardView(new ContextThemeWrapper(this, R.style.CardViewClass));
                    cardView.addView(textView);
                    cardView.setCardBackgroundColor(tmpClassInfo.getBgColor());
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this,tmpClassInfo.toDetailClassInfoString(),Toast.LENGTH_LONG).show();
                        }
                    });
                    GridLayout.Spec rowSpec = GridLayout.spec(j - 1, tmpClassInfo.getClassPeriod());
                    GridLayout.Spec columnSpec = GridLayout.spec(i, 1.0f);
                    GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
                    layoutParams.setGravity(Gravity.FILL);
                    layoutParams.height = 0;
                    layoutParams.width = 0;
                    allClassGrid.addView(cardView, layoutParams);
                    j += tmpClassInfo.getClassPeriod();
                    k++;
                } else {
                    View child = new TextView(this);
                    GridLayout.Spec rowSpec = GridLayout.spec(j - 1, 1);
                    GridLayout.Spec columnSpec = GridLayout.spec(i, 1, 1.0f);
                    GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
                    layoutParams.width = 0;
                    layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.setGravity(Gravity.FILL);
                    child.setBackgroundColor(getResources().getColor(R.color.lightGray, null));
                    allClassGrid.addView(child, layoutParams);
                    j += 1;
                }
            }
        }
    }

    private void getStoredData() {
        allClasses = new ArrayList<>();
 /*       allClasses.add(new ClassInfo("操作系统", "H222", 1, 17, 1, 1, 2));
        allClasses.add(new ClassInfo("Android开发", "BC305", 1, 17, 1, 3, 2));
        allClasses.add(new ClassInfo("Android开发", "BS327", 1, 17, 2, 9, 2));
        allClasses.add(new ClassInfo("项目开发实践", "BW327", 1, 17, 2, 3, 2));
        allClasses.add(new ClassInfo("Oracle应用与开发", "BS227", 1, 17, 3, 1, 4));
        allClasses.add(new ClassInfo("通信原理", "BW201", 1, 17, 3, 5, 2));*/
/*        allClasses.add(new ClassInfo("项目开发实践", "BS317", 1, 17, 3, 7, 2));
        allClasses.add(new ClassInfo("操作系统", "H222", 1, 17, 4, 1, 2));
        allClasses.add(new ClassInfo("网络管理实验", "BS217", 1, 17, 4, 5, 2));
        allClasses.add(new ClassInfo("SDN软件定义网络", "BS227", 1, 17, 4, 7, 2));
        allClasses.add(new ClassInfo("网络管理", "BX221", 1, 17, 5, 3, 2));
        allClasses.add(new ClassInfo("路由与交换", "BS227", 1, 17, 5, 5, 4));
        allClasses.add(new ClassInfo("信息系统项目管理", "BW327", 1, 17, 5, 9, 2));*/

        allClasses.add(new ClassInfo("操作系统", "H222", 1, 15, 1, 6, 2));
        allClasses.add(new ClassInfo("Android开发", "BC305", 2, 16, 1, 11, 3));
        allClasses.add(new ClassInfo("项目开发实践", "BW327", 1, 16, 2, 3, 3));
        allClasses.add(new ClassInfo("Oracle应用与开发", "BS227", 1, 16, 2, 6, 2));
        allClasses.add(new ClassInfo("网络管理", "BX221", 8, 12, 4, 1, 2));
        allClasses.add(new ClassInfo("路由与交换", "BS227", 1, 14, 4, 6, 2));
        semesterIndex = 1;
        grade = 1;
        teachWeekStartDate = "2021-09-01";
    }
}
