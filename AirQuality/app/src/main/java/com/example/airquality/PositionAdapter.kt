package com.example.airquality

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.database.PositionEntity
import com.example.database.StationIndexEntity
import java.util.*
import kotlin.collections.ArrayList

class PositionAdapter(
    context: Context,
    private val data: ArrayList<PositionEntity>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: View = inflater.inflate(R.layout.position, parent, false)

        val code: TextView = rowView.findViewById(R.id.code)
        val name: TextView = rowView.findViewById(R.id.name)
        val formula: TextView = rowView.findViewById(R.id.formula)

        code.text = data[position].ParamCode
        formula.text = data[position].ParamFormula

        var temp: MutableList<String> = data[position].ParamName.split(" ") as MutableList<String>
        var upperResult: String = ""
        val count: Int = temp.size - 1

        for(i in 0..count){
            upperResult += temp[i].substring(0, 1).toUpperCase() + temp[i].substring(1)
            upperResult += " "
        }

        name.text = upperResult

        return rowView
    }

    override fun getItem(position: Int): PositionEntity {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }
}