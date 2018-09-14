package com.itskshitizsh.findingbus.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.itskshitizsh.findingbus.R;
import com.itskshitizsh.findingbus.fragments.FragmentPageAdapter;
import com.itskshitizsh.findingbus.login.LoginActivity;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    private ViewPager mViewPager;

    private String currentUserName = "unknown";
    private String currentUserEmail = "unknown";

    private boolean doubleBackToExitPressedOnce = false;
    private FragmentPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Intent intent = getIntent();
        if (intent.hasExtra("username") && intent.hasExtra("userEmail")) {
            currentUserName = intent.getStringExtra("username");
            currentUserEmail = intent.getStringExtra("userEmail");
        } else {
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }

        /*TextView userDetailTextView = findViewById(R.id.user_detail_text_view);
        String userDetail = currentUserName + "\n" + currentUserEmail;
        userDetailTextView.setText(userDetail);*/


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This Code will take care of Icon on ToolBar for Navigation Drawer.
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        adapter = new FragmentPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);



        //Set up tab layout with view pager
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logOut :
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                }
                return false;
            }
        });

        //Ask for location permission
        askPermission();

    }

    private void askPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mViewPager.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1) {
            if (grantResults[0] == 0) {  // 0 for permission granted
                mViewPager.setAdapter(adapter);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.ic_caution);
                builder.setTitle("Permission needed!");
                builder.setMessage("Please allow location permission to use services");
                builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askPermission();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.back_hint_to_exit, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

}
