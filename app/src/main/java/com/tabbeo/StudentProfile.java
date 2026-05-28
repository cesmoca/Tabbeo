package com.tabbeo;

import android.content.Context;
import android.content.SharedPreferences;

public class StudentProfile {
    private static final String PREFS_NAME = "StudentProfile";
    private static final String HAND_PREFERENCE_LABEL = "HandPreference";
    private static final String NOTES_NOTATION_LABEL = "NotesNotation";
    private static final String DEBUG_MODE_LABEL = "DebugMode";

    public enum HandPreference { RIGHT_HANDED, LEFT_HANDED }

    public enum NotesNotation {DOREMI, ABC}

    public static HandPreference getHandPreference(){
        SharedPreferences sharedPreferences = TabbeoApp.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return HandPreference.values()[sharedPreferences.getInt(HAND_PREFERENCE_LABEL, HandPreference.RIGHT_HANDED.ordinal())];
    }

    public static void setHandPreference(HandPreference handPreference){
        SharedPreferences sharedPreferences = TabbeoApp.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(HAND_PREFERENCE_LABEL, handPreference.ordinal());

        editor.apply();
    }

    public static NotesNotation getNotesNotation(){
        SharedPreferences sharedPreferences = TabbeoApp.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return NotesNotation.values()[sharedPreferences.getInt(NOTES_NOTATION_LABEL, NotesNotation.DOREMI.ordinal())];
    }

    public static void setNotesNotation(NotesNotation notesNotation){
        SharedPreferences sharedPreferences = TabbeoApp.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(NOTES_NOTATION_LABEL, notesNotation.ordinal());

        editor.apply();
    }

    public static boolean getDebugModeEnabled(){
        SharedPreferences sharedPreferences = TabbeoApp.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(DEBUG_MODE_LABEL, false);
    }

    public static void setDebugModeEnabled(boolean debugModeEnabled){
        SharedPreferences sharedPreferences = TabbeoApp.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(DEBUG_MODE_LABEL, debugModeEnabled);

        editor.apply();
    }
}
