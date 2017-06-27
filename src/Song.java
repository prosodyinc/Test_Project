import org.json.JSONException;
import org.json.JSONObject;


/**
 * Wrapper class for a song returned by queries to the Composer API. 
 * @author adambarson
 *
 */
public class Song {
	private String start;
	private String end;
	private String date;
	private String artistName;
	private String trackName;
	private String collectionName;
	
	private Time startTimeObject;
	private Time endTimeObject;
	public static Song createFromJson(JSONObject jsonSong) throws JSONException{
		Song song = new Song();
		
		song.setArtistName(jsonSong.getString("artistName"));
		song.setTrackName(jsonSong.getString("trackName"));
		
		String _start_time = jsonSong.getString("_start_time");
		song.setDate(_start_time.substring(0, 10));
		song.setStart(_start_time.substring(11));
		Time startTimeObject = Time.fromFormattedTime(song.getStart());
		song.setStartTimeObject(startTimeObject);
		
		String _end_time = jsonSong.getString("_end_time");
		song.setEnd(_end_time.substring(11));
		Time endTimeObject = Time.fromFormattedTime(song.getEnd());
		song.setEndTimeObject(endTimeObject);
		
		song.setCollectionName(jsonSong.getString("collectionName"));
		
		return song;
	}
	
	public String getArtistName() {
		return artistName;
	}
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	public String getTrackName() {
		return trackName;
	}
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}
	
	public String getCollectionName() {
		return collectionName;
	}
	
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	public String toString(){
		return String.format("|Artist|: %s |Track Name|: %s |Album|: %s |Start|: %s |End|: %s |Date|: %s", 
				artistName, trackName, collectionName, start, end, date);
	}

	public Time getStartTimeObject() {
		return startTimeObject;
	}

	public void setStartTimeObject(Time startTimeObject) {
		this.startTimeObject = startTimeObject;
	}

	public Time getEndTimeObject() {
		return endTimeObject;
	}

	public void setEndTimeObject(Time endTimeObject) {
		this.endTimeObject = endTimeObject;
	}

	

	
}
