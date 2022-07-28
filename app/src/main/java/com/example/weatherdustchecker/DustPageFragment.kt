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
import java.net.URL

class DustPageFragment : Fragment() {

    lateinit var dustImage : ImageView
    lateinit var ultraStatusText : TextView
    lateinit var ultraTemperatureText :TextView
    lateinit var statusText : TextView
    lateinit var temperatureText :TextView
    val API_ID = "45ad92c292570271c9778950b9a81d37ca16a8a9"

    @JsonDeserialize(using= DustPageFragment.MyDeserializer::class)
    data class OpenDustAPIJSONResponse(
        val pm10 : Int,
        val pm25 : Int
    )

    class MyDeserializer : StdDeserializer<OpenDustAPIJSONResponse>(
        OpenDustAPIJSONResponse::class.java
    ) {
        override fun deserialize(
            p: JsonParser?,
            ctxt: DeserializationContext?
        ): OpenDustAPIJSONResponse {
            val node = p?.codec?.readTree<JsonNode>(p)

            val pm10 = node?.get("data")?.get("iaqi")?.get("pm10")?.get("v")?.asInt()
            val pm25 = node?.get("data")?.get("iaqi")?.get("pm25")?.get("v")?.asInt()

            return OpenDustAPIJSONResponse(
                pm10!!, pm25!!
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dust_page_fragment, container, false)
        dustImage = view.findViewById(R.id.dust_icon)
        ultraStatusText = view.findViewById(R.id.ultra_dust_status_text)
        ultraTemperatureText = view.findViewById(R.id.ultra_dust_temp_text)
        statusText = view.findViewById(R.id.dust_status_text)
        temperatureText = view.findViewById(R.id.dust_temp_text)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments?.getDouble("lat")
        val lon = arguments?.getDouble("lon")
        val url = "http://api.waqi.info/feed/geo:$lat;$lon/?token=$API_ID"

        var mapper = jacksonObjectMapper()

        APICall(object : APICall.APICallback {
            override fun onComplete(result: String) {
                Log.d("my_tag", result)

                var data = mapper?.readValue<OpenDustAPIJSONResponse>(result)
                val pm10 = data.pm10
                val pm25 = data.pm25
                ultraTemperatureText.text = pm25.toString()
                temperatureText.text = pm10.toString()

                if(pm25!=null) {
                    ultraStatusText.text = if (pm25 <= 50) {
                        dustImage.setImageResource(R.drawable.good)
                        "좋음"
                    }
                    else if (pm25 <= 150) {
                        dustImage.setImageResource(R.drawable.normal)
                        "보통"
                    }
                    else if (pm25 <= 300) {
                        dustImage.setImageResource(R.drawable.bad)
                        "나쁨"
                    }
                    else {
                        dustImage.setImageResource(R.drawable.very_bad)
                        "매우 나쁨"
                    }
                }

                ultraStatusText.text = ultraStatusText.text.toString() + " (초미세먼지)"

                if(pm10!=null) {
                    statusText.text = if (pm10 <= 50) {
                        "좋음"
                    }
                    else if (pm10 <= 150) {
                        "보통"
                    }
                    else if (pm10 <= 300) {
                        "나쁨"
                    }
                    else {
                        "매우 나쁨"
                    }
                }

                statusText.text = statusText.text.toString() + " (미세먼지)"

            }

        }).execute(URL(url))


    }

    companion object {
        fun newInstance(lat: Double, lon: Double): DustPageFragment {
            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            val fragment = DustPageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}