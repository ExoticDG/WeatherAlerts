package com.exoticdg.weatheralerts


import retrofit2.http.Query
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset.UTC
import java.util.TimeZone
import retrofit2.http.GET
import java.net.HttpURLConnection
import java.net.URL

class AlertDetails {
    var title = "temp"
    var body = "temp"



    fun getAlerts (@Query("latitude") lat: Double, @Query("longitude") lon: Double): Thread {
    println("getAlerts called")

        val apiUrl = "https://api.weather.gov/alerts/active?point=$lat,$lon"


//        val timezone = TimeZone.getDefault()
//
//        val sentTimeUTC = AlertDataDTO.sent //THIS VALUE IS NOT CORRECT RN FIX IT LATER
//
//        val currentTimeUTC = LocalTime.now(ZoneId.of(UTC.toString()))
//
//        if (currentTimeUTC >= sentTimeUTC) {
//            //send notification
//            //THIS DOSE NOT CURRENTLY WORK!!!!!!!
//        }

        return Thread {

            val url = URL("https://api.weather.gov/alerts/active?point=$lat,$lon")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "wallofthrones")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputSystem = connection.inputStream
                println(inputSystem.toString())
            } else {
                println("Error: ${connection.responseCode}")
            }
        }

    }
}



//    suspend fun getAlerts(lat: Double, lon: Double): String {
//        val apiUrl = "https://api.weather.gov/alerts/active?point=$lat,$lon"
//        println(apiUrl)
//
//        val client = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val request = chain.request().newBuilder()
//                    .header("User-Agent", "wallofthrones")
//                    .build()
//                chain.proceed(request)
//            }
//            .build()
//
//        val request = Request.Builder()
//            .url(apiUrl)
//            .build()
//
//        return withContext(Dispatchers.IO) {
//            val response = client.newCall(request).execute()
//            response.body?.string() ?: throw Exception("API request failed")
//        }
//    }
//
//    suspend fun main() {
//        val lat = 29.9
//        val lon = -85.3
//
//        val body = getAlerts(lat, lon)
//        println(body)
//    }


