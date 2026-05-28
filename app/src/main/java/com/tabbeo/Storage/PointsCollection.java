package com.tabbeo.Storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class PointsCollection extends ArrayList<PointsCollection.DailyPointsEntry> implements Serializable {
    private final static String LOG_TAG = "ExercisePointsCollection";

    public class DailyPointsEntry implements Serializable {
        public DailyPointsEntry() {
            this(Calendar.getInstance()); // Today
        }

        public DailyPointsEntry(Calendar d) {
            date = d;
        }

        public Calendar date;
        public int accumulatedExperiencePoints = 0;
        public float maxPicksScore = 0;

        @Override
        public String toString(){
            return date+"-"+ accumulatedExperiencePoints +" exp-"+ maxPicksScore;
        }
    }

    public DailyPointsEntry getOrCreatePointsEntry(Calendar date) {
        DailyPointsEntry foundEntry = null;

        for (DailyPointsEntry entry : this) {
            if (entry.date.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH) &&
                    entry.date.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
                    entry.date.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
                foundEntry = entry;
                break;
            }
        }

        if (foundEntry == null) {
            foundEntry = new DailyPointsEntry(date);
            add(foundEntry);
        }
        return foundEntry;
    }

    public DailyPointsEntry getMaxPointsEntry(){
        DailyPointsEntry maxDailyPointsEntry = null;
        for(DailyPointsEntry entry : this){
            if(maxDailyPointsEntry == null || maxDailyPointsEntry.accumulatedExperiencePoints < entry.accumulatedExperiencePoints) {
                maxDailyPointsEntry = entry;
            }
        }

        return maxDailyPointsEntry;
    }

    public DailyPointsEntry getOrCreateTodayPointsEntry() {
        Calendar today = Calendar.getInstance();

        return getOrCreatePointsEntry(today);
    }
}

