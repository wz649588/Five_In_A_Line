package com.example.fiveinarow;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by wz649 on 2017/4/25.
 */

public class ChatAreaFragment extends Fragment {

    RelativeLayout activity_main;
    FloatingActionButton fab;
    EditText input;
    ListView listOfMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_area, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        activity_main = (RelativeLayout) view.findViewById(R.id.activity_main);
        input = (EditText) view.findViewById(R.id.input);
        listOfMessage = (ListView) view.findViewById(R.id.list_of_message);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String now = getTime();
                String emaill = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                String childName = dealWith(now, emaill);
                NetBattle.mDatabase.child("Messages").child(childName).setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail(), now));
                input.setText("");
            }
        });
        displayChatMessage();
    }
    private void displayChatMessage() {
        FirebaseListAdapter<ChatMessage> adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class, R.layout.list_item, NetBattle.mDatabase.child("Messages")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText, messageUser, messageTime;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(model.getMessageTime());

                String childName = dealWith(model.getMessageTime(), model.getMessageUser());
                if(!model.isRead() && !model.getMessageUser().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    Toast.makeText(getContext(), "Received a new message",  Toast.LENGTH_SHORT).show();
                    NetBattle.mDatabase.child("Messages").child(childName).child("read").setValue(true);
                }
            }
        };

        listOfMessage.setAdapter(adapter);
    }

    private String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date now = new Date();
        return df.format(now);
    }

    private String dealWith(String now, String emaill) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < now.length(); i++) {
            if(Character.isDigit(now.charAt(i))) {
                sb.append(now.charAt(i));
            }
        }

        for(int i = 0; i < emaill.length(); i++){
            if(Character.isLetterOrDigit(emaill.charAt(i))) {
                sb.append(emaill.charAt(i));
            }
        }
        return sb.toString();
    }

}
