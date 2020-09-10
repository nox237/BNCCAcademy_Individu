package com.example.bnccacademy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_look_up.rvLookUp
import kotlinx.android.synthetic.main.another_layout.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.lang.Exception

class SecondActivity : AppCompatActivity() {

    private val okHttpClient = OkHttpClient()

    private val mockLookUpList = mutableListOf<LookUpData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.another_layout)

        val lookUpAdapter = LookUpAdapter(mockLookUpList)
        rvLookUp.layoutManager = LinearLayoutManager(this)
        rvLookUp.adapter = lookUpAdapter
        val value = intent.getStringExtra(MainActivity.TESTING)
        title_activity.text = value

        back_button.setOnClickListener {
            onBackPressed()
        }

        search_delete_btn.setOnClickListener {

        }

        val request = Request.Builder().url("https://api.kawalcorona.com/indonesia/provinsi/").build()
        okHttpClient.newCall(request).enqueue(getCallback(lookUpAdapter))
    }

    private fun getCallback(lookUpAdapter: LookUpAdapter): Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@SecondActivity.runOnUiThread {
                    Toast.makeText(this@SecondActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    // ngeparsing object json yang dalam bentuk URL tdi
                    val jsonString = response.body?.string()
                    val jsonArray = JSONArray(jsonString)
                    val lookupListFromNetwork= mutableListOf<LookUpData>()

                    for (i in 0 until jsonArray.length()){
                        lookupListFromNetwork.add(
                            LookUpData(
                                provinceName = jsonArray.getJSONObject(i).getJSONObject("attributes").getString("Provinsi"),
                                numberOfPositiveCases = jsonArray.getJSONObject(i).getJSONObject("attributes").getInt("Kasus_Posi"),
                                numberOfRecoveredCases = jsonArray.getJSONObject(i).getJSONObject("attributes").getInt("Kasus_Semb"),
                                numberOfDeathCases = jsonArray.getJSONObject(i).getJSONObject("attributes").getInt("Kasus_Meni"),
                            )
                        )
                    }

                    this@SecondActivity.runOnUiThread{
                        lookUpAdapter.updateData(lookupListFromNetwork)
                    }

                } catch (e: Exception) {
                    this@SecondActivity.runOnUiThread {
                        Toast.makeText(this@SecondActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
