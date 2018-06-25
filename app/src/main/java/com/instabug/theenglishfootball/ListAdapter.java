package com.instabug.theenglishfootball;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Custom adapter to bind list view
 */

public class ListAdapter extends ArrayAdapter {

    LayoutInflater inflater;
    int favVisibility;
    boolean remove;
    FavDB dbHandler;
    Match match;
    ArrayList items;

    public ListAdapter(Context context, ArrayList items, int favVisibility, boolean remove) {
        super(context, 0, items);
        this.favVisibility = favVisibility;
        this.remove = remove;
        this.items = items;
        dbHandler = new FavDB(getContext());
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        match = (Match) getItem(position);

        //If the cell is a section header we inflate the header layout instead of default layout
        if (match.isSectionHeader()) {
            v = inflater.inflate(R.layout.section_header, null);

            //disable clicking as it's not required
            v.setClickable(false);
            TextView header = (TextView) v.findViewById(R.id.section_header);

            //Set the header text to the Date of the matches
            header.setText(match.getDateAsSmallDateString());
        } else {
            v = inflater.inflate(R.layout.item_cell, null);
            TextView team1 = (TextView) v.findViewById(R.id.itemCell_team1);
            TextView team2 = (TextView) v.findViewById(R.id.itemCell_team2);
            TextView result = (TextView) v.findViewById(R.id.itemCell_result);

            //Image button for handling add to/remove from favourite
            ImageButton fav = (ImageButton) v.findViewById(R.id.itemCell_fav);

            team1.setText(match.getTeam1());
            team2.setText(match.getTeam2());
            result.setText(match.getResult());

            //Coloring the result with different color when the match is in play
            if(match.isInPlay()) result.setTextColor(Color.BLUE);

            //Hide favourite button if the user select the normal list
            fav.setVisibility(this.favVisibility);

            //Change the image of favourite button to 'delete' image when the user
            // select favourite list
            if(remove) fav.setImageResource(android.R.drawable.ic_delete);

            //Giving the favourite button the position as a tag to action on right cell
            fav.setTag(Integer.valueOf(position));
            fav.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Match match = (Match)getItem(position);

                    //If the user want to remove a match from his favourite list, then we remove it
                    //from the database and from the live list and notify the adapter to avoid
                    // refreshing the activity
                    if(remove) {
                        dbHandler.deleteMatchByID(match.getId());

                        //Handle the removing process form the live list to remove the header if
                        // the deleted match is the last one in the section
                        if(((Match)items.get(position-1)).isSectionHeader() &&
                                (items.size()-1 == position ||
                                ((Match)items.get(position+1)).isSectionHeader()))
                        {
                            items.remove(position-1);
                            items.remove(position-1);
                        }
                        else items.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(getContext(), "Removed",
                                Toast.LENGTH_SHORT).show();
                    }
                    //If the user added match to his favourite list, then we add it to the database
                    else {
                        dbHandler.addMatch(match);
                        Toast.makeText(getContext(), "Added to Favourite",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return v;
    }
}