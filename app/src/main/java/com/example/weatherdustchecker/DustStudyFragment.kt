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

@JsonDeserialize(using= DustCheckerResponseDeserializer::class)
data class DustCheckResponse(
    val pm10:Int,
    val pm25:Int,
    val pm10Status: String,
    val pm25Status: String)

class DustCheckerResponseDeserializer :
    StdDeserializer<DustCheckResponse>(DustCheckResponse::class.java) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): DustCheckResponse {
        fun checkCategory(aqi : Int): String {
            return when(aqi) {
                in (0..100) ->"좋음"
                in (101..200) -> "보통"
                in (201..300) -> "나쁨"
                else -> "매우 나쁨"
            }
        }

        var node = p?.codec?.readTree<JsonNode>(p)
        val pm10 = node?.get("data")?.get("iaqi")?.get("pm10")?.get("v")?.asInt()
        val pm25 = node?.get("data")?.get("iaqi")?.get("pm25")?.get("v")?.asInt()

        return DustCheckResponse(pm10!!, pm25!!, checkCategory(pm10), checkCategory(pm25))
    }

}

class DustStudyFragment : Fragment() {

    lateinit var dustImage : ImageView
    lateinit var ultraStatusText : TextView
    lateinit var ultraTemperatureText : TextView
    lateinit var statusText : TextView
    lateinit var temperatureText : TextView

    val API_TOKEN = "45ad92c292570271c9778950b9a81d37ca16a8a9"

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
        val url = "http://api.waqi.info/feed/geo:$lat;$lon/?token=$API_TOKEN"

        val mapper = jacksonObjectMapper()

        APICall(object : APICall.APICallback {
            override fun onComplete(result: String) {
                Log.d("my_tag", result)

                val data = mapper?.readValue<DustCheckResponse>(result)
                temperatureText.text = data.pm10.toString()+" (미세먼지)"
                ultraTemperatureText.text = data.pm25.toString()+" (초미세먼지)"

                val pm10 = data.pm25Status
                val pm25 = data.pm10Status

                ultraStatusText.text = pm10
                statusText.text = pm25

                when(pm25) {
                    "좋음" -> dustImage.setImageResource(R.drawable.good)
                    "보통" -> dustImage.setImageResource(R.drawable.normal)
                    "나쁨" -> dustImage.setImageResource(R.drawable.bad)
                    "매우 나쁨" -> dustImage.setImageResource(R.drawable.very_bad)
                }




            }
        }).execute(URL(url))

    }



    companion object {
        fun newInstance(lat : Double, lon : Double): DustStudyFragment{
            val args = Bundle()

            args.putDouble("lat", lat)
            args.putDouble("lon", lon)

            val fragment = DustStudyFragment()
            fragment.arguments = args
            return fragment
        }
    }
}