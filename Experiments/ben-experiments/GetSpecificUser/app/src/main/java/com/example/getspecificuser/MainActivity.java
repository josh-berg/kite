package com.example.getspecificuser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView Status;
    private EditText EnterUsername;
    private EditText EnterBio;
    private EditText NumPosts;
    private Switch AdminBool;
    private Switch ModerBool;
    private Button PostUser;

    private RequestQueue PostRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Status = (TextView) findViewById(R.id.Status);
        EnterUsername = (EditText) findViewById(R.id.EnterUsername);
        EnterBio = (EditText) findViewById(R.id.EnterBio);
        NumPosts = (EditText) findViewById(R.id.NumPosts);
        AdminBool = (Switch) findViewById(R.id.AdminBool);
        ModerBool = (Switch) findViewById(R.id.ModerBool);
        PostUser = (Button) findViewById(R.id.PostUser);

        PostRequests = Volley.newRequestQueue(this);

        PostUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                jsonParse();

                // Status.setText("Pressed");
            }
        });
    }

    private void jsonParse() {

        String userName = EnterUsername.getText().toString();

        final String URL = "http://kite.onn.sh/api/user/" + userName;

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, URL, null,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject user = response.getJSONObject("user");

                            String userName = user.getString("username");
                            boolean isAdmin = user.getBoolean("is_admin");
                            boolean isMod = user.getBoolean("is_mod");
                            int postCount = user.getInt("post_count");
                            String bio = user.getString("bio");

                            // EnterUsername.setText(userName);
                            EnterBio.setText(bio);
                            NumPosts.setText(Integer.toString(postCount));

                            Status.setText(userName + ", " + Boolean.toString(isAdmin) + ", " + Boolean.toString(isMod));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Toast.makeText(getApplication(), response + "", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });

        PostRequests.add(postRequest);
    }
}