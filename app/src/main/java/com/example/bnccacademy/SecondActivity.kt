package com.example.bnccacademy

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_look_up.rvLookUp
import kotlinx.android.synthetic.main.another_layout.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.util.*

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

        // Not Showing until the request has been done
        rvLookUp.visibility = View.GONE

        val value = intent.getStringExtra(MainActivity.TESTING)
        title_activity.text = value

        val request = Request.Builder().url("https://api.kawalcorona.com/indonesia/provinsi/").build()
        okHttpClient.newCall(request).enqueue(getCallback(lookUpAdapter))

        back_button.setOnClickListener {
            onBackPressed()
        }

        search_input.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(p0: Editable?) {
                searchList.clear()
                mockLookUpList.forEach {
                    if (it.provinceName.toLowerCase(Locale.getDefault()).contains(p0.toString().toLowerCase(
                            Locale.getDefault()))) {
                        searchList.add(it)
                    }
                }

                val newlookUpAdapter = LookUpAdapter(searchList)
                rvLookUp.adapter = newlookUpAdapter

                if (searchList.isEmpty()){
                    emptyView.text = "There is no province named \"$p0\""
                    rvLookUp.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                } else {
                    rvLookUp.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                }
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
                        progressBar.visibility = View.GONE
                        searchList.addAll(lookupListFromNetwork)
                        lookUpAdapter.updateData(lookupListFromNetwork)
                        rvLookUp.visibility = View.VISIBLE
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
