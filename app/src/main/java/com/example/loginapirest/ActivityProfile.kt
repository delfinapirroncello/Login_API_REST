package com.example.loginapirest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.loginapirest.databinding.ActivityProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class ActivityProfile: AppCompatActivity () {

    private lateinit var mBinding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        getSupport()
    }

    private fun getSupport() {

        val url = Constants.BASE_URL + Constants.API_PATH + Constants.USERS_PATH + Constants.TWO_PATH

        val jsonObjectRequest = object  : JsonObjectRequest(Method.GET, url, null , { response ->
            Log.i("response", response.toString())

            val gson = Gson()

            val userJson = response.optJSONObject(Constants.DATA_PROPERTY)?.toString()
            val user: User = gson.fromJson(userJson, User:: class.java)

            val supportJson = response.optJSONObject(Constants.SUPPORT_PROPERTY)?.toString()
            val support: Support = gson.fromJson(supportJson, Support::class.java)

            updateUI(user, support)
        },{
            it.printStackTrace()
            if (it.networkResponse != null && it.networkResponse.statusCode == 400){
                showMessage("Error en la petición")
            }
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String> ()

                params["Content-Type"] = "application/json"
                return params
            }
        }

        LoginApplication.reqResAPI.addToRequestQueue(jsonObjectRequest)
    }

    private fun updateUI(user: User, support: Support) {

        with(mBinding) {
            textNameSuport.text = user.getFullName()
            textEmailSuport.text = user.email

            Glide.with(this@ActivityProfile)
                .load(user.avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(CircleCrop())
                .centerCrop()
                .into(avatarSuport)

            txResponse.text = support.text
            tvSupportUrl.text = support.url
        }
    }
    private fun showMessage(message: String){
        Snackbar.make(mBinding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}