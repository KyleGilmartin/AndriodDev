package edu.kylegilmartin.myapplication

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.file.attribute.AclEntry

class MainActivity : AppCompatActivity() , SensorEventListener {

    private var  sensorManager: SensorManager? = null

    private var runnning = false
    private  var totalSteps = 0f
    private var PrevuTotalSteps = 0f


    val FINE_LOC = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()
        resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }



    override fun onResume() {
        super.onResume()
        runnning = true
        val stepSensor : Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null){
            Toast.makeText(this,"This device is old",Toast.LENGTH_LONG).show()
            sensorManager?.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_UI)
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (runnning){
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - PrevuTotalSteps.toInt()
            tv_stepsTaken.text = ("$currentSteps")

            ttProgress_circular.apply {
                setProgressWithAnimation(currentSteps.toFloat())
            }
        }
    }

   private fun resetSteps(){
        ttProgress_circular.setOnClickListener {
            Toast.makeText(this,"Long tap to reset",Toast.LENGTH_SHORT).show()

        }
        ttProgress_circular.setOnLongClickListener{
            PrevuTotalSteps = totalSteps
            tv_stepsTaken.text = 0.toString()
            saveData()

            true
        }
    }

    private  fun saveData() {
        val sharedPref = getSharedPreferences("myPrefs",Context.MODE_PRIVATE)
        val editer = sharedPref.edit()
        editer.putFloat("key1",PrevuTotalSteps)
        editer.apply ()
    }

    private fun loadData(){
        val sharedPreference = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreference.getFloat("key1", 0f)
        Log.d("MainActivity","$savedNumber")
        PrevuTotalSteps = savedNumber
    }



    private fun showDialog(permission: String,name: String,code: Int){
        val builder = AlertDialog.Builder(this)

        builder.apply{
            setMessage("premision")
            setTitle("reqired")
            setPositiveButton("ok") {dialog, which ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission),code)
            }
        }
        val dialog = builder.create()
        dialog.show()

    }
}