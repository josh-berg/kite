package com.team100.kite_master.forum;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.team100.kite_master.R;
import com.team100.kite_master.forum.forum_data_classes.Topic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class ForumTopicListFragment extends Fragment implements View.OnClickListener {

    public String LOCAL_IP_ADDRESS;

    ListView topicListView;
    ProgressBar loadingCircle;
    TextView errMessage;
    Button retryTopics;
    MenuItem newPostButton;

    ArrayList<Topic> topicList = new ArrayList<Topic>();
    private RequestQueue volleyqueue;
    CustomAdapter topicAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.forum_topic_list, container, false);
        //set local ip for testing
        LOCAL_IP_ADDRESS = "10.0.1.2";
        //link list view
        topicListView = v.findViewById(R.id.list_view);
        loadingCircle = v.findViewById(R.id.topics_loading);
        errMessage = v.findViewById(R.id.error_message);
        //initialize volley queue
        volleyqueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        //request topics from the backend
        requestTopics();
        //show loading circle until topics received
        loadingCircle.setVisibility(View.VISIBLE);
        //hide error text view
        errMessage.setVisibility(View.GONE);
        //initialize button
        retryTopics = v.findViewById(R.id.retry_topics);
        //set button on click listener
        retryTopics.setOnClickListener(this);
        //returns value of whatever list item is clicked
        topicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(topicList.get(position).getTopicID());
                openTopic(topicList.get(position).getTopicID());
            }
        });
        //hide button
        retryTopics.setVisibility(View.GONE);
        //initialize custom adapter and set it to list view
        topicAdapter = new CustomAdapter();
        topicListView.setAdapter(topicAdapter);

        setHasOptionsMenu(true);

        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_buttons, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set title
        Objects.requireNonNull(getActivity()).setTitle("Forum");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_topics:
                retryTopics.setVisibility(View.GONE);
                errMessage.setVisibility(View.GONE);
                requestTopics();
                loadingCircle.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                requestTopics();
                loadingCircle.setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }



    //custom topic adapter class
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return topicList.size();
        }

        @Override
        public Object getItem(int i) {
            return topicList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public String getTopicID(int i) {
            return topicList.get(i).getTopicID();
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.forum_topic_list_item, null);
            // initialize text views
            TextView topicTitle = (TextView) view.findViewById(R.id.text_title);
            TextView topicDescription = (TextView) view.findViewById(R.id.text_description);
            // iterate through list to set topic entries
            topicTitle.setText(topicList.get(i).getName());
            topicDescription.setText(topicList.get(i).getDescription());
            return view;
        }
    }




    //swtich to new fragment after list item is selected
    public void openTopic(String topic) {
        Fragment fragment = new ForumPostListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("selectedTopic", topic);
        fragment.setArguments(bundle);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment).addToBackStack("tag");
        ft.commit();
    }


    //NETWORKING
    //requests topic JSON object from backend
    public void requestTopics() {
        System.out.println("REQUESTING TOPICS");
        String URL = "http://" + LOCAL_IP_ADDRESS + ":5000/api/v2/topics";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //parse topics to array from json response
                            parseTopics(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToast(error.toString());
                        loadingCircle.setVisibility(View.GONE);
                        errMessage.setText("Connection Error\n Make sure your forum server is running.");
                        errMessage.setVisibility(View.VISIBLE);
                        retryTopics.setVisibility(View.VISIBLE);

                    }
                }
        );
        volleyqueue.add(getRequest);
    }

    //convert JSON object from backend to arraylist of topics
    public void parseTopics(JSONObject resp) throws JSONException {
        //create output list
        ArrayList<Topic> tops = new ArrayList<Topic>();
        //get JSON array of topics
        JSONArray topics = resp.getJSONObject("data").getJSONArray("topics");
        //for each element in the array create a new topic object and add it to the array list
        for (int i = 0; i < topics.length(); i++) {
            Topic t = new Topic(topics.getJSONObject(i).getString("name"), topics.getJSONObject(i).getString("descript"));
            tops.add(t);
        }
        //update global topic list
        topicList = new ArrayList<Topic>(tops);
        //sort topic list in alphabetical order
        Collections.sort(topicList);
        //notify adapter to update its list with the new topics
        topicAdapter.notifyDataSetChanged();
        //hide loading circle
        loadingCircle.setVisibility(View.GONE);
    }

    //display a toast
    private void showToast(String message) {
        Toast.makeText(getActivity(), message + " ", Toast.LENGTH_LONG).show();
    }
}