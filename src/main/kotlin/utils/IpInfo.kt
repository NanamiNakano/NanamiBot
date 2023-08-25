package utils

import io.ipinfo.api.IPinfo
import io.ipinfo.api.model.IPResponse

object IpInfo {
    private val ipInfo: IPinfo = IPinfo.Builder().setToken(System.getenv("IPINFO_TOKEN")).build()

    fun getIpInfo(ip: String): IPResponse {
        return ipInfo.lookupIP(ip)
    }
}
