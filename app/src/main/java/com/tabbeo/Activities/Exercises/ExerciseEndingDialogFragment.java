package com.tabbeo.Activities.Exercises;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tabbeo.R;
import com.tabbeo.Widgets.PicksScore;

public class ExerciseEndingDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        float picksScore = getArguments().getFloat("PicksScore");
        int experiencePoints = getArguments().getInt("ExperiencePoints");
        int exerciseMaxPoints = getArguments().getInt("ExerciseMaxPoints");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.TabbeoDialogTheme);
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.exercise_ending, null);

        builder.setView(layout)
                .setTitle(R.string.Exercise_Ending_LevelComplete)
                .setPositiveButton(R.string.Exercise_Ending_Continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.Exercise_Ending_Retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((IActivityExerciseFrame)getActivity()).restartLevel();
                        dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button continueButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Drawable continueIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_continue, null);
                continueIcon.setBounds(0, 0, (int)(continueButton.getHeight()*0.7), (int)(continueButton.getHeight()*0.7));
                continueButton.setCompoundDrawables(continueIcon, null, null, null);

                Button retryButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                Drawable retryIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_retry, null);
                retryIcon.setBounds(0, 0, (int)(retryButton.getHeight()*0.5), (int)(retryButton.getHeight()*0.5));

                retryButton.setCompoundDrawables(retryIcon, null, null, null);
            }
        });

        // Setting the values
        PicksScore picScoreWidget = (PicksScore) layout.findViewById(R.id.exercise_ending_picks_score);
        picScoreWidget.setPicksScore(picksScore);

        TextView pointsTextView = (TextView) layout.findViewById(R.id.exercise_ending_points_textview);
        pointsTextView.setText(experiencePoints + " " + getString(R.string.Exercise_Ending_Points) + " / " + ((experiencePoints * 100) / exerciseMaxPoints) + "%");

        return dialog;
    }
}
