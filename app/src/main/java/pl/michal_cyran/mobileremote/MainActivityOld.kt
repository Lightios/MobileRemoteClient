//package pl.michal_cyran.mobileremote
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.ViewModel
//import io.ktor.client.HttpClient
//import io.ktor.client.engine.cio.CIO
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.http.ContentType
//import io.ktor.serialization.kotlinx.json.json
//import kotlinx.coroutines.*
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//import java.io.OutputStreamWriter
//import java.net.Socket
//import io.ktor.client.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.request.*
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//
//
//
//// Data class to hold connection state
//data class ConnectionState(
//    val isConnected: Boolean = false,
//    val isConnecting: Boolean = false,
//    val statusMessage: String = "Not connected"
//)
//
//
//@Serializable
//data class Command(val cmd: String, val value: Int? = null)
//
//// ViewModel to manage app state
//class VolumeRemoteViewModel : ViewModel() {
//    private var socket: Socket? = null
//    private var writer: OutputStreamWriter? = null
//    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
//
//    private val _connectionState = mutableStateOf(ConnectionState())
//    val connectionState: State<ConnectionState> = _connectionState
//
//    private val _ipAddress = mutableStateOf("192.168.1.215")
//    val ipAddress: State<String> = _ipAddress
//
//    private val _port = mutableStateOf("5000")
//    val port: State<String> = _port
//
//    private val _volume = mutableFloatStateOf(50f)
//    val volume: State<Float> = _volume
//
//    private val httpClient = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json(Json {
//                prettyPrint = true
//                ignoreUnknownKeys = true
//            })
//        }
//    }
//
//    private suspend fun sendCommand(command: Command) {
//        try {
//            val response: String = httpClient.post(
//                "http://192.168.1.215:5000/command",
//                contentType(ContentType.Application.Json),
//                setBody(command)
//            )
//            println("Odpowiedź serwera: $response")
//        } catch (e: Exception) {
//            println("Błąd wysyłania komendy: ${e.message}")
//        }
//    }
//
//    fun updateIpAddress(ip: String) {
//        _ipAddress.value = ip
//    }
//
//    fun updatePort(port: String) {
//        _port.value = port
//    }
//
//    fun updateVolume(volume: Float) {
//        _volume.value = volume
//        if (_connectionState.value.isConnected) {
//            sendVolumeCommand(volume.toInt())
//        }
//    }
//
//    fun connect() {
//        val ip = _ipAddress.value.trim()
//        val portNum = _port.value.trim().toIntOrNull()
//
//        if (ip.isEmpty() || portNum == null) {
//            _connectionState.value = _connectionState.value.copy(
//                statusMessage = "Please enter valid IP and port"
//            )
//            return
//        }
//
//        scope.launch {
//            try {
//                _connectionState.value = _connectionState.value.copy(
//                    isConnecting = true,
//                    statusMessage = "Connecting..."
//                )
//
//                withContext(Dispatchers.IO) {
//                    socket = Socket(ip, portNum)
//                    writer = OutputStreamWriter(socket!!.getOutputStream())
//                }
//
//                _connectionState.value = ConnectionState(
//                    isConnected = true,
//                    isConnecting = false,
//                    statusMessage = "Connected to $ip:$portNum"
//                )
//
//            } catch (e: Exception) {
//                _connectionState.value = ConnectionState(
//                    isConnected = false,
//                    isConnecting = false,
//                    statusMessage = "Connection failed: ${e.message}"
//                )
//            }
//        }
//    }
//
//    fun disconnect() {
//        scope.launch {
//            withContext(Dispatchers.IO) {
//                try {
//                    writer?.close()
//                    socket?.close()
//                } catch (e: Exception) {
//                    // Ignore close errors
//                }
//            }
//
//            _connectionState.value = ConnectionState(
//                isConnected = false,
//                isConnecting = false,
//                statusMessage = "Disconnected"
//            )
//        }
//    }
//
//    private fun sendVolumeCommand(volume: Int) {
//        if (!_connectionState.value.isConnected || writer == null) return
//
//        scope.launch {
//            try {
//                withContext(Dispatchers.IO) {
//                    writer?.write("VOLUME:$volume\n")
//                    writer?.flush()
//                }
//            } catch (e: Exception) {
//                _connectionState.value = _connectionState.value.copy(
//                    statusMessage = "Failed to send command: ${e.message}"
//                )
//            }
//        }
//    }
//
//    fun sendMuteCommand() {
//        if (!_connectionState.value.isConnected || writer == null) return
//
//        scope.launch {
//            try {
//                withContext(Dispatchers.IO) {
//                    writer?.write("MUTE\n")
//                    writer?.flush()
//                }
//            } catch (e: Exception) {
//                _connectionState.value = _connectionState.value.copy(
//                    statusMessage = "Failed to send mute command: ${e.message}"
//                )
//            }
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        disconnect()
//        scope.cancel()
//    }
//}
//
//class MainActivity : ComponentActivity() {
//
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (!isGranted) {
//            Toast.makeText(this, "Internet permission is required", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Check for internet permission
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
//            != PackageManager.PERMISSION_GRANTED) {
//            requestPermissionLauncher.launch(Manifest.permission.INTERNET)
//        }
//
//        setContent {
//            VolumeRemoteTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    VolumeRemoteApp()
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun VolumeRemoteApp() {
//    val viewModel = remember { VolumeRemoteViewModel() }
//    val context = LocalContext.current
//    val connectionState by viewModel.connectionState
//    val ipAddress by viewModel.ipAddress
//    val port by viewModel.port
//    val volume by viewModel.volume
//
//    // Show toast messages for errors
//    LaunchedEffect(connectionState.statusMessage) {
//        if (connectionState.statusMessage.contains("failed", ignoreCase = true) ||
//            connectionState.statusMessage.contains("Failed", ignoreCase = true)) {
//            Toast.makeText(context, connectionState.statusMessage, Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Title
//        Text(
//            text = "Volume Remote Control",
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 32.dp)
//        )
//
//        // IP Address Input
//        OutlinedTextField(
//            value = ipAddress,
//            onValueChange = viewModel::updateIpAddress,
//            label = { Text("Laptop IP Address") },
//            placeholder = { Text("192.168.1.215") },
//            enabled = !connectionState.isConnected && !connectionState.isConnecting,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp)
//        )
//
//        // Port Input
//        OutlinedTextField(
//            value = port,
//            onValueChange = viewModel::updatePort,
//            label = { Text("Port") },
//            placeholder = { Text("8080") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            enabled = !connectionState.isConnected && !connectionState.isConnecting,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp)
//        )
//
//        // Connect/Disconnect Button
//        Button(
//            onClick = {
//                if (connectionState.isConnected) {
//                    viewModel.disconnect()
//                } else {
//                    viewModel.connect()
//                }
//            },
//            enabled = !connectionState.isConnecting,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp)
//        ) {
//            when {
//                connectionState.isConnecting -> {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(16.dp),
//                        color = MaterialTheme.colorScheme.onPrimary
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Connecting...")
//                }
//                connectionState.isConnected -> Text("Disconnect")
//                else -> Text("Connect")
//            }
//        }
//
//        // Status Text
//        Text(
//            text = connectionState.statusMessage,
//            textAlign = TextAlign.Center,
//            color = when {
//                connectionState.isConnected -> MaterialTheme.colorScheme.primary
//                connectionState.statusMessage.contains("failed", ignoreCase = true) ->
//                    MaterialTheme.colorScheme.error
//                else -> MaterialTheme.colorScheme.onSurface
//            },
//            modifier = Modifier.padding(bottom = 32.dp)
//        )
//
//        // Volume Controls Section
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                // Volume Text
//                Text(
//                    text = "Volume: ${volume.toInt()}%",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Medium,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
//
//                // Volume Slider
//                Slider(
//                    value = volume,
//                    onValueChange = viewModel::updateVolume,
//                    valueRange = 0f..100f,
//                    enabled = connectionState.isConnected,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 24.dp)
//                )
//
//                // Mute Button
//                Button(
//                    onClick = viewModel::sendMuteCommand,
//                    enabled = connectionState.isConnected,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Toggle Mute")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun VolumeRemoteTheme(content: @Composable () -> Unit) {
//    MaterialTheme(
//        content = content
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun VolumeRemoteAppPreview() {
//    VolumeRemoteTheme {
//        VolumeRemoteApp()
//    }
//}