package testing.sendingsmsapplication.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import testing.sendingsmsapplication.Adapter.MessageListAdapter;
import testing.sendingsmsapplication.Modal.SMSModal;
import testing.sendingsmsapplication.R;
import testing.sendingsmsapplication.Utility.PermissionsHelper;
import testing.sendingsmsapplication.Utility.SmsListHelper;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener {

    private static String Tag = "app";
    private RecyclerView message_list_view;
    private TextView no_messages_tv;
    private ImageView compose_icon, backup_icon;
    private Activity aActivity;
    private String[] permissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        aActivity = this;
        message_list_view = (RecyclerView) findViewById(R.id.message_list_view);
        compose_icon = (ImageView) findViewById(R.id.compose_icon);
        compose_icon.setOnClickListener(this);
        compose_icon.setVisibility(View.GONE);
        backup_icon = (ImageView) findViewById(R.id.backup_icon);
        backup_icon.setOnClickListener(this);
        backup_icon.setVisibility(View.GONE);
        no_messages_tv = (TextView) findViewById(R.id.no_messages_tv);


        permissions = new String[]{Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE,

        };

        if (PermissionsHelper.checkAndAskForPermission(aActivity, permissions, PermissionsHelper.REQUEST_READ_MESSAGES_PERMISION_CODE)) {
            onPermissionGranted();
        }


    }

    //
//    @Override
//    protected  void onStart()
//    {
//        Log.d(Tag,"on Start ");
////        super.onNewIntent(intent);
////        setIntent(intent);
//        super.onStart();
////        if (PermissionsHelper.checkAndAskForPermission(aActivity, permissions, PermissionsHelper.REQUEST_READ_MESSAGES_PERMISION_CODE)) {
////            onPermissionGranted();
////        }
//    }
//
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(Tag, "on new Intent ");
        super.onNewIntent(intent);
        setIntent(intent);
        if (PermissionsHelper.checkAndAskForPermission(aActivity, permissions, PermissionsHelper.REQUEST_READ_MESSAGES_PERMISION_CODE)) {
            onPermissionGranted();
        }
    }

    @SuppressLint({"Override", "NewApi"})
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case PermissionsHelper.REQUEST_READ_MESSAGES_PERMISION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permisssion", "granted");
                    onPermissionGranted();
                } else {
                    boolean neverAskAgain = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                    if (neverAskAgain) {
                        PermissionsHelper.handlePermissionDenied(this, R.string.permission_denied);
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void onPermissionGranted() {
        compose_icon.setVisibility(View.VISIBLE);
        backup_icon.setVisibility(View.VISIBLE);

        SmsListHelper smsListHelper = new SmsListHelper(aActivity, true, "");
        ArrayList<SMSModal> smsList = smsListHelper.fetchInbox();
        if (smsList != null && smsList.size() > 0) {
            MessageListAdapter mMessageListAdapter = new MessageListAdapter(this, smsList, true);
            message_list_view.setAdapter(mMessageListAdapter);
            message_list_view.setLayoutManager(new LinearLayoutManager(this));
        } else {
            no_messages_tv.setVisibility(View.VISIBLE);
            message_list_view.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.compose_icon:
                Intent intent = new Intent(this, ComposeMessageActivity.class);
                this.startActivity(intent);
                break;

            case R.id.backup_icon:
                intent = new Intent(this, HomeActivity.class);
                this.startActivity(intent);
                break;

        }

    }
}
