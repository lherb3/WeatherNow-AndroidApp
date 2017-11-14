package larryherb.weathernow.Libraries;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by lherb3 on 11/10/17.
 */

public class GlobalSharedMethods {

    private Context parentContext;

    public GlobalSharedMethods(Context context){
        //INITIATE FUNCTION HERE

        parentContext=context;
    }

    public int convertToDIP(int pixelValue){
        //Convert Output to DIP Pixels

        int convertedPixelValue= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixelValue, parentContext.getResources().getDisplayMetrics());
        return convertedPixelValue;
    }

}
