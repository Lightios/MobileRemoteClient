package pl.michal_cyran.mobileremote.remote.domain

import kotlinx.serialization.Serializable

@Serializable
data class Volume(
    val value: Float,
    val isMuted: Boolean,
)
