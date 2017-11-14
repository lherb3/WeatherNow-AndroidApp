package larryherb.weathernow.GSON;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lherb3 on 11/13/17.
 */

public class WeatherObject {
    @SerializedName("id")
    public String identifier;

    @SerializedName("main")
    public String conditionLabel;

    @SerializedName("description")
    public String description;

    @SerializedName("icon")
    public String iconCode;

}
