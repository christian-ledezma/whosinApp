package com.ucb.whosin.features.login.domain.model

import com.ucb.whosin.features.login.domain.vo.CountryCodeValue
import com.ucb.whosin.features.login.domain.vo.Email
import com.ucb.whosin.features.login.domain.vo.OptionalPersonName
import com.ucb.whosin.features.login.domain.vo.Password
import com.ucb.whosin.features.login.domain.vo.PersonName
import com.ucb.whosin.features.login.domain.vo.PhoneNumber

data class RegisterData(
    val email: Email,
    val password: Password,
    val name: PersonName,
    val lastname: PersonName,
    val secondLastname: OptionalPersonName,
    val phoneNumber: PhoneNumber,
    val countryCode: CountryCodeValue
) {
    companion object {
        fun create(
            email: String,
            password: String,
            name: String,
            lastname: String,
            secondLastname: String?,
            phone: String,
            countryCode: CountryCode
        ): Result<RegisterData> {
            val countryCodeVO = CountryCodeValue.fromEnum(countryCode)
            val emailVO = Email.create(email).getOrElse { return Result.failure(it) }
            val passwordVO = Password.create(password).getOrElse { return Result.failure(it) }
            val nameVO = PersonName.create(name, "nombre").getOrElse { return Result.failure(it) }
            val lastnameVO = PersonName.create(lastname, "apellido paterno").getOrElse { return Result.failure(it) }
            val secondLastnameVO = OptionalPersonName.create(secondLastname).getOrElse { return Result.failure(it) }
            val phoneVO = PhoneNumber.create(phone, countryCodeVO.value).getOrElse { return Result.failure(it) }

            return Result.success(
                RegisterData(
                    email = emailVO,
                    password = passwordVO,
                    name = nameVO,
                    lastname = lastnameVO,
                    secondLastname = secondLastnameVO,
                    phoneNumber = phoneVO,
                    countryCode = countryCodeVO
                )
            )
        }
    }
}