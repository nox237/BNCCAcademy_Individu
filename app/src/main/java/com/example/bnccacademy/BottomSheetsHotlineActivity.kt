package com.example.bnccacademy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_bottom_sheets.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class BottomSheetsHotlineActivity : BottomSheetDialogFragment() {

    private val okHttpClient = OkHttpClient()

    private val mockHotlineList = mutableListOf(
        HotlineData(name = "Loading...", imgIcon = "", phone = "0880-XXXX-XXXX")
    )

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_bottom_sheets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hotlineAdapter = HotlineAdapter(mockHotlineList)

        rvHotline.layoutManager = LinearLayoutManager(activity)
        rvHotline.adapter = hotlineAdapter

        val request = Request.Builder()
            .url("https://bncc-corona-versus.firebaseio.com/v1/hotlines.json")
            .build()

        okHttpClient.newCall(request).enqueue(getCallback(hotlineAdapter))
        rvHotline.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme)
    }

    private fun getCallback(hotlineAdapter: HotlineAdapter): Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    makeText(activity, e.message, Toast.LENGTH_SHORT).show()
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

                    activity?.runOnUiThread{
                        progressBar.visibility = View.GONE
                        rvHotline.visibility = View.VISIBLE
                        hotlineAdapter.updateData(hotlineListFromNetwork)
                    }

                } catch (e: Exception) {
                    activity?.runOnUiThread {
                        makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}