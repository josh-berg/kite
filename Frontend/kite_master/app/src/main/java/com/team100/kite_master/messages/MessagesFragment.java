package com.team100.kite_master.messages;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    private LinearLayout messageList;

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
        messageList = (LinearLayout) v.findViewById(R.id.message_linear_layout);
        errorTextView = (TextView) v.findViewById(R.id.error_textView);
        messageText = (EditText) v.findViewById(R.id.message_edit_text);
        postButton = (Button) v.findViewById(R.id.message_button);

        implementationWS = new WebSocketImplementation(this, username, LOCAL_IP_ADDRESS);

        //set on click listener
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

                Message msg = new Message(username, txt);
                String messageTime = msg.getMessageTime().toString();
                String messageString =  msg.getUsername() + ": " + msg.getText();

                // Create a textview, and set it up
                RelativeLayout text = setupTextView(username, messageTime, messageString);

                // Add the message to the Linearlayout
                messageList.addView(text);


                // Credit to this source: https://stackoverflow.com/questions/21926644/get-height-and-width-of-a-layout-programmatically
                // Scroll to bottom upon receiving new messages

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });

            }

        });

    }

    public void setErrorText(String errorText) {

        errorTextView.setText(errorText);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public RelativeLayout setupTextView(String username, String messageTime, String messageString) {

        final int DISTANCE_FROM_CLOSE_EDGE = 30;
        final int DISTANCE_FROM_FAR_EDGE = 240;

        final int THIS_USER_BACKGROUND_COLOR = 0xff2bc3ff;
        final int OTHER_USER_BACKGROUND_COLOR = 0xffd9d1c9;

        // TextView timeText = setupTimeTextView(messageTime);
        TextView messageText = setupMessageTextView(username, messageString);



        RelativeLayout messageLayout = new RelativeLayout(getContext());
        // messageLayout.setBackgroundResource(R.drawable.message_layout);

        // Credit to this source: https://stackoverflow.com/questions/18844418/add-margin-programmatically-to-relativelayout
        // Set parameters of relativeLayout object
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(width, height);
        relativeParams.setMargins(DISTANCE_FROM_CLOSE_EDGE, DISTANCE_FROM_CLOSE_EDGE, DISTANCE_FROM_CLOSE_EDGE, 0);



        // relativeParams.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        // relativeParams.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);



        // RelativeLayout

        // Position the messages that you yourself send to the right
        // Position the messages of other users to the left
        if (username == getUsername()) {

            relativeParams.setMarginStart(DISTANCE_FROM_FAR_EDGE);
            relativeParams.setMarginEnd(DISTANCE_FROM_CLOSE_EDGE);

            messageLayout.setBackgroundResource(R.drawable.message_layout_this_user);


            // messageText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

            // messageLayout.setGravity(0);
            // messageLayout.setBackgroundColor(THIS_USER_BACKGROUND_COLOR);
        }
        else {

            relativeParams.setMarginStart(DISTANCE_FROM_CLOSE_EDGE);
            relativeParams.setMarginEnd(DISTANCE_FROM_FAR_EDGE);

            messageLayout.setBackgroundResource(R.drawable.message_layout);

            // messageLayout.setBackgroundColor(OTHER_USER_BACKGROUND_COLOR);
        }

        messageLayout.setLayoutParams(relativeParams);
        messageLayout.requestLayout();


        // messageLayout.addView(timeText);
        messageLayout.addView(messageText);

        return messageLayout;
    }

    public TextView setupTimeTextView(String messageTime) {

        // TextView timeText = new TextView(getContext());
        // timeText.setText(messageTime);

        return null;
    }

    public TextView setupMessageTextView(String username, String messageString) {

        final int DISTANCE_FROM_CLOSE_EDGE = 30;
        final int BLACK_COLOR = 0xff000000;

        TextView messageText = new TextView(getContext());
        messageText.setText(messageString);
        messageText.setTextColor(BLACK_COLOR);
        messageText.setPadding(DISTANCE_FROM_CLOSE_EDGE, DISTANCE_FROM_CLOSE_EDGE, DISTANCE_FROM_CLOSE_EDGE, DISTANCE_FROM_CLOSE_EDGE);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);

        // layoutParams.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        // layoutParams.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        messageText.setLayoutParams(layoutParams);

        return messageText;
    }



    // Getter and setter methods used for JUnit and Mockito testing
    public String getUsername() {

        return this.username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public LinearLayout getMessageView() {

        return this.messageList;
    }

    public void setMessageView(LinearLayout messageView) {

        this.messageList = messageView;
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