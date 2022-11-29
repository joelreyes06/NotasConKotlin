package com.example.notepadusingkotlin

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.notepadusingkotlin.DataBase.DbManager
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.row.*
import java.lang.Exception

class AddNoteActivity : AppCompatActivity() {

    val dbTable = "Notes"
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        try {
            val bundle : Bundle? = intent.extras
            id = bundle!!.getInt("ID", 0)
            if (id!=0)
            {
                //update note
                //change actionbar title
                supportActionBar!!.title = "Actualizar Nota"
                //change button text
                addBtn.text = "Actualizar"
                titleEt.setText(bundle.getString("name"))
                descEt.setText(bundle.getString("des"))
            }
        }catch (ex : Exception){

        }

    }

    fun addFunc(view: View)
    {
        var dbManager = DbManager(this)

        var values = ContentValues()
        values.put("Title", titleEt.text.toString())
        values.put("Description", descEt.text.toString())

        if (id == 0)
        {
            val ID = dbManager.insert(values)
            if (ID>0)
            {

                Toast.makeText(this, "Nota agregada", Toast.LENGTH_SHORT).show()
                finish()
            }
            else
            {
                Toast.makeText(this, "Error al agregar nota", Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            var selectionArgs = arrayOf(id.toString())
            val ID = dbManager.update(values, "ID=?", selectionArgs)
            if (ID>0)
            {
                Toast.makeText(this, "Nota actualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
            else
            {
                Toast.makeText(this, "Error al actualizar nota", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
