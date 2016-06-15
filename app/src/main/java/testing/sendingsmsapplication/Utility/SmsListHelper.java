package testing.sendingsmsapplication.Utility;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import testing.sendingsmsapplication.Modal.SMSModal;

/**
 * Created by deveshbatra on 6/15/16.
 */
public class SmsListHelper {

    private Activity activity;
    private boolean isFromLaunchActivity;
    private String mobileNumber;

    public SmsListHelper(Activity activity, boolean isFromLaunchActivity, String mobileNumber) {
        this.activity = activity;
        this.isFromLaunchActivity = isFromLaunchActivity;
        this.mobileNumber = mobileNumber;
    }

    public String millisToDate(long date) {
        String FormattedDate;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        FormattedDate = sdf.format(date).toString();
        return FormattedDate;
    }

    public ArrayList<SMSModal> fetchInbox() {
        ArrayList<SMSModal> smsList = new ArrayList<SMSModal>();

        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor;
        if (this.isFromLaunchActivity) {
            cursor = activity.getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);
        } else {
            String whereClause = "address=?";
            String[] whereArgs = new String[]{mobileNumber};
            cursor = activity.getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, whereClause, whereArgs, null);
        }
        if (cursor.moveToFirst()) {

            do {
                SMSModal sms = new SMSModal();
                sms.setMobileNumber(cursor.getString(1));
                sms.setMessage(cursor.getString(3));
                sms.setDate(millisToDate(cursor.getLong(2)));
                smsList.add(sms);
            } while (cursor.moveToNext());

        }
        if (cursor != null)
            cursor.close();
        return smsList;

    }
}
