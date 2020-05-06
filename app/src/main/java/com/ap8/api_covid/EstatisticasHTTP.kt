package com.ap8.api_covid

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object EstatisticasHTTP {
    val Json_URL = "https://covid19-brazil-api.now.sh/api/report/v1/countries"

    fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnected
    }

    fun loadEstatisticas(): List<Estatisticas>? {
        val client = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
        val req = Request.Builder()
            .url(Json_URL)
            .build()
        val res = client.newCall(req).execute()

        val jsonString = res.body?.string()
        val json_Object = JSONObject(jsonString)
        val json_array = json_Object.getJSONArray("data")
        return readJson(json_array)
    }

    fun readJson(jsonArray: JSONArray): List<Estatisticas>? {
        val array_estatisticas = mutableListOf<Estatisticas>()

        try {
            for (i in 0 .. jsonArray.length()-1){
                var json = jsonArray.getJSONObject(i)
                val date_ = formatarData(json.getString("updated_at").substring(0,10))
                val hour_ = json.getString("updated_at").substring(11, 16)

                var estatisticas = Estatisticas(
                    json.getString("country"),
                    json.getInt("cases"),
                    json.getInt("confirmed"),
                    json.getInt("deaths"),
                    json.getInt("recovered"),
                    date_,
                    hour_
                )
                array_estatisticas.add(estatisticas)
            }
        }
        catch (e : IOException){
            Log.e("Erro", "Impossivel ler JSON")
        }

        return array_estatisticas
    }

    fun formatarData(data: String): String {
        val diaString =data
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        var date = LocalDate.parse(diaString)
        var formattedDate = date.format(formatter)
        return formattedDate
    }
}