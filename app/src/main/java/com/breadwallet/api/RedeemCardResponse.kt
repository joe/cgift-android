package com.breadwallet.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by afresina on 2020-01-08.
 * Copyright 2020 CGift.io. All rights reserved.
 */
class RedeemCardResponse {

    @Expose
    @SerializedName("status")
    var status = 0

    @Expose
    @SerializedName("code")
    var code: String? = null

    @Expose
    @SerializedName("title")
    var title: String? = null

    @Expose
    @SerializedName("detail")
    var detail: String? = null

}