package com.example.pertemuan13_firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}