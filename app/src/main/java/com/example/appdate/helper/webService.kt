package com.example.appdate.helper

import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class webService() : AsyncTask<JSONObject, String, String>() {

    override fun doInBackground(vararg parameters: JSONObject?): String {
        var url: URL? = null
        var devuelve = ""
        try {
            val urlConn: HttpURLConnection
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
            write.write(parameters.toString())
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
            Log.d("MalformedURLException", ex.message)
            ex.printStackTrace()
        } catch (ex: IOException) {
            Log.d("IOException", ex.message)
            ex.printStackTrace()
        } catch (ex: JSONException) {
            Log.d("JSONException", ex.message)
            ex.printStackTrace()
        }

        return devuelve
    }

}