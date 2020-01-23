package com.breadwallet.presenter.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.breadwallet.R;

import com.breadwallet.api.CgiftAPIClient;
import com.breadwallet.api.RedeemCardRequest;
import com.breadwallet.presenter.activities.util.BRActivity;
import com.breadwallet.presenter.viewmodels.HomeViewModel;
import com.breadwallet.tools.adapter.RedeemListAdapter;
import com.breadwallet.tools.listeners.RecyclerItemClickListener;
import com.breadwallet.tools.manager.BRSharedPrefs;
import com.breadwallet.tools.util.EventUtils;
import com.breadwallet.ui.wallet.WalletActivity;
import com.breadwallet.wallet.wallets.ethereum.WalletTokenManager;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RedeemActivity extends BRActivity {

    private static final String TAG = RedeemActivity.class.getName();
    private EditText mCode;
    private EditText mPin;
    private RecyclerView mRedeemRecycler;
    private RedeemListAdapter mAdapter;
    private HomeViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        mCode = findViewById(R.id.cardNumber);
        mPin = findViewById(R.id.pinNumber);

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.empty_300, R.anim.exit_to_bottom);
    }

    public void scan(View v) {

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

    @Override
    protected void onResume() {
        super.onResume();
    }

}
