package com.example.weatherdustchecker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class WeatherPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.weather_page_fragment, container, false)
        val statusView = view.findViewById<TextView>(R.id.weather_status_text)
        val temperatureView = view.findViewById<TextView>(R.id.weather_temp_text)
        val weatherIcon = view.findViewById<ImageView>(R.id.weather_icon)

        statusView.text = arguments?.getString("status")!!
        temperatureView.text = arguments?.getDouble("temperature")!!.toString()
        weatherIcon.setImageResource(arguments?.getInt("res_id")!!)

        return view
    }

    companion object {
        fun newInstance(status : String, temperature : Double) : WeatherPageFragment {
            val args = Bundle()
            args.putString("status", status)
            args.putDouble("temperature", temperature)
            args.putInt("res_id", R.drawable.sun)
            val fragment = WeatherPageFragment()
            fragment.arguments = args

            return fragment
        }
    }
}