package com.team100.kite_master.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.team100.kite_master.MainActivity;
import com.team100.kite_master.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class LoginFragment extends Fragment implements View.OnClickListener {

    Button loginButton;
    EditText loginUsername;
    EditText loginPassword;

    private RequestQueue volleyqueue;
    private String webToken;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_screen, container, false);


        loginUsername = v.findViewById(R.id.login_username);
        loginPassword = v.findViewById(R.id.login_password);
        loginButton = (Button) v.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("Login");
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).hide();

    }

     @Override
     public void onActivityCreated (Bundle savedInstanceState){
         volleyqueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        super.onActivityCreated(savedInstanceState);

    }




    //handle retry button click
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                System.out.println("LOGIN PUSHED");
                Toast.makeText(getActivity(), "LOGGED IN" + " ", Toast.LENGTH_LONG).show();

                login(loginUsername.getText().toString(), loginPassword.getText().toString(),"http://" + LOCAL_IP_ADDRESS + ":5000/api/auth/login");

                SaveSharedPreference.setUserName(getActivity(), "josh");
                break;
        }
    }



    String LOCAL_IP_ADDRESS = "10.0.1.2";



    private void login(final String username, final String password, final String URL) {

        JSONObject LoginCredentials = new JSONObject();
        try {
            LoginCredentials.put("Username", username);
            LoginCredentials.put("Password", password);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, URL, null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject token = response.getJSONObject("data");
                            webToken = token.getString("access_token");
                            System.out.println("TOKEN: " + webToken);
                            getSingleUser(username);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String err = error.toString();
                        if(err.equals("com.android.volley.AuthFailureError")){
                            System.out.println("INCORRECT PASSWORD");
                        } else {
                            System.out.println("NETWORK ERROR");
                        }
                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() {
                String credentials = username + ":" + password;
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };

        volleyqueue.add(loginRequest);
    }



    public void getSingleUser(String username) {

        if(volleyqueue == null){
            System.out.println("NULL QUEUE");
            volleyqueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        }

        String URL = "http://" + LOCAL_IP_ADDRESS + ":5000/api/v2/users/" + username;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            parseUserInfo(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        volleyqueue.add(getRequest);

    }


    //convert JSON object from backend to arraylist of topics
    public void parseUserInfo(JSONObject resp) throws JSONException {
        //get json array of user info
        JSONObject jinfo = resp.getJSONObject("data");
        //set all the data fields for current user
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setUsername(jinfo.getString("username"));
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setAdmin(Boolean.parseBoolean(jinfo.getString("is_admin")));
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setMod(Boolean.parseBoolean(jinfo.getString("is_mod")));
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setPostCount(Integer.parseInt(jinfo.getString("post_count")));
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setBio(jinfo.getString("bio"));
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.setDisplayname(jinfo.getString("displayName"));
        ((MainActivity) Objects.requireNonNull(getActivity())).currentUser.printUserDetails();
    }


}