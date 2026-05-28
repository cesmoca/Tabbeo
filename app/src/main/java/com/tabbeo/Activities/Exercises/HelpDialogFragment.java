package com.tabbeo.Activities.Exercises;

import com.instabug.library.IBGInvocationMode;
import com.instabug.library.Instabug;
import com.tabbeo.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HelpDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.TabbeoDialogTheme);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dialog_list_item, getResources().getStringArray(R.array.helpdialog_items));

        ListView listView = new ListView(getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // I do not understand the exercise -> feedback
                        dismiss();
                        Instabug.invoke(IBGInvocationMode.IBGInvocationModeFeedbackSender);
                        break;
                    case 1: // Something is wrong -> bug
                        dismiss();
                        Instabug.invoke(IBGInvocationMode.IBGInvocationModeBugReporter);
                        break;
                    case 2: // I have a suggestion for improvement -> feedback
                        dismiss();
                        Instabug.invoke(IBGInvocationMode.IBGInvocationModeFeedbackSender);
                        break;
                    case 3: // What I play is not being detected
                        dismiss();
                        final Toast getTunerAppToast = Toast.makeText(getActivity().getApplicationContext(), R.string.HelpDialog_DownloadTunningApp, Toast.LENGTH_LONG);

                        AlertDialog.Builder isTunedBuilder = new AlertDialog.Builder(getActivity(), R.style.TabbeoDialogTheme);
                        isTunedBuilder.setTitle(R.string.HelpDialog_IsGuitarTuned)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Instabug.invoke(IBGInvocationMode.IBGInvocationModeBugReporter);
                            }
                        }).setNeutralButton(R.string.idk, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getTunerAppToast.show();
                            }
                        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getTunerAppToast.show();
                            }
                        }).create().show();
                        break;
                    default:
                        throw new RuntimeException("Option " + position + " in help dialog not implemented");
                }
            }
        });

        builder.setView(listView)
                .setTitle(R.string.HelpDialog_Title)
                .setNegativeButton(R.string.HelpDialog_Close, null);

        return builder.create();
    }
}