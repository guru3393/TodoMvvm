package com.guru.todomvvm.ui.todoList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.guru.todomvvm.R
import com.guru.todomvvm.data.db.TodoRecord
import com.guru.todomvvm.ui.createTodo.CreateTodoActivity
import com.guru.todomvvm.utils.ConnectionService
import com.guru.todomvvm.utils.Constants
import kotlinx.android.synthetic.main.activity_todo_list.*
import kotlinx.android.synthetic.main.content_main.*

class TodoListActivity : AppCompatActivity(), TodoListAdapter.TodoEvents {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var searchView: SearchView
    private lateinit var todoAdapter: TodoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        startService(Intent(baseContext, ConnectionService::class.java))

        //Setting up RecyclerView
        rv_todo_list.layoutManager = LinearLayoutManager(this)
        todoAdapter = TodoListAdapter(this)
        rv_todo_list.adapter = todoAdapter


        //Setting up ViewModel and LiveData
        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)
        todoViewModel.getAllTodoList().observe(this, Observer {
            todoAdapter.setAllTodoItems(it)
        })

        //FAB click listener
        fab_new_todo.setOnClickListener {
            val intent = Intent(this@TodoListActivity, CreateTodoActivity::class.java)
            startActivityForResult(intent, Constants.INTENT_CREATE_TODO)
        }
    }


    /**
     * RecyclerView Item callbacks
     * */
    //Callback when user clicks on Delete note
    override fun onDeleteClicked(todoRecord: TodoRecord) {
        todoViewModel.deleteTodo(todoRecord)
    }

    //Callback when user clicks on view note
    override fun onViewClicked(todoRecord: TodoRecord) {
        val intent = Intent(this@TodoListActivity, CreateTodoActivity::class.java)
        intent.putExtra(Constants.INTENT_OBJECT, todoRecord)
        startActivityForResult(intent, Constants.INTENT_UPDATE_TODO)
    }


    /**
     * Activity result callback
     * Triggers when Save button clicked from @CreateTodoActivity
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val todoRecord = data?.getParcelableExtra<TodoRecord>(Constants.INTENT_OBJECT)!!
            when (requestCode) {
                Constants.INTENT_CREATE_TODO -> {
                    todoViewModel.saveTodo(todoRecord)
                }
                Constants.INTENT_UPDATE_TODO -> {
                    todoViewModel.updateTodo(todoRecord)
                }
            }
        }
    }

}
