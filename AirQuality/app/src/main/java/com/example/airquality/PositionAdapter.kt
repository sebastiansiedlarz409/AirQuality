package com.example.airquality

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.models.Position

class PositionAdapter(
    context: Context,
    private val data: ArrayList<Position>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: View = inflater.inflate(R.layout.position, parent, false)

        val code: TextView = rowView.findViewById(R.id.code)
        val name: TextView = rowView.findViewById(R.id.name)
        val formula: TextView = rowView.findViewById(R.id.formula)

        code.text = data[position].ParamCode
        name.text = data[position].ParamName
        formula.text = data[position].ParamFormula

        return rowView
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }
}