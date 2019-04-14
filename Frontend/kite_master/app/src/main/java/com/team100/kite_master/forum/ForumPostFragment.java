package com.team100.kite_master.forum;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.team100.kite_master.MainActivity;
import com.team100.kite_master.R;
import com.team100.kite_master.forum.forum_data_classes.DateUtil;
import com.team100.kite_master.forum.forum_data_classes.Post;
import com.team100.kite_master.networking.NetworkManager;
import com.team100.kite_master.networking.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ForumPostFragment extends Fragment implements View.OnClickListener {

    //Local ip for testing
    private String server_ip;
    private String[] userdata;

    //global variables
    String postID;

    //declare error layout items
    ProgressBar loadingCircle;
    TextView errMessage;
    Button retry;

    //declare layout items
    ScrollView postScrollView;
    ImageView postImageView;
    TextView postTitleView;
    TextView postTimeView;
    TextView postAuthorView;
    TextView postBodyView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.forum_post, container, false);
        //receive bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userdata = bundle.getStringArray("userData");
            postID = bundle.getString("selectedPost");
        }



        //LOCAL DEBUGGING
        System.out.println(" ");
        System.out.println("POST FRAGMENT:");
        System.out.println("CURRENT POST: " + postID);
        System.out.println("USER: " + Arrays.toString(userdata));
        System.out.println("IP ADDRESS: " + server_ip);
        System.out.println(" ");

        //initialize layout elements
        loadingCircle = v.findViewById(R.id.topics_loading);
        errMessage = v.findViewById(R.id.error_message);
        postImageView = v.findViewById(R.id.single_post_image);
        postTitleView = v.findViewById(R.id.single_post_title);
        postTimeView = v.findViewById(R.id.single_post_time);
        postAuthorView = v.findViewById(R.id.single_post_author);
        postBodyView = v.findViewById(R.id.single_post_body);
        postScrollView = v.findViewById(R.id.post_scroll_view);
        retry = v.findViewById(R.id.retry_topics);

        //set button on click listener
        retry.setOnClickListener(this);

        //hide everything until post is gotten
        postScrollView.setVisibility(View.GONE);

        //request posts
        requestPost(postID);
        //show loading circle until topics received
        loadingCircle.setVisibility(View.VISIBLE);

        //hide error elements
        errMessage.setVisibility(View.GONE);
        retry.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("Post");
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).hide();
    }

    //handle retry button click
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_topics:
                retry.setVisibility(View.GONE);
                errMessage.setVisibility(View.GONE);
                //requestPost(postID);
                requestPost(postID);
                loadingCircle.setVisibility(View.VISIBLE);
                break;
        }
    }

    //NETWORKING
    //requests topic JSON object from backend
    public void requestPost(String postid) {
        NetworkManager.getInstance().requestPost(postid, new VolleyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject object) {
                try {
                    ForumParser fp = new ForumParser();
                    setViewElements(fp.parsePost(object));
                } catch (JSONException e) {
                    displayErrorRetry(e.toString());
                }
            }

            @Override
            public void getError(VolleyError err) {
                displayErrorRetry(err.toString());
            }
        });
    }


    public void displayErrorRetry(String err) {
        Toast.makeText(getActivity(), err + " ", Toast.LENGTH_LONG).show();
        loadingCircle.setVisibility(View.GONE);
        errMessage.setText("Connection Error\n Make sure your forum server is running.");
        errMessage.setVisibility(View.VISIBLE);
        retry.setVisibility(View.VISIBLE);
    }


    public void setViewElements(Post p) {
        loadingCircle.setVisibility(View.GONE);
        postTitleView.setText(p.getPostTitle());
        String atAuthor = "@" + p.getPostAuthor();
        postAuthorView.setText(atAuthor);
        DateUtil d = new DateUtil();
        String date = d.getCleanDate(Long.parseLong(p.getPostTime()), "MM/dd/yy hh:mma");
        postTimeView.setText(date);
        postBodyView.setText(p.getPostBody());
        postScrollView.setVisibility(View.VISIBLE);
    }
}
