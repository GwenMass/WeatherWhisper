package WeatherWhisper;

import java.io.IOException;

public class WeatherApp {
	
	public static void main (String args[]) throws IOException, InterruptedException {
		// For demonstration:
		String inputLocation = "Conway,,,,,, Arkansas";	//intentionally weird to show how API fixes it
		WeatherDataAPI weatherDataCity1 = new WeatherDataAPI(WeatherService.fetchWeatherData(inputLocation));
		
		// Also for demonstration:
		System.out.println("Address: " + weatherDataCity1.getAddress());
		System.out.println("Current Temp: " + weatherDataCity1.getCurrentTemp());
		System.out.println("Current Conditions: " + weatherDataCity1.getCurrentConditions());
		System.out.println("TempMax (today): " + weatherDataCity1.getDailyMaxTemps().get(0));
		System.out.println("TempMin (today): " + weatherDataCity1.getDailyMinTemps().get(0));
		System.out.println("Current Windspeed: " + weatherDataCity1.getCurrentWindSpeed());
		System.out.println("Current WindDir: " + weatherDataCity1.getCurrentWindDirection());
		System.out.println("Hourly Temps: " + weatherDataCity1.getHourlyTemps());
		System.out.println("Hourly Windspeeds: " + weatherDataCity1.getHourlyWindSpeeds());
		System.out.println("Hourly PrecipProbs: " + weatherDataCity1.getHourlyPrecipProbs());
		System.out.println("Daily Max Temps: " + weatherDataCity1.getDailyMaxTemps());
		System.out.println("Daily Min Temps: " + weatherDataCity1.getDailyMinTemps());
		
	}
}
