package com.example.challange2.note;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.challange2.R;
import com.google.android.material.appbar.AppBarLayout;

public interface ToolbarButtonSetup {
    default void setupToolbarButtons(@NonNull Fragment fragment, Integer... buttonResourceIds){
        View view = fragment.getView();
        if (view == null) return;

        // Inflate the layout for this fragment
        AppBarLayout appBar = view.findViewById(R.id.appBar);
        Toolbar toolbar = view.findViewById(R.id.tb);

        Context context = fragment.requireContext();

        // Iterate through the list of button resource IDs
        for (Integer resourceId : buttonResourceIds) {
            // Create an ImageButton
            ImageButton button = new ImageButton(context);
            button.setImageResource(resourceId);
            button.setBackground(null);

            // Add an OnClickListener if required
            //button.setOnClickListener(clickListener);

            // Set layout parameters and add the button to the toolbar
            Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            toolbar.addView(button, params);
        }
    }
}