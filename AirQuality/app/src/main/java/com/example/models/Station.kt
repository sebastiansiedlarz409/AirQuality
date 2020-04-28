package com.example.models

class Station(var Id: Int, var StationName: String, var CityId: Int,
              var Name: String, var CommuneName: String,
              var DistrictName: String, var ProvinceName: String,
              var Lat: String, var Lon: String) : Comparable<Station>{

    override fun compareTo(other: Station): Int {
        if(this.Name > other.Name){
            return 1
        }
        else{
            return -1;
        }
    }

}