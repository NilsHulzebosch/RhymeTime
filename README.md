# RhymeTime

## Assignment info
Name: Nils Hulzebosch   
Student ID: 10749411   
Assignment: pset6 (final app)   
Date: December 16th, 2016.   
App name: RhymeTime   
Info: This android app is my final app for the course Native App Studio from the University of Amsterdam.   


## App info
RhymeTime is a simple game where players have to rhyme as much words as possible within a given time.
The game has several stages (easy, medium, hard & insane). These influence the difficulty of the word you have to rhyme with as well as the time you have to rhyme.

The gameplay works like this: pick a random word based on chosen difficulty, get all rhyming words and their scores using the Datamuse API, during the game match user input with this list of words and give them correct score etc.
When a game is finished, save their score along with some other info to a FireBase database. When logged in with Google, your Google (first) name will be added to your score. When not logged in, your score will be added under the name 'anonymous'.

A player can also earn achievements after each game when all the restrictions are met (for example rhyming a certain amount of words). Unlocking is shown using a customized Toast and they are stored using SharedPreferences.

Players can also see the global leaderboard: the scores of all players, including their own, in one table, arranged from highest score to lowest score. Other info in this table is amount of rhymed words and difficulty of the stage. The leaderboard is created by retrieving all scores from FireBase, sorting them and putting them in a custom Adapater.


## Main requirements for this app:
- The app concept should be centered around a live open data API.   
Check: the main gameplay is based on the Datamuse API, used to find rhyme words.

- External data should be retrieved using HTTP-requests.   
Check: requesting all rhymewords.

- Very small user data should be persisted on the phone.   
Check: username (String) and achievements (booleans) are stored using SharedPreferences.

- Firebase should be used to save other user data.   
Check: FireBase Authentication and Database are integrated in the app to save scores.

- Not all data from the API needs to be saved on the phone once retrieved, it may be requested more than once.   
Check: rhyme data is only used in a level (within one activity), so it only needs to be requested once.

- Code should be organized well.   
Check: every activity has its own purpose (separation of concerns).

- Code should be documented well (comments as well as READMEs).   
Check: every method has at least one comment.


## Future work
Due to a lack of time, I skipped some functionality I first thought would be nice to add.
Of course this is a demo version, because we only had 2 weeks. But in the future the app can have extra functions like:
- random stage (I tried this, but the API returned strange words without rhymes, so I dropped this idea)
- more options in the leaderboard like sorting on score, sorting on username, etc.
- more achievements
- unique username
- challenging a friend
