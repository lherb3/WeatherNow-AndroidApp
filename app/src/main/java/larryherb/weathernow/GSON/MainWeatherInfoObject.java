package larryherb.weathernow.GSON;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lherb3 on 11/13/17.
 */

public class MainWeatherInfoObject {
    @SerializedName("temp")
    public double temperatureKelvin;

    @SerializedName("pressure")
    public int pressure;

    @SerializedName("humidity")
    public int humidity;

    @SerializedName("temp_min")
    public double temperatureKelvinMin;

    @SerializedName("temp_max")
    public double temperatureKelvinMax;
}
