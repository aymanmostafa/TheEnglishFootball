package com.instabug.theenglishfootball;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Match class to hold all match details
 */

public class Match implements Comparable<Match>{
    private String team1;
    private String team2;
    private String result;
    private boolean isSectionHeader;
    private boolean isFinished;
    private boolean inPlay;
    private Date date;
    private int id;
    private int api_id;
    private SimpleDateFormat formatter;

    public Match() {

    }

    public Match(String team1, String team2, String result, Date date, boolean isFinished,
                 boolean inPlay, int api_id) {
        this.team1 = team1;
        this.team2 = team2;
        this.result = result;
        this.date = date;
        this.isSectionHeader = false;
        this.isFinished = isFinished;
        this.inPlay = inPlay;
        this.api_id = api_id;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isSectionHeader() {
        return isSectionHeader;
    }

    public void setSectionHeader(boolean sectionHeader) {
        isSectionHeader = sectionHeader;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public boolean isInPlay() {
        return inPlay;
    }

    public void setInPlay(boolean inPlay) {
        this.inPlay = inPlay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApi_id() {
        return api_id;
    }

    public void setApi_id(int api_id) {
        this.api_id = api_id;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Get the whole date of a match as a string
     *
     *  @param
     *  @return whole date as a string in "yyyy-MM-dd'T'HH:mm:ss'Z'" format
     */
    public String getDateAsString() {
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return formatter.format(date);
    }

    /**
     * Get the time of a match as a string
     *
     *  @param
     *  @return time as a string in "HH:mm" format
     */
    public String getDateAsTimeString() {
        formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

    /**
     * Get the date of a match
     *
     *  @param
     *  @return date in "yyyy-MM-dd" format
     */
    public Date getDateAsSmallDate() throws ParseException {
        formatter = new SimpleDateFormat("yyyy-MM-dd");

        return formatter.parse(getDateAsString());
    }

    /**
     * Get the date of a match
     *
     *  @param
     *  @return date as a string in "yyyy-MM-dd" format
     */
    public String getDateAsSmallDateString() {
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Set the date of a match
     *
     *  @param date as string
     *  @return
     */
    public void setDate(String date) throws ParseException {
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        this.date = formatter.parse(date);
    }

    public void setToSectionHeader()
    {
        isSectionHeader = true;
    }

    /**
     * Compare between two matches by their date to sort them correctly
     *
     *  @param Match
     *  @return compare result
     */
    @Override
    public int compareTo(Match other) {
        return this.date.compareTo(other.date);
    }

}
