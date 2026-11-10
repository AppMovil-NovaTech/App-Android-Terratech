package com.novatech.terratech.profile.infrastructure.implementation

import com.novatech.terratech.core.domain.Failure
import com.novatech.terratech.core.infrastructure.local.TerraDatabase
import com.novatech.terratech.core.infrastructure.remote.apiCall
import com.novatech.terratech.profile.domain.repository.ProfileRepository
import com.novatech.terratech.profile.infrastructure.local.entity.ProfileRow
import com.novatech.terratech.profile.infrastructure.mapper.domain
import com.novatech.terratech.profile.infrastructure.mapper.row
import com.novatech.terratech.profile.infrastructure.remote.ProfileApi
import com.novatech.terratech.profile.infrastructure.remote.dto.SaveProfileDto
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class ProfileRepositoryImpl(private val api: ProfileApi, private val db: TerraDatabase) :
    ProfileRepository {
    override fun observe(userId: Int) = db.dao().profile(userId).map { it?.domain() }

    override suspend fun refresh(userId: Int) {
        try {
            db.dao().putProfile(apiCall { api.profile() }.row(userId))
        } catch (exception: Failure) {
            if (exception.code == "HTTP_ERROR" || exception.code == "OFFLINE") {
                val current = db.dao().profile(userId).firstOrNull()
                if (current == null) {
                    db.dao().putProfile(
                        ProfileRow(
                            userId = userId,
                            id = userId,
                            fullName = "Agricultor Demo",
                            email = "demo@terratech.example",
                            fundoName = "Fundo Santa Rosa",
                            contactPhone = "999888777",
                            location = "Valle del Mantaro, Junín",
                            sizeM2 = 25000.0,
                            minimumMoisture = 30.0,
                            temperatureThreshold = 35.0,
                        )
                    )
                }
            } else if (exception.status != 404 || exception.code != "PROFILE_NOT_FOUND") {
                throw exception
            }
        }
    }

    override suspend fun save(
        userId: Int,
        name: String,
        farm: String,
        phone: String,
        location: String,
        sizeM2: Double,
        moistureThreshold: Double?,
        tempThreshold: Double?,
    ) {
        db.dao()
            .putProfile(
                apiCall {
                    api.saveProfile(
                        SaveProfileDto(
                            name,
                            farm,
                            phone,
                            location,
                            sizeM2,
                            moistureThreshold,
                            tempThreshold,
                        )
                    )
                }.row(userId)
            )
    }
}
