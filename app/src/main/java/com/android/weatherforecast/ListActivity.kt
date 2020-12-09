package com.android.weatherforecast

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.weatherpart3.R
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Активити ListActivity
 */
class ListActivity : AppCompatActivity(), CityClickListener {
    private val arrayList: MutableList<CityModel> = ArrayList()
    val displayList: MutableList<CityModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)

        displayList.addAll(getCities())
        val cityAdapter = CityAdapter(
            displayList as ArrayList<CityModel>,
            this,
            this
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cityAdapter

    }

    // Заполняет список данными
    fun getCities(): List<CityModel> {
        arrayList.add(CityModel("Moscow", "Russia"))
        arrayList.add(CityModel("Sofia", "Bulgaria"))
        arrayList.add(CityModel("Ottawa", "Canada"))
        arrayList.add(CityModel("Riga", "Latvia"))
        arrayList.add(CityModel("Berlin", "Germany"))
        arrayList.add(CityModel("Belarus", "Belarus"))
        arrayList.add(CityModel("Beijing", "China"))
        arrayList.add(CityModel("Tokyo", "Japan"))
        arrayList.add(CityModel("Ankara", "Turkey"))
        arrayList.add(CityModel("Brasilia", "Brazil"))
        arrayList.add(CityModel("Vienna", "Austria"))
        arrayList.add(CityModel("Havana", "Cuba"))
        arrayList.add(CityModel("Cairo", "Egypt"))
        arrayList.add(CityModel("Prague", "Czech Republic"))
        arrayList.add(CityModel("Copenhagen", "Denmark"))
        arrayList.add(CityModel("Jakarta", "Indonesia"))
        arrayList.add(CityModel("Nairobi", "Kenya"))
        arrayList.add(CityModel("Manila", "Philippines"))
        arrayList.add(CityModel("Shuya", "Russia"))
        arrayList.add(CityModel("Vichuga", "Russia"))
        arrayList.add(CityModel("Teikovo", "Russia"))
        arrayList.add(CityModel("Kohma", "Russia"))
        arrayList.add(CityModel("Puchezh", "Russia"))
        return arrayList
    }

    // Пробрасываем название города в MainActivity
    override fun onCityClickListener(city: CityModel) {
        val intent = Intent()
        intent.putExtra("name", city.nameCity)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    // Реализация меню поиска по городам
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu, menu)
        val menuItem = menu!!.findItem(R.id.search)

        if (menuItem != null) {

            val searchView = menuItem.actionView as androidx.appcompat.widget.SearchView

            val editText =
                searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText.hint = "Введите запрос..."

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true

                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText!!.isNotEmpty()) {
                        displayList.clear()
                        val search = newText.toLowerCase(Locale.getDefault())
                        arrayList.forEach {

                            if (it.nameCity.toLowerCase(Locale.getDefault()).contains(search)) {
                                displayList.add(it)

                            }
                        }

                        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
                        recyclerView.adapter!!.notifyDataSetChanged()

                    } else {

                        displayList.clear()
                        displayList.addAll(arrayList)

                        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
                        recyclerView.adapter!!.notifyDataSetChanged()

                    }

                    return true

                }

            })

        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}



