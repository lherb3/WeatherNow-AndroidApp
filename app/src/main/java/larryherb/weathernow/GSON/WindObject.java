package larryherb.weathernow.GSON;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lherb3 on 11/13/17.
 */

public class WindObject {
    @SerializedName("speed")
    public double speed;

    @SerializedName("deg")
    public double
            degrees;

    @SerializedName("gust")
    public double gust;
}
