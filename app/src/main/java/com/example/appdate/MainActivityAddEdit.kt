package com.example.appdate

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.appdate.database.AppDatabase
import com.example.appdate.database.DateEntity
import com.example.appdate.helper.Utils
import com.example.appdate.helper.doAsync
import kotlinx.android.synthetic.main.activity_main_add_edit.*
import kotlinx.android.synthetic.main.content_main_activity_add_edit.*
import kotlinx.android.synthetic.main.content_registro.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainActivityAddEdit : AppCompatActivity() {

    var vID : String = ""
    var vNameD : String = ""
    var thread : MainActivityAddEdit.webService? = null
    val jsonParam = JSONObject()
    val convert = Utils()

    val vCalendar = Calendar.getInstance()
    var vDay = vCalendar.get(Calendar.DAY_OF_MONTH)
    var vMonth = vCalendar.get(Calendar.MONTH)
    var vYear = vCalendar.get(Calendar.YEAR)

    val vHour = vCalendar.get(Calendar.HOUR)
    val vMinute = vCalendar.get(Calendar.MINUTE)

    companion object {
        // Extra for the task ID to be received in the intent
        val EXTRA_DATE_USERID = "extraDateUserId"
        val EXTRA_DATE_NAME ="extraDateName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_add_edit)

        val intent  = intent
        vID = intent.getStringExtra(MainActivityAddEdit.EXTRA_DATE_USERID)
        vNameD = intent.getStringExtra(MainActivityAddEdit.EXTRA_DATE_NAME)
        etTitulo.setText(vNameD)

        createSpinner()

        cargaListener()

    }

    /**
     * Metodo para cargar las acciones de las funciones de la UI
     */
    private fun cargaListener(){
        etFecha.setOnClickListener {

            if (etFecha.length() != 0){

                val sFec : String = etFecha.text.toString()
                val fecha : Date = convert.convFecha(sFec)
                vDay = convert.dayFromDate(fecha)
                vMonth = convert.monthFromDate(fecha)
                vYear = convert.yearFromDate(fecha)
            }

            val dpkDate = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                val sMes = "$monthOfYear"
                val iMes = sMes.toInt() + 1
                etFecha.setText("$year-" + iMes.toString() + "-$dayOfMonth")
            }, vYear, vMonth, vDay)

            //show datepicker
            dpkDate.show()
        }


        etHora.setOnClickListener {

            val dpkTime = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { view, h, m ->

                val sH = "$h"
                val iM ="$m"
                etHora.setText("$h:" + m.toString())

            }),vHour, vMinute,false)


            //show datepicker
            dpkTime.show()
        }


        fab3.setOnClickListener { view ->
            if (etTitulo.text.length == 0 || edLocation.text.length == 0 || etFecha.text.length ==0 || etHora.text.length ==0){
                Snackbar.make(view, "Error: Faltan datos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                etTitulo.requestFocus()

            }else{
                var nomper = etTitulo.text.toString()
                var location = edLocation.text.toString()
                var vDateCi = etFecha.text.toString()
                var vTimeCi = etHora.text.toString()
                //var Int:idU = 1

                doAsync{
                    Log.d("log 1","Entro al doAsyn del hilo insertar")
                    thread = webService()
                    thread?.execute("Insert", "2", nomper, vDateCi, vTimeCi, location, vID)
                    runOnUiThread {
                    }
                }.execute()
            }
        }
    }

    /**
     * Metodo para crear el spinner the la UI
     */
    private fun createSpinner(){
        val arrayCalles = arrayOf<String>("Centro", "Olivos")
        val Spinner = findViewById(R.id.idspinner) as Spinner
        var adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayCalles)

        Spinner.adapter = adaptador


        Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                if (i == 0){
                    edLocation.setText("1")
                }else if (i == 1){
                    edLocation.setText("2")
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }
    }


    inner class webService(): AsyncTask<String, String, String>() {
        override fun doInBackground(vararg parameters: String?): String {//recibimos los argumentos del hilo.execute(parmet
            var url: URL? = null
            var devuelve = ""
            try {
                val option = parameters[1]
                val urlConn: HttpURLConnection

                if (option == "2") {
                    Log.d("Log 2", "Entro a la opcion 2")
                    val nomper = parameters[2]
                    val vDateCi = parameters[3]
                    val vTimeCi = parameters[4]
                    val vLocation = parameters[5]
                    val idU = parameters[5]
                    jsonParam.put("NomPersona", nomper)
                    jsonParam.put("FechaCita",  vDateCi)
                    jsonParam.put("HoraCita",   vTimeCi)
                    jsonParam.put("idLocacion", vLocation)
                    jsonParam.put("idUsuario",  idU)

                    url = URL("http://localhost/AppDate/WebServicedate/InsertDate.php")

                }

                //Abrimos la conexion al WB alojando en el servidor web
                urlConn = url?.openConnection() as HttpURLConnection
                //Configuracion de parametros
                urlConn.doInput = true
                urlConn.doOutput = true
                urlConn.useCaches = false
                urlConn.setRequestProperty("Content-Type", "application/json")
                urlConn.setRequestProperty("Accept", "application/json")
                urlConn.connect()

                val os = urlConn.outputStream
                val write = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                write.write(jsonParam.toString())
                write.flush()
                write.close()

                val vResponse = urlConn.responseCode
                val vResult = StringBuilder()
                if (vResponse == HttpURLConnection.HTTP_OK) {
                    Log.d("Log 3", "Si se conecto")
                    val inString: InputStream = urlConn.inputStream
                    val isReader = InputStreamReader(inString)
                    val bReader = BufferedReader(isReader)
                    var tempStr: String?
                    while (true) {
                        tempStr = bReader.readLine()
                        if (tempStr == null) {
                            break
                        }
                        vResult.append(tempStr)
                    }
                    urlConn.disconnect()
                    devuelve = vResult.toString()
                }

            } catch (ex: MalformedURLException) {
                Log.d("Erro WS", ex.message)
                ex.printStackTrace()
            } catch (ex: IOException) {
                Log.d("Erro WS", ex.message)
                ex.printStackTrace()
            } catch (ex: JSONException) {
                Log.d("Erro WS", ex.message)
                ex.printStackTrace()
            } catch (ex: Exception) {
                Log.d("Erro WS", ex.message)
                ex.printStackTrace()
            }
            return devuelve
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val inte = Intent(this@MainActivityAddEdit, MainActivity::class.java)
            Log.d("log 4", result)
            try {
                val vResponseJson = JSONObject(result)//dentro de un avariable guardamos el resultado que obtenemos en un jsonObjet
                val resultJSON = vResponseJson.getString("success")//Obtenemos la respuesta
                val vMessage = vResponseJson.getString("message")
                if (resultJSON == "204") {
                    Toast.makeText(this@MainActivityAddEdit, vMessage.toString(), Toast.LENGTH_SHORT).show()
                }
                if (resultJSON == "203"){
                    Toast.makeText(this@MainActivityAddEdit, vMessage.toString(), Toast.LENGTH_SHORT).show()
                    etUsrId.setText("")

                }
                if (resultJSON == "202") {
                    val vMessage = vResponseJson.getString("message")
                    Toast.makeText(this@MainActivityAddEdit, vMessage.toString(), Toast.LENGTH_SHORT).show()
                    val nomper = etTitulo.text.toString()
                    val location = edLocation.text.toString()
                    val vDateCi = etFecha.text.toString()
                    val vTimeCi = etHora.text.toString()
                    val registroEntity = DateEntity(nomper = nomper,fechacita = vDateCi,horacita = vTimeCi,fecha = Date(), Status = "0",
                        idlocacion = location, idUsuario = vID)

                    doAsync {
                        AppDatabase.getInstance(this@MainActivityAddEdit)?.dateDao()?.insertDate(registroEntity)
                    }.execute()

                    startActivity(inte)

                }

            } catch (ex: JSONException) {
                Log.d("JSONException", ex.message)
            } catch (ex: java.lang.Exception) {
                Log.d("Exception", ex.message)
            }
        }
    }

}
