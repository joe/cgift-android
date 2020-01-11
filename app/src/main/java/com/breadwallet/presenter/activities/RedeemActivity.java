package com.breadwallet.presenter.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.breadwallet.R;

import com.breadwallet.model.Wallet;
import com.breadwallet.presenter.activities.util.BRActivity;
import com.breadwallet.presenter.viewmodels.HomeViewModel;
import com.breadwallet.tools.adapter.WalletListAdapter;

import java.util.Collection;
import java.util.stream.Collectors;

public class RedeemActivity extends BRActivity {

    private static final String TAG = RedeemActivity.class.getName();
    private ImageButton mBackButton;
    private RecyclerView mWalletRecycler;
    private WalletListAdapter mAdapter;
    private HomeViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        //Toolbar toolbar = findViewById(R.id.toolbar);

        mWalletRecycler = findViewById(R.id.redeem_wallet_list);
        mWalletRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new WalletListAdapter(this);
        mWalletRecycler.setAdapter(mAdapter);

        // Get ViewModel, observe updates to Wallet and aggregated balance data
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mViewModel.getWallets().observe(this, wallets ->
                mAdapter.setWallets(wallets.subList(0, 2))
        );

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.empty_300, R.anim.exit_to_bottom);
    }

    public void close(View v) {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
