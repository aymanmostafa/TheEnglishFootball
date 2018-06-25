package com.instabug.theenglishfootball;

import org.junit.runner.RunWith;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Test class for FavDB
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19, packageName = "com.instabug.theenglishfootball")
public class FavDBTest {

    private FavDB favdb;

    /**
     * Set up the instance before testing
     */
    @Before
    public void setUp(){
        favdb = new FavDB(RuntimeEnvironment.application);
    }

    /**
     * free the instance after testing
     */
    @After
    public void finish() {
        favdb.close();
    }

    /**
     * Test the initialization of the instance
     */
    @Test
    public void testPreConditions() {
        assertNotNull(favdb);
    }

    /**
     * Test adding match to database and saved right
     */
    @Test
    public void addMatchTest() throws Exception {
        Match m = createMatch(0);
        favdb.addMatch(m);
        List<Match> matches = favdb.getAllMatchs();

        assertEquals(matches.size(), 1);

        assertEquals(matches.get(0).getTeam1(), m.getTeam1());
        assertEquals(matches.get(0).getTeam2(), m.getTeam2());
        assertEquals(matches.get(0).getDate(), m.getDate());
        assertEquals(matches.get(0).getResult(), m.getResult());
        assertEquals(matches.get(0).isFinished(),m.isFinished());
        assertEquals(matches.get(0).isInPlay(), m.isInPlay());
        assertEquals(matches.get(0).getApi_id(), m.getApi_id());
    }

    /**
     * Test deleting an existing match by its ID
     */
    @Test
    public void deleteMatchByIDTest() throws Exception{
        favdb.addMatch(createMatch(0));
        List<Match> matches = favdb.getAllMatchs();

        assertEquals(matches.size(), 1);

        favdb.deleteMatchByID(matches.get(0).getId());
        matches = favdb.getAllMatchs();

        assertEquals(matches.size(), 0);
    }

    /**
     * Test getting all saved matches
     */
    @Test
    public void getAllMatchsTest() throws Exception{
        favdb.addMatch(createMatch(0));
        favdb.addMatch(createMatch(1));

        List<Match> matches = favdb.getAllMatchs();

        assertEquals(matches.size(), 2);
    }

    /**
     * Test updating existing match
     */
    @Test
    public void updateMatchByIDTest() throws Exception{
        Match m = createMatch(0);
        favdb.addMatch(m);

        List<Match> matches = favdb.getAllMatchs();

        m = matches.get(0);
        m.setTeam1("new Team");

        favdb.updateMatch(m);

        matches = favdb.getAllMatchs();

        assertEquals(matches.get(0).getTeam1(), m.getTeam1());
        assertEquals(matches.get(0).getTeam2(), m.getTeam2());
        assertEquals(matches.get(0).getDate(), m.getDate());
        assertEquals(matches.get(0).getResult(), m.getResult());
        assertEquals(matches.get(0).isFinished(),m.isFinished());
        assertEquals(matches.get(0).isInPlay(), m.isInPlay());
        assertEquals(matches.get(0).getApi_id(), m.getApi_id());
    }

    /**
     * Create match to use it in testing
     *
     *  @param index to have different api_id
     *  @return match
     */
    public Match createMatch(int index){

        //create date with zero hours
        Calendar todayCal =  Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);

        Date today = todayCal.getTime();
        return new Match("team1","team2","0-0",today,true,
                false,123+index);
    }

}
