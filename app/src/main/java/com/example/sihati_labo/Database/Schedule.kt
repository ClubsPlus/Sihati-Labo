package com.example.sihati_labo.Database

data class Schedule (
    var date:String,
    var laboratory_id:String,
    var limite:Int,
    var time_Start:String,
    var time_end:String,
    var person:Int?=0
)