package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.ZonedDateTime;
import WeatherWhisper.WeatherDataAPI;

public class FXMLController {
		
	//tags for injection
	@FXML ArrayList<Text> dayLabel;
	@FXML ArrayList<Text> hour;
	@FXML ArrayList<Text> dayBound;
	@FXML ArrayList<Text> hourFahrenheit;
	@FXML ArrayList<Text> hourCelsius;
	@FXML ArrayList<Text> hourWind;
	@FXML ArrayList<Text> hourPrecip;
	@FXML Label address;
	@FXML Text currentTemp;
	@FXML Label weatherDesc;
	@FXML Text compassText;
	@FXML TextField searchBox;
	@FXML Button searchButton;
		
	// Initializes values with placeholders for startup
	@FXML private void startup() 
	{
		
	}

	// Read data from the given WeatherDataAPI object containing data from an API request for a particular city's weather
	@FXML public void initialize(WeatherDataAPI weather) 
	{
		searchButton.setOnAction(this::searchHandler);
		
		ZonedDateTime date = weather.getTime();
		int currentHour = weather.getHour();
		int iterator = 1;
	
		//initialize tags for header info
		address.setText(weather.getAddress().toString());
		currentTemp.setText(weather.getCurrentTemp().toString() + "°F");
		weatherDesc.setText(weather.getCurrentSkyConditions().toString());
		
		//initialize tag for compass info
		compassText.setText("Wind Direction: " + directionToString(weather.getCurrentWindDirection()) + " Speed: " + weather.getCurrentWindSpeed() + " MPH");
		
		//place dates into ArrayList
		iterator = 1;
		for (Text text : dayLabel)
		{
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd");
			text.setText(date.plusDays(iterator).getDayOfWeek() + " " + dateFormat.format(date.plusDays(iterator)));
			iterator++;
		}
		
		//initialize tags for hours 
		iterator = 1;
		for (Text text : hour)
		{
			text.setText(timeToString((currentHour + iterator++) % 24));
		}
		
		//initialize tags for daily temperature bounds
		iterator = 0;
		for (Text text : dayBound)
		{
			text.setText((weather.getDailyMaxTemps().get(iterator) + "°F/" + weather.getDailyMinTemps().get(iterator) + "°F"));
			iterator++;
		}
		
		//initialize tags for hourly temperatures in Fahrenheit
		iterator = 0;
		for (Text text : hourFahrenheit)
		{
			text.setText(weather.getHourlyTemps().get(iterator++) + "°F");
		}
		
		//initialize tags for hour temperatures in Celsius
		iterator = 0;
		for (Text text : hourCelsius)
		{
			text.setText(toCelsius(weather.getHourlyTemps().get(iterator++)));
		}
		
		//initialize  tags for hourly winds
		iterator = 0;
		for (Text text : hourWind)
		{
			text.setText(weather.getHourlyWindSpeeds().get(iterator++) + "MPH");
		}
		
		//initialize tags for hourly precipitation percentage
		iterator = 0;
		for (Text text : hourPrecip)
		{
			text.setText(weather.getHourlyPrecipProbs().get(iterator++) + "%");
		}
		
	}
	
	//Handler for button reload
	
	private String directionToString(Object direction)
	{
		var dir = Double.parseDouble(direction.toString());
		if (dir >= 75 && dir <=105)
			return "E";
		if (dir > 105 && dir < 165)
			return "SE";
		if (dir >=165 && dir <= 195)
			return "S";
		if (dir > 195 && dir < 255)
			return "SW";
		if (dir >= 255 && dir <= 285)
			return "W";
		if (dir > 285 && dir < 345)
			return "NW";
		if ((dir >= 345 && dir <=360) || dir >= 0 && dir <= 15)
			return "N";
		if (dir > 15 && dir < 75)
			return "NE";
		else return "Problem designating direction...";
	}
	
	//return string based on AM or PM of time
	private String timeToString(int hour)
	{
		if (hour == 0)
				return "12 AM";
		if (hour == 12)
			return "12 PM";
		if (hour > 12)
		{
			return hour - 12 + "PM";
		}
		else return hour + "AM";
	}
	
	private String toCelsius(Object fahrenheit)
	{
		DecimalFormat dFormat = new DecimalFormat("#.#");
		return (dFormat.format((Double.parseDouble(fahrenheit.toString())-32)*5/9))+"°C";
	}
	
	private void searchHandler(ActionEvent e)
	{
		String loc = searchBox.getText();
		WeatherDataAPI searchLoc = new WeatherDataAPI(loc);
		//searchLoc.updateWeatherData();	//unnecessary I think, as updateWeatherData() is called inside WeatherDataAPI constructor anyways
		
		// Only initialize location searched by user if API recognized location and returned valid JSON
		if(searchLoc.isValid())
			initialize(searchLoc);
		// else indicate invalid location to user somehow perhaps?
	}
	
}
