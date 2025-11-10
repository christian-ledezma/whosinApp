package com.ucb.whosin.features.event.domain.repository

import com.ucb.whosin.features.event.domain.model.EventModel

interface IEventRepository {
    fun findByName(value: String): Result<EventModel>
}