package com.example.weatherdustchecker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.requestLocationUpdates
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var mPager : ViewPager
    private var lat : Double = 0.0
    private var lon : Double = 0.0

    lateinit var locationManager : LocationManager
    lateinit var locationListener: LocationListener
    val PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상단 제목 표시줄 숨기기
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager // 위치와 관련한 것을 매니징. 있어야 위치 정보 얻기가 가능하다.
        locationListener = LocationListener {
            lat = it.latitude
            lon = it.longitude
            Log.d("my_tag", lat.toString())
            Log.d("my_tag", lon.toString())

            locationManager.removeUpdates(locationListener)

            val pagerAdapter = MyPagerAdapter(supportFragmentManager, lat, lon)
            mPager.adapter = pagerAdapter
        }


        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,       //NETWORK_PROVIDER
                0,
                0f,
                locationListener)
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_REQUEST_CODE)
        }

        mPager = findViewById(R.id.pager)

        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageSelected(position: Int) {
                if(position == 0) {
                    Toast.makeText(applicationContext, "날씨 페이지입니다.", Toast.LENGTH_SHORT).show()
                }
                else if(position==1) {
                    Toast.makeText(applicationContext, "미세먼지 페이지입니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST_CODE) {
            var allPermissionsGranted = true
            for(result in grantResults) {
                allPermissionsGranted = (result == PackageManager.PERMISSION_GRANTED)
                if(!allPermissionsGranted) break
            }
            if(allPermissionsGranted) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,   //NETWORK_PROVIDER
                    0, 0f, locationListener)
            } else {
                Toast.makeText(applicationContext,
                    "위치 정보 제공 동의가 필요합니다.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    class MyPagerAdapter(fm : FragmentManager, val lat : Double, val lon : Double) : FragmentStatePagerAdapter(fm) {
        override fun getCount() = 2

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> WeatherPageFragment.newInstance(lat, lon)
                1 -> DustStudyFragment.newInstance(lat, lon)
                else -> throw Exception("페이지가 존재하지 않음")
            }
        }

    }
}