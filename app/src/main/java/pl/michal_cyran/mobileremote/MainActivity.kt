package pl.michal_cyran.mobileremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.michal_cyran.mobileremote.remote.presentation.RemoteControlScreen
import pl.michal_cyran.mobileremote.remote.presentation.RemoteViewModel
import pl.michal_cyran.mobileremote.ui.theme.MobileRemoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileRemoteTheme {
                val viewModel = koinViewModel<RemoteViewModel>()

                RemoteControlScreen(
                    volume = viewModel.volume.collectAsStateWithLifecycle().value,
                    isMuted = viewModel.isMuted.collectAsStateWithLifecycle().value,
                    ip = viewModel.ip.collectAsStateWithLifecycle().value,
                    port = viewModel.port.collectAsStateWithLifecycle().value,
                    onVolumeChange = { viewModel.setVolume(it) },
                    onMuteToggle = { viewModel.toggleMute() },
                    onPlayToggle = { viewModel.togglePlay() },
                    onLeftArrowClick = { viewModel.leftArrowClick() },
                    onRightArrowClick = { viewModel.rightArrowClick() },
                    onIpChange = { viewModel.setIp(it) },
                    onPortChange = { viewModel.setPort(it) },
                    events = viewModel.events,
                )
            }
        }
    }
}