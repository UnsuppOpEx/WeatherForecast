package com.android.weatherforecast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.weatherpart3.R

/**
 * Класс адаптер
 */
class CityAdapter(val arrayList: ArrayList<CityModel>, val context: Context,
    private val cityClickListener: CityClickListener) :
    RecyclerView.Adapter<CityAdapter.ViewHolder>() {
    private val inflater = LayoutInflater.from(context)

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameCity: TextView = itemView.findViewById(R.id.nameCity)
        private val country: TextView = itemView.findViewById(R.id.country)


        fun bind(model: CityModel) {
            nameCity.text = model.nameCity
            country.text = model.country
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View = inflater.inflate(R.layout.activity_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayList[position])

        val city = arrayList[position]
        holder.itemView.setOnClickListener {
            cityClickListener.onCityClickListener(city)

        }
    }
}

