package com.hiskytechs.muhallinewuserapp.Utill

import com.hiskytechs.muhallinewuserapp.Models.Address

object AddressManager {
    private var savedAddress: Address? = null

    fun getAddress(): Address? = savedAddress

    fun saveAddress(address: Address) {
        savedAddress = address
    }

    fun hasAddress(): Boolean = savedAddress != null
}
