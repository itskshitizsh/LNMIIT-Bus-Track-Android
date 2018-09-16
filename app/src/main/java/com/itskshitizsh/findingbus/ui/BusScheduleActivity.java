package com.itskshitizsh.findingbus.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.itskshitizsh.findingbus.R;

public class BusScheduleActivity extends AppCompatActivity {
    private ImageView scheduleImageView1;
    private ImageView scheduleImageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_schedule);

        scheduleImageView1 = findViewById(R.id.schedule_image_view_1);
        scheduleImageView2 = findViewById(R.id.schedule_image_view_2);


        Glide.with(this).load(R.drawable.time_table_1).into(scheduleImageView1);
        Glide.with(this).load(R.drawable.time_table_2).into(scheduleImageView2);
    }
}
