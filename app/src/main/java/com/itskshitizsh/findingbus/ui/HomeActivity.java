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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.itskshitizsh.findingbus.BuildConfig;
import com.itskshitizsh.findingbus.R;
import com.itskshitizsh.findingbus.fragments.BusFragment;
import com.itskshitizsh.findingbus.fragments.SettingsFragment;
import com.itskshitizsh.findingbus.login.LoginActivity;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    private ViewPager mViewPager;

    private String currentUserName = "unknown";
    private String currentUserEmail = "unknown";

    private boolean doubleBackToExitPressedOnce = false;
    private FragmentPagerAdapter adapter;


    private FirebaseRemoteConfig remoteConfig;
    private String busInfo = "Sorry, no information available";

    private BottomNavigationView bottomNavigationView;
    private boolean isNetworkConnected = false;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                    || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                isNetworkConnected = true;

            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setConfigSettings(configSettings);
        long cacheExpiration = 3600;
        if (remoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        remoteConfig.fetch(cacheExpiration)
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
        String userDetail = currentUserName + "\n" + currentUserEmail;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This Code will take care of Icon on ToolBar for Navigation Drawer.
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        View view = navigationView.getHeaderView(0);
        TextView userInfo = view.findViewById(R.id.user_detail_text_view);
        userInfo.setText(userDetail);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_bus1 :
                        Bundle bus1Args = new Bundle();
                        bus1Args.putDouble("lat", 26.937996);
                        bus1Args.putDouble("lang", 75.922457);
                        BusFragment busFragment = new BusFragment();
                        busFragment.setArguments(bus1Args);
                        loadFragment(busFragment);
                        return true;
                    case R.id.navigation_bus2 :
                        Bundle bus2Args = new Bundle();
                        bus2Args.putDouble("lat", 26.915565);
                        bus2Args.putDouble("lang", 75.817029);
                        BusFragment bus2Fragment = new BusFragment();
                        bus2Fragment.setArguments(bus2Args);
                        loadFragment(bus2Fragment);
                        return true;
                    case R.id.navigation_bus3 :
                        Bundle bus3Args = new Bundle();
                        bus3Args.putDouble("lat", 26.897476);
                        bus3Args.putDouble("lang", 75.831578);
                        BusFragment bus3Fragment = new BusFragment();
                        bus3Fragment.setArguments(bus3Args);
                        loadFragment(bus3Fragment);
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

            loadFragment(new BusFragment());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1) {
            if (grantResults[0] == 0) {  // 0 for permission granted
                loadFragment(new BusFragment());
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
        } else if (bottomNavigationView.getVisibility() == View.GONE) {
            loadFragment(new BusFragment());
            bottomNavigationView.setVisibility(View.VISIBLE);
            navigationView.setCheckedItem(R.id.nav_home);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_logOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.nav_schedule:
                startActivity(new Intent(HomeActivity.this, BusScheduleActivity.class));
                break;
            case R.id.nav_home:
                loadFragment(new BusFragment());
                bottomNavigationView.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_nearMe: // TODO: NearMe calculation
                break;
            case R.id.nav_settings:
                loadFragment(new SettingsFragment());
                bottomNavigationView.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
