package com.jovision.account;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuardTimeActivity extends BaseActivity {

    @Bind(R.id.guard_time_start_time)
    TextView guardTimeStartTime;
    @Bind(R.id.guard_time_stop_time)
    TextView guardTimeStopTime;
    private int hourOfDay,minute;
    private String mStart,mStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_time);
        ButterKnife.bind(this);
        Calendar calendar = Calendar.getInstance();
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

    }

    @OnClick({R.id.guard_time_back, R.id.guard_time_start, R.id.guard_time_stop, R.id.guard_time_all_day, R.id.guard_time_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.guard_time_back:
                finish();
                break;
            case R.id.guard_time_start:
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hour;
                        String minutes;
                        if (hourOfDay<=9) {
                            hour = "0"+hourOfDay;
                        }else {
                            hour = hourOfDay+"";
                        }
                        if (minute<=9) {
                            minutes = "0"+minute;
                        }else {
                            minutes = minute+"";
                        }
                        mStart= hour + ":" + minutes;
                        guardTimeStartTime.setText(mStart);
                    }
                },hourOfDay,minute,true);

                timePickerDialog.show();
                break;
            case R.id.guard_time_stop:
                TimePickerDialog timePickerDialog2 = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hour;
                        String minutes;
                        if (hourOfDay<=9) {
                            hour = "0"+hourOfDay;
                        }else {
                            hour = hourOfDay+"";
                        }
                        if (minute<=9) {
                            minutes = "0"+minute;
                        }else {
                            minutes = minute+"";
                        }
                        mStop= hour + ":" + minutes;
                        guardTimeStopTime.setText(mStop);
                    }
                },hourOfDay,minute,true);

                timePickerDialog2.show();
                break;
            case R.id.guard_time_all_day:
                SharedPreUtil.put(mContext,"guard_time_start","全天");
                SharedPreUtil.put(mContext,"guard_time_stop","全天");
//                BaseUtils.showShortToast(mContext,"修改成功");
                break;
            case R.id.guard_time_save:
                SharedPreUtil.put(mContext,"guard_time_start",guardTimeStartTime.getText().toString());
                SharedPreUtil.put(mContext,"guard_time_stop",guardTimeStopTime.getText().toString());
                BaseUtils.showShortToast(mContext,"修改成功");
                break;
        }
    }
}
