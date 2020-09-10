package com.example.bnccacademy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val okHttpClient = OkHttpClient()

    companion object {
        const val TESTING = "passingVariable"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val request = Request.Builder()
            .url("https://api.kawalcorona.com/indonesia/")
            .build()

        okHttpClient.newCall(request).enqueue(getCallback())
        button.setOnClickListener {
            openActivity2()
        }

        button2.setOnClickListener {
            openActivity3()
        }
    }

    private fun openActivity2(){
        val intent = Intent(this, SecondActivity::class.java).apply {
            putExtra(TESTING, "Indonesia")
        }
        startActivity(intent)
    }

    private fun openActivity3(){
        val intent = Intent(this, HotlineActivity::class.java)
        startActivity(intent)
    }

    private fun getCallback(): Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@MainActivity.runOnUiThread {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    // ngeparsing object json yang dalam bentuk URL tdi
                    val jsonString = response.body?.string()
                    val jsonArray = JSONArray(jsonString)
                    var positive = ""
                    var recovered = ""
                    var died = ""
                    var cases = ""

                    for (i in 0 until jsonArray.length()){
                        cases = jsonArray.getJSONObject(i).getString("positif")
                        positive = jsonArray.getJSONObject(i).getString("dirawat")
                        recovered = jsonArray.getJSONObject(i).getString("sembuh")
                        died = jsonArray.getJSONObject(i).getString("meninggal")
                    }

                    this@MainActivity.runOnUiThread{
                        numberofCases.text = cases
                        numberOfPositiveCases.text = positive
                        numberOfRecoveredCases.text = recovered
                        numberOfDeathCases.text = died
                    }

                } catch (e: Exception) {
                    this@MainActivity.runOnUiThread {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}