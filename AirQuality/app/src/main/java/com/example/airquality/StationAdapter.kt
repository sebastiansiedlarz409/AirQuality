package com.example.airquality

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.models.Station

class StationAdapter(
    context: Context,
    private val data: ArrayList<Station>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var filteredData: ArrayList<Station> = data

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: View = inflater.inflate(R.layout.station, parent, false)

        val stationName: TextView = rowView.findViewById(R.id.stationName)
        val cityName: TextView = rowView.findViewById(R.id.cityName)
        val communeName: TextView = rowView.findViewById(R.id.communeName)
        val districtName: TextView = rowView.findViewById(R.id.districtName)
        val provinceName: TextView = rowView.findViewById(R.id.provinceName)
        val element: LinearLayout = rowView.findViewById(R.id.stationElement)

        stationName.text = filteredData[position].StationName
        cityName.text = filteredData[position].Name
        communeName.text = filteredData[position].CommuneName
        districtName.text = filteredData[position].DistrictName
        provinceName.text = filteredData[position].ProvinceName

        val index: String? = filteredData[position].Index?.Index

        if(index.equals("Bardzo dobry")){
            element.setBackgroundResource(R.drawable.card_background_vg)
        }
        else if(index.equals("Dobry")){
            element.setBackgroundResource(R.drawable.card_background_g)
        }
        else if(index.equals("Umiarkowany")){
            element.setBackgroundResource(R.drawable.card_background_u)
        }
        else if(index.equals("Dostateczny")){
            element.setBackgroundResource(R.drawable.card_background_c)
        }
        else if(index.equals("Zły")){
            element.setBackgroundResource(R.drawable.card_background_b)
        }
        else if(index.equals("Bardzo zły")){
            element.setBackgroundResource(R.drawable.card_background_vb)
        }


        return rowView
    }

    override fun getItem(position: Int): Station {
        return filteredData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return filteredData.size
    }

    fun filter(search: CharSequence?){
        val toSearch: String = search.toString().toLowerCase()

        val filtered: ArrayList<Station> = arrayListOf()
        if(toSearch.isEmpty()){
            filteredData = data
            notifyDataSetChanged()
        }

        val count: Int = data.size - 1

        for(i in 0..count){
            val item: Station = data[i]
            if(item.Name.toLowerCase().contains(toSearch)){
                filtered.add(item)
            }
        }

        filteredData = filtered
        notifyDataSetChanged()
    }

}