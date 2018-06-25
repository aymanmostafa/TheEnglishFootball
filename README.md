# The English Football

Android app that displays information about The English Premier League.

## Features

1-View all matches of current session in local time zone.

2-All matches are sectioned by date.

3-First visible section is the current day or next.

4-Mark matches as favorite and view them offline.

5-Favorite matches updated if they haven't finished yet.

6-Remove any match from the favorite list without refreshing the activity.

7-Get the current result if the match is in play with different color.

##Pros and Cons

###Pros

1-Use Volley library to handle requests fast.

2-Save favorite to local SQLlite database to view them offline.

3-Update offline matches that haven't finished only to avoid non-useful requests.

4-Updating offline matches happened in background to view the list first and update them of front of the user smoothly. 

4-Convert matches time to the local time zone.

5-Handle removing favorite matches with removing sections.

6-Views are handled to work in any orientation.

7-Reduce memory by inflate section or normal match instead of use on view and hide the section most of the time.

8-Fully documented code.

9-Unit test for database handler.

###Cons

1-Parsing the JSON by myself because i didn't use any 3rd party libraries for parsing before and i don't have time to check them as i got a bad flu and a lot of tasks and also I'm a master in parsing JSON by myself.

2-Testing Coverage is too low, because i write unit test for database handler only due to i haven't a lot of experience in writing unit test for Android apps and i don't have time to check them as i got a bad flu and a lot of tasks

##Included

1-The whole project.

2-APK.

3-ScreenShots.