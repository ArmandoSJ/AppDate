package com.example.appdate.helper

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    //Converciones del boton de fecha
    fun convFecha(sFec: String): Date {
        val formatoDelTexto = SimpleDateFormat("dd-MM-YYYY")
        var fecha: Date? = null
        try {
            fecha = formatoDelTexto.parse(sFec);
        } catch (ex: ParseException) {
            val sFec1 = "1900-01-01"
            fecha = formatoDelTexto.parse(sFec1);
        }
        return  fecha!!
    }

    fun monthFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.MONTH)
    }

    fun yearFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.YEAR)
    }

    fun dayFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH)
    }




}