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

    var idU: String=""
    var idD: String=""
    var nameD: String =""
    var DateD: String =""
    var TimeD: String =""
    var idL: String =""
  var hilo: MainActivityEditar.webService? = null
    val jsonParam = JSONObject()
    companion object {
        // Extra for the task ID to be received in the intent
        val EXTRA_DATE_USEID = "extraDateUserId"
        val EXTRA_DATE_USECit = "extraDate"
        val EXTRA_DATE_Name = "extraDateName"
        val EXTRA_DATE_DATEC = "extraDateDatec"
        val EXTRA_DATE_TIMEC = "extraDateTimec"
        val EXTRA_DATE_LOCATION = "extraDateLocation"
        private val DEFAULT_AVISO_ID: Int = -1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_editar)


        etFechaE.setOnClickListener {
            val c = Calendar.getInstance()
            var day = c.get(Calendar.DAY_OF_MONTH)
            var month = c.get(Calendar.MONTH)
            var year = c.get(Calendar.YEAR)
            if (etFechaE.length() != 0){
                val sFec : String = etFechaE.text.toString()
                val fecha : Date = convFecha(sFec)
                day = dayFromDate(fecha)
                month = monthFromDate(fecha)
                year = yearFromDate(fecha)
            }

            val dpd = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                val sMes = "$monthOfYear"
                val iMes = sMes.toInt()+1
                etFechaE.setText("$year-" + iMes.toString() + "-$dayOfMonth")
            }, year, month, day)

            //show datepicker
            dpd.show()
        }

        etHoraE.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR)
            val minute = c.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { view, h, m ->

                val sH = "$h"
                val iM ="$m"
                etHoraE.setText("$h:" + m.toString())

            }),hour,minute,false)


            //show datepicker
            tpd.show()
        }



        val array = arrayOf<String>("Centro", "Olivos")
        val  Spinner = findViewById(R.id.etLocationD) as Spinner
        var adaptador2 = ArrayAdapter(this, android.R.layout.simple_list_item_1,array)
        Spinner.adapter=adaptador2


        Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                // Toast.makeText(this@MainActivity, array[i], Toast.LENGTH_LONG ).show()
                if (i == 0){
                    edLocationD.setText("1")
                }
                if (i == 1)
                {
                    edLocationD.setText("2")
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }




        val intent=intent
        idU = intent.getStringExtra(EXTRA_DATE_USEID)
        idD = intent.getStringExtra(EXTRA_DATE_USECit)
        nameD = intent.getStringExtra(EXTRA_DATE_Name)
        DateD = intent.getStringExtra(EXTRA_DATE_DATEC)
        TimeD = intent.getStringExtra(EXTRA_DATE_TIMEC)
        idL = intent.getStringExtra(EXTRA_DATE_LOCATION)

        etNameD.setText(nameD)
        //etDateD.setText(DateD)
        //etTimeD.setText(TimeD)
        etFechaE.setText(DateD)
        etHoraE.setText(TimeD)
        edLocationD.setText(idL)
        //etLocation. = idL
     //   etName.text = nameD


        fab3.setOnClickListener { view ->
            if (etNameD.text.length == 0 || edLocationD.text.length == 0 || etFechaE.text.length ==0 || etHoraE.text.length ==0)
            {
                Snackbar.make(view, "Error: Faltan datos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                etNameD.requestFocus()
            }
            else
            {
                var id = idD
                var NAME = etNameD.text.toString()
                //var DATE = etDateD.text.toString()
                //var TIME = etTimeD.text.toString()
                var DATECi = etFechaE.text.toString()
                var TIMECi = etHoraE.text.toString()
                var LOCATION = edLocationD.text.toString()

                //var Int:idU = 1

                doAsync{
                     hilo = webService() //Generamos la instancia
                     hilo?.execute("Update","1",id,NAME,DATECi,TIMECi,LOCATION)
                    runOnUiThread{
                    }
                }.execute()
            }
        }



    }//ONCREATE CLOSED

    inner class webService(): AsyncTask<String, String, String>() {
        override fun doInBackground(vararg p0: String?): String {//recivimos los argumentos del hilo.execute(parmet
            var url: URL? = null
            var devuelve = ""
            try {
                val option = p0[1]
                val urlConn: HttpURLConnection
                val printout: DataOutputStream
                val input: DataInputStream

                if (option == "1") {
                    Log.d("Salazar", "Entro a la opcion 1")
                    var id = p0[2]
                    var NameD = p0[3]
                    var DateCi = p0[4]
                    var TimeCi = p0[5]
                    var idL = p0[6]
                    jsonParam.put("idCita", id)
                    jsonParam.put("NomPersona", NameD)
                    jsonParam.put("FechaCita", DateCi)
                    jsonParam.put("HoraCita", TimeCi)
                    jsonParam.put("idLocacion", idL)
                    //jsonParam.put("idUsuario", idD)
                    url = URL("http://192.168.1.107/AppDate/WebServicedate/UpdateDate.php")

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
                    Log.d("Salazar", "Si se conecto")
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
            //inte.putExtra(MainActivity.EXTRA_CONTACTO_CORREO, correo)
            Log.d("Salazar2",result)
            try {
                var respuestaJSON =
                    JSONObject(result)//dentro de un avariable guardamos el resultado que jalamos en un jsonObJET
                val resultJSON = respuestaJSON.getString("success")//decimos que el valor que queremos es el successv
                if (resultJSON == "202") {
                    val msj = respuestaJSON.getString("message")
                    Toast.makeText(this@MainActivityEditar, msj.toString(), Toast.LENGTH_SHORT).show()
                }
                if (resultJSON == "203") {
                    val msj = respuestaJSON.getString("message")
                    Toast.makeText(this@MainActivityEditar, msj.toString(), Toast.LENGTH_SHORT).show()

                }
                if (resultJSON == "204") {
                    Log.d("Salazar2","Ok dentro 204")
                    val msj = respuestaJSON.getString("message")
                    Toast.makeText(this@MainActivityEditar, msj.toString(), Toast.LENGTH_SHORT).show()

                    //val registro2Entity = UsuarioEntity(nomusu = nom.toString(),correo = corr.toString(),telefono = tel.toString(),pwd = pwd.toString(),fecha = Date())
                    Log.d("Salazar2","Amtes del doAsync")
                    var IDU = idU.toString()
                    var NAME = etNameD.text.toString()
                    var id = idD.toInt()
                   // var DATE = etDateD.text.toString()
                   // var TIME = etTimeD.text.toString()
                    var LOCATION = edLocationD.text.toString()
                    var DATECi = etFechaE.text.toString()
                    var TIMECi = etHoraE.text.toString()
                    //var sfec = etFechaE.text.toString()
                    /*
                    if (etFechaE.length() == 0)
                        sfec = "1900-01-01"

                    val fecha = convFecha(sfec)
                   // val dfec = convFecha(DATECi)
                   */
                    val UpdateEntity = DateEntity(idcita = id, nomper=NAME, fechacita = DATECi, horacita = TIMECi, fecha = Date(), Status = "0", idlocacion = LOCATION, idUsuario = IDU )
                    doAsync {
                        Log.d("Salazar2","Entro al registro del de room")
                        //AppDatabase.getInstance(this@RegistroActivity)!!.usuarioDao().insertUsuario(registroEntity)
                        // AppDatabase.getInstance(this@MainActivityAddEdit)!!.dateDao().insertDate(registroEntity)
                        AppDatabase.getInstance(this@MainActivityEditar)?.dateDao()?.updateDate(UpdateEntity)
                    }.execute()
                    //val registroEntity = UsuarioEntity(nomusu = etUsrId.text.toString(),correo = etCorreo.text.toString(),telefono = etTelefono.text.toString(),pwd = etPwd.text.toString(),fecha = Date())
                    //AppDatabase.getInstance(this@RegistroActivity)!!.usuarioDao().insertUsuario(registroEntity)
                    Log.d("Salazar2","Salio okay")

                    // val messageJSON1 = respuestaJSON.getString("message")
                    // resultado = messageJSON1
                    startActivity(inte)

                }


            } catch (ex: JSONException) {
                Log.d("Salazar", ex.message)
            } catch (ex: java.lang.Exception) {
                Log.d("Salazar", ex.message)
            }

            // resultado.setText(result)
        }
    }


    private fun convFecha(sFec: String): Date
    {
        var formatoDelTexto = SimpleDateFormat("yyyy-MM-dd")
        var fecha: Date? = null
        try {
            fecha = formatoDelTexto.parse(sFec);
        } catch (ex: ParseException) {
            val sFec1 = "2019-01-01"
            fecha = formatoDelTexto.parse(sFec1);
        }
        return  fecha!!
    }
    private fun monthFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.MONTH)
    }
    private fun yearFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.YEAR)
    }

    private fun dayFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH)
    }
}
