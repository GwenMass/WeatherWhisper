package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	@FXML ImageView backgroundGif;
	@FXML ArrayList<ImageView> hourSymbolsF;
	@FXML ArrayList<ImageView> hourSymbolsC;
	@FXML Text UVIndex;
	@FXML Text currentTemp;
	@FXML Label weatherDesc;
	@FXML Text compassText;
	@FXML TextField searchBox;
	@FXML Button searchButton;
	@FXML Button hourLeft;
	@FXML Button hourRight;
	@FXML Text uvIndexDisplay;
	@FXML Image arrow = new Image(getClass().getResourceAsStream("compassArrow.png"));
	@FXML ImageView compassArrow = new ImageView(arrow);
	@FXML ImageView uvArrow = new ImageView(arrow);
	@FXML Text moonPhaseDisplay;
	@FXML ImageView moonImg;
	@FXML ArrayList<ImageView> dailySymbols;
	@FXML ArrayList<ImageView> arrows;
	
	Rotate compassRotation = new Rotate(0);
	Rotate uvRotation = new Rotate(0);
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
		
		setBackground();
		setHourlyWeatherSymbols(hourSymbolsF, currentHour);
		setHourlyWeatherSymbols(hourSymbolsC, currentHour);
		setDailySymbols();
		setMoonPhase();
		setWindArrows(currentHour);
	
		//initialize tags for header info
		address.setText(weather.getAddress().toString());
		currentTemp.setText(weather.getCurrentTemp().toString() + "°F");
		weatherDesc.setText(weather.getCurrentSkyConditions().toString());
		//initialize UV INFO
		initializeUV();
		//initialize tag for compass info
		compassText.setText("Wind Direction: " + directionToString(weather.getCurrentWindDirection()) + " Speed: " + weather.getCurrentWindSpeed() + " MPH");
		initializeCompassArrow();
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
	//initialize UV amount & UV Arrow
	private void initializeUV()
	{
		System.out.println(calledWeather.getCurrentUVindex());
		String uvString =calledWeather.getCurrentUVindex().toString();
		uvIndexDisplay.setText(uvString);
		Double uv = Double.parseDouble(uvString);
		double angle = 0;
		
		//logic for getting angle of UV
		if (uv >= 11) angle = 162;
		if (uv > 7 && uv < 11) angle = 126;
		if (uv >= 5 && uv <= 7) angle = 90;
		if (uv >2 && uv < 5) angle = 54;
		if (uv >= 1 && uv <= 2) angle = 18;
		if (uv == 0) angle = 0;
		
		//create arrow direction
		uvRotation.setPivotX(uvArrow.getBoundsInLocal().getWidth()/2);
		uvRotation.setPivotY(uvArrow.getBoundsInLocal().getHeight());
		uvRotation.setAngle(angle);
		uvArrow.getTransforms().clear();
		uvArrow.getTransforms().add(uvRotation);
	}
	
	//initializes direction/rotation of arrow
	private void initializeCompassArrow()
	{
		compassRotation.setPivotX(compassArrow.getBoundsInLocal().getWidth()/2);
		compassRotation.setPivotY(compassArrow.getBoundsInLocal().getHeight());
		compassRotation.setAngle(Double.parseDouble((calledWeather.getCurrentWindDirection()).toString()));
		compassArrow.getTransforms().clear();
		compassArrow.getTransforms().add(compassRotation);
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
			text.setText((calledWeather.getHourlyTemps().get(iterator++)).toString()+"F");
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
	
	
	//Set the background to the correct correlating sky condition
	private void setBackground() {
	    Object currConditions = calledWeather.getCurrentSkyConditions();

	    if (currConditions != null) {
	        String condition = currConditions.toString();

	        //Map of sky condition output --> correlating gif/jpg
	        Map<String, String> conditionImageMap = Map.of(
	                "Clear", "sunny.jpg",
	                "Rain", "rainy.gif",
	                "Partially cloudy", "partlyCloudy.gif",
	                "Overcast", "cloudy.gif",
	                "Rain, Partially cloudy", "rainy.gif",
	                "Rain, Overcast", "rainy.gif",
	                "Snow, Overcast", "snowy.gif",
	                "Snow, Partially cloudy", "snowy.gif",
	                "Snow", "snowy.gif"
	        );

	        // Check if a key is true and set the correct background image
	        if (conditionImageMap.containsKey(condition)) {
	            String imagePath = getClass().getResource(conditionImageMap.get(condition)).toExternalForm();
	            Image image = new Image(imagePath);
	            backgroundGif.setImage(image);
	            
	        } else {
	            System.out.println("Condition Not Found Error!");
	        }
	    }
	}	
	private void setHourlyWeatherSymbols(List<ImageView> hourSymbols, int startingHour) {
		//Map of sky condition output --> correlating symbols
	    Map<String, String> conditionImageMap = Map.of(
	            "Clear", "Sunny.png",
	            "Rain", "Raining.png",
	            "Partially cloudy", "PartlyCloudy.png",
	            "Overcast", "Cloudy.png",
	            "Rain, Partially cloudy", "Raining.png",
	            "Rain, Overcast", "Raining.png",
	            "Snow, Overcast", "snowy.png",
	            "Snow, Partially cloudy", "snowy.png",
	            "Snow", "snowy.png"
	    );
	    Map<String, String> nightImageMap = Map.of(
	    		"Clear", "Night.png",
	    		"Partially Cloudy", "partlyCloudyNight.png"
	    );
	    
	    ZonedDateTime sunsetTime = calledWeather.getSunsetTime();
        ZonedDateTime sunriseTime = calledWeather.getSunriseTime();
        ZonedDateTime currentTime = calledWeather.getTime();
        
	    int iterator = 0;
	    //Loop for setting the images
	    for (ImageView iv : hourSymbols) {
	        Object current = calledWeather.getHourlySkyConditions().get(iterator++);
	        String condition = current.toString();
	        
	        // Check if a key is true and set the correct symbol
	        if(nightImageMap.containsKey(condition) && (currentTime.isAfter(sunsetTime) && currentTime.isBefore(sunriseTime))) {
	        	String imagePath = getClass().getResource(nightImageMap.get(condition)).toExternalForm();
	            Image image = new Image(imagePath);
	            iv.setImage(image);
	        }
	        else if (conditionImageMap.containsKey(condition)) {
	            String imagePath = getClass().getResource(conditionImageMap.get(condition)).toExternalForm();
	            Image image = new Image(imagePath);
	            iv.setImage(image);
	        }
	    }
	}
	
	private void setMoonPhase()
	{
		Object currConditions = calledWeather.getMoonPhase();
		
		if (currConditions != null) {
	        String condition = currConditions.toString();

	        //Map of sky condition output --> correlating gif/jpg
	        Map<String, String> conditionImageMap = Map.of(
	                "New Moon", "newMoon.png",
	                "Waxing Crescent", "waxingCrescent.png",
	                "First Quarter", "firstQuarter.png",
	                "Waxing Gibbous", "waxingGibbous.png",
	                "Full Moon", "fullMoon.png",
	                "Waning Gibbous", "waningGibbous.png",
	                "Last Quarter", "thirdQuarter.png",
	                "Waning Crescent", "waningCrescent.png"
	        );

	        // Check if a key is true and set the correct background image
	        if (conditionImageMap.containsKey(condition)) {
	            String imagePath = getClass().getResource(conditionImageMap.get(condition)).toExternalForm();
	            Image image = new Image(imagePath);
	            moonImg.setImage(image);
	            moonPhaseDisplay.setText(calledWeather.getMoonPhase().toString());
	        } else {
	            System.out.println("Condition Not Found Error!");
	        }
		}
	}
	private void setDailySymbols() {
		//Map of sky condition output --> correlating symbols
	    Map<String, String> conditionImageMap = Map.of(
	            "Clear", "Sunny.png",
	            "Rain", "Raining.png",
	            "Partially cloudy", "PartlyCloudy.png",
	            "Overcast", "Cloudy.png",
	            "Rain, Partially cloudy", "Raining.png",
	            "Rain, Overcast", "Raining.png",
	            "Snow, Overcast", "snowy.png",
	            "Snow, Partially cloudy", "snowy.png",
	            "Snow", "snowy.png"
	    );
	    
	    int iterator = 0;
	    
	    for(ImageView iv : dailySymbols) {
	    	Object current = calledWeather.getDailySkyConditions().get(iterator++);
	        String condition = current.toString();
	        
	        if (conditionImageMap.containsKey(condition)) {
	            String imagePath = getClass().getResource(conditionImageMap.get(condition)).toExternalForm();
	            Image image = new Image(imagePath);
	            iv.setImage(image);
	        }
	    }
	}
	
	private void setWindArrows(int startinghour) {
		List<Object> windDirectionsList = calledWeather.getHourlyWindDirections();

		//Get the image arrow.png
		String imagePath = getClass().getResource("arrow.png").toExternalForm();
		Image arrowImage = new Image(imagePath);

		int iterator = 0;

		for (Object windDirectionObject : windDirectionsList) {
		    
			//Parse for wind direction angle
		    double windDirection = Double.parseDouble(windDirectionObject.toString());
		    System.out.println(windDirection);
		    
		    //Set the image and i++
		    if (iterator < arrows.size()) {
		        ImageView iv = arrows.get(iterator);
		        iv.setImage(arrowImage);
		        iv.setRotate(windDirection); // Set the rotation angle based on the wind direction

		        iterator++;
		    }
		}
	}
}
