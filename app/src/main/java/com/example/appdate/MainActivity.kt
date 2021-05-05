package com.example.appdate

import android.content.Intent

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.Toast
import com.example.appdate.database.AppDatabase
import com.example.appdate.database.DateEntity
import com.example.appdate.helper.doAsync
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_registro.*

import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception
import java.util.*


class MainActivity : AppCompatActivity() {


    //variables globales global variables
    companion object {
        // Extra for the task ID to be received in the intent
        val EXTRA_CONTACTO_CORREO = "extraContactoCorreo"
    }
    private lateinit var nomusu: String
    private lateinit var sPwd: String
    private lateinit var idusu: String
   //--------------ASIGNACION DE VARIABLES DEL ADAPTADOR--------------
    private lateinit var viewAdapter: CitaAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    val DateList: List<DateEntity> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //-------------- VALIDACION DEL REGISTRO DE LA PANTALLA INICIAL--------------//
        val intent = intent

        try {
            doAsync {
                val regist = AppDatabase.getInstance(this@MainActivity)?.usuarioDao()?.loadAllRegistro()

                runOnUiThread {
                    if (regist == null) {
                        val intent = Intent(this, RegistroActivity::class.java)
                        startActivity(intent)
                        Log.d("Salazar","Entro al intent")
                    } else {
                        idusu = regist.idusu.toString()
                        nomusu = regist.nomusu
                        sPwd = regist.pwd

                    }
                }
            }.execute()
        } catch (ex: Exception) {
        }


        //-------------- INICIO DEL RECYCLERVIEW--------------
        viewManager = LinearLayoutManager(this)
        viewAdapter = CitaAdapter(DateList, this, { date: DateEntity ->  onItemClickListener(date) })

        recyclerViewTasks.apply {
            setHasFixedSize(true)

            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                doAsync{
                    val position = viewHolder.adapterPosition
                    val citas= viewAdapter.getTasks()
                    if (citas[position].Status == "0"){
                        citas[position].Status = "1"
                       AppDatabase.getInstance((this@MainActivity))?.dateDao()?.updateDate(citas[position])
                    }
                    retrieveDates()
                }.execute()

            }
        }).attachToRecyclerView(recyclerViewTasks)

        fab2.setOnClickListener { view ->// Cuando agregamos un nuevo cita
            val addCitaIntent = Intent(this@MainActivity, MainActivityAddEdit::class.java)
            addCitaIntent.putExtra(MainActivityAddEdit.EXTRA_DATE_USERID, idusu)
            addCitaIntent.putExtra(MainActivityAddEdit.EXTRA_DATE_NAME, nomusu)
            startActivity(addCitaIntent)
        }

    }


    /**
     * FUNCION DEL CLICK DEL RECYCLERVIEW
     */
    private fun onItemClickListener(date: DateEntity) {
        Toast.makeText(this,"Diste click en :"+date.nomper+date.idcita, Toast.LENGTH_LONG).show()

        val UpdateDateIntent = Intent(this@MainActivity, MainActivityEditar::class.java)
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_USEID, idusu)
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_USECit, date.idcita.toString())
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_Name, date.nomper)
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_DATEC, date.fechacita)
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_TIMEC, date.horacita)
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_LOCATION, date.idlocacion)
        startActivity(UpdateDateIntent)
    }

    override fun onResume() {
        super.onResume()
        retrieveDates()
    }

    private fun retrieveDates() {
        doAsync {
            val tasks = AppDatabase.getInstance(this@MainActivity)?.dateDao()?.getAll()
            runOnUiThread {
                viewAdapter.setTask(tasks!!)
            }
        }.execute()
    }

}
