package com.dev.customuserviewsthermics;

import com.dev.customuserviewsthermics.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnTouchListener;

public class ButtonWithImage extends LinearLayout  implements OnTouchListener {

	  private View mValue;
	  private View mBackground;
	  private ImageView mImage;
	  GradientDrawable gdStatePressed;
	  GradientDrawable gdStateNormal;
	  TextView title;
	  
	public ButtonWithImage(Context context, AttributeSet attrs) {
	    super(context, attrs);

	    //get attributes
	    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.button_with_image, 0, 0);
	    
	    //get title button
	    String buttonText = a.getString(R.styleable.button_with_image_valueTitle);
	    int buttonBackgroundColor = a.getColor(R.styleable.button_with_image_valueBackground, android.R.color.holo_blue_light);
	    int gradientColor1 = a.getColor(R.styleable.button_with_image_valueBackgroundGradient1, buttonBackgroundColor);
	    int gradientColor2 = a.getColor(R.styleable.button_with_image_valueBackgroundGradient2, buttonBackgroundColor);
	    Drawable buttonImage = a.getDrawable(R.styleable.button_with_image_valueImage);
	    
	    int buttonWidthBorder = (int)a.getDimension(R.styleable.button_with_image_valueBorderWith,1.0f);
	    
	    float imageWidth=a.getDimension(R.styleable.button_with_image_valueImageWidth,30);
	    float imageHeight=a.getDimension(R.styleable.button_with_image_valueImageHeight,30);
	    
	    a.recycle();

	    setOrientation(LinearLayout.HORIZONTAL);
	    setGravity(Gravity.CENTER_VERTICAL);

	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    //LayoutInflater inflater = LayoutInflater.from(context);
	    
	    View myv = inflater.inflate(R.layout.button_with_image, this,true);
	    //set background with border
	    mValue = getChildAt(0);   
	    //mValue = myv;
	    gdStateNormal = new GradientDrawable(Orientation.LEFT_RIGHT, new int[]{gradientColor2, gradientColor1});
	    gdStateNormal.setStroke(buttonWidthBorder, Color.BLACK);
	    
	    gdStatePressed = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{Color.LTGRAY, Color.LTGRAY});
	    gdStatePressed.setStroke(buttonWidthBorder, Color.BLACK);
	    
	    StateListDrawable states = new StateListDrawable();
	    
	    states.addState(new int[] { android.R.attr.state_pressed }, gdStatePressed);
	    states.addState( new int[] { android.R.attr.state_hovered } , gdStatePressed );
	    states.addState( new int[] {} , gdStateNormal );
	    
	    //mValue.setBackgroundDrawable(states);
	    mBackground = mValue.findViewById(R.id.layoutContent);
	    if(mBackground!=null)
	    EFViewHelper.setBackgroundDrawableStates(mBackground, gdStateNormal, gdStatePressed);
	    
	    
	    RelativeLayout layout = (RelativeLayout)mBackground;
	    
	    
	    //set image
	    mImage = (ImageView) mValue.findViewById(R.id.imageViewButtonCustom);
	    if(mImage!=null)
	    {
	    	int window_width = mValue.getLayoutParams().width;
	    	int window_height = mValue.getLayoutParams().height;
	    	float value = 0.9f;
	    	mImage.setImageDrawable(buttonImage);
	    	if( (imageWidth > window_width * value) || (imageHeight > window_height*value))
	    	{
	    		mImage.getLayoutParams().width = getPixel(imageWidth*value);
		    	mImage.getLayoutParams().height = getPixel(imageHeight*value);
	    	}
	    	else
	    	{
	    		mImage.getLayoutParams().width = getPixel(imageWidth);
		    	mImage.getLayoutParams().height = getPixel(imageHeight);
	    	}
	    	
	    	RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImage.getLayoutParams();
	    	params.setMargins(10, (window_height)/2+30, 10, 10);
	    	
	    	//mImage.setLayoutParams(params);
	    	//mImage.setY((window_height-mImage.getHeight())/2+10)
	    }
	    
	    
	    //set title
	    title = (TextView) mValue.findViewById(R.id.textViewButton);
	    if(title!=null)
	    	title.setText(buttonText); 
	    
	    mValue.setOnTouchListener(this);
	    
	  }

		public void setTitle(String text)
		{
			if(title!=null)
		    	title.setText(text); 
		}
	
	  public int getPixel(float dip)
	  {
		 Resources r = getResources();
		 return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
	  }
	  
	  public void setImageResource(int resource_id)
	  {
		  mImage.setImageResource(resource_id);
	  }
	  
	  public ButtonWithImage(Context context) {
	    this(context, null);
	  }

	  public void setValueColor(int color) {
		  mBackground.setBackgroundColor(color);
	  }

	  public void setImageVisible(boolean visible) {
	    mImage.setVisibility(visible ? View.VISIBLE : View.GONE);
	  }


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN: // нажатие
	    	mBackground.setBackgroundDrawable(gdStatePressed);
	      break;
	    case MotionEvent.ACTION_MOVE: // движение

	      break;
	    case MotionEvent.ACTION_UP: // отпускание
	    case MotionEvent.ACTION_CANCEL:  
	    	mBackground.setBackgroundDrawable(gdStateNormal);
	      break;
	    }
		return super.onTouchEvent(event);
	}
}