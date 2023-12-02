module WeatherWhisper {
	requires java.desktop;
	requires java.net.http;
	requires org.json;
	requires javafx.controls;
	requires javafx.fxml;
	
	opens application to javafx.graphics, javafx.fxml;
}
