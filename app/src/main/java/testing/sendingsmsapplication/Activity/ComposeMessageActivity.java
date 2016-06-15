package testing.sendingsmsapplication.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import testing.sendingsmsapplication.R;

/**
 * Created by deveshbatra on 6/13/16.
 */
public class ComposeMessageActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView send_message_button, pick_contact_button;
    private EditText contact_number_edit_text, message_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_message_layout);
        send_message_button = (ImageView) findViewById(R.id.send_message_button);
        pick_contact_button = (ImageView) findViewById(R.id.pick_contact_button);
        contact_number_edit_text = (EditText) findViewById(R.id.contact_number_edit_text);
        message_edit_text = (EditText) findViewById(R.id.message_edit_text);
        send_message_button.setOnClickListener(this);
        pick_contact_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_message_button:
                sendMesssage();
                break;

            case R.id.pick_contact_button:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, Helper.SELECT_CONTACT_REQUEST);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Helper.SELECT_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) addSelectedContactNumbers(data);
        }
    }

    private void addSelectedContactNumbers(Intent data) {
        String phoneNumber = "";
        Uri contactData = data.getData();
        Cursor c = getContentResolver().query(contactData, null, null, null, null);
        String contactId = null;
        if (c.moveToFirst())
            contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
        c.close();
        c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
        if (c.moveToFirst()) {

            do {
                int phoneType = c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                    phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                    break;
                }
            } while (c.moveToFirst());

        }
        c.close();
        contact_number_edit_text.setText(phoneNumber);
    }

    private void sendMesssage() {
        String contactNumber = contact_number_edit_text.getText().toString();
        String messageBody = message_edit_text.getText().toString();
        if (isSet(messageBody) && isSet(contactNumber)) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(contactNumber, null, messageBody, null, null);
                message_edit_text.setText("");
                Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if (!isSet(contactNumber)) {
            Toast.makeText(getApplicationContext(), "Enter ContactNo", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "Enter Message", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isSet(String val) {
        return !(val == null || val.trim().equals("") || val.trim().equals("null"));
    }
}
