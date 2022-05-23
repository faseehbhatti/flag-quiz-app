// MainActivity.java
// Hosts the MainActivityFragment on a phone and both the
// MainActivityFragment and SettingsActivityFragment on a tablet
package com.bhattifaseeh.flagquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
   // keys for reading data from SharedPreferences
   public static final String CHOICES = "pref_numberOfChoices";
   public static final String REGIONS = "pref_regionsToInclude";

   private boolean phoneDevice = true; // used to force portrait mode
   private boolean preferencesChanged = true; // did preferences change?

   // configure the MainActivity
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      // set default values in the app's SharedPreferences
      PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

      // register listener for SharedPreferences changes
      PreferenceManager.getDefaultSharedPreferences(this).
         registerOnSharedPreferenceChangeListener(
            preferencesChangeListener);

      // determine screen size
      int screenSize = getResources().getConfiguration().screenLayout &
         Configuration.SCREENLAYOUT_SIZE_MASK;

      // if device is a tablet, set phoneDevice to false
      if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
         screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
         phoneDevice = false; // not a phone-sized device

      // if running on phone-sized device, allow only portrait orientation
      if (phoneDevice)
         setRequestedOrientation(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   }

   // called after onCreate completes execution
   @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
   @Override
   protected void onStart() {
      super.onStart();

      if (preferencesChanged) {
         // now that the default preferences have been set,
         // initialize MainActivityFragment and start the quiz
         MainActivityFragment quizFragment = (MainActivityFragment)
            getSupportFragmentManager().findFragmentById(
               R.id.quizFragment);
         assert quizFragment != null;
         quizFragment.updateGuessRows(
            PreferenceManager.getDefaultSharedPreferences(this));
         quizFragment.updateRegions(
            PreferenceManager.getDefaultSharedPreferences(this));
         quizFragment.resetQuiz();
         preferencesChanged = false;
      }
   }

   // show menu if app is running on a phone or a portrait-oriented tablet
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // get the device's current orientation
      int orientation = getResources().getConfiguration().orientation;

      // display the app's menu only in portrait orientation
      if (orientation == Configuration.ORIENTATION_PORTRAIT) {
         // inflate the menu
         getMenuInflater().inflate(R.menu.menu_main, menu);
         return true;
      }
      else
         return false;
   }

   // displays the SettingsActivity when running on a phone
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      Intent preferencesIntent = new Intent(this, SettingsActivity.class);
      startActivity(preferencesIntent);
      return super.onOptionsItemSelected(item);
   }

   // called when the user changes the app's preferences
   // listener for changes to the app's SharedPreferences
   private final OnSharedPreferenceChangeListener preferencesChangeListener =
           (sharedPreferences, key) -> {
              preferencesChanged = true; // user changed app setting

              MainActivityFragment quizFragment = (MainActivityFragment)
                 getSupportFragmentManager().findFragmentById(
                    R.id.quizFragment);

              if (key.equals(CHOICES)) { // # of choices to display changed
                 assert quizFragment != null;
                 quizFragment.updateGuessRows(sharedPreferences);
                 quizFragment.resetQuiz();
              }
              else if (key.equals(REGIONS)) { // regions to include changed
                 Set<String> regions =
                    sharedPreferences.getStringSet(REGIONS, null);

                 if (regions != null && regions.size() > 0) {
                    assert quizFragment != null;
                    quizFragment.updateRegions(sharedPreferences);
                    quizFragment.resetQuiz();
                 }
                 else {
                    // must select one region--set North America as default
                    SharedPreferences.Editor editor =
                       sharedPreferences.edit();
                    assert regions != null;
                    regions.add(getString(R.string.default_region));
                    editor.putStringSet(REGIONS, regions);
                    editor.apply();

                    Toast.makeText(MainActivity.this,
                       R.string.default_region_message,
                       Toast.LENGTH_SHORT).show();
                 }
              }

              Toast.makeText(MainActivity.this,
                 R.string.restarting_quiz,
                 Toast.LENGTH_SHORT).show();
           };
}


// “On my honor, I have neither received nor given any unauthorized assistance on this examination (assignment).” - Faseeh Bhatti