package com.latihan.firebase

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.latihan.firebase.ui.theme.FirebaseTheme

class MainActivity : ComponentActivity() {
    val db = Firebase.firestore

    var DataProvinsi = ArrayList<daftarProvinsi>()

//    lateinit var lvAdapter : ArrayAdapter<daftarProvinsi>

    var data : MutableList<Map<String, String>> = ArrayList()
    lateinit var lvAdapter: SimpleAdapter


    lateinit var _etProvinsi: EditText
    lateinit var _etIbuKota: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_activity)

        _etProvinsi = findViewById<EditText>(R.id.namaProvinsi)
        _etIbuKota = findViewById<EditText>(R.id.namaIbuKota)

        val _btnSimpan = findViewById<Button>(R.id.btnSimpanData)
        val _lvData = findViewById<ListView>(R.id.lvData)

//        lvAdapter = ArrayAdapter(
//            this, android.R.layout.simple_list_item_1,
//            DataProvinsi
//        )
//        _lvData.adapter = lvAdapter

        lvAdapter = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf<String>("Pro","Ibu"),
            intArrayOf(
                android.R.id.text1,
                android.R.id.text2
            )
        )

        _lvData.adapter = lvAdapter

        _lvData.setOnItemLongClickListener { parent, view, position, id ->
            val namaPro = data[position].get("Pro")
            if(namaPro != null){
                db.collection("tbProvinsi")
                    .document(namaPro)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("Firebase","Berhasil Dihapus")
                        readData(db)
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firebase", e.message.toString())
                    }
            }

            true
        }

        _btnSimpan.setOnClickListener {
            var provinsi = _etProvinsi.text.toString()
            var ibuKota = _etIbuKota.text.toString()
            TambahData(db,provinsi, ibuKota)
        }

        readData(db)

    }

    fun TambahData(db: FirebaseFirestore, Provinsi: String, Ibukota: String){
        val dataBaru = daftarProvinsi(Provinsi, Ibukota)
        db.collection("tbProvinsi").document(dataBaru.provinsi)
            .set(dataBaru).addOnSuccessListener {
            _etProvinsi.setText("")
            _etIbuKota.setText("")
            Log.d("Firebase","Data Berhasil Ditambahkan")
        }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }

        readData(db)
    }

    fun readData (db:FirebaseFirestore){
        db.collection("tbProvinsi").get()
            .addOnSuccessListener {
                result ->
                DataProvinsi.clear()
                for (document in result){
                    val readData = daftarProvinsi(
                        document.data.get("provinsi").toString(),
                        document.data.get("ibukota").toString()
                    )
                    DataProvinsi.add(readData)
                    data.clear()
                    DataProvinsi.forEach {
                        val dt: MutableMap<String, String> = HashMap(2)
                        dt["Pro"] = it.provinsi
                        dt["Ibu"] = it.ibukota
                        data.add(dt)
                    }
                }
                lvAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase",it.message.toString())
            }
    }
}

