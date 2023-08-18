package com.cosmiccodecraft.nebulatranslator.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

sealed class ConnectionState {
    object WifiAvailable : ConnectionState()
    object CellularAvailable : ConnectionState()
    object Unavailable : ConnectionState()
}

val Context.currentConnectivityState: ConnectionState
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getCurrentConnectivityState(connectivityManager)
    }

private fun getCurrentConnectivityState(connectivityManager: ConnectivityManager): ConnectionState {
    val currentNetwork = connectivityManager.activeNetwork ?: return ConnectionState.Unavailable

    val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        ?: return ConnectionState.Unavailable

    return if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
        ConnectionState.CellularAvailable
    } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
        ConnectionState.WifiAvailable
    } else {
        ConnectionState.Unavailable
    }
}


