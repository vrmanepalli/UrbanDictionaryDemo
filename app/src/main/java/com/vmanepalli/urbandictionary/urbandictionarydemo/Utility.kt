package com.vmanepalli.urbandictionary.urbandictionarydemo

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.vmanepalli.urbandictionary.urbandictionarydemo.activities.SearchableActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

var Application.ascendingOrder: Boolean
    get() = getSharedPreferences(
        resources.getString(R.string.app_name),
        AppCompatActivity.MODE_PRIVATE
    ).getBoolean(SearchableActivity.SORT_ORDER, true)
    set(value) {
        getSharedPreferences(
            resources.getString(R.string.app_name),
            AppCompatActivity.MODE_PRIVATE
        ).edit().putBoolean(SearchableActivity.SORT_ORDER, value).apply()
    }

fun Application.toast(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun Configuration.orientationForRecyclerView(): Int {
    // The integer values of recyclerview orientation are offset by 1 than
    // screen configuration orientation. So performing this check.
    return if (orientation == 1) {
        DividerItemDecoration.VERTICAL
    } else {
        DividerItemDecoration.HORIZONTAL
    }
}

fun RecyclerView.addDivider(applicationContext: Application, newConfig: Configuration) {
    val dividerItemDecoration =
        DividerItemDecoration(applicationContext, newConfig.orientationForRecyclerView())
    this.addItemDecoration(dividerItemDecoration)
}

val Application.isConnectedToInternet: Boolean?
    get() {
        var result = false
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

fun MenuItem?.show() {
    this?.let { isVisible = true }
}

fun MenuItem?.hide() {
    this?.let { isVisible = false }
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}