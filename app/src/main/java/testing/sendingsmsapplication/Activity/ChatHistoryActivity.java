package testing.sendingsmsapplication.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import testing.sendingsmsapplication.Adapter.MessageListAdapter;
import testing.sendingsmsapplication.Modal.SMSModal;
import testing.sendingsmsapplication.R;
import testing.sendingsmsapplication.Utility.SmsListHelper;

/**
 * Created by deveshbatra on 6/15/16.
 */
public class ChatHistoryActivity extends AppCompatActivity {

    private Activity activity;
    private String mobileNumber;
    private RecyclerView message_list_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        mobileNumber = getIntent().getExtras().getString("mobileNumber");
        setContentView(R.layout.chat_history_layout);

        message_list_view = (RecyclerView) findViewById(R.id.chat_history_list_view);
        SmsListHelper smsListHelper = new SmsListHelper(activity, false, mobileNumber);
        ArrayList<SMSModal> smsList = smsListHelper.fetchInbox();
        if (smsList != null && smsList.size() > 0) {
            MessageListAdapter mMessageListAdapter = new MessageListAdapter(this, smsList, false);
            message_list_view.setAdapter(mMessageListAdapter);
            message_list_view.setLayoutManager(new LinearLayoutManager(this));
        }
    }


}
