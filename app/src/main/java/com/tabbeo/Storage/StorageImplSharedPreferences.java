package com.tabbeo.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.tabbeo.Utils.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static com.tabbeo.Storage.StorageService.IStorageImpl;

public class StorageImplSharedPreferences implements IStorageImpl {
    private static final String LOG_TAG = "StorageImplSharedPrefs";
    private static final String PREFS_NAME = "ExercisePoints";

    @Override
    public PointsCollection getExercisePoints(Context context, String exerciseId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String strPoints = sharedPreferences.getString("" + exerciseId, "");

        return fromString(strPoints);
    }

    @Override
    public void storeCollection(Context context, String exerciseId, PointsCollection points) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        try {
            editor.putString("" + exerciseId, toString(points));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    @Override
    public void ClearScores(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Read the object from Base64 string.
     */
    private static PointsCollection fromString(String s) {
        PointsCollection points = new PointsCollection();
        if (s.isEmpty())
            return points;

        byte[] data = Base64Coder.decode(s);
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(data));
            points = (PointsCollection) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return points;
    }

    /**
     * Write the object to a Base64 string.
     */
    private static String toString(Serializable o) throws IOException {
        if (o == null)
            return "";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return new String(Base64Coder.encode(baos.toByteArray()));
    }
}
