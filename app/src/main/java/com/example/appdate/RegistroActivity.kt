package com.example.appdate

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.appdate.database.AppDatabase
import com.example.appdate.database.UsuarioEntity
import com.example.appdate.helper.doAsync
import kotlinx.android.synthetic.main.activity_registro.*
import kotlinx.android.synthetic.main.content_registro.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class RegistroActivity : AppCompatActivity() {

    var thread: webService? = null
    var vUserID : String =""
    var vNameUser : String =""
    var vPassword : String =""
    var vRoleID : String =""
    var vEmail: String = ""
    var vPhoneNumber : String=""
    val jsonParam = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
         vNameUser = etUsrId.text.toString()
         vEmail = etCorreo.text.toString()
         vPassword = etPwd.text.toString()
         vPhoneNumber = etTelefono.text.toString()

        cargaListeners()


    }

    private fun cargaListeners(){
        fab.setOnClickListener { view ->
            if (etUsrId.text.length == 0 || etPwd.text.length == 0 || etCorreo.text.length == 0 || etTelefono.text.length == 0){
                Snackbar.make(view, "Error: Faltan datos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                etUsrId.requestFocus()
            }else {

                doAsync{
                    thread = webService()
                    thread?.execute("Insert", "3", vNameUser, vEmail, vPassword, vPhoneNumber)
                    runOnUiThread {
                    }
                }.execute()
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

                if (option == "3") {
                    Log.d("Salazar","Entro a la opcion 3")
                    val nomusu = parameters[2]
                    val corr = parameters[3]
                    val pwd = parameters[4]
                    val tel = parameters[5]
                    jsonParam.put("NomUsuario", nomusu)
                    jsonParam.put("Correo", corr)
                    jsonParam.put("Password", pwd)
                    jsonParam.put("Telefono", tel)
                    url = URL("http://localhost/WebServicedate/InsertUser.php")

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

            val inte = Intent(this@RegistroActivity, MainActivity::class.java)
            inte.putExtra(MainActivity.EXTRA_CONTACTO_CORREO, vEmail)
            try {
                val respuestaJSON = JSONObject(result)
                val resultJSON = respuestaJSON.getString("success")
                val vMessage = respuestaJSON.getString("message")
                if (resultJSON == "204") {

                    Toast.makeText(this@RegistroActivity, vMessage.toString(), Toast.LENGTH_SHORT).show()
                }
                if (resultJSON == "203") {
                    Toast.makeText(this@RegistroActivity, vMessage.toString(), Toast.LENGTH_SHORT).show()
                    etUsrId.setText("")

                }
                if (resultJSON == "202") {
                    Toast.makeText(this@RegistroActivity, vMessage.toString(), Toast.LENGTH_SHORT).show()

                    val nomusu = etUsrId.text.toString()
                    val password =etPwd.text.toString()
                    val correo=  etCorreo.text.toString()
                    val telefono =etTelefono.text.toString()

                    val registroEntity = UsuarioEntity(nomusu = nomusu, correo = correo, telefono = telefono, pwd = password, fecha = Date())
                    doAsync {
                        AppDatabase.getInstance(this@RegistroActivity)!!.usuarioDao().insertUsuario(registroEntity)
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
