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
import com.example.appdate.database.UsuarioEntity
import com.example.appdate.helper.doAsync
import kotlinx.android.synthetic.main.activity_main_add_edit.*
import kotlinx.android.synthetic.main.activity_registro.*
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

    var idD: String=""
    var NameD: String=""
    var hilo: MainActivityAddEdit.webService? = null
    val jsonParam = JSONObject()
    companion object {
        // Extra for the task ID to be received in the intent
        val EXTRA_DATE_USERID = "extraDateUserId"
        val EXTRA_DATE_NAME ="extraDateName"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_add_edit)
        //setSupportActionBar(toolbar)

        etFecha.setOnClickListener {
            val c = Calendar.getInstance()
            var day = c.get(Calendar.DAY_OF_MONTH)
            var month = c.get(Calendar.MONTH)
            var year = c.get(Calendar.YEAR)
            if (etFecha.length() != 0){
                val sFec : String = etFecha.text.toString()
                val fecha : Date = convFecha(sFec)
                day = dayFromDate(fecha)
                month = monthFromDate(fecha)
                year = yearFromDate(fecha)
            }

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                val sMes = "$monthOfYear"
                val iMes = sMes.toInt()+1
                etFecha.setText("$year-" + iMes.toString() + "-$dayOfMonth")
            }, year, month, day)

            //show datepicker
            dpd.show()
        }




        etHora.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR)
            val minute = c.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { view, h, m ->

                val sH = "$h"
                val iM ="$m"
                etHora.setText("$h:" + m.toString())

            }),hour,minute,false)


            //show datepicker
            tpd.show()
        }




        val intent=intent
        idD = intent.getStringExtra(MainActivityAddEdit.EXTRA_DATE_USERID)
        NameD = intent.getStringExtra(MainActivityAddEdit.EXTRA_DATE_NAME)

        etTitulo.setText(NameD)

        val array = arrayOf<String>("Centro", "Olivos")
        val  Spinner = findViewById(R.id.idspinner) as Spinner
        var adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1,array)
        Spinner.adapter=adaptador


        Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                // Toast.makeText(this@MainActivity, array[i], Toast.LENGTH_LONG ).show()
                if (i == 0){
                    edLocation.setText("1")
                }
                if (i == 1)
                {
                    edLocation.setText("2")
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }


        fab3.setOnClickListener { view ->
            if (etTitulo.text.length == 0 || edLocation.text.length == 0 || etFecha.text.length ==0 || etHora.text.length ==0)
            {
                Snackbar.make(view, "Error: Faltan datos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                etTitulo.requestFocus()
            }
            else
            {
                var nomper = etTitulo.text.toString()
                //var DateC = etDate.text.toString()
                //var TimeC = etTime.text.toString()
                var location = edLocation.text.toString()
                var DateCi = etFecha.text.toString()
                var TimeCi = etHora.text.toString()
                //var Int:idU = 1

                doAsync{
                    Log.d("Salazar","Entro al doAsyn del hilo insertar")
                    hilo = webService()
                    hilo?.execute("Insert", "2", nomper, DateCi, TimeCi, location,idD)
                    runOnUiThread {
                    }
                }.execute()
            }
        }


    }//final del override del onCreate



    inner class webService(): AsyncTask<String, String, String>() {
        override fun doInBackground(vararg p0: String?): String {//recivimos los argumentos del hilo.execute(parmet
            var url: URL? = null
            var devuelve = ""
            try {
                val option = p0[1]
                val urlConn: HttpURLConnection
                val printout: DataOutputStream
                val input: DataInputStream

                if (option == "2") {
                    Log.d("Salazar", "Entro a la opcion 2")
                    var nomper = p0[2]
                    var DateCi = p0[3]
                    var TimeCi = p0[4]
                    var Location = p0[5]
                    var idU = p0[5]
                    jsonParam.put("NomPersona", nomper)
                    jsonParam.put("FechaCita", DateCi)
                    jsonParam.put("HoraCita", TimeCi)
                    jsonParam.put("idLocacion",Location)
                    jsonParam.put("idUsuario", idU)
                    url = URL("http://192.168.1.107/AppDate/WebServicedate/InsertDate.php")

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

            val inte = Intent(this@MainActivityAddEdit, MainActivity::class.java)
            //inte.putExtra(MainActivity.EXTRA_CONTACTO_CORREO, correo)
            Log.d("Salazar2",result)
            try {
                var respuestaJSON =
                    JSONObject(result)//dentro de un avariable guardamos el resultado que jalamos en un jsonObJET
                val resultJSON = respuestaJSON.getString("success")//decimos que el valor que queremos es el successv
                if (resultJSON == "204") {
                    val msj = respuestaJSON.getString("message")
                    Toast.makeText(this@MainActivityAddEdit, msj.toString(), Toast.LENGTH_SHORT).show()
                }
                if (resultJSON == "203") {
                    val msj = respuestaJSON.getString("message")
                    Toast.makeText(this@MainActivityAddEdit, msj.toString(), Toast.LENGTH_SHORT).show()
                    etUsrId.setText("")

                }
                if (resultJSON == "202") {
                    Log.d("Salazar2","Ok dentro 202")
                    val msj = respuestaJSON.getString("message")
                    Toast.makeText(this@MainActivityAddEdit, msj.toString(), Toast.LENGTH_SHORT).show()
                    var nomper = etTitulo.text.toString()
                    //var DateC = etDate.text.toString()
                    //var TimeC = etTime.text.toString()
                    var location = edLocation.text.toString()
                    var DateCi = etFecha.text.toString()
                    var TimeCi = etHora.text.toString()
                   // val dfec = convFecha(DateCi)

                    //val registro2Entity = UsuarioEntity(nomusu = nom.toString(),correo = corr.toString(),telefono = tel.toString(),pwd = pwd.toString(),fecha = Date())
                    Log.d("Salazar2","Amtes del doAsync")

                    val registroEntity = DateEntity(nomper=nomper,fechacita = DateCi,horacita = TimeCi,fecha = Date(),Status = "0",idlocacion = location,idUsuario = idD)
                    doAsync {
                        Log.d("Salazar2","Entro al registro del de room")
                        //AppDatabase.getInstance(this@RegistroActivity)!!.usuarioDao().insertUsuario(registroEntity)
                       // AppDatabase.getInstance(this@MainActivityAddEdit)!!.dateDao().insertDate(registroEntity)
                        AppDatabase.getInstance(this@MainActivityAddEdit)?.dateDao()?.insertDate(registroEntity)
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

   //Comienzo de las converciones del boton de fecha
    private fun convFecha(sFec: String): Date
    {
        var formatoDelTexto = SimpleDateFormat("yyyy-MM-dd")
        var fecha: Date? = null
        try {
            fecha = formatoDelTexto.parse(sFec);
        } catch (ex: ParseException) {
            val sFec1 = "1900-01-01"
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
    //Termino de las converciones del boton de fecha




}
