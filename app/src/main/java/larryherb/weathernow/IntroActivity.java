package larryherb.weathernow;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import larryherb.weathernow.Libraries.GlobalSharedMethods;

public class IntroActivity extends AppCompatActivity {

    int screenHeight;
    int screenWidth;

    private GlobalSharedMethods globalSharedMethods;
    private RelativeLayout mainRelativeLayout;

    //Brand Box
    private RelativeLayout brandBoxLayout;
    private ImageView weatherNowLogoImage;
    private TextView weatherNowTitleTextView;

    private RelativeLayout copyrightContainerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //Set Shared Preferences
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("SharedPreferences", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        boolean isReturningUser=preferences.getBoolean("firstTimeUser", false);
        if(isReturningUser==false){
            editor.putBoolean("firstTimeUser", true);
            editor.putString("locationName", "New York, NY");
            editor.commit();
        }

        buildLayout();
    }

    public void buildLayout(){
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

        //Brand Layout
        brandBoxLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams brandBoxLayoutLParams = new RelativeLayout.LayoutParams(screenWidth, (screenHeight/2)+globalSharedMethods.convertToDIP(60));
        brandBoxLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        brandBoxLayout.setLayoutParams(brandBoxLayoutLParams);
        mainRelativeLayout.addView(brandBoxLayout);

        //Brand Box Linear Layout
        LinearLayout brandBoxLinearLayout = new LinearLayout(this);
        brandBoxLinearLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams brandBoxLinearLayoutLParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        brandBoxLinearLayoutLParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        brandBoxLinearLayout.setLayoutParams(brandBoxLinearLayoutLParams);
        brandBoxLinearLayout.setGravity(Gravity.CENTER);
        brandBoxLayout.addView(brandBoxLinearLayout);

        //Weather Now Logo
        weatherNowLogoImage = new ImageView(this);
        weatherNowLogoImage.setImageResource(R.mipmap.weather_launch_image);
        RelativeLayout.LayoutParams weatherNowLogoImageLParams = new RelativeLayout.LayoutParams(globalSharedMethods.convertToDIP(200), globalSharedMethods.convertToDIP(200));
        weatherNowLogoImageLParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        weatherNowLogoImage.setLayoutParams(weatherNowLogoImageLParams);
        weatherNowLogoImage.setTranslationX(screenWidth);
        brandBoxLinearLayout.addView(weatherNowLogoImage);

        //Weather Now Text
        weatherNowTitleTextView = new TextView(this);
        weatherNowTitleTextView.setText(getResources().getString(R.string.introView_appTitle));
        weatherNowTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        weatherNowTitleTextView.setTextColor(Color.WHITE);
        weatherNowTitleTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        weatherNowTitleTextView.setTextSize(36);
        weatherNowTitleTextView.setAlpha(0.0f);
        weatherNowTitleTextView.setPadding(30, 30, 30, 30);
        brandBoxLinearLayout.addView(weatherNowTitleTextView);

        //===================================
        //= BOTTOM HALF
        //===================================

        //Copyright Container
        copyrightContainerLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams copyrightContainerLayoutLParams = new RelativeLayout.LayoutParams(screenWidth, globalSharedMethods.convertToDIP(60));
        copyrightContainerLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        copyrightContainerLayout.setLayoutParams(copyrightContainerLayoutLParams);
        copyrightContainerLayout.setTranslationY(globalSharedMethods.convertToDIP(60));
        mainRelativeLayout.addView(copyrightContainerLayout);

        //Copyright Container White
        RelativeLayout whiteBorderLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams whiteBorderLayoutLParams = new RelativeLayout.LayoutParams(screenWidth-globalSharedMethods.convertToDIP(40), globalSharedMethods.convertToDIP(1));
        whiteBorderLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        whiteBorderLayoutLParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        whiteBorderLayout.setLayoutParams(whiteBorderLayoutLParams);
        whiteBorderLayout.setBackgroundColor(Color.WHITE);
        whiteBorderLayout.setAlpha(0.25f);
        copyrightContainerLayout.addView(whiteBorderLayout);

        //Copyright Text View
        TextView copyrightTextView = new TextView(this);
        copyrightTextView.setText(getResources().getString(R.string.introView_copyrightText));
        copyrightTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        copyrightTextView.setTextColor(Color.WHITE);
        copyrightTextView.setGravity(Gravity.CENTER);
        copyrightTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        copyrightTextView.setTextSize(16);
        copyrightTextView.setAlpha(0.25f);
        copyrightTextView.setPadding(0, 0, 0, 0);
        copyrightContainerLayout.addView(copyrightTextView);


    }

    @Override
    protected void onResume() {
        super.onResume();

        //Animate Brand Box with Pause
        brandBoxLayout.animate().translationY(0).setStartDelay(600).withEndAction(new Runnable() {
            @Override
            public void run() {

                //Animate Weather Logo From Side
                weatherNowLogoImage.animate().translationX(0).setDuration(900).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {

                        //Weather Now Title Fades In Animation
                        weatherNowTitleTextView.animate().alpha(1).setDuration(650).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                            @Override
                            public void run() {

                                //Copyright Box Label Slides Up Animation
                                copyrightContainerLayout.animate().translationY(0).setDuration(650).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {

                                        //Fade Out
                                        mainRelativeLayout.animate().alpha(0).setDuration(600).withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent mainActivityIntent = new Intent(IntroActivity.this, MainActivity.class);
                                                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                startActivity(mainActivityIntent);
                                                overridePendingTransition(0,0);
                                                finish();
                                            }
                                        });

                                    }
                                });

                            }
                        });

                    }
                });


            }
        });
    }
}
