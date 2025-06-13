package pl.michal_cyran.mobileremote.remote.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pl.michal_cyran.mobileremote.core.data.networking.safeCall
import pl.michal_cyran.mobileremote.core.domain.util.NetworkError
import pl.michal_cyran.mobileremote.remote.domain.RemoteDataSource
import pl.michal_cyran.mobileremote.remote.domain.Volume
import pl.michal_cyran.mobileremote.core.domain.util.Result
import pl.michal_cyran.mobileremote.remote.domain.IpProvider

class RemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val ipProvider: IpProvider
): RemoteDataSource {
    private var ip: String
    private var port: String

    init {
        ip = ipProvider.getIp()
        port = ipProvider.getPort()
    }

    val url: String
        get() = "http://$ip:$port"

    val volumeUrl: String
        get() = "$url/volume"

    val playUrl: String
        get() = "$url/toggle-play"

     override suspend fun setVolume(value: Int, isMuted: Boolean): Result<Unit, NetworkError> {
         Log.d("RemoteDataSource", "Address:  $url Setting volume to $value, muted: $isMuted")

        return safeCall<Unit> {
            httpClient.post(
                urlString = volumeUrl,
            ) {
                contentType(ContentType.Application.Json)
                setBody(Volume(value.toFloat(), isMuted))
            }
        }
    }

    override suspend fun togglePlay(): Result<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.post(
                urlString = playUrl,
            ) {
                contentType(ContentType.Application.Json)
            }
        }
    }

    override fun setIpAddress(ipAddress: String) {
        this.ip = ipAddress
        ipProvider.saveIp(ipAddress)
    }

    override fun setPort(port: String) {
        this.port = port.toString()
        ipProvider.savePort(port)
    }

    override fun getIp(): String {
        return ip
    }

    override fun getPort(): String {
        return port
    }
}