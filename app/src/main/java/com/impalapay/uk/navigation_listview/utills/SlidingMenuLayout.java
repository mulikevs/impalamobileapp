package com.impalapay.uk.navigation_listview.utills;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SlidingMenuLayout extends LinearLayout
{

	private static final int SLIDING_DURATION = 500;
	private static final int QUERY_INTERVAL = 16;
	int mainLayoutWidth;
	private View menu;
	private View content;
	private static int menuRightMargin = 50;

	private enum MenuState 
	{
		HIDING, HIDDEN, SHOWING, SHOWN,
	};

	private int contentXOffset;
	private MenuState currentMenuState = MenuState.HIDDEN;
	private Scroller menuScroller = new Scroller(this.getContext(),new EaseInInterpolator());
	private Runnable menuRunnable = new MenuRunnable();
	private Handler menuHandler = new Handler();
	int prevX = 0;
	boolean isDragging = false;
	int lastDiffX = 0;

	public SlidingMenuLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		//getParent().requestDisallowInterceptTouchEvent(true);
	}

	public SlidingMenuLayout(Context context) 
	{
		
		super(context);
		//getParent().requestDisallowInterceptTouchEvent(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		mainLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
		menuRightMargin = mainLayoutWidth* 23 / 100;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		menu = this.getChildAt(0);
		content = this.getChildAt(1);
		/*content.setOnTouchListener(new OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				return SlidingMenuLayout.this.onContentTouch(v, event);
			}
		});*/
		
		menu.setVisibility(View.GONE);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,	int bottom)
	{
		if (changed) 
		{
			LayoutParams contentLayoutParams = (LayoutParams) content.getLayoutParams();
			contentLayoutParams.height = this.getHeight();
			contentLayoutParams.width = this.getWidth();
			LayoutParams menuLayoutParams = (LayoutParams) menu.getLayoutParams();
			menuLayoutParams.height = this.getHeight();
			menuLayoutParams.width = this.getWidth() - menuRightMargin;
		}
		menu.layout(left, top, right - menuRightMargin, bottom);
		content.layout(left + contentXOffset, top, right + contentXOffset,bottom);

	}

	public void toggleMenu()
	{

		if (currentMenuState == MenuState.HIDING || currentMenuState == MenuState.SHOWING)
			return;

		switch (currentMenuState) 
		{
			case HIDDEN:
				currentMenuState = MenuState.SHOWING;
				menu.setVisibility(View.VISIBLE);
	
				menuScroller.startScroll(0, 0, menu.getLayoutParams().width, 0, SLIDING_DURATION);
				break;
				
			case SHOWN:
				currentMenuState = MenuState.HIDING;
				menuScroller.startScroll(contentXOffset, 0, -contentXOffset, 0, SLIDING_DURATION);
				break;
			default:
				break;
		}
		menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
		this.invalidate();
	}

	protected class MenuRunnable implements Runnable {
		@Override
		public void run() {
			boolean isScrolling = menuScroller.computeScrollOffset();
			adjustContentPosition(isScrolling);
		}
	}

	private void adjustContentPosition(boolean isScrolling) {
		int scrollerXOffset = menuScroller.getCurrX();

		content.offsetLeftAndRight(scrollerXOffset - contentXOffset);

		contentXOffset = scrollerXOffset;
		this.invalidate();
		if (isScrolling)
			menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
		else
			this.onMenuSlidingComplete();
	}

	private void onMenuSlidingComplete() 
	{
		switch (currentMenuState)
		{
		case SHOWING:
			currentMenuState = MenuState.SHOWN;
			break;
			
		case HIDING:
			currentMenuState = MenuState.HIDDEN;
			menu.setVisibility(View.GONE);
			break;
		default:
			return;
		}
	}

	protected class EaseInInterpolator implements Interpolator
	{
		@Override
		public float getInterpolation(float t) 
		{
			return (float) Math.pow(t - 1, 5) + 1;
		}

	}

	public boolean isMenuShown()
	{
		return currentMenuState == MenuState.SHOWN;
	}

	
}
