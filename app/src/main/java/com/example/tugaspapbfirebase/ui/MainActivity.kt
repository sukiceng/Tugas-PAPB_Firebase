package com.example.tugaspapbfirebase.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tugaspapbfirebase.Database.Laporan
import com.example.tugaspapbfirebase.R
import com.example.tugaspapbfirebase.databinding.ActivityMainBinding
import com.example.tugaspapbfirebase.ui.FormActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    companion object {
        const val ID_LAPORAN = "id_laporan"
        const val NAMA = "nama"
        const val DESKRIPSI = "deskripsi"
        const val TANGGAL = "tanggal"

    }

    // binding untuk
    private lateinit var binding: ActivityMainBinding
    // Inisialisasi variabel `db` dan berikan nilainya sebagai instance dari database Cloud Firestore
    val db = Firebase.firestore
    // Deklarasi variabel `firestore` pribadi dan inisialisasi dengan instance dari database Cloud Firestore
    private val firestore = FirebaseFirestore.getInstance()
    // Buat referensi ke koleksi `budgets` dalam database Cloud Firestore
    private val laporanCollectionRef = firestore.collection("laporans")
    // Deklarasi variabel `updateId` pribadi untuk menyimpan ID item anggaran yang sedang diedit, jika ada
    private var updateId = ""
    // Deklarasi variabel `budgetListLiveData` pribadi MutableLiveData untuk menyimpan daftar objek `Laporan` (item anggaran)
    private val laporanListLiveData: MutableLiveData<List<Laporan>> by lazy {
        MutableLiveData<List<Laporan>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeLaporan()
        getAllLaporan()

        binding.addButton.setOnClickListener {
            // Start FormActivity for adding a new budget
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAllLaporan() {
        observeLaporanChanges()
    }

    private fun observeLaporan() {
        laporanListLiveData.observe(this) { laporan ->
            val adapterLaporan = LaporanAdapter(this,laporan)
            binding.recycleView.apply {
                adapter = adapterLaporan
                layoutManager = LinearLayoutManager(this@MainActivity)
                setHasFixedSize(true)
            }
        }
    }

    private fun observeLaporanChanges() {
        laporanCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }
            val budgets = snapshots?.toObjects(Laporan::class.java)
            if (budgets != null) {
                laporanListLiveData.postValue(budgets)
            }
        }
    }

    private fun deleteBudget(laporan: Laporan) {
        if (laporan.id.isNotEmpty()) {
            laporanCollectionRef.document(laporan.id)
                .delete()
                .addOnSuccessListener {
                    // Handle success
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
    }

    inner class LaporanAdapter(
        private val context: Context,
        private val laporanList: List<Laporan>
    ) : RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_keluhan, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val laporan = laporanList[position]
            holder.bind(laporan)
        }

        override fun getItemCount(): Int {
            return laporanList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val namaTextView: TextView = itemView.findViewById(R.id.txt_nama)
            private val deskripsiTextView: TextView = itemView.findViewById(R.id.txt_keluhan)
            private val tanggalTextView: TextView = itemView.findViewById(R.id.txt_tanggal)
            private val btnEdit: ImageButton = itemView.findViewById(R.id.edit_button)
            private val btnDelete: ImageButton = itemView.findViewById(R.id.delete_button)

            fun bind(laporan: Laporan) {
                // Bind the data to the views
                namaTextView.text = laporan.nama
                deskripsiTextView.text = laporan.deskripsi_laporan
                tanggalTextView.text = laporan.date

                // Set click listener for item click
                itemView.setOnClickListener {
                    val intent = Intent(context, FormActivity::class.java)
                    intent.putExtra(ID_LAPORAN, laporan.id)
                    intent.putExtra(NAMA, laporan.nama)
                    intent.putExtra(DESKRIPSI, laporan.deskripsi_laporan)
                    intent.putExtra(TANGGAL, laporan.date)
                    context.startActivity(intent)
                }

                // Set long click listener for item long click
                itemView.setOnLongClickListener {
                    deleteBudget(laporan)
                    true // Indicate that the long click event is handled
                }

                // Set click listener for edit button
                btnEdit.setOnClickListener {
                    // Handle edit button click
                    val intent = Intent(context, FormActivity::class.java)
                    intent.putExtra(ID_LAPORAN, laporan.id)
                    intent.putExtra(NAMA, laporan.nama)
                    intent.putExtra(DESKRIPSI, laporan.deskripsi_laporan)
                    intent.putExtra(TANGGAL, laporan.date)
                    context.startActivity(intent)
                }

                // Set click listener for delete button
                btnDelete.setOnClickListener {
                    // Handle delete button click
                    deleteBudget(laporan)
                }
            }
        }
    }
}