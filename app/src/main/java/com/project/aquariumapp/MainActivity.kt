package com.project.aquariumapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.aquariumapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataBase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Thread.sleep(2000)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // readData()
        databaseListner()
        // pumpOn()

        binding.waterSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                pumpStatus("true")
            } else {
                pumpStatus("false")
            }
        }
        binding.oxygenSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                oxyPumpStatus("true")
            } else {
                oxyPumpStatus("false")
            }
        }
        binding.autoSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                autoControl("true")
            } else {
                autoControl("false")
            }
        }


    }

    private fun pumpStatus(switch: String) {
        dataBase = FirebaseDatabase.getInstance().getReference("Switch")
        dataBase.child("pump").setValue(switch).addOnSuccessListener {
            //Toast.makeText(applicationContext, "Pump is On", Toast.LENGTH_SHORT).show()
        }
    }

    private fun oxyPumpStatus(switch: String) {
        dataBase = FirebaseDatabase.getInstance().getReference("Switch")
        dataBase.child("oxy_pump").setValue(switch).addOnSuccessListener {
            //Toast.makeText(applicationContext, "Pump is On", Toast.LENGTH_SHORT).show()
        }
    }

    private fun autoControl(switch: String) {
        dataBase = FirebaseDatabase.getInstance().getReference("Switch")
        dataBase.child("Auto").setValue(switch).addOnSuccessListener {
            //Toast.makeText(applicationContext, "Pump is On", Toast.LENGTH_SHORT).show()
        }
    }

    private fun databaseListner() {
        dataBase = FirebaseDatabase.getInstance().getReference()
        val postListner = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temp = snapshot.child("Sensor/temp").value
                val ph = snapshot.child("Sensor/ph").value
                val waterLevel = snapshot.child("Sensor/waterLevel").value
                val dissolvedOxygen = snapshot.child("Sensor/diss_oxy").value
                binding.tempTv.text = temp.toString()
                binding.phTv.text = ph.toString()
                binding.waterLvlTv.text = waterLevel.toString()
                binding.oxygenTv.text = dissolvedOxygen.toString()

                //switch
                val autoSwitchStatus = snapshot.child("Switch/Auto").value
                val oxyPumpStatus = snapshot.child("Switch/oxy_pump").value
                val waterPumpSwitchStatus = snapshot.child("Switch/pump").value

                if (autoSwitchStatus.toString().equals("true")) {
                    binding.autoSwitch.isChecked = true
                } else {
                    binding.autoSwitch.isChecked = false
                }

                if (oxyPumpStatus.toString().equals("true")) {
                    binding.oxygenSwitch.isChecked = true
                } else {
                    binding.oxygenSwitch.isChecked = false
                }

                if (waterPumpSwitchStatus.toString().equals("true")) {
                    binding.waterSwitch.isChecked = true
                } else {
                    binding.waterSwitch.isChecked = false
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed to read sensor data", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        dataBase.addValueEventListener(postListner)
    }
}