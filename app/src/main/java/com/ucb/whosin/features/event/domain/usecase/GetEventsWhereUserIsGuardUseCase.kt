package com.ucb.whosin.features.event.domain.usecase

import android.util.Log
import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.repository.IEventRepository
import java.util.Calendar

class GetEventsWhereUserIsGuardUseCase(
    private val repository: IEventRepository
) {
    suspend operator fun invoke(guardId: String): List<EventModel> {
        Log.d("USE_CASE_TEST", "========================================")
        Log.d("USE_CASE_TEST", "UseCase invocado con guardId: $guardId")
        Log.d("USE_CASE_TEST", "Repository: $repository")
        Log.d("USE_CASE_TEST", "========================================")
        val result = repository.getEventsWhereUserIsGuard(guardId)

        Log.d("USE_CASE_TEST", "UseCase resultado: ${result.size} eventos")
        return result
    }
}