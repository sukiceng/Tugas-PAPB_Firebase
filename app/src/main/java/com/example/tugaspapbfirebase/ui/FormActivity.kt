package com.example.tugaspapbfirebase.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.example.tugaspapbfirebase.Database.Laporan
import com.example.tugaspapbfirebase.R
import com.example.tugaspapbfirebase.databinding.FormBinding
import com.google.firebase.firestore.FirebaseFirestore

class FormActivity : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val budgetCollectionRef = firestore.collection("budgets")
    private lateinit var binding : FormBinding
    private var updateId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateId = intent.getStringExtra("UPDATE_ID") ?: ""
        val nama = intent.getStringExtra("NAMA")
        val deskripsi = intent.getStringExtra("DESKRIPSI")
        val tanggal = intent.getStringExtra("DATE")

        binding.namaPengadu.setText(nama)
        binding.descKeluhan.setText(deskripsi)
        binding.tanggalLapor.setText(tanggal)

        binding.saveButton.setOnClickListener {
            onSaveClicked()
        }

        binding.updateButton.setOnClickListener {
            onUpdateClicked()
        }
    }

    private fun onSaveClicked() {
        val nominal = binding.namaPengadu.text.toString()
        val description = binding.descKeluhan.text.toString()
        val date = binding.tanggalLapor.text.toString()

        val newBudget = Laporan(nama = nominal, deskripsi_laporan = description, date = date)

        if (updateId.isNotEmpty()) {
            newBudget.id = updateId
            updateLaporan(newBudget)
        } else {
            addLaporan(newBudget)
        }
    }
    private fun addLaporan(laporan: Laporan) {
        budgetCollectionRef.add(laporan)
            .addOnSuccessListener { documentReference ->
                val createdBudgetId = documentReference.id
                laporan.id = createdBudgetId
                documentReference.set(laporan)
                    .addOnSuccessListener {
                        showToast("Laporan sukses di simpan")
                        navigateToMainActivity()
                    }
                    .addOnFailureListener { e ->
                        showToast("Error submitting report ID: $e")
                    }
            }
            .addOnFailureListener { e ->
                showToast("Error adding budget: $e")
            }
    }
    private fun updateLaporan(laporan: Laporan) {
        budgetCollectionRef.document(laporan.id)
            .set(laporan)
            .addOnSuccessListener {
                showToast("Laporan berhasil di update")
                navigateToMainActivity()
            }
            .addOnFailureListener { e ->
                showToast("Gagal  mengupdate laporan: $e")
            }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun onUpdateClicked() {
        val nominal = binding.namaPengadu.text.toString()
        val description = binding.descKeluhan.text.toString()
        val date = binding.tanggalLapor.text.toString()
        val updatelaporan = Laporan(nama = nominal, deskripsi_laporan = description, date = date)

        if (updateId.isNotEmpty()) {
            updatelaporan.id = updateId
            updateLaporan(updatelaporan)
        } else {
            addLaporan(updatelaporan)
        }
    }
    private fun navigateToMainActivity() {
        val intent = Intent(this@FormActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}