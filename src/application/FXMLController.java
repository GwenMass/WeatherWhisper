package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.ZonedDateTime;
import WeatherWhisper.WeatherDataAPI;

public class FXMLController {
	private WeatherDataAPI calledWeather;
	ZonedDateTime date;
	int currentHour;
	int viewHour;

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
	@FXML Button hourLeft;
	@FXML Button hourRight;
	@FXML Image compassArrow = new Image(getClass().getResourceAsStream("compassArrow.png"));
	@FXML ImageView dynamicArrow = new ImageView(compassArrow);
	// Initializes values with placeholders for startup
	@FXML private void startup() 
	{
		
	}

	// Read data from the given WeatherDataAPI object containing data from an API request for a particular city's weather
	@FXML public void initialize(WeatherDataAPI weather) 
	{
		calledWeather = weather;
		date = calledWeather.getTime();
		currentHour = calledWeather.getHour();
		viewHour = currentHour;

		//set actions to buttons 
		searchButton.setOnAction(this::searchHandler);
		searchBox.setOnMouseClicked(this::searchTextHandler);
		//hourLeft.setOnAction(this::shiftHourHandler);
		//hourRight.setOnAction(this::shiftHourHandler);
	
		//initialize tags for header info
		address.setText(weather.getAddress().toString());
		currentTemp.setText(weather.getCurrentTemp().toString() + "°F");
		weatherDesc.setText(weather.getCurrentSkyConditions().toString());
		
		//initialize tag for compass info
		compassText.setText("Wind Direction: " + directionToString(weather.getCurrentWindDirection()) + " Speed: " + weather.getCurrentWindSpeed() + " MPH");
		dynamicArrow.setRotate(Double.parseDouble((weather.getCurrentWindDirection()).toString()));
		//place dates into ArrayList
		initializeDates();

		//initialize tags for hours 
		initializeHours(currentHour);
		
		//initialize tags for daily temperature bounds
		initializeBounds();
		
		//initialize tags for hourly temperatures in Fahrenheit
		initializeHourlyFahrenheit(currentHour);
		
		//initialize tags for hour temperatures in Celsius
		initializeHourlyCelsius(currentHour);
		
		//initialize  tags for hourly winds
		initializeHourlyWind(currentHour);
		
		//initialize tags for hourly precipitation percentage
		initializeHourlyPrecip(currentHour);
	}
	//generate string based on weather direction
	private String directionToString(Object direction)
	{
		var dir = Double.parseDouble(direction.toString());
		if (dir >= 75 && dir <=105)
		{
			return "E";
		}
			
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
	
	//initalizes the dates 
	private void initializeDates()
	{
		int iterator = 1;
		for (Text text : dayLabel)
		{
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd");
			text.setText(date.plusDays(iterator).getDayOfWeek() + " " + dateFormat.format(date.plusDays(iterator)));
			iterator++;
		}
	}
	
	//initialize hours
	private void initializeHours(int startingHour)
	{
		int iterator = 1;
		for (Text text : hour)
		{
			text.setText(timeToString((currentHour + iterator++) % 24));
		}
	}
	
	//initialize daily bounds
	private void initializeBounds()
	{
		int iterator = 0;
		for (Text text : dayBound)
		{
			text.setText((calledWeather.getDailyMaxTemps().get(iterator) + "°F/" + calledWeather.getDailyMinTemps().get(iterator) + "°F"));
			iterator++;
		}
	}
	
	//initialize hourly farenheit temps
	private void initializeHourlyFahrenheit(int startingHour)
	{
		int iterator = 0;
		for (Text text : hourFahrenheit)
		{
			text.setText((calledWeather.getHourlyTemps().get(iterator++)).toString());
		}
	}
	
	//initialize hourly celsius temps
	private void initializeHourlyCelsius(int startingHour)
	{
		int iterator = 0;
		for (Text text : hourCelsius)
		{
			text.setText(toCelsius(calledWeather.getHourlyTemps().get(iterator++)));
		}
	}
	
	//initalize hourly wind speeds
	private void initializeHourlyWind(int startingHour)
	{
		int iterator = 0;
		for (Text text : hourWind)
		{
			text.setText(calledWeather.getHourlyWindSpeeds().get(iterator++) + "MPH");
		}
	}
	
	//initalize hourly precipitation chance
	private void initializeHourlyPrecip(int startingHour)
	{
		int iterator = 0;
		for (Text text : hourPrecip)
		{
			text.setText(calledWeather.getHourlyPrecipProbs().get(iterator++) + "%");
		}
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
	
	//convert farenheit to celsius
	private String toCelsius(Object fahrenheit)
	{
		DecimalFormat dFormat = new DecimalFormat("#.#");
		return (dFormat.format((Double.parseDouble(fahrenheit.toString())-32)*5/9))+"°C";
	}
	
	//reloads hours when time is shifted
	private void shiftHourHandler(ActionEvent e)
	{
		if (e.getSource().equals(hourLeft))
		{
			if (!(viewHour == currentHour)){viewHour--;}
		}
		else if (e.getSource().equals(hourRight))
		{
			if (!(viewHour == currentHour+7)) {viewHour++;}
		}
		
	}
	
	//Clears textbox when clicked
	private void searchTextHandler(MouseEvent e)
	{
		searchBox.setText("");
	}
	
	
	//generates new JFX based on search action
	private void searchHandler(ActionEvent e)
	{
		String loc = searchBox.getText();
		WeatherDataAPI searchLoc = new WeatherDataAPI(loc);
		//searchLoc.updateWeatherData();	//unnecessary I think, as updateWeatherData() is called inside WeatherDataAPI constructor anyways
		
		// Only initialize location searched by user if API recognized location and returned valid JSON
		if(searchLoc.isValid())
			initialize(searchLoc);
		// else indicate invalid location to user somehow perhaps?
		else searchBox.setText("Invalid Location, Try again");
	}
	
}
