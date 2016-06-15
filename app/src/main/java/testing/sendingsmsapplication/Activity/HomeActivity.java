package testing.sendingsmsapplication.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;

import testing.sendingsmsapplication.R;

public class HomeActivity extends BaseCBActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private static final int
            PROGRESS_DIALOG = 100,
            CONFIRM_DIALOG = 101,
            EULA_DIALOG = 102;

    private Button processButton;
    private ProgressDialog progressDialog;
    private AlertDialog confirmDialog;
    private AlertDialog eulaDialog;
    private Intent shareIntent;
    private Button selectContactButton;
    private EditText specNumBackupEditView;
    // private OnClickListener checkboxListener;

    @Override
    protected void onCreateBaseCallback() {
        if (Helper.SAFETY_DEV_MODE) {
            Helper.debugAllContentResolvers(this);
            Helper.getPreferences(this).edit().putString(Helper.PREFERENCE_EULA_ACCEPTED, Helper.VERSION).commit();
        }
        SharedPreferences sharedPreferences = Helper.getPreferences(this);
//    if(!sharedPreferences.getString(Helper.PREFERENCE_EULA_ACCEPTED, "-").equals(Helper.VERSION)) {
//      showDialog(EULA_DIALOG);
//    }
        setContentView(R.layout.main);
        specNumBackupEditView = (EditText) findViewById(R.id.specific_numbers_to_backup_edit);
        aboutAppButton = (Button) findViewById(R.id.about_app);
        exitAppButton = (Button) findViewById(R.id.exit_app);
        processButton = (Button) findViewById(R.id.confirm);
        processButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "process button clicked");
                showDialog(CONFIRM_DIALOG);
            }
        });
        selectContactButton = (Button) findViewById(R.id.select_contact);
        selectContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "select contact button clicked");
                Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
                startActivityForResult(intent, Helper.SELECT_CONTACT_REQUEST);
            }
        });
        // checkboxListener = new OnClickListener() {
        //     public void onClick(View v) {
        //     }
        //   };

        resetState(sharedPreferences, R.id.backup_all_numbers, "backup_all_numbers", false);
        resetState(sharedPreferences, R.id.backup_messages, "backup_messages", true);
        resetState(sharedPreferences, R.id.backup_mms_attachments, "backup_mms_attachments", true);
        resetState(sharedPreferences, R.id.backup_call_records, "backup_call_records", true);
        resetState(sharedPreferences, R.id.delete_after_backup, "delete_after_backup", false);
        resetState(sharedPreferences, R.id.share_archive, "share_archive", true);
        resetState(sharedPreferences, R.id.random_question, "random_question", true);
        resetState(sharedPreferences, R.id.specific_numbers_to_backup_edit, "specific_numbers_to_backup_edit", false);

        final CheckBox backupAllCB = (CheckBox) findViewById(R.id.backup_all_numbers);
        final EditText specNumBackupEditView2 = specNumBackupEditView;
        final Button selectContactButton2 = selectContactButton;
        specNumBackupEditView.setEnabled(!backupAllCB.isChecked());
        selectContactButton.setEnabled(!backupAllCB.isChecked());
        backupAllCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specNumBackupEditView2.setEnabled(!backupAllCB.isChecked());
                selectContactButton2.setEnabled(!backupAllCB.isChecked());
            }
        });

        shareIntent = new Intent(Intent.ACTION_SEND);
        //shareIntent.setType("message/rfc822");
        shareIntent.setType("application/zip");
        //shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"blah@blah.com"});
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.attached_message));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.attached_subject));
        showDialog(CONFIRM_DIALOG);
    }

    // public void onSaveInstanceState(Bundle savedInstanceState) {
    //   saveState(savedInstanceState, R.id.backup_all_numbers, "backup_all_numbers");
    //   super.onSaveInstanceState(savedInstanceState);
    // }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = super.onCreateDialog(id);
        if (dialog != null) return dialog;
        Log.d(TAG, "onCreateDialog");
        AlertDialog.Builder builder = null;
        switch (id) {
            case EULA_DIALOG:
                Log.d(TAG, "Eula");
                String eulaMsg = null;
                try {
                    eulaMsg = Helper.read(new InputStreamReader(getAssets().open(Helper.ASSET_EULA)), true);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading eula", e);
                    throw new RuntimeException(e);
                }
                final SharedPreferences sharedPreferences = Helper.getPreferences(HomeActivity.this);
                builder = new AlertDialog.Builder(this)
                        .setTitle(R.string.eula_title)
                        .setMessage(eulaMsg)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.eula_accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int id_) {
                                sharedPreferences.edit().putString(Helper.PREFERENCE_EULA_ACCEPTED, Helper.VERSION).commit();
                            }
                        })
                        .setNegativeButton(getString(R.string.eula_refuse), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int id1) {
                                dialog1.cancel();
                                HomeActivity.this.finish();
                            }
                        });
                Log.d(TAG, "Builder done");
                eulaDialog = builder.create();
                Log.d(TAG, "Builder dialog created done");
                dialog = eulaDialog;
                break;
            case CONFIRM_DIALOG:
                Log.d(TAG, "Builder about to create");
                builder = new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.start_processing_prompt_message))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.prompt_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int id1) {
                                HomeActivity.this.process();
                                //HomeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.prompt_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int id1) {
                                dialog1.cancel();
                                finish();
                            }
                        });
                Log.d(TAG, "Builder done");
                confirmDialog = builder.create();
                Log.d(TAG, "Builder dialog created done");
                dialog = confirmDialog;
                break;
            case PROGRESS_DIALOG:
                //duh!!! U can't call updateProgress from here
                //updateProgress("Processing Conversation backup", null, 0, false);
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                //progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getString(R.string.progress_message_default));
                dialog = progressDialog;
                break;
            default:
                dialog = null;
        }
        Log.d(TAG, "For id: " + id + ", returning dialog: " + dialog);
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case PROGRESS_DIALOG:
                progressDialog.setMessage("");
                progressDialog.setProgress(0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode + ", resultCode: " + resultCode);
        Log.d(TAG, "onActivityResult: RESULT_OK: " + RESULT_OK);
        if (requestCode == Helper.SEND_ARCHIVE_REQUEST) {
            String longMsg = (resultCode == RESULT_OK ?
                    getString(R.string.archive_shared_success) :
                    getString(R.string.archive_shared_fail));
            //for some reason, we always got RESULT_CANCELLED even when the email was successfully sent (TODO)
            //so don't be specific TBD
            longMsg = getString(R.string.archive_shared);
            processingDone(longMsg);
        } else if (requestCode == Helper.SELECT_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) addSelectedContactNumbers(data);
        }
    }

    private void addSelectedContactNumbers(Intent data) {
        Log.d(TAG, "addSelectedContactNumbers");
        StringBuilder sb = new StringBuilder();
        Set<String> phNums = new LinkedHashSet<String>();
        String s = null;
        Uri contactData = data.getData();
        Cursor c = getContentResolver().query(contactData, null, null, null, null);
        String contactId = null;
        if (c.moveToFirst())
            contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
        c.close();
        c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
        if (c.moveToFirst()) {
            do {
                s = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //canonicalize it
                sb.setLength(0);
                for (int i = 0; i < s.length(); i++) {
                    char ch = s.charAt(i);
                    if (ch >= '0' && ch <= '9') sb.append(ch);
                }
                phNums.add(sb.toString());
            } while (c.moveToNext());
        }
        c.close();
        Log.d(TAG, String.format("addSelectedContactNumbers: contactId: %s, numbers: %s", contactId, phNums));
        specNumBackupEditView.setText(specNumBackupEditView.getText() + " " + Helper.write(phNums, " ", "", ""));
    }

    private void processingDone(String longMsg) {
        if (longMsg == null) longMsg = getString(R.string.archive_shared);
        updateProgress(longMsg, null, -1, false);
        //TBD: Preferably Use a broadcast, so everyone can get this.
        Helper.writeToFile(Helper.RESULT_LOG_FILE, true, longMsg, "\n");
        //show result activity
        startActivity(new Intent(this, ResultActivity.class));
    }

    @Override
    protected void updateProgress(Intent intent) {
        Bundle extras = intent.getExtras();
        updateProgress(extras.getString("message"),
                extras.getString("zipfile"),
                extras.getInt("percent_completed"),
                extras.getBoolean("share_archive"));

    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    private void updateProgress(String message, String zipfile, int completed, boolean share_archive) {
        if (progressDialog == null) showDialog(PROGRESS_DIALOG);
        progressDialog.setMessage(message);
        if (completed >= 0) progressDialog.setProgress(Math.min(100, completed));
        if (completed >= 100) {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            if (share_archive) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(zipfile))); //Uri.parse("file://"+ zipfile));
                startActivityForResult
                        (Intent.createChooser(shareIntent, getString(R.string.share_archive_message)),
                                Helper.SEND_ARCHIVE_REQUEST);
            } else {
                processingDone(null);
            }
        } else if (completed >= 0) {
            if (!progressDialog.isShowing()) progressDialog.show();
        }
    }

    //TBD
    private void process() {
        //save preferences first (since service needs it)
        SharedPreferences sharedPreferences = Helper.getPreferences(this);
        //getSharedPreferences(Helper.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        savePrefs(editor, R.id.backup_all_numbers, "backup_all_numbers");
        savePrefs(editor, R.id.backup_messages, "backup_messages");
        savePrefs(editor, R.id.backup_mms_attachments, "backup_mms_attachments");
        savePrefs(editor, R.id.backup_call_records, "backup_call_records");
        savePrefs(editor, R.id.delete_after_backup, "delete_after_backup");
        savePrefs(editor, R.id.share_archive, "share_archive");
        savePrefs(editor, R.id.random_question, "random_question");
        savePrefs(editor, R.id.specific_numbers_to_backup_edit, "specific_numbers_to_backup_edit");
        editor.commit();

        Intent intent = new Intent(this, ProcessingService.class);
        startService(intent);
        showDialog(PROGRESS_DIALOG);
        //service broadcasts extra info in the Intent
    }

    private void resetState(SharedPreferences prefs, int id, String key, boolean defValue) {
        Object o = findViewById(id);
        if (o != null && prefs != null) {
            if (o instanceof CheckBox) ((CheckBox) o).setChecked(prefs.getBoolean(key, defValue));
            else if (o instanceof EditText) ((EditText) o).setText(prefs.getString(key, ""));
        }
    }

    private void savePrefs(SharedPreferences.Editor editor, int id, String key) {
        Object o = findViewById(id);
        if (o != null && editor != null) {
            if (o instanceof CheckBox) editor.putBoolean(key, ((CheckBox) o).isChecked());
            else if (o instanceof EditText)
                editor.putString(key, ((EditText) o).getText().toString());
        }
    }

}

