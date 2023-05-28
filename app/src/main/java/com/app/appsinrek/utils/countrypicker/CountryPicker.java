package com.app.appsinrek.utils.countrypicker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import com.app.appsinrek.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CountryPicker extends DialogFragment {

    private CountryAdapter adapter;
    private List<Country> countriesList = new ArrayList<>();
    private List<Country> selectedCountriesList = new ArrayList<>();
    private CountryPickerListener listener;

    public CountryPicker() {
        this.setCountriesList(Country.getAllCountries());
    }

    public static CountryPicker newInstance(String dialogTitle) {
        CountryPicker picker = new CountryPicker();
        Bundle bundle = new Bundle();
        bundle.putString("dialogTitle", dialogTitle);
        picker.setArguments(bundle);
        return picker;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_country_picker, null);
        Bundle args = this.getArguments();
        if (args != null) {
            String dialogTitle = args.getString("dialogTitle");
            this.getDialog().setTitle(dialogTitle);
        }
        EditText searchEditText = view.findViewById(R.id.country_code_picker_search);
        ListView countryListView = view.findViewById(R.id.country_code_picker_listview);
        this.selectedCountriesList = new ArrayList<>(this.countriesList.size());
        this.selectedCountriesList.addAll(this.countriesList);
        this.adapter = new CountryAdapter(this.getActivity(), this.selectedCountriesList);
        countryListView.setAdapter(this.adapter);
        countryListView.setOnItemClickListener((parent, view1, position, id) -> {
            if (CountryPicker.this.listener != null) {
                Country country = CountryPicker.this.selectedCountriesList.get(position);
                CountryPicker.this.listener.onSelectCountry(country.getName(), country.getCode(), country.getDialCode(), country.getFlag());
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                CountryPicker.this.search(s.toString());
            }
        });
        return view;
    }

    public void setListener(CountryPickerListener listener) {
        this.listener = listener;
    }

    @SuppressLint({"DefaultLocale"})
    private void search(String text) {
        this.selectedCountriesList.clear();
        for (Country country : this.countriesList) {
            if (country.getName().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase())) {
                this.selectedCountriesList.add(country);
            }
        }
        this.adapter.notifyDataSetChanged();
    }

    private void setCountriesList(List<Country> newCountries) {
        this.countriesList.clear();
        this.countriesList.addAll(newCountries);
    }
}
