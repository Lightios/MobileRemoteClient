package pl.michal_cyran.mobileremote.remote.presentation

import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.michal_cyran.mobileremote.core.domain.util.onError
import pl.michal_cyran.mobileremote.core.domain.util.onSuccess
import pl.michal_cyran.mobileremote.remote.domain.RemoteDataSource
import pl.michal_cyran.mobileremote.remote.domain.Volume

class RemoteViewModel(
    private val remoteDataSource: RemoteDataSource,
): ViewModel() {
    val _volume = MutableStateFlow(50f)
    val volume = _volume.asStateFlow()

    val _isMuted = MutableStateFlow(false)
    val isMuted = _isMuted.asStateFlow()

    val _ip = MutableStateFlow("")
    val ip = _ip.asStateFlow()

    val _port = MutableStateFlow("")
    val port = _port.asStateFlow()

    private val _event = Channel<RemoteEvent>()
    val events = _event.receiveAsFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    init {
        _ip.update { remoteDataSource.getIp() }
        _port.update { remoteDataSource.getPort() }

        checkConnection()
    }

    private fun checkConnection() {
        viewModelScope.launch {
            remoteDataSource
                .testConnection()
                .onSuccess {
                    _isConnected.value = true
                }
                .onError {
                    _isConnected.value = false
                }
        }
    }

    fun setVolume(value: Float) {
        _volume.value = value
        sendToServer()
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        sendToServer()
    }

    fun leftArrowClick() {
        arrowClick("left")
    }

    fun rightArrowClick() {
        arrowClick("right")
    }

    fun arrowClick(direction: String) {
        Log.d("RemoteViewModel", "Arrow clicked: $direction")
        viewModelScope.launch {
            remoteDataSource
                .arrowClick(direction)
                .onSuccess {
                    _isConnected.update { true }
                }
                .onError { error ->
                    _event.send(RemoteEvent.Error(error))
                    checkConnection()
                }
        }
    }

    fun sendToServer() {
        Log.d("RemoteViewModel", "Sending to server: volume=${_volume.value}, isMuted=${_isMuted.value}")
        viewModelScope.launch {
            remoteDataSource
                .setVolume(_volume.value.toInt(), _isMuted.value)
                .onSuccess {
                    _isConnected.update { true }
                }
                .onError { error ->
                    _event.send(RemoteEvent.Error(error))
                    checkConnection()
                }
        }
    }

    fun togglePlay() {
        Log.d("RemoteViewModel", "Toggling play")
        viewModelScope.launch {
            remoteDataSource
                .togglePlay()
                .onSuccess {
                    _isConnected.update { true }
                }
                .onError { error ->
                    _event.send(RemoteEvent.Error(error))
                    checkConnection()
                }
        }
    }

    fun tabClick() {
        Log.d("RemoteViewModel", "Tab clicked")
        viewModelScope.launch {
            remoteDataSource
                .tabClick()
                .onSuccess {
                    _isConnected.update { true }
                }
                .onError { error ->
                    _event.send(RemoteEvent.Error(error))
                    checkConnection()
                }
        }
    }

    fun setIp(ip: String) {
        _ip.update { ip }
        remoteDataSource.setIpAddress(ip)
    }

    fun setPort(port: String) {
        _port.update { port }
        remoteDataSource.setPort(port)
    }
}