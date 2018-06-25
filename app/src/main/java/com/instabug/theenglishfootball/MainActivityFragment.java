package com.instabug.theenglishfootball;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private RequestQueue mQueue;
    ProgressBar mProgressbar;
    MultiStateToggleButton toggle;
    ListView listview;
    ArrayList items;
    ListAdapter adapter;
    FavDB dbHandler;
    SimpleDateFormat dateFormat,timeFormat,smallDateFormat;
    int startIndex = -1;
    public static final String REQUEST_TAG = "getFixtures";
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listview = rootView.findViewById(R.id.main_listView);
        mProgressbar = rootView.findViewById(R.id.main_progressBar);
        toggle = rootView.findViewById(R.id.main_toggle);

        //Set the toggle to first element at creation
        toggle.setValue(0);

        mQueue = VolleyRequestQueue.getInstance(getContext().getApplicationContext())
                .getRequestQueue();

        items = new ArrayList();

        //Many formatters to handle date operations
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        timeFormat = new SimpleDateFormat("HH:mm");
        smallDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //Get the local time zone to view matches in local time zone of the device
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        dbHandler = new FavDB(getActivity());

        //Listener on the toggle button to load the list each time
        toggle.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int position) {
                bindList();
            }
        });

        //Bind the list view fot the first time
        bindList();

        return rootView;
    }

    /**
     * Bind list view depending on toggle selection
     *
     *  @param
     *  @return
     */
    protected void bindList(){
        mProgressbar.setVisibility(View.VISIBLE);
        listview.setAdapter(null);
        
        //bind the list from the database if the user select favourite section
        if(toggle.getValue() == 1) bindListFromDB();
        else sendVolley();
    }

    /**
     * Bind the list from the database as favourite items
     *
     *  @param
     *  @return
     */
    protected void bindListFromDB(){
        try {
            //get all favourite matches for the database
            items = dbHandler.getAllMatchs();
            
            //Update matches from server API if they haven't finished yet
            updateOfflineMatches(items);
            mProgressbar.setVisibility(View.INVISIBLE);
            
            //Sort them to section them probably
            Collections.sort(items);
            
            //Section the matches and bind them to the adapter
            adapter = new ListAdapter(getContext(), addSections(items), View.VISIBLE,
                    true);
            listview.setAdapter(adapter);

            //Set the first viewed element to today or nearest next day
            listview.setSelection(startIndex);
        } catch (Exception e) {
            Log.e("dbException", e.toString());
            Toast.makeText(getActivity(), getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Send Volley request to fetch all matches form server API
     *
     *  @param
     *  @return
     */
    protected void sendVolley(){
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                getString(R.string.all_matches_url),
                new JSONObject(),new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mProgressbar.setVisibility(View.INVISIBLE);
                            items.clear();
                            JSONArray fixtures = response.optJSONArray(getString(R.string.api_fixtures));
                            StringBuilder team1,team2,result,link;
                            int api_id;
                            Date date;
                            boolean isFinished, inPlay;
                            JSONObject match,resultObject;

                            //I know it's not a good way to parse JSON by myself but i didn't use
                            // any 3rd party libraries for parsing before and
                            // i don't have time to check them as i got a bad flu and also
                            // i'm a master in parsing JSON by myself
                            for(int i=0;i<fixtures.length();i++){
                                match = fixtures.optJSONObject(i);
                                team1 = new StringBuilder(match.optString(getString(R.string.api_homeTeamName)));
                                team2 = new StringBuilder(match.optString(getString(R.string.api_awayTeamName)));
                                date = dateFormat.parse(match.optString(getString(R.string.api_date)));
                                link = new StringBuilder(match.optJSONObject(getString(R.string.api_links))
                                        .optJSONObject(getString(R.string.api_self)).optString(getString(R.string.api_href)));
                                api_id = Integer.valueOf(link.substring(
                                        link.lastIndexOf("/")+1));
                                isFinished = false;
                                inPlay = false;

                                //Set result to real result if the match is finished or in play,
                                // otherwise set it to its time
                                if(match.getString(getString(R.string.api_status)).
                                        equals(getString(R.string.api_finished)) ||
                                        match.getString(getString(R.string.api_status)).
                                                equals(getString(R.string.api_inplay))){
                                    resultObject = match.optJSONObject(getString(R.string.api_result));
                                    result = new StringBuilder(resultObject.
                                            optString(getString(R.string.api_result_goalsHomeTeam))
                                            + '-' + resultObject.optString(getString
                                            (R.string.api_result_goalsAwayTeam)));
                                    if(match.getString(getString(R.string.api_status)).
                                            equals(getString(R.string.api_finished)))
                                        isFinished = true;
                                    else inPlay = true;
                                }
                                else{
                                    result = new StringBuilder(timeFormat.format(date));
                                }
                                items.add(new Match(team1.toString(),team2.toString(),
                                        result.toString(),date,isFinished, inPlay,api_id));
                                }
                            //Check if the user choose normal or selectable list
                            if(toggle.getValue() == 2) adapter = new ListAdapter(getContext(),
                                    addSections(items), View.VISIBLE,false);
                            else adapter = new ListAdapter(getContext(), addSections(items),
                                    View.GONE, false);
                            listview.setAdapter(adapter);

                            //Set the first viewed element to today or nearest next day
                            listview.setSelection(startIndex);
                        }
                        catch(Exception e){
                            mProgressbar.setVisibility(View.INVISIBLE);
                            Log.e("SendVolleyException", e.toString());
                            Toast.makeText(getActivity(), getString(R.string.unexpected_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), getString(R.string.server_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }){
            //Send the token with the request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String,String>();
                headers.put(getString(R.string.api_auth_header), BuildConfig.token);
                return headers;
            }
        };
        jsonRequest.setTag(REQUEST_TAG);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonRequest);
    }

    /**
     * Get List of matches sectioned correctly
     *
     *  @param ArrayList<Match>
     *  @return ArrayList<Match>
     */
    private ArrayList<Match> addSections(ArrayList<Match> itemList) throws ParseException {

        //Set the starting index to its default value
        startIndex = -1;
        ArrayList tempList = new ArrayList<Match>();
        Match sectionCell;
        Date header = new Date();

        //Get the current date and set its time to zero to get the nearest matches by comparing
        // date only not time to view them first
        Calendar  todayCal =  Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);

        Date today = todayCal.getTime();

        //Loops thorugh the list and add a section before each sectioncell start
        for(int i = 0; i < itemList.size(); i++)
        {
            //If it is the start of a new section we create a new listcell and add it to our list
            // and the adapter will handle the correct inflating
            if(header.compareTo(itemList.get(i).getDateAsSmallDate()) != 0){
                sectionCell = new Match();
                sectionCell.setDate(itemList.get(i).getDate());
                sectionCell.setToSectionHeader();
                tempList.add(sectionCell);
                header = itemList.get(i).getDateAsSmallDate();

                //Compare the date of the header with the current date to get the right position
                // of the first viewed matches
                if(header.compareTo(today) >= 0 && startIndex == -1)
                    startIndex = tempList.size()-1;
            }
            tempList.add(itemList.get(i));
        }

        return tempList;
    }

    /**
     * Update each non-finished favourite match from the server API
     *
     *  @param ArrayList<Match>
     *  @return
     */
    private void updateOfflineMatches(ArrayList<Match> itemList) {

        for(int i = 0; i < itemList.size(); i++)
        {
            //Check if the match is not finished to avoid updating all matches
            if(!itemList.get(i).isFinished()){
                sendVolleyForNewResults(itemList.get(i),i);
            }
        }
    }

    /**
     * Send a volley request to update a match form the server API
     *
     *  @param Match, index of the match in the list
     *  @return
     */
    private void sendVolleyForNewResults(final Match match,final int index){
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                getString(R.string.one_matches_url) + match.getApi_id(),
                new JSONObject(),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fixture = response.optJSONObject(getString(R.string.api_fixture));
                    if(fixture.getString(getString(R.string.api_status)).
                            equals(getString(R.string.api_finished)) ||
                            fixture.getString(getString(R.string.api_status)).
                                    equals(getString(R.string.api_inplay))){
                        JSONObject result = fixture.optJSONObject(getString(R.string.api_result));
                        match.setResult(result.optString(getString(
                                R.string.api_result_goalsHomeTeam)) + '-' +
                                result.optString(getString(R.string.api_result_goalsAwayTeam)));
                        if(fixture.getString(getString(R.string.api_status)).
                                equals(getString(R.string.api_finished)))
                            match.setFinished(true);
                        else match.setInPlay(true);
                    }

                    //Update the match in the database
                    dbHandler.updateMatch(match);

                    //Update the match in the live list to avoid refreshing hte activity
                    items.set(index,match);

                    //Notify the adapter to update itself
                    adapter.notifyDataSetChanged();
                }
                catch(Exception e){
                    Log.e("SendVolleyException", e.toString());
                    Toast.makeText(getActivity(), getString(R.string.unexpected_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), getString(R.string.server_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }){
            //Send the token with the request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String,String>();
                headers.put(getString(R.string.api_auth_header), BuildConfig.token);
                return headers;
            }
        };
        jsonRequest.setTag(REQUEST_TAG);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonRequest);
    }
}

