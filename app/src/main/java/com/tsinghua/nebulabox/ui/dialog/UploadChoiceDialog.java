package com.tsinghua.nebulabox.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.SeadroidApplication;
import com.tsinghua.nebulabox.fileschooser.MultiFileChooserActivity;
import com.tsinghua.nebulabox.gallery.MultipleImageSelectionActivity;
import com.tsinghua.nebulabox.ui.activity.MainActivity;

public class UploadChoiceDialog extends DialogFragment {
    private Context ctx = SeadroidApplication.getAppContext();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).
                setTitle(getResources().getString(R.string.pick_upload_type)).
                setItems(R.array.pick_upload_array,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                        case 0:
                            Intent intent = new Intent(ctx, MultiFileChooserActivity.class);
                            getActivity().startActivityForResult(intent, MainActivity.PICK_FILES_REQUEST);
                            break;
                        case 1:
                            // photos
                            intent = new Intent(ctx, MultipleImageSelectionActivity.class);
                            getActivity().startActivityForResult(intent, MainActivity.PICK_PHOTOS_VIDEOS_REQUEST);
                            break;
                        case 2:
                            // TODO: 16-7-17  thirdparty file chooser
//                            Intent target = Utils.createGetContentIntent();
//                            intent = Intent.createChooser(target, getString(R.string.choose_file));
//                            getActivity().startActivityForResult(intent, MainActivity.PICK_FILE_REQUEST);
                            break;
                        default:
                            return;
                        }
                    }
                });
        return builder.show();
    }
}
