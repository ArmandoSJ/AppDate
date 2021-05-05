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
import kotlinx.android.synthetic.main.content_main_activity_editar.*
import kotlinx.android.synthetic.main.content_registro.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.sql.Time
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainActivityEditar : AppCompatActivity() {

    var vIDu: String=""
    var vIDd: String=""
    var vNameD: String =""
    var vDateD: String =""
    var vTimeD: String =""
    var vIDl: String =""
    var thread: MainActivityEditar.webService? = null
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
        val EXTRA_DATE_USEID = "extraDateUserId"
        val EXTRA_DATE_USECit = "extraDate"
        val EXTRA_DATE_Name = "extraDateName"
        val EXTRA_DATE_DATEC = "extraDateDatec"
        val EXTRA_DATE_TIMEC = "extraDateTimec"
        val EXTRA_DATE_LOCATION = "extraDateLocation"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_editar)

        val intent = intent
        vIDu = intent.getStringExtra(EXTRA_DATE_USEID)
        vIDd = intent.getStringExtra(EXTRA_DATE_USECit)
        vNameD = intent.getStringExtra(EXTRA_DATE_Name)
        vDateD = intent.getStringExtra(EXTRA_DATE_DATEC)
        vTimeD = intent.getStringExtra(EXTRA_DATE_TIMEC)
        vIDl = intent.getStringExtra(EXTRA_DATE_LOCATION)

        etNameD.setText(vNameD)
        etFechaE.setText(vDateD)
        etHoraE.setText(vTimeD)
        edLocationD.setText(vIDl)

        createSpinner()
        cargaListeners()
    }

    private fun cargaListeners(){
        etFechaE.setOnClickListener {
            if (etFechaE.length() != 0){
                val sFec : String = etFechaE.text.toString()
                val fecha : Date = convert.convFecha(sFec)
                vDay = convert.dayFromDate(fecha)
                vMonth = convert.monthFromDate(fecha)
                vYear = convert.yearFromDate(fecha)
            }

            val dpd = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                val sMes = "$monthOfYear"
                val iMes = sMes.toInt()+1
                etFechaE.setText("$year-" + iMes.toString() + "-$dayOfMonth")
            }, vYear, vMonth, vDay)

            //show datepicker
            dpd.show()
        }

        etHoraE.setOnClickListener {
            val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { view, h, m ->

                val sH = "$h"
                val iM ="$m"
                etHoraE.setText("$h:" + m.toString())

            }),vHour, vMinute,false)


            //show datepicker
            tpd.show()
        }


        fab3.setOnClickListener { view ->
            if (etNameD.text.length == 0 || edLocationD.text.length == 0 || etFechaE.text.length ==0 || etHoraE.text.length ==0){
                Snackbar.make(view, "Error: Faltan datos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                etNameD.requestFocus()

            }else{
                val vID = vIDd
                val vName = etNameD.text.toString()
                val vDateCita = etFechaE.text.toString()
                val vTimeCita = etHoraE.text.toString()
                val vLocation = edLocationD.text.toString()

                //var Int:idU = 1

                doAsync{
                    thread = webService() //Generamos la instancia
                    thread?.execute("Update", "1", vID, vName, vDateCita, vTimeCita, vLocation)
                    runOnUiThread{
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
        override fun doInBackground(vararg parameters: String?): String {//recivimos los argumentos del hilo.execute(parmet
            var url: URL? = null
            var devuelve = ""
            try {
                val option = parameters[1]
                val urlConn: HttpURLConnection

                if (option == "1") {
                    Log.d("Salazar", "Entro a la opcion 1")
                    val vID = parameters[2]
                    val vNameD = parameters[3]
                    val vDateCi = parameters[4]
                    val vTimeCi = parameters[5]
                    val vIdLocation = parameters[6]
                    jsonParam.put("idCita", vID)
                    jsonParam.put("NomPersona", vNameD)
                    jsonParam.put("FechaCita", vDateCi)
                    jsonParam.put("HoraCita", vTimeCi)
                    jsonParam.put("idLocacion", vIdLocation)
                    //jsonParam.put("idUsuario", idD)
                    url = URL("http://localhost/AppDate/WebServicedate/UpdateDate.php")

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

                val respuesta = urlConn.responseCode
                val result = StringBuilder()
                if (respuesta == HttpURLConnection.HTTP_OK) {
                    val inString: InputStream = urlConn.inputStream
                    val isReader = InputStreamReader(inString)
                    val bReader = BufferedReader(isReader)
                    var tempStr: String?
                    while (true) {
                        tempStr = bReader.readLine()
                        if (tempStr == null) {
                            break
                        }
                        result.append(tempStr)
                    }
                    urlConn.disconnect()
                    devuelve = result.toString()
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
            val inte = Intent(this@MainActivityEditar, MainActivity::class.java)

            try {
                val respuestaJSON = JSONObject(result)
                val resultJSON = respuestaJSON.getString("success")
                val vMessage = respuestaJSON.getString("message")
                if (resultJSON == "202") {
                    Toast.makeText(this@MainActivityEditar, vMessage.toString(), Toast.LENGTH_SHORT).show()
                }
                if (resultJSON == "203") {
                    Toast.makeText(this@MainActivityEditar, vMessage.toString(), Toast.LENGTH_SHORT).show()

                }
                if (resultJSON == "204") {
                    Toast.makeText(this@MainActivityEditar, vMessage.toString(), Toast.LENGTH_SHORT).show()

                    val vIdUser = vIDu
                    val vName = etNameD.text.toString()
                    val vID = vIDd.toInt()
                    val vLocation = edLocationD.text.toString()
                    val vDateCita = etFechaE.text.toString()
                    val vTimeCita = etHoraE.text.toString()
                    val UpdateEntity = DateEntity(idcita = vID, nomper = vName, fechacita = vDateCita, horacita = vTimeCita,
                        fecha = Date(), Status = "0", idlocacion = vLocation, idUsuario = vIdUser )
                    doAsync {
                        AppDatabase.getInstance(this@MainActivityEditar)?.dateDao()?.updateDate(UpdateEntity)
                    }.execute()

                    startActivity(inte)
                }

            } catch (ex: JSONException) {
                Log.d("Salazar", ex.message)
            } catch (ex: java.lang.Exception) {
                Log.d("Salazar", ex.message)
            }
        }
    }
}
