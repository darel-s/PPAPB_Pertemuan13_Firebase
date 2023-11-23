package com.example.pertemuan13_firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.example.pertemuan13_firebase.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var updateId = ""
    private val budgetListLiveData : MutableLiveData<List<Budget>>
    by lazy {
        MutableLiveData<List<Budget>>()
    }

    private val budgetClollectionRef = firestore.collection("budgets")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            addButton.setOnClickListener {
                val nominal = nominalEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val date = dateEditText.text.toString()
                val budget = Budget("", nominal, description, date)
                addBudget(budget)
                setEmptyField()
            }
            updateButton.setOnClickListener {
                val nominal = nominalEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val date = dateEditText.text.toString()
                val budget = Budget("", nominal, description, date)
                updateBudget(budget)
                setEmptyField()
            }

            listView.setOnItemClickListener {
                adapterView, view, i, l ->
                val item = adapterView.adapter.getItem(i) as Budget
                updateId = item.id

                nominalEditText.setText(item.nominal)
                descriptionEditText.setText(item.description)
                dateEditText.setText(item.date)
            }

            listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
                val item = adapterView.adapter.getItem(i) as Budget
                deleteBudget(item)
                true
            }
        }
        observeBudgets()
        getAllBudgets()
    }

    private fun getAllBudgets() {
        budgetClollectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            val budgets = arrayListOf<Budget>()
            snapshot?.forEach {
                documentReference ->
                budgets.add(
                    Budget(
                        documentReference.id,
                        documentReference.get("nominal").toString(),
                        documentReference.get("description").toString(),
                        documentReference.get("date").toString()
                    )
                )
            }

            if (budgets != null) {
                budgetListLiveData.postValue(budgets)
            }
        }
    }

    private fun observeBudgets() {
        budgetListLiveData.observe(this) {
            budgets ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                budgets.toMutableList()
            )
            binding.listView.adapter = adapter
        }
    }

    private fun addBudget(budget: Budget) {
        budgetClollectionRef.add(budget).addOnFailureListener {
            Log.d("MainActivity", "Error adding budget")
        }
    }

    private fun updateBudget (budget: Budget) {
        budgetClollectionRef.document(updateId).set(budget).addOnFailureListener {
            Log.d("MainActivity", "Error updating budget")
        }
    }

    private fun deleteBudget (budget: Budget) {
        budgetClollectionRef.document(updateId).delete().addOnFailureListener {
            Log.d("MainActivity", "Error deleting budget")
        }
    }

    private fun setEmptyField() {
        with(binding) {
            nominalEditText.setText("")
            descriptionEditText.setText("")
            dateEditText.setText("")
        }
    }
}