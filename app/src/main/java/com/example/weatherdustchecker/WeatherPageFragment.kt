package com.example.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.w3c.dom.Text
import java.net.URL

class WeatherPageFragment : Fragment() {


    private lateinit var weatherImage : ImageView
    lateinit var statusText : TextView
    lateinit var temperatureText :TextView
    var APP_ID = "12c889ac1806a5097d5064a0f56577c2"

    @JsonDeserialize(using=MyDeserializer::class)
    data class OpenWeatherAPIJSONResponse(val id : Int, val temp : Double)

    class MyDeserializer : StdDeserializer<OpenWeatherAPIJSONResponse>(
        OpenWeatherAPIJSONResponse::class.java
    ) {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): OpenWeatherAPIJSONResponse {
            val node = p?.codec?.readTree<JsonNode>(p)
//            val weatherNode = node?.get("weather")
//            val firstWeather = weatherNode?.elements()?.next()
//            val id = firstWeather?.get("id")?.asInt()
//            val mainNode = node?.get("main")
//            val temp = mainNode?.get("temp")?.asDouble()

            // 줄인 코드
            val id = node?.get("weather")?.elements()?.next()?.get("id")?.asInt()
            val temp = node?.get("main")?.get("temp")?.asDouble()
            
            return OpenWeatherAPIJSONResponse(id!!, temp!!)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.weather_page_fragment, container, false)
        statusText = view.findViewById<TextView>(R.id.weather_status_text)
        temperatureText = view.findViewById<TextView>(R.id.weather_temp_text)
        weatherImage = view.findViewById<ImageView>(R.id.weather_icon)

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments?.getDouble("lat")
        val lon = arguments?.getDouble("lon")
        val url = "http://api.openweathermap.org/data/2.5/weather?units=metric&appid=$APP_ID&lat=$lat&lon=$lon"

        var mapper = jacksonObjectMapper()

        APICall(object : APICall.APICallback {
            override fun onComplete(result: String) {
                Log.d("my_tag", result)
                var data = mapper?.readValue<OpenWeatherAPIJSONResponse>(result)

                temperatureText.text = data.temp.toString()

                val id = data.id.toString()
                if(id != null) {
                    statusText.text = when {
                        id.startsWith("2") -> {
                            weatherImage.setImageResource(R.drawable.flash)
                            "천둥, 번개"
                        }
                        id.startsWith("3") -> {
                            weatherImage.setImageResource(R.drawable.rain)
                            "이슬비"
                        }
                        id.startsWith("5") -> {
                            weatherImage.setImageResource(R.drawable.rain)
                            "비"
                        }
                        id.startsWith("6") -> {
                            weatherImage.setImageResource(R.drawable.snow)
                            "눈"
                        }
                        id.startsWith("7") -> {
                            weatherImage.setImageResource(R.drawable.cloudy)
                            "흐림"
                        }
                        id.equals("800") -> {
                            weatherImage.setImageResource(R.drawable.sun)
                            "화창"
                        }
                        id.startsWith("8") -> {
                            weatherImage.setImageResource(R.drawable.cloud)
                            "구름 낌"
                        }
                        else -> "알 수 없음"
                    }
                }

            }

        }).execute(URL(url))


    }

    companion object {
        fun newInstance(lat : Double, lon:Double) : WeatherPageFragment {
            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            val fragment = WeatherPageFragment()
            fragment.arguments = args

            return fragment
        }
    }
}