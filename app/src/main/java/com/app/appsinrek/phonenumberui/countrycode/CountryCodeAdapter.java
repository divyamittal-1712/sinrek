package com.app.appsinrek.phonenumberui.countrycode;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

public class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.CountryCodeViewHolder>
        implements FastScrollRecyclerViewInterface {

    private List<Country> mCountries;
    private Callback mCallback;
    private HashMap<String, Integer> mMapIndex;

    public interface Callback {
        void onItemCountrySelected(Country country);
    }

    public CountryCodeAdapter(List<Country> countries, Callback callback, HashMap<String, Integer> mapIndex) {
        this.mCountries = countries;
        this.mCallback = callback;
        mMapIndex = mapIndex;
    }

    @Override
    public CountryCodeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        android.view.View rootView = inflater.inflate(R.layout.item_country, viewGroup, false);
        return new CountryCodeViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(CountryCodeViewHolder viewHolder, final int i) {
        final int position = viewHolder.getAdapterPosition();
        viewHolder.setCountry(mCountries.get(position));
        viewHolder.rlyMain.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                mCallback.onItemCountrySelected(mCountries.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCountries.size();
    }

    class CountryCodeViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlyMain;
        android.widget.TextView tvName, tvFlag, tvCode;
//        ImageView imvFlag;
//        LinearLayout llyFlagHolder;
        android.view.View viewDivider;

        CountryCodeViewHolder(android.view.View itemView) {
            super(itemView);
            rlyMain = (RelativeLayout) itemView;
            tvFlag = rlyMain.findViewById(R.id.country_flag_tv);
            tvName = rlyMain.findViewById(R.id.country_name_tv);
            tvCode = rlyMain.findViewById(R.id.code_tv);
//            imvFlag = rlyMain.findViewById(R.id.flag_imv);
//            llyFlagHolder = rlyMain.findViewById(R.id.flag_holder_lly);
            viewDivider = rlyMain.findViewById(R.id.preference_divider_view);
        }

        private void setCountry(Country country) {
            if (country != null) {
                viewDivider.setVisibility(android.view.View.GONE);
                tvName.setVisibility(android.view.View.VISIBLE);
                tvCode.setVisibility(android.view.View.VISIBLE);
//                llyFlagHolder.setVisibility(android.view.View.VISIBLE);
                String countryNameAndCode = tvName.getContext()
                        .getString(R.string.country_name_and_code, country.getName(),
                                country.getIso().toUpperCase());
                tvFlag.setText(country.getFlag());
                tvName.setText(countryNameAndCode);
                tvCode.setText(
                        tvCode.getContext().getString(R.string.phone_code, country.getPhoneCode()));
//                imvFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));

            } else {
                viewDivider.setVisibility(android.view.View.VISIBLE);
                tvName.setVisibility(android.view.View.GONE);
                tvCode.setVisibility(android.view.View.GONE);
//                llyFlagHolder.setVisibility(android.view.View.GONE);
            }
        }
    }

    @Override
    public HashMap<String, Integer> getMapIndex() {
        return this.mMapIndex;
    }
}
