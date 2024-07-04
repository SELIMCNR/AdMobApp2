package com.example.admobapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var mInterstitialAd: InterstitialAd? = null
    private final val TAG = "MainActivity"
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }

        adView = findViewById(R.id.adView) // Ensure this matches your layout
        loadBanner()

        // Interstitial reklamı yükle
        loadInterstitialAd()
    }

    private fun loadBanner() {
        val adRequest = AdRequest.Builder().build()

        adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "Banner ad failed to load: ${adError.message}")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded for an ad.
            }

            override fun onAdLoaded() {
                Log.d(TAG, "Banner ad loaded successfully")
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that covers the screen.
            }
        }

        adView.loadAd(adRequest)
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                Log.d(TAG, "Interstitial ad loaded successfully")

                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Reklam kapatıldığında yeni bir reklam yükle
                        Log.d(TAG, "Interstitial ad dismissed")
                        loadInterstitialAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        // Hata durumunda yeni bir reklam yükle
                        Log.d(TAG, "Interstitial ad failed to show: ${adError.message}")
                        loadInterstitialAd()
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Reklam gösterildiğinde mInterstitialAd nesnesini null yap
                        Log.d(TAG, "Interstitial ad showed")
                        mInterstitialAd = null
                    }
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "Interstitial ad failed to load: ${adError.message}")
            }
        })
    }

    fun geçiki(view: View) {
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
        }
    }
}
