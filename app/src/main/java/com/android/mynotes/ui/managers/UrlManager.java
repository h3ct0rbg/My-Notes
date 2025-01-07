package com.android.mynotes.ui.managers;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.mynotes.R;

/**
 * Manages URL-related functionalities, such as adding and removing URLs, and displaying them
 * within a specified layout in an Activity.
 */
public class UrlManager {

    private final Activity activity;
    private AlertDialog dialogAddURL;
    private final TextView textWebURL;
    private final LinearLayout layoutWebURL;

    /**
     * Constructs a new UrlManager instance.
     *
     * @param activity    The Activity where this manager is being used.
     * @param textWebURL  The TextView used to display the URL.
     * @param layoutWebURL The LinearLayout containing the URL view elements.
     */
    public UrlManager(Activity activity, TextView textWebURL, LinearLayout layoutWebURL) {
        this.activity = activity;
        this.textWebURL = textWebURL;
        this.layoutWebURL = layoutWebURL;
    }

    /**
     * Displays a dialog allowing the user to enter a URL.
     * If the entered URL is valid, it will be set to the TextView
     * and the containing layout will be made visible.
     */
    public void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View view = LayoutInflater.from(activity).inflate(
                    R.layout.layout_add_url,
                    activity.findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);

            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(v -> {
                if (inputURL.getText().toString().trim().isEmpty()) {
                    Toast.makeText(activity, "Enter URL", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                    Toast.makeText(activity, "Enter valid URL", Toast.LENGTH_SHORT).show();
                } else {
                    textWebURL.setText(inputURL.getText().toString());
                    layoutWebURL.setVisibility(View.VISIBLE);
                    dialogAddURL.dismiss();
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(v -> dialogAddURL.dismiss());
        }
        dialogAddURL.show();
    }

    /**
     * Removes the current URL from the TextView and hides the containing layout.
     */
    public void removeURL() {
        textWebURL.setText(null);
        layoutWebURL.setVisibility(View.GONE);
    }

    /**
     * Retrieves the current URL displayed in the TextView.
     *
     * @return The URL as a String. Returns an empty string if no URL is currently set.
     */
    public String getWebURL() {
        return textWebURL.getText().toString();
    }

    /**
     * Sets the specified URL in the TextView and makes the containing layout visible.
     *
     * @param webURL The URL to display.
     */
    public void setWebURL(String webURL) {
        textWebURL.setText(webURL);
        layoutWebURL.setVisibility(View.VISIBLE);
    }
}