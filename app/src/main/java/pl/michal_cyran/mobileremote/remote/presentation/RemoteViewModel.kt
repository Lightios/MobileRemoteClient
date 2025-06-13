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

    init {
        _ip.update { remoteDataSource.getIp() }
        _port.update { remoteDataSource.getPort() }
    }

    fun setVolume(value: Float) {
        _volume.value = value
        sendToServer()
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        sendToServer()
    }

    fun sendToServer() {
        Log.d("RemoteViewModel", "Sending to server: volume=${_volume.value}, isMuted=${_isMuted.value}")
        viewModelScope.launch {
            remoteDataSource
                .setVolume(_volume.value.toInt(), _isMuted.value)
                .onSuccess {

                }
                .onError { error ->
                    _event.send(RemoteEvent.Error(error))
                }
        }
    }

    fun togglePlay() {
        Log.d("RemoteViewModel", "Toggling play")
        viewModelScope.launch {
            remoteDataSource
                .togglePlay()
                .onSuccess {
                    // Handle success if needed
                }
                .onError { error ->
                    _event.send(RemoteEvent.Error(error))
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