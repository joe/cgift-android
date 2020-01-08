package com.breadwallet.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

/**
 * Created by afresina on 2020-01-08.
 * Copyright 2020 CGift.io. All rights reserved.
 */
interface CgiftAPI {

//        RedeemCardRequest rcr = new RedeemCardRequest();
//        rcr.cardNumber = "1111";
//        rcr.pin = "0000";
//        rcr.walletAddress = "1Ej4Jy4S8Zo1V7MVexX41bDgpDzkj1vc5r";
//        rcr.destinationCurrency = "BTC";
//
//        CgiftAPIClient.getApi(this).redeem(rcr)
//                .subscribeOn(Schedulers.io())
//                .subscribe(
//                        response -> {
//                            if (!response.isSuccessful()) {
//                                Log.d("REDEEM", String.format("response.message %s", response.message()));
//                            } else {
//                                Log.d("REDEEM", String.format("response.message %s", response.message()));
//                            }
//                        },
//                        error -> {
//                            //new Alerts(Alerts.Type.NETWORK_ERROR).withThrowable(error).show(this);
//                            Log.d("REDEEM", String.format("ERROR"));
//                        }
//                );

    // curl -X POST -H "Content-Type: application/json" -d
    // '{"card_number":"1111","pin":"0000","wallet_address":"1Ej4Jy4S8Zo1V7MVexX41bDgpDzkj1vc5r","destination_currency":"BTC"}'
    // https://api.cgift.io/api/v1/gift_card_redemptions
    @POST("gift_card_redemptions")
    fun redeem(@Body request: RedeemCardRequest?): Observable<Response<RedeemCardResponse?>?>?

}