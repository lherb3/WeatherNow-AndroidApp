package larryherb.weathernow.GSON;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lherb3 on 11/13/17.
 */

public class CoordinateObject {
    @SerializedName("lon")
    public double longitude;

    @SerializedName("lat")
    public double latitude;
}
