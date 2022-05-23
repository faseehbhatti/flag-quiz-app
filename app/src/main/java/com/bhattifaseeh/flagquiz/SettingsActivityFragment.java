// SettingsActivityFragment.java
// Subclass of PreferenceFragment for managing app settings
package com.bhattifaseeh.flagquiz;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsActivityFragment extends PreferenceFragment {
   // creates preferences GUI from preferences.xml file in res/xml
   @Override
   public void onCreate(Bundle bundle) {
      super.onCreate(bundle);
      addPreferencesFromResource(R.xml.preferences); // load from XML
   }
}

