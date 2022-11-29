package com.example.notepadusingkotlin

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.ClipboardManager
import android.view.*
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import com.example.notepadusingkotlin.DataBase.DbManager
import com.example.notepadusingkotlin.Model.Note
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.view.*

class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()

    //shared preference
    var mSharedPref : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSharedPref = this.getSharedPreferences("My_Data", Context.MODE_PRIVATE)

        //load sorting technique as selected before, default setting will be newest
        val mSorting = mSharedPref!!.getString("Ordenar", "reciente")
        when(mSorting)
        {
            "reciente" -> LoadQueryNewest("%")
            "antiguo" -> LoadQueryOldest("%")
            "ascendente" -> LoadQueryAscending("%")
            "descentende" -> LoadQueryDescending("%")
        }

    }

    override fun onResume() {
        super.onResume()
        //load sorting technique as selected before, default setting will be newest
        val mSorting = mSharedPref!!.getString("Ordenar", "reciente")
        when(mSorting)
        {
            "reciente" -> LoadQueryNewest("%")
            "antiguo" -> LoadQueryOldest("%")
            "ascendente" -> LoadQueryAscending("%")
            "descentende" -> LoadQueryDescending("%")
        }
    }

    private fun LoadQueryAscending(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        //sort by title
        val cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "Title")
        listNotes.clear()
        //ascending
        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID, Title, Description))

            } while (cursor.moveToNext())
        }

        //adapter
        var myNotesAdapter = MyNotesAdapter(this, listNotes)
        //set adapter
        noteLv.adapter = myNotesAdapter

        //get total number of tasks from List
        val total = noteLv.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null)
        {
            //set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "Tiene $total nota(s) en la lista..."
        }
    }

    private fun LoadQueryDescending(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        //sort by title
        val cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "Title")
        listNotes.clear()
        //descending
        if (cursor.moveToLast()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID, Title, Description))

            } while (cursor.moveToPrevious())
        }

        //adapter
        var myNotesAdapter = MyNotesAdapter(this, listNotes)
        //set adapter
        noteLv.adapter = myNotesAdapter

        //get total number of tasks from List
        val total = noteLv.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null)
        {
            //set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "Tiene $total nota(s) en la lista..."
        }
    }

    private fun LoadQueryNewest(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        //sort by ID
        val cursor = dbManager.Query(projections, "ID like ?", selectionArgs, "ID")
        listNotes.clear()

        //Newest first(the record will be entered at the bottom of previous records and has larger ID then previous records)

        if (cursor.moveToLast()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID, Title, Description))

            } while (cursor.moveToPrevious())
        }

        //adapter
        var myNotesAdapter = MyNotesAdapter(this, listNotes)
        //set adapter
        noteLv.adapter = myNotesAdapter

        //get total number of tasks from List
        val total = noteLv.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null)
        {
            //set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "Tiene $total nota(s) en la lista..."
        }
    }

    private fun LoadQueryOldest(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        //sort by ID
        val cursor = dbManager.Query(projections, "ID like ?", selectionArgs, "ID")
        listNotes.clear()

        //oldest first(the record will be entered at the bottom of previous records and has larger ID then previous records, so lesser the ID is the oldest the record is)

        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID, Title, Description))

            } while (cursor.moveToNext())
        }

        //adapter
        var myNotesAdapter = MyNotesAdapter(this, listNotes)
        //set adapter
        noteLv.adapter = myNotesAdapter

        //get total number of tasks from List
        val total = noteLv.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null)
        {
            //set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "Tiene $total nota(s) en la lista..."
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val sv:SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQueryAscending("%"+query+"%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQueryAscending("%"+newText+"%")
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item != null)
        {
            when(item.itemId)
            {
                R.id.addNote ->
                {
                    startActivity(Intent(this, AddNoteActivity::class.java))
                }
                R.id.action_sort ->
                {
                    //show sorting dialog
                    showSortDialog()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSortDialog() {
        val sortOption = arrayOf("Reciente", "Antiguo", "Titulo(Ascendente)", "Titulo(Descendiente)")
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Ordenar por")
        mBuilder.setIcon(R.drawable.ic_sort_black_24dp)
        mBuilder.setSingleChoiceItems(sortOption, -1)
        {
            dialogInterface, i ->
            if (i==0)
            {
                //newest first
                Toast.makeText(this,"Reciente",Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Ordenar", "reciente")
                editor.apply()
                LoadQueryNewest("%")

            }
            if (i==1)
            {
                //older first
                Toast.makeText(this,"Antiguo",Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Ordenar", "antiguo")
                editor.apply()
                LoadQueryOldest("%")
            }
            if (i==2)
            {
                //title ascending
                Toast.makeText(this,"Titulo(Ascendente)",Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Ordenar", "ascendente")
                editor.apply()
                LoadQueryAscending("%")
            }
            if (i==3)
            {
                //title descending
                Toast.makeText(this,"Titulo(Descendiente)",Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Ordenar", "descendiente")
                editor.apply()
                LoadQueryDescending("%")
            }
            dialogInterface.dismiss()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    inner class MyNotesAdapter : BaseAdapter {
        var listNotesAdapter = ArrayList<Note>()
        var context: Context? = null

        constructor(context: Context, listNotesAdapter: ArrayList<Note>) : super() {
            this.listNotesAdapter = listNotesAdapter
            this.context = context
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            //inflate layout row.xml
            var myView = layoutInflater.inflate(R.layout.row, null)
            val myNote = listNotesAdapter[position]
            myView.titleTv.text = myNote.noteName
            myView.descTv.text = myNote.noteDes
            //delete btn click
            myView.deleteBtn.setOnClickListener {
                var dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(myNote.noteID.toString())
                dbManager.delete("ID=?", selectionArgs)
                LoadQueryAscending("%")
            }
            //edit//update button click
            myView.editBtn.setOnClickListener {
                GoToUpdateFun(myNote)
            }
            //copy btn click
            myView.copyBtn.setOnClickListener {
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatinate
                val s = title + "\n" + desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.text = s // add to clipboard
                Toast.makeText(this@MainActivity, "Nota copiada...", Toast.LENGTH_SHORT).show()
            }
            //share btn click
            myView.shareBtn.setOnClickListener {

                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatinate
                val s = title + "\n" + desc
                //share intent
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, s)
                startActivity(Intent.createChooser(shareIntent, s))

            }

            return myView
        }

        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }

    }

    private fun GoToUpdateFun(myNote: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("ID", myNote.noteID)
        intent.putExtra("name", myNote.noteName)
        intent.putExtra("des", myNote.noteDes)
        startActivity(intent)
    }
}
