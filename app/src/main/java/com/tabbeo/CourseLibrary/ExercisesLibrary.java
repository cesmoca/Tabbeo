package com.tabbeo.CourseLibrary;

import com.tabbeo.Trainer.ITrainer;
import com.tabbeo.Trainer.Trainer;
import com.tabbeo.Widgets.FretboardWidget;

import java.util.ArrayList;
import java.util.List;

public class ExercisesLibrary {
    private static List<Exercise> loadedExercises = new ArrayList<>();

    private ExercisesLibrary() {
    } // This class is not instantiable

    public static void loadExercises(Exercise[] exercises) {
        for (Exercise e : exercises) {
            loadedExercises.add(e);
        }
    }

    public static void loadExercise(Exercise exercise) {
        loadedExercises.add(exercise);
    }

    public static Exercise[] courseExercises = {
//            new Exercise("TutorialBasicConcepts", Exercise.Type.Slideshow, null, null /*track*/, 0, 0, R.string.TutorialBasicConcepts_Description) {
//                @Override
//                public ITutorialSequencer createTutorial() {
//                    final ITutorialSequencer.TutorialSequencerExerciseFrame ts = new ITutorialSequencer.TutorialSequencerExerciseFrame();
//
//                    ts.addStep(R.string.TutorialBasicConcepts_Explanation_0, ITutorialSequencer.Event.Touch, new ITutorialSequencer.TutorialStepListener() {
//                        @Override
//                        public void onStart(IActivityExerciseFrame activity) {
//                            ts.getImageSwitcher(activity).setImageResource(R.drawable.basic_concepts_tuto_1);
//                        }
//                    }).addStep(R.string.TutorialBasicConcepts_Explanation_1, ITutorialSequencer.Event.Touch, new ITutorialSequencer.TutorialStepListener() {
//                        @Override
//                        public void onStart(IActivityExerciseFrame activity) {
//                            ts.getImageSwitcher(activity).setImageResource(R.drawable.basic_concepts_tuto_2);
//                        }
//                    }).addStep(R.string.TutorialBasicConcepts_Explanation_2, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.TutorialBasicConcepts_Explanation_3, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.TutorialBasicConcepts_Explanation_4, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.TutorialBasicConcepts_Explanation_5, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.TutorialBasicConcepts_Explanation_6, ITutorialSequencer.Event.Touch, new ITutorialSequencer.TutorialStepListener() {
//                                @Override
//                                public void onStart(IActivityExerciseFrame activity) {
//                                    ts.getImageSwitcher(activity).setImageResource(R.drawable.basic_concepts_tuto_3);
//                                }
//                            })
//                            .addStep(R.string.TutorialBasicConcepts_Explanation_7, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.TutorialBasicConcepts_Explanation_8, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.TutorialBasicConcepts_Explanation_9, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.TutorialBasicConcepts_Explanation_10, ITutorialSequencer.Event.Touch, new ITutorialSequencer.TutorialStepListener() {
//                                @Override
//                                public void onStart(IActivityExerciseFrame activity) {
//                                    ts.getImageSwitcher(activity).setImageResource(R.drawable.basic_concepts_tuto_1);
//                                }
//                            }).addStep(null, null, new ITutorialSequencer.TutorialStepListener() {
//                        @Override
//                        public void onStart(IActivityExerciseFrame activity) {
//                            activity.endOfTrainingShowSummary();
//                        }
//                    });
//
//                    return ts;
//                }
//            },
//
//            new Exercise("FirstAMajor", Exercise.Type.ChordsRhythm, Trainer.Type.Interactive, SongsLibrary.firstAMajor, 0, 3, R.string.ExerciseFirstAMajor_Description) {
//                @Override
//                public ITutorialSequencer createTutorial() {
//                    final ITutorialSequencer ts = new ITutorialSequencer.TutorialSequencerExerciseOnOffButtonChords();
//
//                    ts.addStep(R.string.ExerciseFirstAMajor_Explanation_0, ITutorialSequencer.Event.Touch, new ITutorialSequencer.TutorialStepListener() {
//                        @Override
//                        public void onStart(IActivityExerciseFrame activity) {
//                            ts.dimAllViewsExcept(activity, R.id.exercise_onoffbutton_RightSide_AnimatedChordy);
//                        }
//                    }).addStep(R.string.ExerciseFirstAMajor_Explanation_1, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.ExerciseFirstAMajor_Explanation_2, ITutorialSequencer.Event.OnOffButton, new ITutorialSequencer.TutorialStepListener() {
//                                @Override
//                                public void onStart(IActivityExerciseFrame activity) {
//                                    ts.lightUpView(activity, R.id.exercise_onoffbutton_OnOffButton);
//                                }
//                            }).addStep(R.string.ExerciseFirstAMajor_Explanation_3, ITutorialSequencer.Event.Touch, new ITutorialSequencer.TutorialStepListener() {
//                        @Override
//                        public void onStart(IActivityExerciseFrame activity) {
//                            ts.dimView(activity, R.id.exercise_onoffbutton_OnOffButton);
//                        }
//                    }).addStep(R.string.ExerciseFirstAMajor_Explanation_4, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.ExerciseFirstAMajor_Explanation_5, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.ExerciseFirstAMajor_Explanation_6, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.ExerciseFirstAMajor_Explanation_7, ITutorialSequencer.Event.PlayablePlayedCorrectly, new ITutorialSequencer.TutorialStepListener() {
//                                @Override
//                                public void onStart(IActivityExerciseFrame activity) {
//                                    ts.lightUpAllViews(activity);
//                                }
//                            })
//                            .addStep(R.string.ExerciseFirstAMajor_Explanation_8, ITutorialSequencer.Event.PlayablePlayedCorrectly, null)
//                            .addStep(null, null, null);
//
//                    return ts;
//                }
//            },
//
//            new Exercise("FirstDMajor", Exercise.Type.ChordsRhythm, Trainer.Type.Interactive, SongsLibrary.firstDMajor, 0, 3, R.string.ExerciseFirstDMajor_Description),
//
//            new Exercise("FirstEMajor", Exercise.Type.ChordsRhythm, Trainer.Type.Interactive, SongsLibrary.firstEMajor, 0, 3, R.string.ExerciseFirstEMajor_Description),
//
//            new Exercise("UpAndDownAMajor", Exercise.Type.ChordsRhythm, Trainer.Type.Timed, SongsLibrary.upAndDownAMajor, 0, 3, R.string.ExerciseUpAndDownAMajor_Description),
//
//            new Exercise("FirstTimeSwitchingChords", Exercise.Type.ChordsRhythm, Trainer.Type.Interactive, SongsLibrary.firstSwitchingChords, 0, 3, R.string.ExerciseFirstTimeSwitchingChords_Description),
//
//            new Exercise("TutorialMelody", Exercise.Type.Melody, Trainer.Type.Interactive, SongsLibrary.tutorialMelody, 0, 3, R.string.TutorialMelody_Description) {
//                @Override
//                public ITutorialSequencer createTutorial() {
//                    final ITutorialSequencer ts = new ITutorialSequencer.TutorialSequencerExerciseOnOffButtonMelody();
//
//                    ts.addStep(R.string.TutorialMelody_Explanation_0, ITutorialSequencer.Event.Touch, new ITutorialSequencer.TutorialStepListener() {
//                        @Override
//                        public void onStart(IActivityExerciseFrame activity) {
//                            ts.dimAllViewsExcept(activity, R.id.exercise_onoffbutton_RightSide_AnimatedChordy);
//                        }
//                    }).addStep(R.string.TutorialMelody_Explanation_1, ITutorialSequencer.Event.Touch, new ITutorialSequencer.TutorialStepListener() {
//                        @Override
//                        public void onStart(IActivityExerciseFrame activity) {
//                            ts.lightUpView(activity, R.id.exercise_melody_fretboard);
//                        }
//                    }).addStep(R.string.TutorialMelody_Explanation_2, ITutorialSequencer.Event.OnOffButton, new ITutorialSequencer.TutorialStepListener() {
//                        @Override
//                        public void onStart(IActivityExerciseFrame activity) {
//                            ts.dimView(activity, R.id.exercise_melody_fretboard);
//                            ts.lightUpView(activity, R.id.exercise_onoffbutton_OnOffButton);
//                        }
//                    }).addStep(R.string.TutorialMelody_Explanation_3, ITutorialSequencer.Event.Touch, new ITutorialSequencer.TutorialStepListener() {
//                        @Override
//                        public void onStart(IActivityExerciseFrame activity) {
//                            ts.dimView(activity, R.id.exercise_onoffbutton_OnOffButton);
//                        }
//                    }).addStep(R.string.TutorialMelody_Explanation_4, ITutorialSequencer.Event.Touch, null)
//                            .addStep(R.string.TutorialMelody_Explanation_5, ITutorialSequencer.Event.PlayablePlayedCorrectly, new ITutorialSequencer.TutorialStepListener() {
//                                @Override
//                                public void onStart(IActivityExerciseFrame activity) {
//                                    ts.lightUpAllViews(activity);
//                                }
//                            })
//                            .addStep(R.string.TutorialMelody_Explanation_6, ITutorialSequencer.Event.PlayablePlayedCorrectly, null)
//                            .addStep(R.string.TutorialMelody_Explanation_7, ITutorialSequencer.Event.PlayablePlayedCorrectly, null)
//                            .addStep(R.string.TutorialMelody_Explanation_8, ITutorialSequencer.Event.PlayablePlayedCorrectly, null)
//                            .addStep(null, null, new ITutorialSequencer.TutorialStepListener() {
//                                @Override
//                                public void onStart(IActivityExerciseFrame activity) {
//                                    activity.endOfTrainingShowSummary();
//                                }
//                            });
//
//                    return ts;
//                }
//            },
//
//            new Exercise("OdeToJoyInteractive", Exercise.Type.Melody, Trainer.Type.Interactive, SongsLibrary.odeToJoy, 0, 4, R.string.ExerciseOdeToJoy_Description),
//
//            new Exercise("OdeToJoyTimed", Exercise.Type.Melody, Trainer.Type.Timed, SongsLibrary.odeToJoy, 0, 4, R.string.ExerciseOdeToJoy_Description),

            new Exercise("NormalRepresentation", Exercise.Type.Melody, Trainer.Type.Timed, SongsLibrary.aMinorPentatonicPos1Ex1, 5, 8, null, FretboardWidget.NOTE_REPRESENTATION.MELODY),
            new Exercise("ChangeColor", Exercise.Type.Melody, Trainer.Type.Timed, SongsLibrary.aMinorPentatonicPos1Ex1, 5, 8, null, FretboardWidget.NOTE_REPRESENTATION.SCALE_CHANGE_LIGHTING),
            new Exercise("AnticipationCircle", Exercise.Type.Melody, Trainer.Type.Timed, SongsLibrary.aMinorPentatonicPos1Ex1, 5, 8, null, FretboardWidget.NOTE_REPRESENTATION.SCALE_ANTICIPATION_CIRCLE),
            new Exercise("RotatingBall", Exercise.Type.Melody, Trainer.Type.Timed, SongsLibrary.aMinorPentatonicPos1Ex1, 5, 8, null, FretboardWidget.NOTE_REPRESENTATION.SCALE_ROTATING_BALL),
            new Exercise("MelodyDynamic", Exercise.Type.MelodyDynamic, Trainer.Type.Timed, SongsLibrary.aMinorPentatonicPos1Ex1, 5, 8, null, FretboardWidget.NOTE_REPRESENTATION.SCALE_ROTATING_BALL),
    };

    public static Exercise[] testExercises = {

//            new Exercise("MelodyTest", Exercise.Type.Melody, Trainer.Type.Timed, SongsLibrary.tutorialMelody, 0, 4, null /*descriptionId*/),
//            new Exercise("LozanaTest", Exercise.Type.ChordsRhythm, Trainer.Type.Timed, SongsLibrary.lozana, 0, 4, null /*descriptionId*/),
//            new Exercise("Lozana", Exercise.Type.ChordsRhythm, Trainer.Type.Timed, SongsLibrary.lozana, 0, 4, null /*descriptionId*/),
//            new Exercise("LozanaInteractive", Exercise.Type.ChordsRhythm, Trainer.Type.Interactive, SongsLibrary.lozana, 0, 4, null /*descriptionId*/),

    };

    public static Exercise getExerciseByName(String name) {
        for (Exercise exercise : loadedExercises) {
            if (exercise.getName().equals(name))
                return exercise;
        }

        throw new RuntimeException("Could not find exercise: " + name);
    }

}
