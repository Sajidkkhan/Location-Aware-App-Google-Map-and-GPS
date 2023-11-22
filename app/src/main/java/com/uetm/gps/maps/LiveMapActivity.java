package com.uetm.gps.maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class LiveMapActivity extends AppCompatActivity implements LocationListener,
        android.location.LocationListener, OnMapReadyCallback, View.OnClickListener {
    int PERMISSION_ID = 44;
    private GoogleMap mMap;
    private View mapView;
    public static final String MAP_PREFERENCES = "MapPrefs";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView normal, hybrid, satellite, terrain;
    private TextView slowerTraffic, tv_traffic;
    private SwitchCompat traffic;
    private Boolean isTraffic;
    private double mLat;
    private double mLng;
//    private InterstitialAd mInterstitialAd;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_livemap);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Live Map of Current Location");
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);
        toolbar.setNavigationIcon(drawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
// sharedPreferences to store traffic mode, and map type
        sharedPreferences = getSharedPreferences(MAP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isTraffic = sharedPreferences.getBoolean("TRAFFIC", true);
        // initializing views
        traffic = findViewById(R.id.traffic_switch);
        tv_traffic = findViewById(R.id.tv_traffic);
        normal = findViewById(R.id.normal);
        hybrid = findViewById(R.id.hybrid);
        satellite = findViewById(R.id.satellite);
        terrain = findViewById(R.id.terrain);
        slowerTraffic = findViewById(R.id.slower_traffic);
        // implementation of animation on textview
        slowerTraffic.startAnimation(AnimationUtils.loadAnimation(this, R.anim.text_fade_out));
        animateTextView(slowerTraffic);
// click Listeners on views
        normal.setOnClickListener(this);
        hybrid.setOnClickListener(this);
        satellite.setOnClickListener(this);
        terrain.setOnClickListener(this);
// set state for toggle button and setting text to textView traffic
        traffic.setChecked(isTraffic);
        tv_traffic.setText("Traffic ON");

        // getting map fragment here
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        // getting location manager object
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // runtime permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // registering for location updated
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1, 1, this);

        // traffic toggle button listener.
        traffic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
               //change toggle button state
                traffic.setChecked(checked);
                // set map traffic
                mMap.setTrafficEnabled(checked);
                //save traffic mode
                editor.putBoolean("TRAFFIC", checked);
                editor.commit();
                // change text on traffic textview
                if (checked==true){
                    tv_traffic.setText("Traffic ON");
                }else {
                    tv_traffic.setText("Traffic OFF");
                }

            }
        });

    }

//    Animating textview
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void animateTextView(TextView slowerTraffic) {
        slowerTraffic.setText("Slower traffic than normal");
        Paint textPaint = slowerTraffic.getPaint();
        String text = slowerTraffic.getText().toString();//get text
        int width = Math.round(textPaint.measureText(text));//measure the text size
        ViewGroup.LayoutParams params =  slowerTraffic.getLayoutParams();
        params.width = width;
        slowerTraffic.setLayoutParams(params); //refine

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getRealMetrics(displaymetrics);

        TranslateAnimation slide = new TranslateAnimation(0, -width, 0, 0);
        slide.setDuration(5000);
        slide.setRepeatCount(Animation.INFINITE);
        slide.setRepeatMode(Animation.RESTART);
        slide.setInterpolator(new LinearInterpolator());
        slowerTraffic.startAnimation(slide);
    }

//    when the map gets ready this method is called
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // enabling zoom controller on map
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // enableing current location to map
        mMap.setMyLocationEnabled(true);
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 30, 30, 0);
        }
        // enabling traffic mode on map
        mMap.setTrafficEnabled(isTraffic);
        getViewPrefs();

    }

    // when the location is changed onLocationChanged method is called
    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        // animate map to new location, the camera will be moved to new location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.normal) {
            setViewPrefs(0);
            getViewPrefs();
        } else if (id == R.id.hybrid) {
            setViewPrefs(1);
            getViewPrefs();
        } else if (id == R.id.satellite) {
            setViewPrefs(2);
            getViewPrefs();
        } else if (id == R.id.terrain) {
            setViewPrefs(3);
            getViewPrefs();
        }
        view.setBackgroundResource(R.drawable.view_shape_selected);
    }
    //change map type in sharedPreferences
    private void setViewPrefs(int i) {
        editor.putInt("MAP_TYPE", i);
        editor.commit();
    }



    // get map type from shared preferences and setting map type normal, hybrid, satellite, terrain, etc.
    private void getViewPrefs() {
        normal.setBackgroundResource(R.drawable.view_shape_normal);
        hybrid.setBackgroundResource(R.drawable.view_shape_normal);
        satellite.setBackgroundResource(R.drawable.view_shape_normal);
        terrain.setBackgroundResource(R.drawable.view_shape_normal);
        switch (sharedPreferences.getInt("MAP_TYPE", 2)) {
            case 0:
                // setting map type normal
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                normal.setBackgroundResource(R.drawable.view_shape_selected);
                break;
            case 1:
                // setting map type Hybrid
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                hybrid.setBackgroundResource(R.drawable.view_shape_selected);
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                satellite.setBackgroundResource(R.drawable.view_shape_selected);
                break;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                terrain.setBackgroundResource(R.drawable.view_shape_selected);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}