package com.snehonir.niyeesho.features.drive.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DriveFileDto(
    val id: String? = null,
    val name: String? = null,
    val mimeType: String? = null,
    val webContentLink: String? = null,
)
