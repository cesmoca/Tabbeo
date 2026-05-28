package com.tabbeo.unittests.Mocks;

import android.content.Context;

import com.tabbeo.Storage.PointsCollection;
import com.tabbeo.Storage.StorageService;

import java.util.HashMap;

public class MockStorageImpl implements StorageService.IStorageImpl {
    private HashMap<String, PointsCollection> _hashMap = new HashMap<>();

    @Override
    public PointsCollection getExercisePoints(Context context, String exerciseId) {
        PointsCollection collection = _hashMap.get(exerciseId);
        if (collection == null) {
            collection = new PointsCollection();
            storeCollection(context, exerciseId, collection);
        }
        return collection;
    }

    @Override
    public void storeCollection(Context context, String exerciseId, PointsCollection points) {
        _hashMap.put(exerciseId, points);
    }

    @Override
    public void ClearScores(Context context) {
        _hashMap.clear();
    }
}
