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

    //-------------- FIN ASIGNACION DE VARIABLES DEL ADAPTADOR--------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

        //--------------INSERT DE LA CITA DE PRUEBA--------------
        /*
        try {
            doAsync{
                Log.d("Salazar","Entro al insert")
                val regist = AppDatabase.getInstance(this@MainActivity)?.dateDao()?.loadAllRegistro()
                runOnUiThread { //Este es un método del objeto Activity y puede usarlo desde cualquier hilo que tenga acceso a los métodos del objeto Activity para ejecutar una función en el hilo de la interfaz de usuario.
                    if (regist == null) {// si el valor del registro es null ejecuta la funcion InsertaClasif()
                        Log.d("Salazar","Entro al if")
                        InsertaDate()
                    }
                }
            }.execute() //ejecutamos el doAsync

        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG)
        }*/
        //-------------- FIN DEL INSERT DE LA CITA DE PRUEBA--------------


        //-------------- VALIDACION DEL REGISTRO DE LA PANTALLA INICIAL--------------
        val intent = intent

        try {
            doAsync {
                val regist = AppDatabase.getInstance(this@MainActivity)?.usuarioDao()?.loadAllRegistro()

                runOnUiThread {
                    if (regist == null) {
                        val intent = Intent(this, RegistroActivity::class.java)
                        startActivity(intent)
                       // InsertaDate()
                        Log.d("Salazar","Entro al intent")
                    } else {
                       // InsertaDate()
                        idusu = regist.idusu.toString()
                        nomusu = regist.nomusu
                        sPwd = regist.pwd




                    }
                }
            }.execute()
        } catch (ex: Exception) {
        }
       //--------------FIN VALIDACION DEL REGISTRO DE LA PANTALLA INICIAL--------------

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
                    //val position = viewHolder.adapterPosition//nos reglesa la posicion en donde se ejecuto la linea
                    //val dates= viewAdapter.getTasks()
                    //AppDatabase.getInstance(this@MainActivity)?.dateDao()?.deleteDate(dates[position])

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

        //-------------- INICIO DEL RECYCLERVIEW--------------

        fab2.setOnClickListener { view ->// Cuando agregamos un nuevo cita
            val addCitaIntent = Intent(this@MainActivity, MainActivityAddEdit::class.java)
            addCitaIntent.putExtra(MainActivityAddEdit.EXTRA_DATE_USERID, idusu)
            addCitaIntent.putExtra(MainActivityAddEdit.EXTRA_DATE_NAME, nomusu)
            startActivity(addCitaIntent)
        }

    } //--------------LLAVE DEL CERRADO DEL ONCREATE--------------

    //-------------- fUNCION DEL INSERT DE LA CITA--------------


     //--------------FINAL DE LA FUNCION DEL INSERT DE LA CITA--------------

    //-------------- FUNCION DEL CLICK DEL RECYCLERVIEW--------------
    private fun onItemClickListener(date: DateEntity) {
        Toast.makeText(this,"Diste click en :"+date.nomper+date.idcita, Toast.LENGTH_LONG).show()


        val UpdateDateIntent = Intent(this@MainActivity, MainActivityEditar::class.java)
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_USEID, idusu.toString())
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_USECit, date.idcita.toString())
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_Name, date.nomper)
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_DATEC, date.fechacita.toString())
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_TIMEC, date.horacita)
        UpdateDateIntent.putExtra(MainActivityEditar.EXTRA_DATE_LOCATION, date.idlocacion)
        startActivity(UpdateDateIntent)


        // Launch AddTaskActivity adding the itemId as an extra in the intent
        //val intent = Intent(this,MainActivityDetalle::class.java)
        //intent.putExtra(MainActivityDetalle.EXTRA_AVISO_ID, aviso.id)
        //startActivity(intent)
        //Toast.makeText(this, "Clicked item" + task.description, Toast.LENGTH_LONG).show()
    }
    //--------------FIN DE LA  FUNCION DEL CLICK DEL RECYCLERVIEW--------------


  //-------------- FUNCION DEL CLICK DEL RECYCLERVIEW--------------
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
