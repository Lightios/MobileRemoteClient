package pl.michal_cyran.mobileremote.remote.domain

interface IpProvider {
    fun getIp(): String
    fun getPort(): String
    fun saveIp(ip: String)
    fun savePort(port: String)
}