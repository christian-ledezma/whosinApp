package com.ucb.whosin.features.login.domain.model

class RegisterData(
    val email: String,
    val password: String,
    val name: String,
    val lastname: String,
    val secondLastname: String?,
    val phone: String,
    val countryCode: CountryCode
)