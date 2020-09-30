package com.example.algorandandroidshowcase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.algorand.algosdk.algod.client.AlgodClient;
import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.api.AlgodApi;

import java.math.BigInteger;

public class AccountInfoActivity extends AppCompatActivity {
    TextView publicKeyText;
    TextView mnemonicText;
    TextView accountBalance;
    ProgressBar progressBar;
    AlgodApi algodApiInstance;

    Handler handler=new Handler() {
        public void handleMessage(Message msg) {
            String amount=msg.getData().getString("amount");
            accountBalance.setText(amount+" Algo");
            progressBar.setVisibility(View.INVISIBLE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        publicKeyText=findViewById(R.id.public_key_value);
        mnemonicText=findViewById(R.id.mnemonic_value);
        accountBalance=findViewById(R.id.balance_value);
        progressBar=findViewById(R.id.progress_bar);



        SharedPreferences sharedPreferences=getSharedPreferences(Constants.SharedPreferencesName,MODE_PRIVATE);
        final String publicKey=sharedPreferences.getString(Constants.publicKey,"");
        String mnemonic=sharedPreferences.getString(Constants.mnemonic,"");
        publicKeyText.setText(publicKey);
        mnemonicText.setText(mnemonic);

        final String ALGOD_API_ADDR = "https://testnet-algorand.api.purestake.io/ps1";
        final String ALGOD_API_TOKEN = "your-api-key";

        AlgodClient client = new AlgodClient();
        client.addDefaultHeader("X-API-Key", ALGOD_API_TOKEN);
        client.setBasePath(ALGOD_API_ADDR);
        algodApiInstance = new AlgodApi(client);
        new  Thread(new Runnable() {
            @Override
            public void run() {
                try {

                 BigInteger amount=getWalletBalance(publicKey);
                 Bundle bundle=new Bundle();
                 bundle.putString("amount",String.valueOf(amount));
                 Message message=new Message();
                 message.setData(bundle);
                 handler.sendMessage(message);
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public  void logOut(View v){
        SharedPreferences sharedPreferences=getSharedPreferences(Constants.SharedPreferencesName,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public BigInteger getWalletBalance(String address) throws ApiException {
        com.algorand.algosdk.algod.client.model.Account account= algodApiInstance.accountInformation(address);
        Log.d("algoDebug","Amount of account is "+account.getAmount());
        return account.getAmount().divide(new BigInteger("1000000"));
    }

}
