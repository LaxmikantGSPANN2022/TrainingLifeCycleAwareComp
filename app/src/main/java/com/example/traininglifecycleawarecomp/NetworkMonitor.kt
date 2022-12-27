package com.example.traininglifecycleawarecomp

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NetworkMonitor constructor(val context: Context) : DefaultLifecycleObserver {
    var connectivityManager: ConnectivityManager
    lateinit var networkCallback: ConnectivityManager.NetworkCallback
    var validNetworks: HashSet<Network> = HashSet()
    lateinit var coroutineScope: CoroutineScope
    lateinit var job: Job
    var _networkAvailableStateFlow: MutableStateFlow<NetworkMonitor.NetworkState> =
        MutableStateFlow(NetworkMonitor.NetworkState.Available)

    override fun onStart(owner: LifecycleOwner) {
        Toast.makeText(context, "on Start Method Call", Toast.LENGTH_SHORT).show()
        registerNetworkCallbacks()
    }

    override fun onStop(owner: LifecycleOwner) {
        Toast.makeText(context, "on Stop Method Call", Toast.LENGTH_SHORT).show()
        unRegisterNetworkCallbacks()
    }

    init {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun registerNetworkCallbacks() {
        initCoroutine()
        initNetworkMonitoring()
        checkCurrentNetworkState()
    }

    private fun initCoroutine() {
        job = Job()
        coroutineScope = CoroutineScope(Dispatchers.Default + job)
    }

    private fun initNetworkMonitoring() {
        networkCallback = CreateNetworkCallback()

        var networkRequest =
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun checkCurrentNetworkState() {
        connectivityManager.allNetworks.let {
            validNetworks.addAll(it)
        }
        checkValidNetwork()
    }

    private fun CreateNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            connectivityManager.getNetworkCapabilities(network).also {
                if (it?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
                    validNetworks.add(network)
                }
            }
            checkValidNetwork()
        }

        override fun onLost(network: Network) {
            validNetworks.remove(network)
            checkValidNetwork()
        }
    }

    private fun checkValidNetwork() {
        coroutineScope.launch {
            _networkAvailableStateFlow.emit(
                if (validNetworks.size > 0)
                    NetworkState.Available
                else
                    NetworkState.Unvailable
            )
        }
    }


    fun unRegisterNetworkCallbacks() {
        validNetworks.clear()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        job.cancel()
    }

    sealed class NetworkState {
        object Available : NetworkState()
        object Unvailable : NetworkState()
    }
}