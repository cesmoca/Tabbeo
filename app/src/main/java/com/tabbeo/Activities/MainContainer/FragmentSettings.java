package com.tabbeo.Activities.MainContainer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;

import com.tabbeo.BuildConfig;
import com.tabbeo.R;
import com.tabbeo.Storage.StorageService;
import com.tabbeo.StudentProfile;

public class FragmentSettings extends Fragment {

    // Test widgets
    Button _clearScoresButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        // Notes notation switch
        final RadioButton notesNotationDOREMIRadioButton = (RadioButton) getView().findViewById(R.id.fragment_settings_notesnotation_doremi_radiobutton);
        final RadioButton notesNotationABCRadioButton = (RadioButton) getView().findViewById(R.id.fragment_settings_notesnotation_abc_radiobutton);

        StudentProfile.NotesNotation notesNotation = StudentProfile.getNotesNotation();
        if(notesNotation == StudentProfile.NotesNotation.DOREMI){
            notesNotationDOREMIRadioButton.setChecked(true);
            notesNotationABCRadioButton.setChecked(false);
        }else if(notesNotation == StudentProfile.NotesNotation.ABC){
            notesNotationDOREMIRadioButton.setChecked(false);
            notesNotationABCRadioButton.setChecked(true);
        }else{
            throw new RuntimeException("Unknown notes notation");
        }

        notesNotationDOREMIRadioButton.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                StudentProfile.setNotesNotation(StudentProfile.NotesNotation.DOREMI);
                notesNotationABCRadioButton.setChecked(false);
            }
        });

        notesNotationABCRadioButton.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                StudentProfile.setNotesNotation(StudentProfile.NotesNotation.ABC);
                notesNotationDOREMIRadioButton.setChecked(false);
            }
        });

        // Left handed?
        final Switch leftHandedSwitch = (Switch) getView().findViewById(R.id.fragment_settings_hand_preference_switch);

        StudentProfile.HandPreference handPreference = StudentProfile.getHandPreference();
        leftHandedSwitch.setChecked(handPreference == StudentProfile.HandPreference.LEFT_HANDED);
        leftHandedSwitch.setOnClickListener(new Switch.OnClickListener(){
            @Override
            public void onClick(View v) {
                StudentProfile.HandPreference handPreference = leftHandedSwitch.isChecked()? StudentProfile.HandPreference.LEFT_HANDED : StudentProfile.HandPreference.RIGHT_HANDED;
                StudentProfile.setHandPreference(handPreference);
            }
        });

        // Debug mode?
        final Switch debugModeSwitch = (Switch) getView().findViewById(R.id.fragment_settings_debug_mode_switch);

        boolean debugModeEnabled = StudentProfile.getDebugModeEnabled();
        debugModeSwitch.setChecked(debugModeEnabled);

        debugModeSwitch.setOnClickListener(new Switch.OnClickListener(){
            @Override
            public void onClick(View v) {
                StudentProfile.setDebugModeEnabled(debugModeSwitch.isChecked());
                visibilityTestOptions(debugModeSwitch.isChecked());
            }
        });

        // If SHIP - There is no option for debug mode
        if(!BuildConfig.DEBUG){
            StudentProfile.setDebugModeEnabled(false);
            debugModeSwitch.setVisibility(View.GONE);
        }

        // Clear scores
        _clearScoresButton = (Button) getView().findViewById(R.id.fragment_settings_clearscores_button);
        _clearScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageService.ClearScores(getActivity().getApplicationContext());
            }
        });

        // Let's show/hide depending on debug mode
        visibilityTestOptions(debugModeEnabled);
    }

    private void visibilityTestOptions(boolean visible){
        int visibility = visible?View.VISIBLE : View.INVISIBLE;
        _clearScoresButton.setVisibility(visibility);
    }

}
