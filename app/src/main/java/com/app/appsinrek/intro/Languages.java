package com.app.appsinrek.intro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.appsinrek.databinding.ActivityLanguagesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class Languages extends AppCompatActivity {
    ActivityLanguagesBinding bi;
    String LANGUAGE_STR = "[{\"code\":\"jp\",\"name\":\"Japanese\",\"sub_name\":\"日本\"},{\"code\":\"en\",\"name\":\"English\",\"sub_name\":\"(Phone's language)\"},{\"code\":\"hi\",\"name\":\"हिंदी\",\"sub_name\":\"Hindi\"},{\"code\":\"pa\",\"name\":\"ਪੰਜਾਬੀ\",\"sub_name\":\"Punjabi\"},{\"code\":\"ta\",\"name\":\"தமிழ்\",\"sub_name\":\"Tamil\"},{\"code\":\"fr_FR\",\"name\":\"Français\",\"sub_name\":\"French\"}]";
    JSONArray languages;

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = ActivityLanguagesBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());
        try {
            languages = new JSONArray(LANGUAGE_STR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < languages.length(); i++) {
            try {
                JSONObject lang = languages.getJSONObject(i);
                bi.radioGroup.addOption(lang.getString("name"), lang.getString("sub_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        bi.radioGroup.setOnSelectionChangedListener(this::doSomething);
    }

    private void doSomething(int id) {
        try {
            JSONObject selected = languages.getJSONObject(id);
            setLocale(selected.getString("code"));
            Toast.makeText(this, "Language  " + selected.get("name"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= 24) {
            config.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        } else {
            config.locale = locale;
            getBaseContext().getApplicationContext().createConfigurationContext(config);
        }

        // shared pref.
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("lang", lang);
        editor.apply();
        Toast.makeText(this, "Language  Changed!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }


}


//                rb[i] = new RadioButton(this);
//                rb[i].setText(lang.getString("name"));
//                rb[i].setId(i);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
//                layoutParams.setMargins(0,7,0, 7);
//                rb[i].setLayoutParams(layoutParams);
//                rb[i].setPadding(20,0,0,0);
//                rb[i].setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//                rb[i].setChecked(false);
//                rb[i].setButtonTintList(ColorStateList.valueOf(R.color.green_lite));
//                rb[i].setButtonDrawable(R.drawable.radio_selector);
//                Typeface typeface = ResourcesCompat.getFont(this, R.font.muli_regular);
//                rb[i].setTypeface(typeface);
//                rb[i].setOnClickListener(this);

//                view[i] = inflater.inflate(R.layout.language_radio_button, null);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
//                layoutParams.setMargins(0, 7, 0, 7);
//                view[i].setLayoutParams(layoutParams);
//                TextView title1 = (TextView) view[i].findViewById(R.id.title_1);
//                TextView title2 = (TextView) view[i].findViewById(R.id.title_2);
//                title1.setText(lang.getString("name"));
//                title2.setText(lang.getString("sub_name"));
//                bi.radioGroup.addView(view[i]);