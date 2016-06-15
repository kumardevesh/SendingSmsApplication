package testing.sendingsmsapplication.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by deveshbatra on 6/14/16.
 */
public class PermissionsHelper {
    public static final int REQUEST_READ_MESSAGES_PERMISION_CODE = 101;

    public static boolean hasPermission(@NonNull Activity activity, @NonNull String permission) {
        return (PermissionChecker.checkSelfPermission(activity, permission)) == PermissionChecker.PERMISSION_GRANTED;
    }

    public static boolean checkAndAskForPermission(@NonNull Activity activity, @NonNull String permission, int requestCode) {
        if (hasPermission(activity, permission))
            return true;
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        return false;
    }

    public static boolean checkAndAskForPermission(@NonNull Activity activity, @NonNull String[] permissions, int requestCode) {
        ArrayList<String> permissionsToAskList = new ArrayList<>();
        for (String permission : permissions) {
            if (!hasPermission(activity, permission))
                permissionsToAskList.add(permission);
        }
        if (permissionsToAskList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsToAskList.toArray(new String[0]), requestCode);
            return false;
        } else
            return true;
    }

    public static void handlePermissionDenied(@NonNull final Activity activity, int messageId) {
        handlePermissionDeniedWithDialog(activity, messageId);
    }

    private static void handlePermissionDeniedWithDialog(@NonNull final Activity activity, int messageId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(null);
        alertDialogBuilder
                .setMessage("Grant Permissions")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                                try {
                                    activity.startActivity(intent);
                                } catch (NullPointerException ignored) {
                                    // gionee
                                }
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        try {
            alertDialog.show();
        } catch (WindowManager.BadTokenException ignored) {
        }
    }
}
