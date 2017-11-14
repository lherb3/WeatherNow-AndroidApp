package larryherb.weathernow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import larryherb.weathernow.Libraries.GlobalSharedMethods;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class LocationSettingsActivity extends AppCompatActivity {

    int screenHeight;
    int screenWidth;
    private GlobalSharedMethods globalSharedMethods;
    public RelativeLayout mainRelativeLayout;
    private RelativeLayout locationTextboxContainerLayout;
    private TextView indicatorCurrentLocationLabel;
    private TextView indicatorTopLabel;
    private EditText searchTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_settings);

        buildLayout();

        SharedPreferences preferences = this.getSharedPreferences("SharedPreferences", this.MODE_PRIVATE);
        String currentLocationBtnText= preferences.getString("locationName", "DEFAULT");
        indicatorCurrentLocationLabel.setText(currentLocationBtnText);

    }
    private void validateTextEntry(){
        //Validates that there is text in the text entry.

        Log.i("KEY", "ON SEARCH ACTION HERE Text Is:"+searchTextField.getText());
        if(searchTextField.getText().length()==0){
            Toast.makeText(LocationSettingsActivity.this, getResources().getString(R.string.locationSettingsView_toastMessage_enterCity), Toast.LENGTH_SHORT).show();
        }else{
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("SharedPreferences", getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("locationName", searchTextField.getText().toString());
            editor.commit();
            Toast.makeText(LocationSettingsActivity.this, getResources().getString(R.string.locationSettingsView_toastMessage_cityUpdated), Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Function Which helps hides Keyboard when user selects a different point on a screen.
        View v = getCurrentFocus();
        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        //Function Which hides Keyboard when user selects a different point on a screen.

        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
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
        mainRelativeLayout.setBackgroundColor(Color.BLACK);

        //===================================
        //= TOP HALF
        //===================================

        //High Level Information Container
        RelativeLayout topHalfLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams topHalfLayoutLParams = new RelativeLayout.LayoutParams(screenWidth, ((screenHeight/2) ));
        topHalfLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        topHalfLayout.setLayoutParams(topHalfLayoutLParams);
        topHalfLayout.setPadding(globalSharedMethods.convertToDIP(15), globalSharedMethods.convertToDIP(0), globalSharedMethods.convertToDIP(15), globalSharedMethods.convertToDIP(0));

        mainRelativeLayout.addView(topHalfLayout);


        //-----------------------------------
        //- Location Textbox Container
        //-----------------------------------
        //Location Container
        locationTextboxContainerLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams locationTextboxContainerLayoutLParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, globalSharedMethods.convertToDIP(60));
        locationTextboxContainerLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        locationTextboxContainerLayout.setLayoutParams(locationTextboxContainerLayoutLParams);
        topHalfLayout.addView(locationTextboxContainerLayout);

        //Bottom Border
        RelativeLayout locationSelectorWhiteBorderLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams locationSelectorWhiteBorderLayoutLParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, globalSharedMethods.convertToDIP(4));
        locationSelectorWhiteBorderLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        locationSelectorWhiteBorderLayoutLParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        locationSelectorWhiteBorderLayout.setLayoutParams(locationSelectorWhiteBorderLayoutLParams);
        locationSelectorWhiteBorderLayout.setBackgroundColor(Color.WHITE);
        locationTextboxContainerLayout.addView(locationSelectorWhiteBorderLayout);


        //Search Text Field
        searchTextField = new EditText(this);
        searchTextField.setId(View.generateViewId());
        RelativeLayout.LayoutParams searchTextFieldLParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        searchTextFieldLParams.setMargins(0, 0, globalSharedMethods.convertToDIP(50), 0);
        searchTextField.setLayoutParams(searchTextFieldLParams);
        searchTextField.setTextColor(Color.WHITE);
        searchTextField.setTextSize(18.0f);
        searchTextField.setHint(getResources().getString(R.string.locationSettingsView_enterCity));
        searchTextField.setHintTextColor(Color.WHITE); //hint color
        searchTextField.setHighlightColor(Color.WHITE);
        searchTextField.setLinkTextColor(Color.WHITE);
        searchTextField.setSingleLine();
        searchTextField.setBackgroundColor(Color.TRANSPARENT);
        searchTextField.setInputType(InputType.TYPE_CLASS_TEXT);
        searchTextField.setImeOptions(IME_ACTION_DONE);
        searchTextField.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            validateTextEntry();
                            //
                            //do something


                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });

        locationTextboxContainerLayout.addView(searchTextField);


        ImageButton applyButton= new ImageButton(this);
        RelativeLayout.LayoutParams followListButtonLParams= new RelativeLayout.LayoutParams(globalSharedMethods.convertToDIP(50),globalSharedMethods.convertToDIP(50));
        followListButtonLParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        applyButton.setAlpha(1.0f);
        applyButton.setLayoutParams(followListButtonLParams);
        applyButton.setPadding(globalSharedMethods.convertToDIP(10), globalSharedMethods.convertToDIP(10), globalSharedMethods.convertToDIP(10), globalSharedMethods.convertToDIP(10));
        applyButton.setBackgroundColor(Color.BLACK);
        applyButton.setScaleType(ImageView.ScaleType.FIT_CENTER); //Create Scalled Bitmap Alt
        applyButton.setImageResource(R.mipmap.search_icon);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateTextEntry();

            }
        });
        locationTextboxContainerLayout.addView(applyButton);


        //-----------------------------------
        //- Set To Indicator Code
        //-----------------------------------

        ///Temperature Container
        RelativeLayout setToIndicatorContainerLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams setToIndicatorContainerLayoutLParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (((screenHeight/2)-globalSharedMethods.convertToDIP(60))));
        setToIndicatorContainerLayoutLParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        setToIndicatorContainerLayout.setLayoutParams(setToIndicatorContainerLayoutLParams);
        topHalfLayout.addView(setToIndicatorContainerLayout);

        LinearLayout setToLinearLayout = new LinearLayout(this);
        setToLinearLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams setToLinearLayoutLParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        setToLinearLayoutLParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        setToLinearLayout.setLayoutParams(setToLinearLayoutLParams);
        setToLinearLayout.setGravity(Gravity.CENTER);
        setToIndicatorContainerLayout.addView(setToLinearLayout);

        //Top Indicator Label
        indicatorTopLabel = new TextView(this);
        indicatorTopLabel.setText(getResources().getString(R.string.locationSettingsView_currentlySetToLabel));
        indicatorTopLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        indicatorTopLabel.setTextColor(Color.WHITE);
        indicatorTopLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        indicatorTopLabel.setTextSize(16);
        indicatorTopLabel.setAlpha(1.0f);
        indicatorTopLabel.setPadding(30, 15, 30, 15);
        setToLinearLayout.addView(indicatorTopLabel);

        indicatorCurrentLocationLabel = new TextView(this);
        indicatorCurrentLocationLabel.setText("Loading...");
        indicatorCurrentLocationLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        indicatorCurrentLocationLabel.setTextColor(Color.WHITE);
        indicatorCurrentLocationLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        indicatorCurrentLocationLabel.setTextSize(36);
        indicatorCurrentLocationLabel.setAlpha(1.0f);
        indicatorCurrentLocationLabel.setPadding(30, 15, 30, 15);
        setToLinearLayout.addView(indicatorCurrentLocationLabel);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mainRelativeLayout.setAlpha(0.0f);
        animateInterfaceIn();
    }

    private void animateInterfaceIn(){
        //Visual Effect for Loading the Interface In
        mainRelativeLayout.setAlpha(1.0f);
        indicatorTopLabel.setAlpha(0.0f);
        indicatorCurrentLocationLabel.setAlpha(0.0f);
        indicatorCurrentLocationLabel.setScaleY(1.25f);
        indicatorCurrentLocationLabel.setScaleX(1.25f);
        locationTextboxContainerLayout.setAlpha(0.0f);

        indicatorTopLabel.animate().alpha(1.0f).setDuration(300).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                indicatorCurrentLocationLabel.animate().alpha(1.0f).setDuration(600).scaleY(1.0f).scaleX(1.0f).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        locationTextboxContainerLayout.animate().alpha(1.0f).setDuration(300).setInterpolator(new DecelerateInterpolator());
                    }
                });
            }
        });

    }
}
