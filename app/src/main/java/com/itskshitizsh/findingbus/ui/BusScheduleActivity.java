package com.itskshitizsh.findingbus.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itskshitizsh.findingbus.R;

public class BusScheduleActivity extends AppCompatActivity {
    private ImageView scheduleImageView1;
    private ImageView scheduleImageView2;

    private ProgressBar progressBar;
    private boolean doneTask1 = false;
    private boolean doneTask2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_schedule);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("time_table");

        scheduleImageView1 = findViewById(R.id.schedule_image_view_1);
        scheduleImageView2 = findViewById(R.id.schedule_image_view_2);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        storageReference.child("time_table_1.jpg").getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(BusScheduleActivity.this).load(uri.toString()).into(scheduleImageView1);
                doneTask1 = true;
                if (doneTask2) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        storageReference.child("time_table_2.jpg").getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(BusScheduleActivity.this).load(uri.toString()).into(scheduleImageView2);
                doneTask2 = true;
                if (doneTask1) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
