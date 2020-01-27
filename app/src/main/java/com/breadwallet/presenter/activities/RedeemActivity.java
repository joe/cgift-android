package com.breadwallet.presenter.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.breadwallet.R;

import com.breadwallet.api.CgiftAPIClient;
import com.breadwallet.api.RedeemCardRequest;
import com.breadwallet.presenter.activities.util.BRActivity;
import com.breadwallet.presenter.viewmodels.HomeViewModel;
import com.breadwallet.tools.adapter.RedeemListAdapter;
import com.breadwallet.tools.animation.UiUtils;
import com.breadwallet.tools.listeners.RecyclerItemClickListener;
import com.breadwallet.tools.manager.AppEntryPointHandler;
import com.breadwallet.tools.manager.InternetManager;
import com.breadwallet.tools.util.EventUtils;
import com.breadwallet.tools.util.Utils;
import com.jakewharton.rxbinding2.widget.RxTextView;

//import rx.Observable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.reactivex.observers.DisposableObserver;
import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Function;
import rx.schedulers.Schedulers;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;


public class RedeemActivity extends BRActivity {

    private static final String TAG = RedeemActivity.class.getName();
    private EditText mCode;
    private EditText mPin;
    private Button mRedeemButton;
    private RecyclerView mRedeemRecycler;
    private RedeemListAdapter mAdapter;
    private HomeViewModel mViewModel;

    Observable<Boolean> observable;
    public static final String REDEEM_EXTRA_DATA = "com.breadwallet.presenter.activities.RedeemActivity.REDEEM_EXTRA_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_redeem);
        mCode = findViewById(R.id.cardNumber);
        mPin = findViewById(R.id.pinNumber);
        mRedeemButton = findViewById(R.id.redeemButton);

        mRedeemRecycler = findViewById(R.id.redeem_wallet_list);
        mRedeemRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new RedeemListAdapter(this);
        mRedeemRecycler.setAdapter(mAdapter);

        // Get ViewModel, observe updates to Wallet and aggregated balance data
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mViewModel.getWallets().observe(this, wallets ->
                mAdapter.setWallets(wallets.subList(0, 2))
        );

        mRedeemRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this,
                mRedeemRecycler,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, float x, float y) {
                        if (position >= mAdapter.getItemCount() || position < 0) {
                            return;
                        }
//                        if (mAdapter.getItemViewType(position) == 0) {
//                            String currencyCode = mAdapter.getItemAt(position).getCurrencyCode();
//                            BRSharedPrefs.putCurrentWalletCurrencyCode(RedeemActivity.this, currencyCode);
//                            // Use BrdWalletActivity to show rewards view and animation if BRD and not shown yet.
//                            if (WalletTokenManager.BRD_CURRENCY_CODE.equalsIgnoreCase(currencyCode)) {
//                                if (!BRSharedPrefs.getRewardsAnimationShown(RedeemActivity.this)) {
//                                    Map<String, String> attributes = new HashMap<>();
//                                    attributes.put(EventUtils.EVENT_ATTRIBUTE_CURRENCY, WalletTokenManager.BRD_CURRENCY_CODE);
//                                    EventUtils.pushEvent(EventUtils.EVENT_REWARDS_OPEN_WALLET, attributes);
//                                }
//                                BrdWalletActivity.start(RedeemActivity.this, currencyCode);
//                            } else {
//                                WalletActivity.start(RedeemActivity.this, currencyCode);
//                            }
//                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
//                        } else {
//                            Intent intent = new Intent(RedeemActivity.this, AddWalletsActivity.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
//                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                }));

        Observable<String> code = RxTextView.textChanges(mCode).skip(1).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        });

        Observable<String> pin = RxTextView.textChanges(mPin).skip(1).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        });

        observable = Observable.combineLatest(code, pin, new BiFunction<String, String, Boolean>() {
            @Override
            public Boolean apply(String s, String s2) throws Exception {
                return isValidForm(s, s2);
            }
        });

        observable.subscribe(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                updateButton(aBoolean);
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {}
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        onNewIntent(getIntent());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.empty_300, R.anim.exit_to_bottom);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String data = intent.getStringExtra(REDEEM_EXTRA_DATA);
        data = "811111111199999";
        if (Pattern.matches("[1-9]{1}[0-9]{11,15}", data)) {
            mCode.setText(data, TextView.BufferType.NORMAL);
        }
    }

    public void updateButton(boolean valid) {
        mRedeemButton.setEnabled(valid);
        mRedeemButton.setAlpha(valid ? 1 : (float).5);
    }

    public boolean isValidForm(String code, String pin) {
        boolean validCode = !code.isEmpty() && code.length()>1;
        boolean validPin = !pin.isEmpty() && pin.length()>3;
        return validCode && validPin;
    }

    public void scan(View v) {
        UiUtils.openScanner(this);
    }

    public void redeem(View v) {
        RedeemCardRequest rcr = new RedeemCardRequest();
        //1111
        rcr.setCardNumber(mCode.getText().toString());
        //0000
        rcr.setPin(mPin.getText().toString());
        rcr.setWalletAddress("1Ej4Jy4S8Zo1V7MVexX41bDgpDzkj1vc5r");
        rcr.setDestinationCurrency("BTC");

        CgiftAPIClient.getApi(this).redeem(rcr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (!response.isSuccessful()) {
                                Log.d("REDEEM", String.format("response.message %s", response.message()));
                            } else {
                                Log.d("REDEEM", String.format("response.message %s", response.message()));
                            }
                        },
                        error -> {
                            //new Alerts(Alerts.Type.NETWORK_ERROR).withThrowable(error).show(this);
                            Log.d("REDEEM", String.format("ERROR"));
                        }
                );
    }

}
