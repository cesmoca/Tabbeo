package com.tabbeo.Activities.TutorialSequencer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tabbeo.Activities.Exercises.ActivityExerciseOnOffButton;
import com.tabbeo.Activities.Exercises.IActivityExerciseFrame;
import com.tabbeo.Trainer.ITrainer;
import com.tabbeo.R;
import com.tabbeo.Widgets.OnOffButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ITutorialSequencer {
    private final float ALPHA_DIM = 0.25f;
    private final int DIM_DURATION = 350; /*ms*/

    private int _explanationIndex;
    private List<TutorialStep> _steps;
    private AnimatorSet _animatorSet;
    protected List<Integer> _dimmableViewsIds;

    public enum Event {
        Touch, PlayablePlayedCorrectly, OnOffButton
    }

    public interface TutorialStepListener {
        void onStart(IActivityExerciseFrame activity);
    }

    private static class TutorialStep {
        private Integer _explanationStringId;
        private Event _eventToNext;
        private TutorialStepListener _listener;

        public TutorialStep(Integer explanationStringId, Event event, TutorialStepListener listener) {
            _explanationStringId = explanationStringId;
            _eventToNext = event;
            _listener = listener;
        }

        public Event getEventType() {
            return _eventToNext;
        }

        public Integer getExplanationStringId() {
            return _explanationStringId;
        }

        public void onStart(IActivityExerciseFrame activity) {
            if (_listener != null)
                _listener.onStart(activity);
        }
    }


    public ITutorialSequencer() {
        _animatorSet = new AnimatorSet();
        _steps = new ArrayList<>();
        _dimmableViewsIds = new ArrayList<>();

        setupDimmableViewsIds();
    }

    protected abstract void setupDimmableViewsIds();

    protected abstract void performPreStepActions(IActivityExerciseFrame activity, Event event);

    protected Event getCurrentEvent() {
        return _steps.get(_explanationIndex).getEventType();
    }

    private void setCurrentContent(IActivityExerciseFrame activity) {
        // Set the text
        if (_steps.get(_explanationIndex).getExplanationStringId() != null)
            getExplanationTextView(activity).setText(_steps.get(_explanationIndex).getExplanationStringId());

        Event event = getCurrentEvent();
        if (event != null) {
            // Set the icon
            int resId;
            switch (event) {
                case Touch:
                    resId = R.drawable.icon_touch;
                    break;
                case PlayablePlayedCorrectly:
                    resId = R.drawable.icon_note;
                    break;
                case OnOffButton:
                    resId = R.drawable.icon_touch_onoffbutton;
                    break;
                default:
                    throw new RuntimeException("We do no know what icon to assign for this kind of event");
            }
            getExplanationIcon(activity).setImageResource(resId);

            performPreStepActions(activity, event);

        } else { // We are done, let's hide the main container
            getExplanationContainer(activity).setVisibility(View.GONE);
        }

        // Perform the actions
        _steps.get(_explanationIndex).onStart(activity);
    }

    public ITutorialSequencer addStep(Integer explanationStringId, Event eventToNextStep, TutorialStepListener listener) {
        TutorialStep tutorialStep = new TutorialStep(explanationStringId, eventToNextStep, listener);
        _steps.add(tutorialStep);
        return this;
    }

    public int getCurrentExplanationIndex() {
        return _explanationIndex;
    }

    public int getNumberExplanations() {
        return _steps.size();
    }

    public boolean injectEvent(IActivityExerciseFrame activity, Event event) {
        // We are already in the last step
        if (_explanationIndex == _steps.size() - 1) return false;

        if (_steps.get(_explanationIndex).getEventType() == event) {
            _explanationIndex++;
            setCurrentContent(activity);
        }

        return true;
    }

    public void start(IActivityExerciseFrame activity) {
        _explanationIndex = 0;
        getExplanationContainer(activity).setVisibility(View.VISIBLE);
        setCurrentContent(activity);
    }

    // Helpers to control the illumination
    public void lightUpView(IActivityExerciseFrame activity, Integer viewId) {
        setAlphaObjectAnimator(activity, viewId, new float[]{ALPHA_DIM, 1.0f});
    }

    protected void lightUpAllViewsExcept(IActivityExerciseFrame activity, Integer viewIdToIgnore) {
        setAlphaObjectAnimators(activity, _dimmableViewsIds, new float[]{ALPHA_DIM, 1.0f}, viewIdToIgnore);
    }

    public void lightUpAllViews(IActivityExerciseFrame activity) {
        lightUpAllViewsExcept(activity, null);
    }

    public void dimView(IActivityExerciseFrame activity, Integer viewId) {
        setAlphaObjectAnimator(activity, viewId, new float[]{1.0f, ALPHA_DIM});
    }

    public void dimAllViewsExcept(IActivityExerciseFrame activity, Integer viewIdToIgnore) {
        setAlphaObjectAnimators(activity, _dimmableViewsIds, new float[]{1.0f, ALPHA_DIM}, viewIdToIgnore);
    }

    protected void dimAllViews(IActivityExerciseFrame activity) {
        dimAllViewsExcept(activity, null);
    }

    private void setAlphaObjectAnimator(IActivityExerciseFrame activity, Integer viewId, float[] animationValues) {
        View view = activity.findViewById(viewId);
        ObjectAnimator animator = new ObjectAnimator();
        animator.setTarget(view);
        animator.setDuration(DIM_DURATION);
        animator.setPropertyName("alpha");
        animator.setFloatValues(animationValues);

        animator.start();
    }

    private void setAlphaObjectAnimators(IActivityExerciseFrame activity, List<Integer> viewsIds, float[] animationValues, Integer viewIdToIgnore) {
        Collection<Animator> animators = new ArrayList<>();
        for (Integer viewId : viewsIds) {
            if (viewId.equals(viewIdToIgnore)) continue;

            View view = activity.findViewById(viewId);
            ObjectAnimator animator = new ObjectAnimator();
            animator.setTarget(view);
            animator.setDuration(DIM_DURATION);
            animator.setPropertyName("alpha");
            animator.setFloatValues(animationValues);

            animators.add(animator);
        }

        _animatorSet.playTogether(animators);
        _animatorSet.start();
    }

    // Methods to access the actual views
    protected RelativeLayout getExplanationContainer(IActivityExerciseFrame activity) {
        return (RelativeLayout) activity.findViewById(R.id.exercise_frame_bottom_explanation_layout);
    }

    protected TextView getExplanationTextView(IActivityExerciseFrame activity) {
        return (TextView) activity.findViewById(R.id.exercise_frame_explanation_text_view);
    }

    protected ImageView getExplanationIcon(IActivityExerciseFrame activity) {
        return (ImageView) activity.findViewById(R.id.exercise_frame_explanation_icon);
    }


    // Specific classes for each type of tutorial
    public static class TutorialSequencerExerciseFrame extends ITutorialSequencer {

        @Override
        protected void setupDimmableViewsIds() {
            // This will be empty for this guy
        }

        @Override
        protected void performPreStepActions(IActivityExerciseFrame activity, Event event) {
            // Nothing to do, really
        }

        // Methods to accces the actual views
        public ImageSwitcher getImageSwitcher(IActivityExerciseFrame activity) {
            return (ImageSwitcher) activity.findViewById(R.id.exercise_slideshow_imageswitcher);
        }
    }

    public static abstract class TutorialSequencerExerciseOnOffButton extends ITutorialSequencer {

        public TutorialSequencerExerciseOnOffButton() {
            super();
        }

        @Override
        protected void setupDimmableViewsIds() {
            _dimmableViewsIds.add(R.id.exercise_onoffbutton_OnOffButton);
            _dimmableViewsIds.add(R.id.exercise_onoffbutton_RightSide_AnimatedChordy);
            _dimmableViewsIds.add(R.id.exercise_frame_main_layout_background);
            _dimmableViewsIds.add(R.id.exercise_frame_toolbar);
        }

        @Override
        protected void performPreStepActions(IActivityExerciseFrame activity, Event event) {
            ITrainer trainer = ((ActivityExerciseOnOffButton) activity).getTrainer();

            // Enable or disable the detection depending on what we are waiting next
            if (trainer != null) {
                if (event == Event.PlayablePlayedCorrectly) {
                    trainer.resumeDetecting();
                } else {
                    trainer.pauseDetecting();
                }
            }

            // Enable or disable the onOffButton, so that it can't be clicked when it's not time
            if (getOnOffButtonView(activity) != null) {
                if (event == Event.OnOffButton) {
                    getOnOffButtonView(activity).setClickable(true); // Now you can click it
                } else {
                    getOnOffButtonView(activity).setClickable(false); // Can't be turned off
                }
            }
        }

        // Methods to accces the actual views
        protected OnOffButton getOnOffButtonView(IActivityExerciseFrame activity) {
            return (OnOffButton) activity.findViewById(R.id.exercise_onoffbutton_OnOffButton);
        }
    }

    public static class TutorialSequencerExerciseOnOffButtonChords extends TutorialSequencerExerciseOnOffButton {

        @Override
        protected void setupDimmableViewsIds() {
            super.setupDimmableViewsIds();
            _dimmableViewsIds.add(R.id.exercise_chords_rhythm_chord_names);
            _dimmableViewsIds.add(R.id.exercise_chords_rhythm_chord_strokes_stream);
            _dimmableViewsIds.add(R.id.exercise_chords_rhythm_fretboard);
        }
    }

    public static class TutorialSequencerExerciseOnOffButtonMelody extends TutorialSequencerExerciseOnOffButton {

        @Override
        protected void setupDimmableViewsIds() {
            super.setupDimmableViewsIds();
            _dimmableViewsIds.add(R.id.exercise_melody_fretboard);
        }
    }

}
