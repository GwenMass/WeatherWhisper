package WeatherWhisper;
//Creates a location object for weather data.
public class Location {
private String cityName;
private double latitude;
private double longitude;

//getter & setter for location name
String getCityName() {return cityName;}

void setCityName(String input) {cityName = input;}

//getter & setter for latitude
double getLatitude() {return latitude;}

void setLatitude(double input) {latitude = input;}

//getter & setter for longitude
double getLongitude() {return longitude;}

void setLongitude(double input) {longitude = input;}
}
