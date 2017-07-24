package com.example.bibesh.paypal;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    PayPalConfiguration m_configuration;
    String m_paypalClientId = "AWClhIVCoyoXrAsqXHKuXi6CcpZcB9afl-2_q_SAcai728wy740I7eHxC_hRhGzZGz7RpwF-ve2tZj32"; //We get this Id from the Application created in paypal account.
    Intent m_service;
    int m_paypalRequestCode = 999; // Any number

    @BindView(R.id.response) TextView response;
    @BindView(R.id.click) Button click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); //Butter Knife

        //Used sandbox for practise purpose
        m_configuration = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(m_paypalClientId);
        m_service = new Intent(this, PayPalService.class);
        //For Configuration
        m_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
        startService(m_service);
    }

    @OnClick(R.id.click)
    void click() {
        // Big Decimal 10 -> Amount, EUR,USD -> payment Currency, Text Pay.. -> Message.
        PayPalPayment payment = new PayPalPayment(new BigDecimal(10), "EUR", "Test Payment with paypal", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, m_paypalRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == m_paypalRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    String state = confirmation.getProofOfPayment().getState();
                    if (state.equals("approved"))
                        response.setText("Done");
                    else
                        response.setText("error");
                } else
                    response.setText("Confirmation is null");
            }
        }
    }
}
