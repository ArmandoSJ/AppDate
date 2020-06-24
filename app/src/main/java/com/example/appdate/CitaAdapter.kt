package com.example.appdate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appdate.database.DateEntity
import kotlinx.android.synthetic.main.citas_list_item.view.*

class CitaAdapter(private var mCitaEntries:List<DateEntity>,
                   private val mContext: Context, private val clickListener: (DateEntity) -> Unit)
    : RecyclerView.Adapter<CitaAdapter.CitaViewHolder>() {


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val layoutInflater = LayoutInflater.from(mContext)
        return CitaViewHolder(layoutInflater.inflate(R.layout.citas_list_item, parent, false))
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(mCitaEntries[position], mContext, clickListener)
    }

    /**
     * Devuelve la cantidad de elementos para mostrar.
     */
    override fun getItemCount(): Int = mCitaEntries.size

    /**
     * Cuando los datos cambian, este metodo actualiza la lista de avisoEntries
     * y notifica al adaptador a usar estos nuevos valores
     */
    fun setTask(citasEntries: List<DateEntity>){
        mCitaEntries = citasEntries
        notifyDataSetChanged()
    }

    fun getTasks(): List<DateEntity> = mCitaEntries


    // Clase interna para crear ViewHolders
    class CitaViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind (Date:DateEntity, context: Context, clickListener: (DateEntity) -> Unit){
            //Asigna los valores a los elementos del aviso_list_item
            itemView.deNom.text =Date.nomper
            itemView.deFecha.text =Date.fechacita.toString()
            itemView.deHora.text =Date.horacita
            //<itemView.tvTelefono.text = contact.telcelular.toString()

            itemView.setOnClickListener{ clickListener(Date)}
        }
    }
}
