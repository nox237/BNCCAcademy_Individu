package com.example.bnccacademy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_look_up.rvLookUp
import kotlinx.android.synthetic.main.another_layout.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class SecondActivity : AppCompatActivity() {

    private val okHttpClient = OkHttpClient()

    private val mockLookUpList = mutableListOf<LookUpData>()

    private var searchList = mutableListOf<LookUpData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.another_layout)

        val lookUpAdapter = LookUpAdapter(mockLookUpList)
        rvLookUp.layoutManager = LinearLayoutManager(this)
        rvLookUp.adapter = lookUpAdapter

        val value = intent.getStringExtra(MainActivity.TESTING)
        title_activity.text = value

        val request = Request.Builder().url("https://api.kawalcorona.com/indonesia/provinsi/").build()
        okHttpClient.newCall(request).enqueue(getCallback(lookUpAdapter))

        back_button.setOnClickListener {
            onBackPressed()
        }

        search_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                searchList.clear()
                mockLookUpList.forEach {
                    if (it.provinceName.toLowerCase().contains(p0.toString().toLowerCase())){
                        searchList.add(it)
                    }
                }
                val new_lookUpAdapter = LookUpAdapter(searchList)
                rvLookUp.adapter = new_lookUpAdapter
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

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
                        searchList.addAll(lookupListFromNetwork)
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
