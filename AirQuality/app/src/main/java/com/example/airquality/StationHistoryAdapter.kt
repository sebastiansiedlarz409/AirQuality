package com.example.airquality

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.database.StationHistoryEntity
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class StationHistoryAdapter(
    context: Context,
    private val data: MutableList<StationHistoryEntity>): BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var filteredData: MutableList<StationHistoryEntity> = data

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: View = inflater.inflate(R.layout.station_history, parent, false)

        val stationName: TextView = rowView.findViewById(R.id.stationName)
        val cityName: TextView = rowView.findViewById(R.id.cityName)
        val measureTime: TextView = rowView.findViewById(R.id.measureTime)
        val readTime: TextView = rowView.findViewById(R.id.readTime)
        val idx: TextView = rowView.findViewById(R.id.index)
        val element: LinearLayout = rowView.findViewById(R.id.stationElement)
        val icon: ImageView = rowView.findViewById(R.id.icon)

        stationName.text = filteredData[position].StationName
        cityName.text = filteredData[position].Name
        measureTime.text = "Pomiar: ${filteredData[position].Date}"
        idx.text = "Indeks: ${filteredData[position].Index}"

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val instant = Instant.ofEpochMilli(filteredData[position].ReadTime)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        readTime.text = "Odczyt: ${formatter.format(date)}"

        val index: String = filteredData[position].Index

        when {
            index.equals("Bardzo dobry") -> {
                element.setBackgroundResource(R.drawable.card_background_vg)
                icon.setImageResource(R.drawable.very_good)
            }
            index.equals("Dobry") -> {
                element.setBackgroundResource(R.drawable.card_background_g)
                icon.setImageResource(R.drawable.good)
            }
            index.equals("Umiarkowany") -> {
                element.setBackgroundResource(R.drawable.card_background_u)
                icon.setImageResource(R.drawable.um)
            }
            index.equals("Dostateczny") -> {
                element.setBackgroundResource(R.drawable.card_background_c)
                icon.setImageResource(R.drawable.com)
            }
            index.equals("Zły") -> {
                element.setBackgroundResource(R.drawable.card_background_b)
                icon.setImageResource(R.drawable.bad)
            }
            index.equals("Bardzo zły") -> {
                element.setBackgroundResource(R.drawable.card_background_vb)
                icon.setImageResource(R.drawable.very_bad)
            }
        }

        return rowView
    }

    override fun getItem(position: Int): StationHistoryEntity {
        return filteredData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return filteredData.size
    }

    fun filter(search: CharSequence?) {
        val toSearch: String = search.toString().toLowerCase(Locale.ROOT)

        val filtered: ArrayList<StationHistoryEntity> = arrayListOf()
        if (toSearch.isEmpty()) {
            filteredData = data
            notifyDataSetChanged()
        }

        val count: Int = data.size - 1

        for (i in 0..count) {
            val item: StationHistoryEntity = data[i]
            if (item.Date.toLowerCase(Locale.ROOT).contains(toSearch)) {
                filtered.add(item)
            }
        }

        filteredData = filtered
        notifyDataSetChanged()
    }
}