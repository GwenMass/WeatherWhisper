package application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import WeatherWhisper.WeatherDataAPI;
public class FXMLController {
		
		private LocalDate date = LocalDate.now();
		//tags for injection
		@FXML ArrayList<Text> dayLabel;
		@FXML ArrayList<Text> hour;
		@FXML ArrayList<Text> dayBound;
		@FXML ArrayList<Text> hourFarenheit;
		@FXML ArrayList<Text> hourCelsius;
		@FXML ArrayList<Text> hourWind;
		@FXML ArrayList<Text> hourPrecip;
		@FXML Label address;
		@FXML Label weatherDesc;
		@FXML Text compassText;
	//initializes values with placeholders for startup
@FXML private void startup()
{
	
}
	//read data from the provided weather api pull
@FXML public void initialize(WeatherDataAPI weather) 
{
	int iterator = 1;
	int currentHour = LocalTime.now().getHour();

	//initialize tags for header info
	address.setText(weather.getAddress().toString());
	weatherDesc.setText(weather.getCurrentSkyConditions().toString());
	
	//initialize tag for compass info
	compassText.setText("Wind Direction: " + directionToString(weather.getCurrentWindDirection())+" Speed: "+weather.getCurrentWindSpeed()+ " MPH");
	
	//place dates into ArrayList
	iterator =1;
	for (Text text : dayLabel)
	{
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd");
		text.setText(dateFormat.format(date.plusDays(iterator++)));
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
	
	//initialize tags for hourly temperatures in Farenheit
	iterator = 1;
	for (Text text : hourFarenheit)
	{
		text.setText(weather.getHourlyTemps().get((currentHour + iterator++) % 24) + "°F");
	}
	
	//initialize tags for hour temperatures in Celsius
	iterator = 1;
	for (Text text : hourCelsius)
	{
		text.setText(toCelsius(weather.getHourlyTemps().get(iterator++)));
	}
	
	//initialize  tags for hourly winds
	iterator = 1;
	for (Text text : hourWind)
	{
		text.setText(weather.getHourlyWindSpeeds().get((currentHour + iterator++) % 24) + "MPH");
	}
	
	//initialize tags for hourly precipitation percentage
	iterator = 1;
	for (Text text : hourPrecip)
	{
		text.setText(weather.getHourlyPrecipProbs().get((currentHour + iterator++) % 24) + "%");
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
	private String timeToString(int time)
	{
		if (time == 0)
				return "12 AM";
		if (time == 12)
			return "12 PM";
		if (time > 12)
		{
			return time - 12 + "PM";
		}
		else return time + "AM";
	}
	private String toCelsius(Object farenheit)
	{
		DecimalFormat dFormat = new DecimalFormat("#.#");
		return (dFormat.format((Double.parseDouble(farenheit.toString())-32)*5/9))+"°C";
	}
}
