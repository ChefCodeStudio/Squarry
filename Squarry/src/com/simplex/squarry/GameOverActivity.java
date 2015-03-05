package com.simplex.squarry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * This class custom the Actitivty on the game over screen
 * It contains an actions to restart the game und show the
 * score and highscore.
 * 
 * Date: 22.12.2014
 * 
 * @author Sayan.Vaaheean
 *
 */
public class GameOverActivity extends Activity {

	/**
	 * The TexView ScoreValue
	 */
	private TextView textViewFinalScoreValue;

	/**
	 * The TextView HighScoreValue
	 */
	private TextView textViewHighScoreValue;
	
	/**
	 * The FinalScore
	 */
	private String finalScore;
	
	/**
	 * The MediaPlayer GameOverSound
	 */
	public MediaPlayer mp_gameover;
	
	private InterstitialAd interstitialAd;
	AdView adView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Make View Fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_gameover);
		
		// Add Advertisment
		interstitialAd = new InterstitialAd(GameOverActivity.this);
		interstitialAd.setAdUnitId("ca-app-pub-3204051552386925/4131791496");
		
		adView = (AdView)this.findViewById(R.id.adViewGameOver);
		AdRequest adRequest = new AdRequest.Builder()
		    .build();
		
		adView.loadAd(adRequest);
		interstitialAd.loadAd(adRequest);
		interstitialAd.setAdListener(new AdListener() {
			public void onAdLoaded() {
				if(interstitialAd.isLoaded()) {
					interstitialAd.show();
				}
			}
		});
		
		// start gameover Melodie
		SharedPreferences prefs = getSharedPreferences("audio", MODE_PRIVATE); 
		String audioState = prefs.getString("mute", null);
		if (audioState.equals("unmute")) {
			mp_gameover = MediaPlayer.create(getApplicationContext(),R.raw.gameover);
			mp_gameover.start();
		}
		
		// get score		
		Intent intent = getIntent();
		finalScore = intent.getStringExtra("score");
		textViewFinalScoreValue = (TextView) findViewById(R.id.textViewScore);
		textViewFinalScoreValue.setText(finalScore);
		
		// get highscore
		SharedPreferences sharedPreferencesHighScore = this.getSharedPreferences("highScore", Context.MODE_PRIVATE);
		int oldScore = sharedPreferencesHighScore.getInt("score", 0);
		if (Integer.parseInt(finalScore) > oldScore) {
			Editor editor = sharedPreferencesHighScore.edit();
			editor.putInt("score", Integer.parseInt(finalScore));
			editor.commit();			
		}
		
		textViewHighScoreValue = (TextView) findViewById(R.id.textViewHighScore);
		textViewHighScoreValue.setText(sharedPreferencesHighScore.getInt("score", 0) + "");
	}
	
	/**
	 * Restart the game
	 * @param view
	 */
	public void restart(View view) {
		Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		SharedPreferences prefs = getSharedPreferences("audio", MODE_PRIVATE); 
		String audioState = prefs.getString("mute", null);
		if (audioState.equals("unmute")) {
			mp_gameover.stop();
		}
		Intent intent = new Intent(GameOverActivity.this, StartActivity.class);
		startActivity(intent);
	}
	
	@Override
    public void onResume() {
        super.onResume();
        // Resume the AdView.
        adView.resume();
    }

    @Override
    public void onPause() {
    	mp_gameover.stop();
    	
        // Pause the AdView.
    	adView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Destroy the AdView.
    	adView.destroy();
        super.onDestroy();
    }
}
