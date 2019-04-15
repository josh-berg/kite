package com.team100.kite_master.messages;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.team100.kite_master.R;
import com.team100.kite_master.messages.messages_data_classes.Message;
import com.team100.kite_master.messages.messages_data_classes.WebSocketImplementation;

import java.util.Arrays;
import java.util.Objects;


public class MessagesFragment extends Fragment implements OutputHandler {

    private String LOCAL_IP_ADDRESS;
    private String[] userdata;
    private String username;

    private ScrollView scrollView;
    private LinearLayout messageView;

    private TextView errorTextView;
    private EditText messageText;
    private Button postButton;

    private WebSocketImplementation implementationWS;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.messages_fragment, container, false);

        //receive bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userdata = bundle.getStringArray("userData");
            username = userdata[0]; // Get the username of the user
            LOCAL_IP_ADDRESS = bundle.getString("serverIP");
            System.out.println("USER DATA:");
            System.out.println(Arrays.toString(userdata));
        }

        //initialize user interface objects
        scrollView = (ScrollView) v.findViewById(R.id.message_scroll_view);
        messageView = (LinearLayout) v.findViewById(R.id.message_layout);
        errorTextView = (TextView) v.findViewById(R.id.error_textView);
        messageText = (EditText) v.findViewById(R.id.message_edit_text);
        postButton = (Button) v.findViewById(R.id.message_button);

        implementationWS = new WebSocketImplementation(this, username, LOCAL_IP_ADDRESS);

        //set on click listener
        //postButton.setOnClickListener(this);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageString = messageText.getText().toString();

                // Make sure the string isn't empty
                if (!messageString.equals("")) {

                    // Send the message
                    implementationWS.sendJSONText(messageString);

                    // Clear the message text
                    messageText.setText("");
                }
                else {

                    Toast.makeText(getActivity(), "Please enter a message" + " ", Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("Messages");
    }

    public void output(final String username, final String txt) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                final int TEXT_OFFSET = 350;
                final int THIS_USER_BACKGROUND_COLOR = 0xff2bc3ff;
                final int OTHER_USER_BACKGROUND_COLOR = 0xffffea00;

                Message msg = new Message(username, txt);
                String messageString = msg.getMessageTime() + "\n" + msg.getUsername() + ": " + msg.getText() + "\n";

                TextView text = new TextView(getContext());
                text.setText(messageString);
                text.setTextColor(0xff000000); // Set text color to black

                int width = messageView.getMeasuredWidth() - TEXT_OFFSET;
                int height = LinearLayout.LayoutParams.MATCH_PARENT;
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
                text.setLayoutParams(lp);
                // text.setWidth(200);
                // text.setMaxWidth(250);

                // Position the messages that you yourself send to the right
                // Position the messages of other users to the left
                if (username == getUsername()) {

                    text.setX(TEXT_OFFSET - 10);

                    text.setBackgroundColor(THIS_USER_BACKGROUND_COLOR);
                }
                else {

                    text.setX(10);
                    // text.setBackgroundColor(0xff2bff2b);
                    text.setBackgroundColor(OTHER_USER_BACKGROUND_COLOR);
                }

                messageView.addView(text);

                // Scroll to bottom upon receiving new messages
                scrollView.fullScroll(text.FOCUS_DOWN);
            }
        });
    }

    public void setErrorText(String errorText) {

        errorTextView.setText(errorText);
    }



    // Getter and setter methods used for JUnit and Mockito testing
    public String getUsername() {

        return this.username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public LinearLayout getMessageView() {

        return this.messageView;
    }

    public void setMessageView(LinearLayout messageView) {

        this.messageView = messageView;
    }

    public String getIPaddress() {

        return this.LOCAL_IP_ADDRESS;
    }

    public void setIPaddress(String LOCAL_IP_ADDRESS) {

        this.LOCAL_IP_ADDRESS = LOCAL_IP_ADDRESS;
    }

    public TextView getErrorTextView() {

        return this.errorTextView;
    }

    public View getView() {

        return this.getView();
    }

    public WebSocketImplementation getWebSocketImplementation() {

        return this.implementationWS;
    }
}