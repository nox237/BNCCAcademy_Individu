package com.example.bnccacademy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_hotline.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.lang.Exception

class HotlineActivity : AppCompatActivity() {

    private val okHttpClient = OkHttpClient()

//    private val mockLookUpList = mutableListOf(){
//        LookUpData(imgIcon = "", name = "Loading...", phone = "")
//    }

    private val mockHotlineList = mutableListOf(
        HotlineData(name = "Loading...", imgIcon = "", phone = "")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotline)

        val hotlineAdapter = HotlineAdapter(mockHotlineList)

        rvLookUp.layoutManager = LinearLayoutManager(this)
        rvLookUp.adapter = hotlineAdapter

        val request = Request.Builder()
            .url("https://bncc-corona-versus.firebaseio.com/v1/hotlines.json")
            .build()

        okHttpClient.newCall(request).enqueue(getCallback(hotlineAdapter))
        // enqueue untuk mengantri filenya
        // newCall akan memprepare request kita, yang dimana akan dipersiapin
        // enqueue akan membuka url tersebut
        
        
        okHttpClient
            .newCall(request)
            .enqueue(
                getCallback(hotlineAdapter)
            )
    }

    private fun getCallback(hotlineAdapter: HotlineAdapter): Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@HotlineActivity.runOnUiThread {
                    Toast.makeText(this@HotlineActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    // ngeparsing object json yang dalam bentuk URL tdi
                    val jsonString = response.body?.string()
                    val jsonArray = JSONArray(jsonString)
                    val hotlineListFromNetwork= mutableListOf<HotlineData>()

                    for (i in 0 until jsonArray.length()){
                        hotlineListFromNetwork.add(
                            HotlineData(
                                imgIcon = jsonArray.getJSONObject(i).getString("img_icon"),
                                phone = jsonArray.getJSONObject(i).getString("phone"),
                                name = jsonArray.getJSONObject(i).getString("name"),
                            )
                        )
                    }

                    this@HotlineActivity.runOnUiThread{
                        hotlineAdapter.updateData(hotlineListFromNetwork)
                    }

                } catch (e:Exception) {
                    this@HotlineActivity.runOnUiThread {
                        Toast.makeText(this@HotlineActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}