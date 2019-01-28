package com.example.applicationtest2;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        */

        Button sendButton = (Button) findViewById (R.id.send_button);
        final TextView centerText = (TextView) findViewById (R.id.center_string); // This needs to be declared as final for some reason.
        final EditText typeText = (EditText) findViewById (R.id.typing_text); // This needs to be declared as final for some reason.

        final ScrollView scroll = (ScrollView) findViewById (R.id.scrollView);

        final TextView text1 = (TextView) findViewById (R.id.textView);
        final TextView text2 = (TextView) findViewById (R.id.textView2);
        final TextView text3 = (TextView) findViewById (R.id.textView3);

        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                centerText.setText(typeText.getText());

                scroll.addView(new TextView((Context) typeText.getText()));

                text1.setText(typeText.getText());
                text2.setText(typeText.getText());
                text3.setText(typeText.getText());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCenterString() {



    }
}
