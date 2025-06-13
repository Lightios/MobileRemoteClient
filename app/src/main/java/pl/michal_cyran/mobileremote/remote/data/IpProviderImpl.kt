package pl.michal_cyran.mobileremote.remote.data

import android.content.Context
import android.util.Log
import pl.michal_cyran.mobileremote.remote.domain.IpProvider
import androidx.core.content.edit

class IpProviderImpl(
    context: Context
): IpProvider {
    val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    override fun getIp(): String {
        val value = sharedPref.getString("ip_address", "192.168.1.54")
        return value!!
    }

    override fun getPort(): String {
        val value = sharedPref.getString("port", "8085")
        return value!!
    }

    override fun saveIp(ip: String) {
        Log.d("IpProviderImpl", "Saving IP: $ip")
        sharedPref.edit {
            putString("ip_address", ip)
            apply()
        }
    }

    override fun savePort(port: String) {
        sharedPref.edit {
            putString("port", port)
        }
    }
}