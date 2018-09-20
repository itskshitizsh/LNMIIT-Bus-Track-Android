package com.itskshitizsh.findingbus.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.itskshitizsh.findingbus.R;
import com.itskshitizsh.findingbus.fragments.Bus1Fragment;
import com.itskshitizsh.findingbus.fragments.Bus2Fragment;
import com.itskshitizsh.findingbus.fragments.Bus3Fragment;
import com.itskshitizsh.findingbus.login.LoginActivity;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    private ViewPager mViewPager;

    private String currentUserName = "unknown";
    private String currentUserEmail = "unknown";

    private boolean doubleBackToExitPressedOnce = false;
    private FragmentPagerAdapter adapter;


    private FirebaseRemoteConfig remoteConfig;
    private String busInfo = "Sorry, no information available";

    private boolean isNetworkConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            isNetworkConnected = true;

        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        remoteConfig = FirebaseRemoteConfig.getInstance();


        remoteConfig.fetch(0)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            remoteConfig.activateFetched();
                        } else {
                            Toast.makeText(HomeActivity.this, "Loading bus information Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        busInfo = remoteConfig.getString("bus_information");
                    }
                });


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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_logOut) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
                if (item.getItemId() == R.id.nav_schedule) {
                    startActivity(new Intent(HomeActivity.this, BusScheduleActivity.class));
                }

                return false;
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_bus1 :
                        loadFragment(new Bus1Fragment());
                        return true;
                    case R.id.navigation_bus2 :
                        loadFragment(new Bus2Fragment());
                        return true;
                    case R.id.navigation_bus3 :
                        loadFragment(new Bus3Fragment());
                        return true;
                }
                return false;
            }
        });

        //Ask for location permission
        askPermission();

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,fragment)
                .commit();
    }

    private void askPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            loadFragment(new Bus1Fragment());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1) {
            if (grantResults[0] == 0) {  // 0 for permission granted
                loadFragment(new Bus1Fragment());
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.ic_caution);
                builder.setTitle("Permission needed!");
                builder.setMessage("Please allow location permission to use services");
                builder.setCancelable(false);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                showInfoAlertDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInfoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog;
        builder.setCancelable(false);
        builder.setTitle("Bus information");
        builder.setMessage(busInfo);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_bus_icon));
        builder.setPositiveButton("GOT IT", null);
        alertDialog = builder.create();
        alertDialog.show();
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
