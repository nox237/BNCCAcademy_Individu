package com.example.bnccacademy

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

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

        imageButton.setOnClickListener {
            infoDialog()
        }

        // Turn off visibility on layout numberOfCases
        Layout_numberOfCases.visibility = View.GONE
    }

    private fun openActivity2(){
        val intent = Intent(this, SecondActivity::class.java).apply {
            putExtra(TESTING, "Indonesia")
        }
        startActivity(intent)
    }

    private fun openActivity3(){
//        val dialog = BottomSheetDialog(this)
//        val view=layoutInflater.inflate(R.layout.activity_bottom_sheets,null)
//        dialog.setContentView(view)
//        dialog.show()

        val bottomSheetsActivity = BottomSheetsHotlineActivity()
        bottomSheetsActivity.show(supportFragmentManager, "add_hotline_fragment")

//        val intent = Intent(this, HotlineActivity::class.java)
//        startActivity(intent)
    }

    private fun infoDialog() {
        val dialog = Dialog(this, R.style.BottomSheetDialog)
        dialog.setContentView(R.layout.info_dialog_fragment)
        dialog.show()
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
                        Layout_numberOfCases.visibility = View.VISIBLE

                        progressBar_numberOfCases.visibility = View.GONE
                        progressBar_numberOfDeathCases.visibility = View.GONE
                        progressBar_numberOfRecoveredCases.visibility = View.GONE
                        progressBar_numberOfPositiveCases.visibility = View.GONE

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