package com.example.algorandandroidshowcase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.algorand.algosdk.account.Account;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText seedPhrasditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seedPhrasditText=findViewById(R.id.seed_phrase_input);
        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 0);

        SharedPreferences sharedPreferences=getSharedPreferences(Constants.SharedPreferencesName,MODE_PRIVATE);
        String publicKey=sharedPreferences.getString(Constants.publicKey,"");
        if(publicKey!=""){
            Intent intent=new Intent(this,AccountInfoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
    public void importAccount(View v){
        String seedword=seedPhrasditText.getText().toString();
        Account account=createAccountWithMnemonic(seedword);
        SharedPreferences sharedPreferences=getSharedPreferences(Constants.SharedPreferencesName,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constants.publicKey,account.getAddress().toString());
        editor.putString(Constants.mnemonic,account.toMnemonic());
        editor.apply();
        Intent intent=new Intent(this,AccountInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void createAccount(View v){
        String seedword=seedPhrasditText.getText().toString();
        Account account=createAccountWithoutMnemonic();
        SharedPreferences sharedPreferences=getSharedPreferences(Constants.SharedPreferencesName,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constants.publicKey,account.getAddress().toString());
        editor.putString(Constants.mnemonic,account.toMnemonic());
        editor.apply();

        Intent intent=new Intent(this,AccountInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public static Account createAccountWithoutMnemonic( ){
        Account myAccount1= null;

        try {
            myAccount1 = new Account();
            Log.d("algoDebug"," algod account address: " + myAccount1.getAddress());
            Log.d("algoDebug"," algod account MNEMONIC: " + myAccount1.toMnemonic());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d("algoDebug"," Eror while creating new account "+e);
        }
        return myAccount1;
    }

    public static Account createAccountWithMnemonic(String mnemonic){
        Account myAccount1= null;
        try {
            myAccount1 = new Account(mnemonic);
            Log.d("algoDebug"," algod account address: " + myAccount1.getAddress());
            Log.d("algoDebug"," algod account MNEMONIC: " + myAccount1.toMnemonic());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d("algoDebug"," Eror while creating new account "+e);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return  myAccount1;
    }
}
