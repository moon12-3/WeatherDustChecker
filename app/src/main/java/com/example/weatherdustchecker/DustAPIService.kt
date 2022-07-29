package com.example.weatherdustchecker

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type

interface DustAPIService {
    @GET("/feed/geo:{lat};{lon}")
    fun getDustStatusInfo(
        @Path("lat") lat : Double,
        @Path("lon") lon : Double,
        @Query("token") token : String
    ) : Call<DustCheckResponseGSON>
}

data class DustCheckResponseGSON(
    val pm10:Int,
    val pm25:Int,
    val pm10Status: String,
    val pm25Status: String
)

class DustCheckerResponseDeserializerGSON: JsonDeserializer<DustCheckResponseGSON> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): DustCheckResponseGSON {
        fun checkCategory(aqi : Int): String {
            return when(aqi) {
                in (0..100) ->"좋음"
                in (101..200) -> "보통"
                in (201..300) -> "나쁨"
                else -> "매우 나쁨"
            }
        }
        val root = json?.asJsonObject   //getter를 속성처럼 바꿔줌
        var data = root?.getAsJsonObject("data")
        var iaqi = data?.getAsJsonObject("iaqi")
        var pm10Node = iaqi?.getAsJsonObject("pm10")
        var pm25Node = iaqi?.getAsJsonObject("pm25")
        var pm10 = pm10Node?.getAsJsonPrimitive("v")?.asInt
        var pm25 = pm25Node?.getAsJsonPrimitive("v")?.asInt

        return DustCheckResponseGSON(
            pm10!!,
            pm25!!,
            checkCategory(pm10),
            checkCategory(pm25)
        )
    }

}