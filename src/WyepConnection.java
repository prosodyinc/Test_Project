import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WyepConnection {
	//Used in test. Will return the last 20 songs, including "On Now", that were played on Wyep today.
	public static final String testURL = "https://api.composer.nprstations.org/v1/widget/50e451b6a93e91ee0a00028e/tracks?format=json&limit=20&hide_amazon=false&hide_itunes=false&hide_arkiv=false&share_format=false";
	
	public static final String playingNowURL = "https://api.composer.nprstations.org/v1/widget/50e451b6a93e91ee0a00028e/tracks?format=json&limit=1&hide_amazon=false&hide_itunes=false&hide_arkiv=false&share_format=false";
	
	public static final String URL_ROOT = "https://api.composer.nprstations.org/v1/widget/";
	public static final String UCS = "50e451b6a93e91ee0a00028e";
	public static final String FORMAT = "json";
	public static final String LIMIT = "1";
	public static final String URL_PARAMETERS = "%s/tracks?format=%s&datestamp=%s&limit=%s&hide_amazon=false&hide_itunes=false&hide_arkiv=false&share_format=false";
	
	public static Scanner reader = new Scanner(System.in);
	public static void main(String [] args) throws IOException, JSONException{
		System.out.println("Welcome to WYEP Alexa Proof of Concept!");
		menu();
		String request = reader.nextLine();
		while (!request.equals("4")){
			if (request.equals("1")){
				findSong();
			} else if (request.equals("2")){
				getPlayingNow();
			} else if (request.equals("3")){
				test();
			} else {
				System.out.println("Invalid option.");
			}
			menu();
			request = reader.nextLine();
		}
		System.out.println("Goodbye!");
		reader.close();
	}
	
	private static void menu(){
		System.out.println("Menu:");
		System.out.println("1: See what song was playing on a particular date and time");
		System.out.println("2: See what song is currently playing");
		System.out.println("3: See what the last 20 songs to play were");
		System.out.println("4: quit");
	}
	/**
	 * Helper method to ensure the time is in the correct format.
	 * The format of the time must be <HH:MM:SS> for the URL used in the API call.
	 * @param tIn The time String to be validated
	 * @return Whether or not the time is valid.
	 */
	private static boolean validateTime(String tIn){
		if (tIn.length() != 8 || tIn.charAt(2) != ':' || tIn.charAt(5) != ':'){
			return false;
		}
		try {
			int hour = Integer.parseInt(tIn.substring(0, 2));
			int minute = Integer.parseInt(tIn.substring(3, 5));
			int seconds = Integer.parseInt(tIn.substring(6, 8));
			
			return (hour >= 0 && hour < 24 && minute >= 0 && minute < 60 && seconds >= 0 && seconds < 60);
		} catch (NumberFormatException e){
			return false;
		}
	}
	
	/**
	 * Helper method to ensure that the day and month provided make sense.
	 * @param y The year
	 * @param m The month
	 * @param d The day
	 * @return True if a logical date is provided.
	 */
	private static boolean validDay(int y, int m, int d){
		boolean leap;
		if (y % 4 == 0){
			if (y % 100 == 0){
				if (y % 400 == 0){
					leap = true;
				} else {
					leap = false;
				}
			} else {
				leap = true;
			}
		} else {
			leap = false;
		}
		
		if (d < 1 || m < 1 || m > 12){
			return false;
		}
		
		if (m == 1 || m == 3 || m == 5 || m == 7
				|| m == 8 || m == 10 || m == 12){
			return d <= 31;
		}
		else if (m == 4 || m == 6 || m == 9 || m == 11){
			return d <= 30;
		}
		
		// if it's a leap year, February (2) is out of 29 days.
		else if (m == 2 && leap){
			return d <= 29;
		}
		
		else{
			return d <= 28;
		}
	}
	
	/**
	 * Helper method to ensure the requested date is both in the correct format and is not in the future.
	 * The format of the date must be <YYYY-MM-DD> for the URL used in the API call.
	 * @param dIn The date String to be validated
	 * @param time The time obtained prior
	 * @return Whether or not the date is valid.
	 */
	private static boolean validateDate(String dIn, String time){
		Calendar rightNow = Calendar.getInstance();
		Calendar songPlaying = Calendar.getInstance();
		songPlaying.setLenient(false);
		if (dIn.length() != 10 || dIn.charAt(4) != '-' || dIn.charAt(7) != '-'){
			return false;
		}
		try {
			int year = Integer.parseInt(dIn.substring(0, 4));
			int month = Integer.parseInt(dIn.substring(5, 7));
			int day = Integer.parseInt(dIn.substring(8));
			if (!validDay(year, month, day)){
				return false;
			}
			songPlaying.set(Calendar.YEAR, year);
			songPlaying.set(Calendar.MONTH, month - 1);
			songPlaying.set(Calendar.DAY_OF_MONTH, day);
			songPlaying.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0,2)));
			songPlaying.set(Calendar.MINUTE, Integer.parseInt(time.substring(3,5)));
			if (songPlaying.before(rightNow)){
				return true;
			}
			System.out.println("The time you have provided has not happened yet.");
			System.out.println(songPlaying.getTime() + "---" + rightNow.getTime());
			return false;
		} catch (NumberFormatException e){
			return false;
		}
	}
	
	/**
	 * Get the current song playing on WYEP, if there is one in fact playing.
	 * @throws IOException
	 * @throws JSONException
	 */
	public static void getPlayingNow() throws IOException, JSONException{
		JSONObject json = JsonUtils.readJsonFromUrl(playingNowURL);
		try {
			JSONObject playingNow = json.getJSONObject("onNow").getJSONObject("song");
			Song nowSong = Song.createFromJson(playingNow);
			System.out.println("Playing now:");
			System.out.println(nowSong + "\n");
		} catch (JSONException e){
			System.out.println("There are currently no songs playing.");
		}
	}
	
	/**
	 * This is a more realistic use case. This method will return the song playing on WYEP at a given date and time.
	 * @throws IOException
	 * @throws JSONException
	 */
	public static void findSong() throws IOException, JSONException{
		// Date and time validation
		String time = "";
		String date = "";
		
		System.out.println("Enter the time your song was playing: (HH:mm:ss)");
		time = reader.nextLine();
		while (!validateTime(time)){
			System.out.println("Time is not in correct format. Please try again.");
			System.out.println("Enter the time your song was playing: (HH:mm:ss)");
			time = reader.nextLine();
		}
		
		System.out.println("Enter the date your song was playing: (YYYY-MM-DD)");
		date = reader.nextLine();
		while(!validateDate(date, time)){
			System.out.println("Date is not in correct format. Please try again.");
			System.out.println("Enter the date your song was playing: (YYYY-MM-DD)");
			date = reader.nextLine();
		}
		
		
		Time requestedTime = Time.fromFormattedTime(time);
		
		String dateStamp = date + "T" + time;
		
		//URL used to connect to the Composer API
		String fullURL = URL_ROOT + String.format(URL_PARAMETERS, UCS, FORMAT, dateStamp, LIMIT);
		JSONObject json = JsonUtils.readJsonFromUrl(fullURL);
		JSONArray result = json.getJSONObject("tracklist").getJSONArray("results");
		
		//the one and only song returned in results is the song closest to the requested date and time
		JSONObject jsonSong = result.getJSONObject(0).getJSONObject("song");
		Song song = Song.createFromJson(jsonSong);
		
		//In some instances, if the requested time is either to early or too late, no song will be playing at that time.
		//The following block of code is to alert the user of that.
		String feedback = "";
		if (requestedTime.compareTo(song.getStartTimeObject()) < 0){
			feedback = String.format("The time you have requested may be too early, as there were no songs playing at %s. Here is the song"
					+ " that played closest to your request:", requestedTime);
		} else if (requestedTime.compareTo(song.getEndTimeObject()) > 0){
			feedback = String.format("The time you have requested may be too late, as there were no songs playing at %s. Here is the song"
					+ " that played closest to your request:", requestedTime);
		} else {
			 feedback = "At " + time + ", this song was playing:";
		}
		System.out.println(feedback + "\n" + song);
		
	}
	
	/**
	 * This was just to get started. It makes a request to the Composer API with WYEP's ucs and with no date specified,
	 * giving you back the 20 most recent songs played.
	 * @throws IOException
	 * @throws JSONException
	 */
	public static void test() throws IOException, JSONException{
		List<Song> playlist = new ArrayList<Song>();
		JSONObject json = JsonUtils.readJsonFromUrl(testURL);
		JSONArray results = json.getJSONObject("tracklist").getJSONArray("results");
		for (int i = 0; i < results.length(); i++){
			JSONObject jsonSong = results.getJSONObject(i).getJSONObject("song");
			Song song = Song.createFromJson(jsonSong);
			playlist.add(song);
		}
		
		try {
			//if there is no song playing when this is called, onNow will have no song attribute.
			JSONObject playingNow = json.getJSONObject("onNow").getJSONObject("song");
			Song nowSong = Song.createFromJson(playingNow);
			System.out.println("Playing now:");
			System.out.println(nowSong + "\n");
		} catch (JSONException e){
			System.out.println("There are no songs currently playing. Here are the last 20 songs that played previously:");
		}
		for (Song song : playlist){
			System.out.println(song);
		}
	}
}
