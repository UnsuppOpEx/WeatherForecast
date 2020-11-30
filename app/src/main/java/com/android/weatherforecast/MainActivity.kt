package com.android.weatherforecast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.weatherpart3.R
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


/**
 * Основное активити
 */
class MainActivity : AppCompatActivity() {
    private var tvGpsLocation: TextView? = null
    private var locationCallback: LocationCallback? = null
    private var locationHelper: GPSHelper? = null


    var lat: Double? = null
    var log: Double? = null
    val API: String = "2abbf670320e50e72a885cfc8c748933"
    var CITY: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationHelper = GPSHelper(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE
                ), 2
            )
        } else {
            locationHelper?.getLocation {
                tvGpsLocation?.text = it
            }
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationHelper?.locationRequest == null) {
                    findViewById<TextView>(R.id.updated_at).text = "Данные не получены"
                    return
                }

                val location = locationResult?.lastLocation

                lat = location?.latitude
                log = location?.longitude

                if (locationResult != null) {
                    for (loc in locationResult.locations) {
                        lat = loc.latitude
                        log = loc.longitude
                    }
                }
            }
        }

        findViewById<Button>(R.id.gps_button).setOnClickListener {
            if (lat != null && log != null) {
                CITY = getCityName(lat!!, log!!)
            }

            weatherTask().execute()
        }

        findViewById<Button>(R.id.searchButton).setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivityForResult(intent, 1)
        }

        findViewById<Button>(R.id.back).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()


            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }


        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API&lang=ru").readText(
                        Charsets.UTF_8

                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText = "Последнее обновление: " + SimpleDateFormat(
                    "dd/MM/yyyy hh:mm a",
                    Locale.ENGLISH
                ).format(Date(updatedAt * 1000))
                val temp = main.getInt("temp")
                val tempMin = "Mин темп.: " + main.getInt("temp_min") + "°C"
                val tempMax = "Max темп: " + main.getInt("temp_max") + "°C"
                val pressure = main.getInt("pressure")
                val humidity = main.getString("humidity") + "%"

                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed") + " м/с"
                val weatherDescription = weather.getString("description")
                val weatherIcon = weather.getString("icon")

                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                Picasso.get()
                    .load("https://openweathermap.org/img/wn/$weatherIcon@4x.png")
                    .into(findViewById<ImageView>(R.id.weatherIcon))

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = "$temp°C"
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat(
                    "hh:mm a",
                    Locale.ENGLISH
                ).format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat(
                    "hh:mm a",
                    Locale.ENGLISH
                ).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = "${pressure * 0.75} мм рт.ст"
                findViewById<TextView>(R.id.humidity).text = humidity
                findViewById<ImageView>(R.id.weatherIcon)
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
                findViewById<TextView>(R.id.back).visibility = View.VISIBLE
            }

        }
    }

    override fun onResume() {
        super.onResume()
        locationHelper?.startLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        locationHelper?.stopLocationUpdates(locationCallback)
    }


    // Возвращает ответ о статусе разрешения на испоьзование GPS позиционирования
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show()
                    locationHelper?.getLocation {}
                } else {
                    Toast.makeText(this, "Разрешение не получено", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // Получаем название города по координатам при использовании GPS
    private fun getCityName(lat: Double, log: Double): String {
        var cityName: String = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat, log, 3)

        cityName = Adress.get(0).locality
        return cityName
    }


    //Получаем название города
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val extras = data?.extras
            if (extras != null) {
                val cityName = extras.getString("name", "Moscow")
                CITY = null
                CITY = cityName

                weatherTask().execute()
            }
        }
    }
}

