package com.app.appsinrek.intro;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.app.appsinrek.databinding.LanguageRadioButtonBinding;

import java.util.ArrayList;

public class LanguageRadioGroup extends RadioGroup implements View.OnClickListener {

    private final ArrayList<LanguageRadioButtonBinding> radioButtons = new ArrayList<>();
    private OnSelectionChangedListener selectionChangedListener;

    public LanguageRadioGroup(Context context) {
        super(context);
    }

    public LanguageRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener selectionChangedListener) {
        this.selectionChangedListener = selectionChangedListener;
    }

    public int addOption(String title, String description) {
        // inflate view and get binding
        LanguageRadioButtonBinding buttonBinding = LanguageRadioButtonBinding.inflate(LayoutInflater.from(getContext()), this, true);
        // set title and description
        buttonBinding.setTitle(title);
        buttonBinding.setDescription(description);
        // give the button an id (just use the index)
        buttonBinding.setButtonId(radioButtons.size());
        // set the root view's tag to the binding, so we can get the binding from the view
        View root = buttonBinding.getRoot();
        root.setTag(buttonBinding);
        // set click listener on the whole view, so we can click anywhere
        root.setOnClickListener(this);
        radioButtons.add(buttonBinding);
        // return button id to caller, so they know what was clicked
        return buttonBinding.getButtonId();
    }

    @Override
    public void onClick(View v) {
        for (LanguageRadioButtonBinding binding : radioButtons) {
            binding.setChecked(v.getTag() == binding);
        }
        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged(getSelected());
        }
    }

    public int getSelected() {
        for (LanguageRadioButtonBinding binding : radioButtons) {
            if (binding.getChecked()) {
                return binding.getButtonId();
            }
        }
        return -1;
    }

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int buttonId);
    }

}