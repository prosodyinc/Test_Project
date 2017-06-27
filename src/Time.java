
/**
 * A class used to encapsulate time data, to make it easier to compare songs.
 * @author adambarson
 *
 */
public class Time implements Comparable<Time> {
	private int hour;
	private int minute;
	private int second;
	
	public Time(){
		
	}
	public Time(int hour, int minute, int second){
		this.setHour(hour);
		this.setMinute(minute);
		this.setSecond(second);
	}
	
	public static Time fromFormattedTime(String tIn){
		Time time = new Time();
		time.setHour(Integer.parseInt(tIn.substring(0,2)));
		time.setMinute(Integer.parseInt(tIn.substring(3,5)));
		time.setSecond(Integer.parseInt(tIn.substring(6)));
		return time;
	}
	
	
	public int compareTo(Time time){
		if (getHour() > time.getHour()){
			return 1;
		} else if (getHour() < time.getHour()){
			return -1;
		} else {
			if (getMinute() > time.getMinute()){
				return 1;
			} else if (getMinute() < time.getMinute()){
				return -1;
			} else {
				if (getSecond() > time.getSecond()){
					return 1;
				} else if (getSecond() < time.getSecond()){
					return -1;
				} else {
					return 0;
				}
			}
		}
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}
	
	public String toString(){
		String h = hour+"";
		if (hour < 10){
			h = "0" + h;
		}
		String m = minute+"";
		if (minute < 10){
			m = "0" + m;
		}
		String s = second+"";
		if (second < 10){
			s = "0" + second;
		}
		return String.format("%s:%s:%s", h, m, s);
	}
	
}
