package WeatherWhisper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import org.json.*;

// Stores all of the Weather Data for a single location
public class WeatherDataAPI {
	
	private JSONObject weatherData;
	private Object address;
	private Object currentTemp;
	private Object currentSkyConditions;
	private Object currentWindSpeed;
	private Object currentWindDirection;
	private ArrayList<Object> hourlyTemps;
	private ArrayList<Object> hourlyWindSpeeds;
	private ArrayList<Object> hourlyPrecipProbs;
	private ArrayList<Object> dailyMaxTemps;
	private ArrayList<Object> dailyMinTemps;
	private ZonedDateTime time;

	// Constructs all relevant weather data contained in given JSON file
	public WeatherDataAPI (JSONObject weatherData){
		this.weatherData = weatherData;
		
		hourlyTemps = new ArrayList<Object>();
		hourlyWindSpeeds = new ArrayList<Object>();
		hourlyPrecipProbs = new ArrayList<Object>();
		dailyMaxTemps = new ArrayList<Object>();
		dailyMinTemps = new ArrayList<Object>();
		
		setAddress();
		setCurrentTemp();
		setCurrentSkyConditions();
		setCurrentWindSpeed();
		setCurrentWindDirection();
		setHourlyTemps();
		setHourlyWindSpeeds();
		setHourlyPrecipProbs();
		setDailyMaxTemps();
		setDailyMinTemps();
		setTime();
	}
	
	// Set address of city according to resolved address determined by Visual Crossing API
	// (e.g., "Conway,,,,, Arkansas" resolves to "Conway, AR, United States" by API itself
	public void setAddress() {
		address = weatherData.get("resolvedAddress");
	}
	
	public Object getAddress() {
		return address;
	}
	
	// Sets currentTemp based on "temp" field of the "currentConditions" JSONObject
	public void setCurrentTemp() {
		currentTemp = weatherData.getJSONObject("currentConditions").get("temp");
	}
	
	public Object getCurrentTemp() {
		return currentTemp;
	}
	
	// Sets currentSkyConditions based on "conditions" field of the "currentConditions" JSONObject
	public void setCurrentSkyConditions() {
		currentSkyConditions = weatherData.getJSONObject("currentConditions").get("conditions");
	}
	
	public Object getCurrentSkyConditions() {
		return currentSkyConditions;
	}
	
	// Sets currentWindSpeed based on "windspeed" field of the "currentConditions" JSONObject
	public void setCurrentWindSpeed() {
		currentWindSpeed = weatherData.getJSONObject("currentConditions").get("windspeed");
	}
	
	public Object getCurrentWindSpeed() {
		return currentWindSpeed;
	}
	
	// Sets currentWindDirection based on "winddir" field of the "currentConditions" JSONObject
	public void setCurrentWindDirection() {
		currentWindDirection = weatherData.getJSONObject("currentConditions").get("winddir");
	}
	
	public Object getCurrentWindDirection() {
		return currentWindDirection;
	}
	
	// For each JSONOBject in the "hours" JSONArray (i.e., each hour) of the first JSONObject in the "days" JSONArray (i.e., today), 
	// appends the value in the "temp" field to our ArrayList of hourlyTemps. Index 0 represents midnight, not the current hour.
	public void setHourlyTemps() {
		hourlyTemps.clear();
		
		for(int hour = 0; hour < 24; hour++) {
			hourlyTemps.add(weatherData.getJSONArray("days").getJSONObject(0).getJSONArray("hours").getJSONObject(hour).get("temp"));
		}
	}
	
	public ArrayList<Object> getHourlyTemps() {
		return hourlyTemps;
	}
	
	// For each JSONOBject in the "hours" JSONArray (i.e., each hour) of the first JSONObject in the "days" JSONArray (i.e., today), 
	// appends the value in the "windspeed" field to our ArrayList of hourlyWindSpeeds. Index 0 represents midnight, not the current hour.
	public void setHourlyWindSpeeds() {
		hourlyWindSpeeds.clear();
		
		for(int hour = 0; hour < 24; hour++) {
			hourlyWindSpeeds.add(weatherData.getJSONArray("days").getJSONObject(0).getJSONArray("hours").getJSONObject(hour).get("windspeed"));
		}
	}
	
	public ArrayList<Object> getHourlyWindSpeeds() {
		return hourlyWindSpeeds;
	}
	
	// For each JSONOBject in the "hours" JSONArray (i.e., each hour) of the first JSONObject in the "days" JSONArray (i.e., today), 
	// appends the value in the "precipprob" field to our ArrayList of hourlyPrecipProbs. Index 0 represents midnight, not the current hour.
	public void setHourlyPrecipProbs() {
		hourlyPrecipProbs.clear();
		
		for(int hour = 0; hour < 24; hour++) {
			hourlyPrecipProbs.add(weatherData.getJSONArray("days").getJSONObject(0).getJSONArray("hours").getJSONObject(hour).get("precipprob"));
		}
	}
	
	public ArrayList<Object> getHourlyPrecipProbs() {
		return hourlyPrecipProbs;
	}
	
	// For each JSONObject in the "days" JSONArray (i.e., for each day), 
	// appends the value in the "tempmax" field to our ArrayList of dailyMaxTemps. Index 0 is today's maximum temp.
	public void setDailyMaxTemps() {
		dailyMaxTemps.clear();
		
		for(int day = 0; day < 15; day++) {
			dailyMaxTemps.add(weatherData.getJSONArray("days").getJSONObject(day).get("tempmax"));
		}
	}
	
	public ArrayList<Object> getDailyMaxTemps() {
		return dailyMaxTemps;
	}
	
	// For each JSONObject in the "days" JSONArray (i.e., for each day), 
	// appends the value in the "tempmin" field to our ArrayList of dailyMinTemps. Index 0 is today's minimum temp.
	public void setDailyMinTemps() {
		dailyMinTemps.clear();
		
		for(int day = 0; day < 15; day++) {
			dailyMinTemps.add(weatherData.getJSONArray("days").getJSONObject(day).get("tempmin"));
		}
	}
	
	public ArrayList<Object> getDailyMinTemps() {
		return dailyMinTemps;
	}
	
	// Determines the local time of the city whose weather is being stored by converting our system time to the city's local time via
	// timezones. We ignore the "datetime" and "datetimeEpoch" JSON variables because they only update in intervals of 15 minutes while 
	// also not always showing the most recent 15-minute marker (likely due to the free pricing plan of the Visual Crossing API), which
	// can be problematic if the city's time is 3:08pm and the JSON is storing 2:45pm, for example.
	public void setTime() {
		// Determine weather-location's time zone
		String locationTimeZone = (String) weatherData.get("timezone");
		ZoneId locationZone = ZoneId.of(locationTimeZone);
		
		// Determine system's time zone
		LocalDateTime systemTime = LocalDateTime.now();
		ZoneId systemZone = ZoneId.systemDefault();
		
		// Convert system's local time to weather-location's local time
		ZonedDateTime currentZonedDateTime = ZonedDateTime.of(systemTime, systemZone);
		ZonedDateTime locationZonedDateTime = currentZonedDateTime.withZoneSameInstant(locationZone);
		
		time = locationZonedDateTime;
		
		/* The following methods of the ZonedDateTime class (which we use for the 'time' variable) can be used to show relevant data
		System.out.println(time.toLocalTime());	// output example: "15:48:53.917602600" (Hour: Minutes: Seconds w/ precision)
		
		System.out.println(time.getHour());		// output example: "15"
		// Note: The WeatherDataAPI class (this class) has a getHour() for the sake of convenience
		System.out.println(time.getMinute());	// output example: "48"
		System.out.println(time.getHour()%12 + ":" + time.getMinute());		// output example: "3:52"
		
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		System.out.println(time.format(formatter));	// output example: "2023-12-01 15:48:53"
		
		//The DateTimeFormatter.ofPattern method above can be used to produce different formats (e.g., "HH:mm" -> Hour:minute)
		*/ 
	}
	
	public ZonedDateTime getTime() {
		return time;
	}
	
	public int getHour() {
		return time.getHour();
	}
	
}
