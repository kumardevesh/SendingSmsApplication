package testing.sendingsmsapplication.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import testing.sendingsmsapplication.Activity.ChatHistoryActivity;
import testing.sendingsmsapplication.Modal.SMSModal;
import testing.sendingsmsapplication.R;

/**
 * Created by deveshbatra on 6/13/16.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolderMessageListItem> implements View.OnClickListener {


    private final LayoutInflater inflater;
    private ArrayList<SMSModal> smsList;
    private boolean isFromLaunchActivity;
    private Activity activity;


    public MessageListAdapter(Activity act, ArrayList<SMSModal> smsList, boolean isFromLaunchActivity) {
        this.smsList = smsList;
        activity = act;
        inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.isFromLaunchActivity = isFromLaunchActivity;
    }

    @Override
    public ViewHolderMessageListItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View sView = inflater.inflate(R.layout.message_list_item, parent,
                false);
        return new ViewHolderMessageListItem(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolderMessageListItem holder, int position) {

        SMSModal smsModal = smsList.get(position);
        holder.messageTv.setText(smsModal.getMessage());
        holder.dateTv.setText(smsModal.getDate());
        holder.idTv.setText(smsModal.getMobileNumber());
        if (isFromLaunchActivity) {
            holder.itemView.setTag(smsModal.getMobileNumber());
            holder.itemView.setOnClickListener(this);
        }

    }

    @Override
    public int getItemCount() {
        if (smsList == null)
            return 0;
        return smsList.size();
    }

    @Override
    public void onClick(View view) {
        String mobileNumber = (String) view.getTag();
        Intent intent = new Intent(activity, ChatHistoryActivity.class);
        intent.putExtra("mobileNumber", mobileNumber);
        activity.startActivity(intent);

    }

    public class ViewHolderMessageListItem extends RecyclerView.ViewHolder {
        private TextView dateTv, messageTv, idTv;

        public ViewHolderMessageListItem(View itemView) {
            super(itemView);
            dateTv = (TextView) itemView.findViewById(R.id.date_tv);
            messageTv = (TextView) itemView.findViewById(R.id.message_tv);
            idTv = (TextView) itemView.findViewById(R.id.id_tv);


        }
    }
}
