package com.tabbeo.unittests;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.unittests.Mocks.MockStorageImpl;
import com.tabbeo.Storage.PointsCollection;
import com.tabbeo.Storage.StorageService;

import java.util.Calendar;

class ExerciseTest extends Exercise {
    public int maxPoints = 10;

    public ExerciseTest() {
        super("Ej", Type.Slideshow, null, null, 0, 5, null, null);
    }

    @Override
    public int getMaxPoints(){ return maxPoints; }
}
public class StorageTests extends InstrumentationTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Avoid to write to the actual storage
        StorageServiceTest.setStorageImplementation(new MockStorageImpl());
    }

    @SmallTest
    public void testExercisePointsCollection_GetOrCreatePointsEntry() {
        Calendar calendar = Calendar.getInstance();
        PointsCollection collection = new PointsCollection();

        PointsCollection.DailyPointsEntry entry = collection.getOrCreatePointsEntry(calendar);
        assertEquals(entry, collection.getOrCreatePointsEntry(calendar));
        assertEquals(collection.size(), 1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, 10);

        PointsCollection.DailyPointsEntry entry2 = collection.getOrCreatePointsEntry(calendar2);
        assertEquals(entry2, collection.getOrCreatePointsEntry(calendar2));
        assertEquals(collection.size(), 2);
    }

    @SmallTest
    public void testExercisePointsCollection_GetOrCreateTodayPointsEntry() {
        PointsCollection collection = new PointsCollection();

        PointsCollection.DailyPointsEntry entry = collection.getOrCreateTodayPointsEntry();
        assertEquals(entry, collection.getOrCreateTodayPointsEntry());
        assertEquals(collection.size(), 1);
    }

    @SmallTest
    public void testStorage() {
        PointsCollection collection;

        ExerciseTest e = new ExerciseTest();
        e.maxPoints = 2;

        // For exercise_frame 0
        collection = StorageService.getExercisePoints(null, "0");
        assertEquals(collection.size(), 0);


        StorageService.addPracticePoints(null, "0", 1, e);
        collection = StorageService.getExercisePoints(null, "0");
        assertEquals(1, collection.size()); // Only for one day
        assertEquals(1, collection.get(0).accumulatedExperiencePoints);
        assertEquals(2.5f, collection.get(0).maxPicksScore);

        StorageService.addPracticePoints(null, "0", 2, e);
        collection = StorageService.getExercisePoints(null, "0");
        assertEquals(1, collection.size());
        assertEquals(2, collection.get(0).accumulatedExperiencePoints, 2);
        assertEquals(5.0f, collection.get(0).maxPicksScore);


        // For exercise_frame 1
        StorageService.addPracticePoints(null, "1", 1, e);
        collection = StorageService.getExercisePoints(null, "1");
        assertEquals(1, collection.size()); // Only for one day
        assertEquals(1, collection.get(0).accumulatedExperiencePoints);
        assertEquals(2.5f, collection.get(0).maxPicksScore);

        StorageService.addPracticePoints(null, "1", 2, e);
        collection = StorageService.getExercisePoints(null, "1");
        assertEquals(1, collection.size());
        assertEquals(2, collection.get(0).accumulatedExperiencePoints, 2);
        assertEquals(5.0f, collection.get(0).maxPicksScore);

        StorageService.ClearScores(null);
        collection = StorageService.getExercisePoints(null, "0");
        assertEquals(0, collection.size());
        collection = StorageService.getExercisePoints(null, "1");
        assertEquals(0, collection.size());

    }
}
