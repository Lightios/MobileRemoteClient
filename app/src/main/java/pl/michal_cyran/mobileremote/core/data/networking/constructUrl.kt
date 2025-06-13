package pl.michal_cyran.mobileremote.core.data.networking



fun constructUrl(url: String): String {
    return "?"
//    return when {
//        url.contains(BuildConfig.BASE_URL) -> url
//        url.startsWith("/") -> BuildConfig.BASE_URL + url.drop(1)
//        else -> "${BuildConfig.BASE_URL}/$url"
//    }
}