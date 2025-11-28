package pl.michal_cyran.mobileremote.remote.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import pl.michal_cyran.mobileremote.core.presentation.util.toString
import pl.michal_cyran.mobileremote.core.presentation.util.ObserveAsEvents

@Composable
fun RemoteControlScreen(
    volume: Float,
    isMuted: Boolean,
    ip: String,
    port: String,
    onIpChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    isConnected: Boolean = true,
    onVolumeChange: (Float) -> Unit = {},
    onMuteToggle: () -> Unit = {},
    onPlayToggle: () -> Unit = {},
    onLeftArrowClick: () -> Unit = {},
    onRightArrowClick: () -> Unit = {},
    onTabClick: () -> Unit = {},
    events: Flow<RemoteEvent>
) {
    val context = LocalContext.current

    ObserveAsEvents(events = events) { event ->
        when (event) {
            is RemoteEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.toString(context),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            IpPortPickerSection(
                ip = ip,
                port = port,
                onIpChange = onIpChange,
                onPortChange = onPortChange,
            )
            ConnectionStatusCard(isConnected = isConnected)

            VolumeControlCard(
                volume = volume,
                isMuted = isMuted,
                onVolumeChange = onVolumeChange,
                onMuteToggle = onMuteToggle,
                onPlayToggle = onPlayToggle,
                onLeftArrowClick = onLeftArrowClick,
                onRightArrowClick = onRightArrowClick,
                onTabClick = onTabClick,
            )
        }
    }
}

@Preview
@Composable
private fun RemoteControlScreenPreview() {
    RemoteControlScreen(
        volume = 65f,
        isMuted = false,
        ip = "",
        port = "8080",
        isConnected = true,
        onVolumeChange = {},
        onMuteToggle = {},
        onPlayToggle = {},
        onIpChange = {},
        onPortChange = {},
        events = emptyFlow()
    )
}