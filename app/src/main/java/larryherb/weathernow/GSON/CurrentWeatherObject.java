package larryherb.weathernow.GSON;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by lherb3 on 11/12/17.
 * GSON Object for the weather object received.
 */

public class CurrentWeatherObject {

    @SerializedName("coord")
    public CoordinateObject coordinates;

    @SerializedName("weather")
    public ArrayList<WeatherObject> weatherConditionsArrayList;

    @SerializedName("base")
    public String base;

    @SerializedName("main")
    public MainWeatherInfoObject weatherOverview;

    @SerializedName("visibility")
    public int visibility;

    @SerializedName("wind")
    public WindObject wind;

    @SerializedName("id")
    public int identifier;

    @SerializedName("dt")
    public int dt;

    @SerializedName("name")
    public String cityName;

    @SerializedName("cod")
    public int httpCode;

}
