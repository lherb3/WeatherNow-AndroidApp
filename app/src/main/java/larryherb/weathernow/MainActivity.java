package larryherb.weathernow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import larryherb.weathernow.GSON.CurrentWeatherObject;
import larryherb.weathernow.Libraries.GlobalSharedMethods;

public class MainActivity extends AppCompatActivity {

    int screenHeight;
    int screenWidth;

    private GlobalSharedMethods globalSharedMethods;
    public RelativeLayout mainRelativeLayout;
    private RelativeLayout blackOverlayRelativeLayout;
    private RelativeLayout locationContainerLayout;
    private ArrayList<TextView> currentConditionsLabelArrayList;
    private ArrayList<RelativeLayout> currentConditionsLabelContainerArrayList;
    private TextView locationNameLabel;
    private TextView currentTemperatureLabel;
    private TextView currentConditionsDescLabel;
    private ImageView weatherConditionIcon;

    private JSONObject currentWeatherJSONObject;
    private CurrentWeatherObject currentWeatherObject;

    private String openWeatherMapAPIKey = "EnterAPIKeyHere"; //Register for API Key Here: https://openweathermap.org/api/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentConditionsLabelArrayList = new ArrayList<TextView>();
        currentConditionsLabelContainerArrayList= new ArrayList<RelativeLayout>();
        currentWeatherJSONObject = new JSONObject();

        buildLayout();
        mainRelativeLayout.setAlpha(0.0f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainRelativeLayout.setAlpha(0.0f);
        loadLocation();

    }


    private void loadLocation (){
        //Load the Location Using Volley and Turns it into a JSON Object.

        SharedPreferences preferences = this.getSharedPreferences("SharedPreferences", this.MODE_PRIVATE);
        String currentLocationBtnText= preferences.getString("locationName", "DEFAULT");
        locationNameLabel.setText(currentLocationBtnText);
        Log.i("URL", "http://api.openweathermap.org/data/2.5/weather?q="+currentLocationBtnText+"&appid="+openWeatherMapAPIKey+"&lang="+ Locale.getDefault().getLanguage().toString());
        final Uri.Builder urlBuilder = Uri.parse("http://api.openweathermap.org/data/2.5/weather?q="+currentLocationBtnText+"&appid="+openWeatherMapAPIKey+"&lang="+ Locale.getDefault().getLanguage().toString()).buildUpon();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlBuilder.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                currentWeatherJSONObject = response;
                Log.i("LOADAPIURL", " DATA (response): " + response);
                Gson gson = new Gson();
                currentWeatherObject = gson.fromJson(currentWeatherJSONObject.toString(), CurrentWeatherObject.class);
                if(currentWeatherObject.httpCode==200){
                    //200 Code OK to Proceed
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.mainView_loadCity_successMessage)+currentWeatherObject.cityName, Toast.LENGTH_SHORT).show();
                    addDataToInterface();
                }else{
                    //Not 200 Throw Error
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.mainView_loadCity_errorMessage), Toast.LENGTH_LONG).show();
                    loadLocationSettingsScreen();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("there was an error:", "" + error.toString());
                Toast.makeText(MainActivity.this, getResources().getString(R.string.mainView_loadCity_errorMessage), Toast.LENGTH_SHORT).show();
                loadLocationSettingsScreen();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);

    }

    private void loadLocationSettingsScreen(){
        //Loads the Location Settings Screen.s
        blackOverlayRelativeLayout.animate().alpha(1.0f).setDuration(150).withEndAction(new Runnable() {
            @Override
            public void run() {

                Intent locationActivityIntent = new Intent(MainActivity.this, LocationSettingsActivity.class);
                locationActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(locationActivityIntent);
                overridePendingTransition(0,0);

                blackOverlayRelativeLayout.animate().alpha(0.0f).setDuration(150);

            }
        });
    }


    private void addDataToInterface(){
        //Adds the Information to the UI Screen


        //Update Temperature
        String tempString = String.format("%.0f", convertKelvinToF(currentWeatherObject.weatherOverview.temperatureKelvin));
        currentTemperatureLabel.setText(tempString+"°");

        //Add Conditions Description Strings
        String currentConditions="";

        for(int i = 0; i<currentWeatherObject.weatherConditionsArrayList.size(); i++) {
            String startComma=" ,";
            if(i==0){
                //Add A Blank Start Comma and Load first Image Icon for Conditions
                startComma="";
                final String firstConditionWeatherIconCode= currentWeatherObject.weatherConditionsArrayList.get(i).iconCode;

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            final Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL("http://openweathermap.org/img/w/"+firstConditionWeatherIconCode+".png").getContent());
                            weatherConditionIcon.post(new Runnable() {
                                public void run() {
                                    if (bitmap != null) {
                                        //Flash Image In After Load
                                        weatherConditionIcon.animate().alpha(0.0f).setDuration(0).withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                weatherConditionIcon.setImageBitmap(bitmap);
                                                weatherConditionIcon.animate().alpha(1.0f).setDuration(150).withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //Present Image Here
                                                    }
                                                });

                                            }
                                        });
                                    }
                                }
                            });
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }).start();
            }
            currentConditions= currentConditions+startComma+currentWeatherObject.weatherConditionsArrayList.get(i).conditionLabel;
        }
        currentConditionsDescLabel.setText(currentConditions);

        //Add Humidity String
        currentConditionsLabelArrayList.get(0).setText(getResources().getString(R.string.mainView_currentConditions_humidityLabel)+": "+currentWeatherObject.weatherOverview.humidity+"%");

        //Add Wind Info
        String pressureString = String.format("%.1f", currentWeatherObject.wind.degrees);
        currentConditionsLabelArrayList.get(1).setText(getResources().getString(R.string.mainView_currentConditions_windLabel)+": "+currentWeatherObject.wind.speed+" "+getResources().getString(R.string.mainView_CurrentConditions_metersPerSecondLabel)+" @ "+pressureString+"°");

        //Add Pressure Info
        currentConditionsLabelArrayList.get(2).setText(getResources().getString(R.string.mainView_currentConditions_pressureLabel)+": "+currentWeatherObject.weatherOverview.pressure+" mb");

        //Add Min Temperature Info
        String tempMinString=  String.format("%.0f", convertKelvinToF(currentWeatherObject.weatherOverview.temperatureKelvinMin));
        currentConditionsLabelArrayList.get(3).setText(getResources().getString(R.string.mainView_currentConditions_minLabel)+": "+tempMinString+" ° F");

        //Add Max Temperature Info
        String tempMaxString=  String.format("%.0f", convertKelvinToF(currentWeatherObject.weatherOverview.temperatureKelvinMax));
        currentConditionsLabelArrayList.get(4).setText(getResources().getString(R.string.mainView_currentConditions_maxLabel)+": "+tempMaxString+" ° F");





        animateInterfaceIn();

    }

    private double convertKelvinToF(double originalValue){
        //Converts Kelvin Temperature to Fahrenheit

        double convertedValue=0;
        convertedValue=originalValue*9/5-459.67;
        return convertedValue;
    }



    public void buildLayout() {
        //The Layout is built here.

        //SET DISPLAY DIMENSIONS
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        screenHeight= screenHeight-24; //Account for Status Bar

        globalSharedMethods = new GlobalSharedMethods(this);

        //Main Relative Layout
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);



        //===================================
        //= TOP HALF
        //===================================

        //High Level Information Container
        RelativeLayout topHalfLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams topHalfLayoutLParams = new RelativeLayout.LayoutParams(screenWidth, ((screenHeight/2) ));
        topHalfLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        topHalfLayout.setLayoutParams(topHalfLayoutLParams);
        topHalfLayout.setPadding(globalSharedMethods.convertToDIP(15), globalSharedMethods.convertToDIP(0), globalSharedMethods.convertToDIP(15), globalSharedMethods.convertToDIP(15));
        mainRelativeLayout.addView(topHalfLayout);

        //-----------------------------------
        //- Location Selector
        //-----------------------------------

        //Location Container
        locationContainerLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams locationContainerLayoutLParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, globalSharedMethods.convertToDIP(60));
        locationContainerLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        locationContainerLayout.setLayoutParams(locationContainerLayoutLParams);
        topHalfLayout.addView(locationContainerLayout);

        ImageView locationIconImage = new ImageView(this);
        locationIconImage.setImageResource(R.mipmap.location_icon);
        RelativeLayout.LayoutParams locationIconImageLParams = new RelativeLayout.LayoutParams(globalSharedMethods.convertToDIP(40), globalSharedMethods.convertToDIP(50));
        locationIconImageLParams.addRule(RelativeLayout.CENTER_VERTICAL);
        locationIconImageLParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        locationIconImage.setLayoutParams(locationIconImageLParams);
        locationContainerLayout.addView(locationIconImage);

        //Current Conditions Header Container White
        RelativeLayout locationSelectorWhiteBorderLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams locationSelectorWhiteBorderLayoutLParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, globalSharedMethods.convertToDIP(4));
        locationSelectorWhiteBorderLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        locationSelectorWhiteBorderLayoutLParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        locationSelectorWhiteBorderLayout.setLayoutParams(locationSelectorWhiteBorderLayoutLParams);
        locationSelectorWhiteBorderLayout.setBackgroundColor(Color.WHITE);
        locationContainerLayout.addView(locationSelectorWhiteBorderLayout);

        //Location Name Text
        locationNameLabel = new TextView(this);
        locationNameLabel.setText("New York, NY");
        RelativeLayout.LayoutParams locationNameLabelLParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        locationNameLabelLParams.setMargins(globalSharedMethods.convertToDIP(50), 0, 0, 0);
        locationNameLabel.setLayoutParams(locationNameLabelLParams);
        locationNameLabel.setTextColor(Color.WHITE);
        locationNameLabel.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        locationNameLabel.setGravity(Gravity.CENTER_VERTICAL);
        locationNameLabel.setPadding(globalSharedMethods.convertToDIP(60), 0, 0, 0);
        locationNameLabel.setTextSize(20);
        locationNameLabel.setAlpha(1.0f);
        locationNameLabel.setPadding(0, 0, 0, 0);
        locationContainerLayout.addView(locationNameLabel);


        final Button buttonOverlay = new Button(this);
        buttonOverlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        buttonOverlay.setTransformationMethod(null);
        buttonOverlay.setBackgroundColor(Color.TRANSPARENT);
        buttonOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationContainerLayout.animate().scaleX(0.95f).scaleY(0.95f).setDuration(150).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        locationContainerLayout.animate().scaleX(1f).scaleY(1f).setDuration(150).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //Fade to Black

                                loadLocationSettingsScreen();
                            }
                        });
                    }
                });

            }
        });
        locationContainerLayout.addView(buttonOverlay);



        //-----------------------------------
        //- Temperature Code
        //-----------------------------------

        ///Temperature Container
        RelativeLayout temperatureContainerLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams temperatureContainerLayoutLParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (((screenHeight/2)-globalSharedMethods.convertToDIP(60+30))));
        temperatureContainerLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        temperatureContainerLayout.setLayoutParams(temperatureContainerLayoutLParams);
        topHalfLayout.addView(temperatureContainerLayout);

        LinearLayout temperatureContainerLinearLayout = new LinearLayout(this);
        temperatureContainerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams temperatureContainerLinearLayoutLParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        temperatureContainerLinearLayoutLParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        temperatureContainerLinearLayout.setLayoutParams(temperatureContainerLinearLayoutLParams);
        temperatureContainerLinearLayout.setGravity(Gravity.CENTER);
        temperatureContainerLayout.addView(temperatureContainerLinearLayout);

        //Current Temp Text
        currentTemperatureLabel = new TextView(this);
        currentTemperatureLabel.setText("0°");
        currentTemperatureLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        currentTemperatureLabel.setTextColor(Color.WHITE);
        currentTemperatureLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        currentTemperatureLabel.setTextSize(80);
        currentTemperatureLabel.setAlpha(1.0f);
        currentTemperatureLabel.setPadding(30, 30, 30, 30);
        temperatureContainerLinearLayout.addView(currentTemperatureLabel);


         //Needed for Image?
        LinearLayout currentContainerLinearLayout = new LinearLayout(this);
        currentContainerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams currentContainerLinearLayoutLParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        currentContainerLinearLayoutLParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        currentContainerLinearLayout.setLayoutParams(currentContainerLinearLayoutLParams);
        currentContainerLinearLayout.setGravity(Gravity.CENTER);
        temperatureContainerLinearLayout.addView(currentContainerLinearLayout);

        //Weather Condition Icon
        weatherConditionIcon = new ImageView(this);
        RelativeLayout.LayoutParams weatherNowLogoImageLParams = new RelativeLayout.LayoutParams(globalSharedMethods.convertToDIP(40), globalSharedMethods.convertToDIP(40));
        weatherNowLogoImageLParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        weatherConditionIcon.setLayoutParams(weatherNowLogoImageLParams);
        currentContainerLinearLayout.addView(weatherConditionIcon);

        //Current Conditions Desc Text
        currentConditionsDescLabel = new TextView(this);
        currentConditionsDescLabel.setText("Loading...");
        currentConditionsDescLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        currentConditionsDescLabel.setTextColor(Color.WHITE);
        currentConditionsDescLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        currentConditionsDescLabel.setTextSize(20);
        currentConditionsDescLabel.setAlpha(1.0f);
        currentConditionsDescLabel.setPadding(30, 30, 30, 30);
        currentContainerLinearLayout.addView(currentConditionsDescLabel);


        //===================================
        //= BOTTOM HALF
        //===================================

        //High Level Information Container
        RelativeLayout bottomHalfLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams bottomHalfLayoutLParams = new RelativeLayout.LayoutParams(screenWidth, ((screenHeight/2) ));
        bottomHalfLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomHalfLayout.setLayoutParams(bottomHalfLayoutLParams);
        bottomHalfLayout.setPadding(globalSharedMethods.convertToDIP(15), globalSharedMethods.convertToDIP(0), globalSharedMethods.convertToDIP(15), globalSharedMethods.convertToDIP(15));
        mainRelativeLayout.addView(bottomHalfLayout);


        //-----------------------------------
        //- CURRENT CONDITIONS
        //-----------------------------------

        //Current Conditions Linear Layout
        LinearLayout currentConditionsLinearLayout = new LinearLayout(this);
        RelativeLayout.LayoutParams currentConditionsLinearLayoutLParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.MATCH_PARENT);
        currentConditionsLinearLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        currentConditionsLinearLayout.setLayoutParams(currentConditionsLinearLayoutLParams);
        currentConditionsLinearLayout.setOrientation(LinearLayout.VERTICAL);
        bottomHalfLayout.addView(currentConditionsLinearLayout);

        //Current Conditions Header Container
        RelativeLayout currentConditionsHeaderLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams currentConditionsHeaderLayoutLParams = new RelativeLayout.LayoutParams(screenWidth, globalSharedMethods.convertToDIP(60));
        currentConditionsHeaderLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        currentConditionsHeaderLayout.setLayoutParams(currentConditionsHeaderLayoutLParams);
        currentConditionsLinearLayout.addView(currentConditionsHeaderLayout);

        //Current Conditions Header Container White
        RelativeLayout whiteBorderLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams whiteBorderLayoutLParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, globalSharedMethods.convertToDIP(2));
        whiteBorderLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        whiteBorderLayoutLParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        whiteBorderLayout.setLayoutParams(whiteBorderLayoutLParams);
        whiteBorderLayout.setBackgroundColor(Color.WHITE);
        currentConditionsHeaderLayout.addView(whiteBorderLayout);

        //Current Conditions Header Text View
        TextView currentConditionsTextView = new TextView(this);
        currentConditionsTextView.setText(getResources().getString(R.string.mainView_currentConditions_titleLabel));
        currentConditionsTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        currentConditionsTextView.setTextColor(Color.WHITE);
        currentConditionsTextView.setGravity(Gravity.CENTER);
        currentConditionsTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        currentConditionsTextView.setTextSize(20);
        currentConditionsTextView.setPadding(0, 0, 0, 0);
        currentConditionsHeaderLayout.addView(currentConditionsTextView);


        for(int i = 0; i<5; i++) {
            RelativeLayout tempCurrentConditionRelativeLayout = generateCurrentConditionsInformation("Loading...");
            currentConditionsLinearLayout.addView(tempCurrentConditionRelativeLayout);
        }

        blackOverlayRelativeLayout = new RelativeLayout(this);
        blackOverlayRelativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        blackOverlayRelativeLayout.setBackgroundColor(Color.BLACK);
        blackOverlayRelativeLayout.setAlpha(0.0f);
        mainRelativeLayout.addView(blackOverlayRelativeLayout);

    }

    private RelativeLayout generateCurrentConditionsInformation(String textLabel){
        //Generate the Layout for the current condition items

        RelativeLayout currentConditionItemLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams currentConditionsItemLayoutLParams = new RelativeLayout.LayoutParams(screenWidth, globalSharedMethods.convertToDIP(40));
        //currentConditionsItemLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        currentConditionItemLayout.setPadding(globalSharedMethods.convertToDIP(0), globalSharedMethods.convertToDIP(0), globalSharedMethods.convertToDIP(0), globalSharedMethods.convertToDIP(0));
        currentConditionItemLayout.setLayoutParams(currentConditionsItemLayoutLParams);
        currentConditionItemLayout.setId(View.generateViewId());

        RelativeLayout whiteBorderLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams whiteBorderLayoutLParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, globalSharedMethods.convertToDIP(1));
        whiteBorderLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        whiteBorderLayoutLParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        whiteBorderLayout.setLayoutParams(whiteBorderLayoutLParams);
        whiteBorderLayout.setBackgroundColor(Color.WHITE);
        whiteBorderLayout.setAlpha(0.25f);
        currentConditionItemLayout.addView(whiteBorderLayout);

        //Current Conditions Header Text View
        TextView currentConditionsTextView = new TextView(this);
        currentConditionsTextView.setText("Loading...");
        currentConditionsTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        currentConditionsTextView.setTextColor(Color.WHITE);
        currentConditionsTextView.setGravity(Gravity.CENTER);
        currentConditionsTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        currentConditionsTextView.setTextSize(12);
        currentConditionsTextView.setPadding(0, 0, 0, 0);
        currentConditionItemLayout.addView(currentConditionsTextView);
        currentConditionsLabelArrayList.add(currentConditionsTextView);
        currentConditionsLabelContainerArrayList.add(currentConditionItemLayout);

        return  currentConditionItemLayout;
    }


    private void animateInterfaceIn(){
        //Visual Effect for Loading the Interface In

        currentTemperatureLabel.setAlpha(0.0f);
        currentTemperatureLabel.setScaleX(0.0f);
        currentTemperatureLabel.setScaleY(0.0f);

        for(int i = 0; i<currentConditionsLabelContainerArrayList.size(); i++) {
            currentConditionsLabelContainerArrayList.get(i).setAlpha(0.0f);
        }

        mainRelativeLayout.animate().alpha(0.0f).setDuration(0).scaleX(1.25f).scaleY(1.25f).withEndAction(new Runnable() {
            @Override
            public void run() {
                mainRelativeLayout.animate().alpha(1.0f).setDuration(300).scaleX(1.0f).scaleY(1.0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        //Present Image Here

                        currentTemperatureLabel.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //Add Additional Actions
                                //currentConditionsLabelContainerArrayList)
                                for(int i = 0; i<currentConditionsLabelContainerArrayList.size(); i++) {
                                    //Stagger Animate Items In
                                    currentConditionsLabelContainerArrayList.get(i).animate().alpha(1.0f).setDuration(250).setStartDelay(100*i).setInterpolator(new DecelerateInterpolator());
                                }

                            }
                        });
                    }
                });

            }
        });
    }
}
