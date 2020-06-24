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

    var hilo: webService? = null
    var idusu : String =""
    var nomusu : String =""
    var password : String =""
    var idrole : String =""
    var correo: String = ""
    var telefono : String=""
    val jsonParam = JSONObject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        //setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            if (etUsrId.text.length == 0 || etPwd.text.length == 0 || etCorreo.text.length == 0 || etTelefono.text.length == 0)
            {
                Snackbar.make(view, "Error: Faltan datos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                etUsrId.requestFocus()
            }
            else
            {

                var nomusu = etUsrId.text.toString()
                var corr = etCorreo.text.toString()
                var pwd = etPwd.text.toString()
                var tel = etTelefono.text.toString()

                doAsync{
                    Log.d("Salazar","Entro al doAsyn del hilo insertar")
                    hilo = webService()
                    hilo?.execute("Insert", "3", nomusu, corr, pwd, tel)
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

                if (option == "3") {
                    Log.d("Salazar","Entro a la opcion 3")
                    var nomusu = p0[2]
                    var corr = p0[3]
                    var pwd = p0[4]
                    var tel = p0[5]
                    jsonParam.put("NomUsuario", nomusu)
                    jsonParam.put("Correo", corr)
                    jsonParam.put("Password", pwd)
                    jsonParam.put("Telefono", tel)
                    url = URL("http://192.168.1.107/WebServicedate/InsertUser.php")

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
            inte.putExtra(MainActivity.EXTRA_CONTACTO_CORREO, correo)
            Log.d("Salazar2",result)
            try {
                var respuestaJSON =
                    JSONObject(result)//dentro de un avariable guardamos el resultado que jalamos en un jsonObJET
                val resultJSON = respuestaJSON.getString("success")//decimos que el valor que queremos es el successv
                if (resultJSON == "204") {
                    val msj = respuestaJSON.getString("message")
                    Toast.makeText(this@RegistroActivity, msj.toString(), Toast.LENGTH_SHORT).show()
                }
                if (resultJSON == "203") {
                    val msj = respuestaJSON.getString("message")
                    Toast.makeText(this@RegistroActivity, msj.toString(), Toast.LENGTH_SHORT).show()
                    etUsrId.setText("")

                }
                if (resultJSON == "202") {
                    Log.d("Salazar2","Ok dentro 202")
                    val msj = respuestaJSON.getString("message")
                    Toast.makeText(this@RegistroActivity, msj.toString(), Toast.LENGTH_SHORT).show()
                   // val usuarioJSON = respuestaJSON.getJSONArray("usuario")
                    //si el arreglo tienen varios elementos se utiliza un for y en el indice va el valor i
                    //for (i in 0 until alumnoJSON.length() ){}
                    //val nom = usuarioJSON.getJSONObject(0).getString("NomUsuario")
                    //val corr = usuarioJSON.getJSONObject(0).getString("Correo")
                    //val tel = usuarioJSON.getJSONObject(0).getString("Telefono")
                    //val  pwd= usuarioJSON.getJSONObject(0).getString("Password")
                  //  etUsrId.setText(nom.toString())
                    // etCorreo.setText(corr.toString())
                    //etTelefono.setText(tel.toString())
                    //etPwd.setText(pwd.toString())

                    var nomusu = etUsrId.text.toString()
                    var password =etPwd.text.toString()
                    var correo=  etCorreo.text.toString()
                    var telefono =etTelefono.text.toString()


                   //val registro2Entity = UsuarioEntity(nomusu = nom.toString(),correo = corr.toString(),telefono = tel.toString(),pwd = pwd.toString(),fecha = Date())
                    Log.d("Salazar2","Amtes del doAsync")

                    val registroEntity = UsuarioEntity(nomusu = nomusu,correo = correo,telefono = telefono,pwd = password,fecha = Date())
                    doAsync {
                        Log.d("Salazar2","Entro al registro del de room")

                        AppDatabase.getInstance(this@RegistroActivity)!!.usuarioDao().insertUsuario(registroEntity)
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
}
