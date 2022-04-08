package com.watermelon.misgastos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.watermelon.misgastos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val database = FirebaseDatabase.getInstance("")
    val listass = ArrayList<Gastos>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.rvListas.layoutManager=LinearLayoutManager(this)
        binding.rvListas.setHasFixedSize(true)
        getData()

        binding.btnAddLista.setOnClickListener {
            val view = EditText(this)
            view.hint="Nombre de la lista"
            AlertDialog.Builder(this)
                .setTitle("Ingrese el nombre de la lista")
                .setView(view)
                .setPositiveButton("Añadir"){dialog, _ ->
                    val nombre = view.text.toString()
                    val lista = Gastos(nombre)
                    database.getReference("Listas").child(nombre).setValue(lista).addOnSuccessListener {
                        getData()
                        dialog.dismiss()
                    }
                }
                .create().show()
        }
        val swipeGesture = object :SwipeGesture(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.RIGHT->{
                        Adapter(this@MainActivity,listass).deleteItem(viewHolder.adapterPosition+1)
                        database.getReference("Listas").child(listass[viewHolder.adapterPosition].nombre!!).removeValue().addOnSuccessListener {
                            getData()
                        }
                    }
                }

            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.rvListas)
    }

    private fun getData() {
        database.getReference("Listas").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                listass.clear()
                    for (nombre in p0.children){
                        val listas = nombre.getValue(Gastos::class.java)
                        listass.add(listas!!)
                    }
                    binding.rvListas.adapter=Adapter(this@MainActivity,listass)

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}