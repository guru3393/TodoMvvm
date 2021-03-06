package com.guru.todomvvm.ui.createTodo

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.guru.todomvvm.R
import com.guru.todomvvm.data.db.TodoRecord
import com.guru.todomvvm.utils.Constants
import kotlinx.android.synthetic.main.activity_create_todo.*
import java.text.SimpleDateFormat
import java.util.*



class CreateTodoActivity : AppCompatActivity() {

    var todoRecord: TodoRecord? = null
    var date: Calendar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_todo)

        //Prepopulate existing content from intent
        val intent = intent
        if (intent != null && intent.hasExtra(Constants.INTENT_OBJECT)) {
            val todoRecord: TodoRecord = intent.getParcelableExtra(Constants.INTENT_OBJECT)!!
            this.todoRecord = todoRecord
            prePopulateData(todoRecord)
        }

        title = if (todoRecord != null) getString(R.string.viewOrEditTodo) else getString(R.string.createTodo)

        et_todo_date.setOnClickListener { showDateTimePicker() }
    }

    private fun prePopulateData(todoRecord: TodoRecord) {
        et_todo_title.setText(todoRecord.title)
        et_todo_creator.setText(todoRecord.creator)
        et_todo_date.setText(todoRecord.date)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflate = menuInflater
        menuInflate.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.save_todo -> {
                saveTodo()
            }
        }
        return true
    }

    /**
     * Sends the updated information back to calling Activity
     * */
    private fun saveTodo() {
        if (validateFields()) {
            val id = if (todoRecord != null) todoRecord?.id else null
            val todo = TodoRecord(id = id, title = et_todo_title.text.toString(), creator = et_todo_creator.text.toString(),date = et_todo_date.text.toString())
            val intent = Intent()
            intent.putExtra(Constants.INTENT_OBJECT, todo)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    /**
     * Validation of EditText
     * */
    private fun validateFields(): Boolean {
        if (et_todo_title.text.isEmpty()) {
            til_todo_title.error = getString(R.string.pleaseEnterTitle)
            et_todo_title.requestFocus()
            return false
        }
        if (et_todo_creator.text.isEmpty()) {
            til_todo_creator.error = getString(R.string.pleaseEnterContent)
            et_todo_creator.requestFocus()
            return false
        }
        if(et_todo_date.text.isEmpty()) {
            til_todo_date.error = getString(R.string.pleaseEnterDate)
            et_todo_date.requestFocus()
            return false
        }
        return true
    }


    fun showDateTimePicker() {
        val currentDate: Calendar = Calendar.getInstance()
        date = Calendar.getInstance()
        DatePickerDialog(this, OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            date!!.set(year, monthOfYear, dayOfMonth)
            TimePickerDialog(this, OnTimeSetListener { view, hourOfDay, minute ->
                date!!.set(Calendar.HOUR_OF_DAY, hourOfDay)
                date!!.set(Calendar.MINUTE, minute)
                if( currentDate.before(date) ) {
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
                et_todo_date.setText(dateFormat.format(date!!.getTime()))
                Log.v("Log", "The choosen one " + date!!.getTime())
                }else{
                    Toast.makeText(this, "Cannot set a past date and time", Toast.LENGTH_LONG).show();
                }
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show()
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show()
    }

}