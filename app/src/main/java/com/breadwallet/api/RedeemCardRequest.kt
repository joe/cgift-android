package com.breadwallet.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by afresina on 2020-01-08.
 * Copyright 2020 CGift.io. All rights reserved.
 */
class RedeemCardRequest {

    @Expose
    @SerializedName("card_number")
    var cardNumber: String? = null

    @Expose
    @SerializedName("pin")
    var pin: String? = null

    @Expose
    @SerializedName("wallet_address")
    var walletAddress: String? = null

    @Expose
    @SerializedName("destination_currency")
    var destinationCurrency: String? = null

}