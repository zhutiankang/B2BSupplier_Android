package com.micen.suppliers.widget.product;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.micen.suppliers.R;
import com.micen.suppliers.widget.product.PullToNextLayout.LayoutType;

public class PullToNextView extends LinearLayout
{

	private static final int ANIMATION_DURATION = 500;
	/**
	 * last y
	 */
	private int mLastMotionY;
	/**
	 * header view
	 */
	private View mHeaderView;
	/**
	 * scrollview
	 */
	private ScrollView mScrollView;

	/**
	 * webView
	 *
	 */
	private WebView mWebView;

	private GridView mGridView;

	private SwipeRefreshLayout mSwipeRefreshLayout;

	private boolean isHashPrevious = true;
	private boolean isHashNext = true;

	private PullStateE mPullStateE = PullStateE.PULL_STATE_NONE;

	private View mFootView;
	private int mTouchSlop;

	public PullToNextView(Context context)
	{
		super(context);
		init(context);
	}

	public PullToNextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	@SuppressLint("NewApi")
	public PullToNextView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}

	/**
	 * init
	 */
	private void init(Context context)
	{
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		// 需要设置成vertical
		setOrientation(LinearLayout.VERTICAL);
		hiddenHeaderView();
	}

	// 计算大小
	private void measureView(View child)
	{
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null)
		{
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0)
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		}
		else
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	private int mHeadViewHeight;

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();

		hiddenFootView();
		initContentAdapterView(this);
	}

	private ViewGroup contentView;

	private TextView headPromptTV;
	private TextView footPromptTV;

	private ImageView headPromptImage;
	private ImageView footPromptImage;

	public void setContentView(ViewGroup view)
	{
		// hiddenHeaderView();
		contentView = view;
		// 添加了内容控件.
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		addView(view, params);
		// 添加了脚部控件
		hiddenFootView();
		initContentAdapterView(contentView);

	}

	private void hiddenFootView()
	{
		mFootView = LayoutInflater.from(getContext()).inflate(R.layout.product_pull_to_next_prompt_view, null);
		measureView(mFootView);
		mHeadViewHeight = mFootView.getMeasuredHeight();

		ViewGroup.LayoutParams footParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				mHeadViewHeight);

		footPromptTV = (TextView) mFootView.findViewById(R.id.promptTV);
		footPromptImage = (ImageView) mFootView.findViewById(R.id.promptImage);

		addView(mFootView, footParams);
	}

	private void hiddenHeaderView()
	{
		mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.product_pull_to_next_prompt_view, null);
		measureView(mHeaderView);
		headPromptTV = (TextView) mHeaderView.findViewById(R.id.promptTV);
		headPromptImage = (ImageView) mHeaderView.findViewById(R.id.promptImage);

		mHeadViewHeight = mHeaderView.getMeasuredHeight();
		addView(mHeaderView);
		// 隐藏了头部
		setHeaderTopMargin(-mHeadViewHeight);
	}

	/**
	 * 设置header view 的topMargin的值
	 *
	 * @param topMargin ，为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
	 */
	private void setHeaderTopMargin(int topMargin)
	{
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		params.topMargin = topMargin;
		mHeaderView.setLayoutParams(params);
		invalidate();
	}

	public void initContentView(final FragmentManager fm, final int position)
	{

		if (contentView != null)
		{

			contentView.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					Fragment f = fm.findFragmentByTag("position" + position);

					// FrameLayout frameLayout = (FrameLayout) contentView;
					//
					// ViewGroup v = (ViewGroup) frameLayout.getRootView();
					if (f != null)
					{
						a(f.getView());

					}
				}
			}, 20);
		}

	}

	public void a(Object o)
	{

		if (o instanceof ViewGroup)
		{

			ViewGroup vp = (ViewGroup) o;

			if (o instanceof ScrollView)
			{
				mScrollView = (ScrollView) o;
				mScrollView.setOverScrollMode(OVER_SCROLL_NEVER);
			}
			else if (o instanceof WebView)
			{
				mWebView = (WebView) o;
				mWebView.setOverScrollMode(OVER_SCROLL_NEVER);
			}
			else if (o instanceof GridView)
			{
				mGridView = (GridView) o;
			}
			else if (o instanceof SwipeRefreshLayout)
			{
				mSwipeRefreshLayout = (SwipeRefreshLayout) o;
				mSwipeRefreshLayout.setOverScrollMode(OVER_SCROLL_NEVER);
			}

			for (int i = 0; i < vp.getChildCount(); i++)
			{

				a(vp.getChildAt(i));
			}
		}

	}

	public void initContentAdapterView(ViewGroup contentView)
	{
		if (contentView == null)
		{
			contentView = this;
		}
		int count = contentView.getChildCount();

		View view = null;
		View view2 = null;
		for (int i = 0; i < count; ++i)
		{
			view = contentView.getChildAt(i);

			if (view instanceof ScrollView)
			{
				// finish later
				mScrollView = (ScrollView) view;
				mScrollView.setOverScrollMode(OVER_SCROLL_NEVER);
			}
			// For home bottom layout
			else if (view instanceof FrameLayout)
			{
				for (int j = 0; j < ((FrameLayout) view).getChildCount(); ++j)
				{
					view2 = ((FrameLayout) view).getChildAt(j);
					if (view2 instanceof GridView)
					{
						mGridView = (GridView) view2;
					}
				}
			}
			// For home top layout
			else if (view instanceof SwipeRefreshLayout)
			{
				mSwipeRefreshLayout = (SwipeRefreshLayout) view;
				mSwipeRefreshLayout.setOverScrollMode(OVER_SCROLL_NEVER);
			}
		}
	}

	boolean isBlock = false;

	int downy;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e)
	{
		int y = (int) e.getRawY();
		switch (e.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			// 首先拦截down事件,记录y坐标
			mLastMotionY = y;
			downy = y;

			break;
		case MotionEvent.ACTION_MOVE:
			// deltaY > 0 是向下运动,< 0是向上运动
			int deltaY = y - downy;
			return isRefreshViewScroll(deltaY);

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			isBlock = false;
			break;
		}
		return super.onInterceptTouchEvent(e);
	}

	/*
	 * 如果在onInterceptTouchEvent()方法中没有拦截(即onInterceptTouchEvent()方法中 return false)则由PullToRefreshView
	 * 的子View来处理;否则由下面的方法来处理(即由PullToRefreshView自己来处理)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// if (mLock) {
		// return true;
		// }
		int y = (int) event.getRawY();
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			// For home bottom layout
			if (mSwipeRefreshLayout == null && mGridView != null && mGridView.getChildCount() == 0)
			{
				return true;
			}

			break;
		case MotionEvent.ACTION_MOVE:
			int deltaY = y - mLastMotionY;
			changingHeaderViewTopMargin(deltaY);
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			int topMargin = getHeaderTopMargin();

			if (topMargin < -3 * mHeadViewHeight)
			{

				if (isHashNext)
				{
					pullToNextI.next();
				}
				else
				{
					moveTo(-mHeadViewHeight, ANIMATION_DURATION);
				}

			}
			else if (topMargin > mHeadViewHeight)
			{

				if (isHashPrevious)
				{
					pullToNextI.previous();

				}
				else
				{
					moveTo(-mHeadViewHeight, ANIMATION_DURATION);
				}

			}
			else
			{
				moveTo(-mHeadViewHeight, ANIMATION_DURATION);
			}

			break;
		}
		return super.onTouchEvent(event);
	}

	@SuppressLint("NewApi")
	public void moveTo(final float i, int duration)
	{
		int topMargin = getHeaderTopMargin();
		if (Build.VERSION.SDK_INT > 10)
		{
			ValueAnimator animator = ValueAnimator.ofFloat(topMargin, i);
			animator.setDuration(duration);
			animator.setInterpolator(new DecelerateInterpolator());
			mPullStateE = PullStateE.PULL_STATE_NONE;
			animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
			{

				@TargetApi(Build.VERSION_CODES.HONEYCOMB)
				public void onAnimationUpdate(ValueAnimator valueAnimator)
				{

					float temp = (Float) valueAnimator.getAnimatedValue();

					if (temp == i)
					{
						mPullStateE = PullStateE.PULL_STATE_NONE;
					}
					setHeaderTopMargin((int) temp);
				}
			});
			animator.start();
		}
	}

	public void reset(FragmentManager fm, int position)
	{
		setHeaderTopMargin(-mHeadViewHeight);
		initContentView(fm, position);

	}

	private boolean isRefreshViewScroll(int deltaY)
	{
		if (PullStateE.PULL_STATE_REFRESH == mPullStateE)
		{
			return false;
		}
		if (mSwipeRefreshLayout != null && deltaY > mTouchSlop)
		{
			// For refresh to load data
			return false;
		}
		// 对于ScrollView
		else if (mScrollView != null)
		{
			// 子scroll view滑动到最顶端
			View child = mScrollView.getChildAt(0);

			if (deltaY > mTouchSlop && mScrollView.getScrollY() == 0)
			{
				// 向下
				mPullStateE = PullStateE.PULL_STATE_DOWN;
				return true;
			}
			else if (deltaY < -mTouchSlop &&

			child.getMeasuredHeight() <= contentView.getHeight() + mScrollView.getScrollY())
			{
				// 向上
				mPullStateE = PullStateE.PULL_STATE_UP;
				return true;
			}
		}
		else if (mWebView != null)
		{
			if (deltaY > mTouchSlop && mWebView.getScrollY() == 0)
			{
				// 向下
				mPullStateE = PullStateE.PULL_STATE_DOWN;

				return true;

			}
			else if (deltaY < -mTouchSlop
					&& (int) (mWebView.getContentHeight() * mWebView.getScale()) - mWebView.getHeight()
							- mWebView.getScrollY() == 0

			)
			{
				// 向上

				mPullStateE = PullStateE.PULL_STATE_UP;
				return true;

			}

		}
		else if (mGridView != null)
		{
			final int count = mGridView.getChildCount();
			View firstChild = mGridView.getChildAt(0);
			View lastChild = mGridView.getChildAt(count - 1);
			if (deltaY > mTouchSlop && mGridView.getFirstVisiblePosition() == 0)
			{
				if (firstChild.getTop() == mGridView.getPaddingTop())
				{
					// 向下
					mPullStateE = PullStateE.PULL_STATE_DOWN;
					return true;
				}

			}
			else if (deltaY < -mTouchSlop && mGridView.getLastVisiblePosition() == mGridView.getCount() - 1)
			{
				if (lastChild.getBottom() == mGridView.getHeight() - mGridView.getPaddingBottom())
				{
					// 向上
					mPullStateE = PullStateE.PULL_STATE_UP;
					return true;
				}
			}
		}
		else
		{
			return true;
		}

		return false;
	}

	/**
	 * 修改Header view top margin的值
	 *
	 * @param deltaY
	 */
	private int changingHeaderViewTopMargin(int deltaY)
	{

		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		float newTopMargin = params.topMargin + deltaY * 0.5f;

		if (mPullStateE == PullStateE.PULL_STATE_UP)
		{
			newTopMargin = Math.min(newTopMargin, -mHeadViewHeight);
		}
		else if (PullStateE.PULL_STATE_DOWN == mPullStateE)
		{
			newTopMargin = Math.max(newTopMargin, -mHeadViewHeight);
		}
		params.topMargin = (int) newTopMargin;
		mHeaderView.setLayoutParams(params);

		if (newTopMargin < -mHeadViewHeight)
		{
			// 下一个
			if (!isHashNext)
			{
				mFootView.setVisibility(View.INVISIBLE);
				footPromptTV.setText(R.string.no_more_data);
				// footPromptImage.setVisibility(View.GONE);
			}
			else if (newTopMargin < -mHeadViewHeight)
			{
				mFootView.setVisibility(View.INVISIBLE);
				// footPromptImage.setVisibility(View.VISIBLE);
				// if (type == LayoutType.HOME)
				// {
				// footPromptTV.setText(R.string.recent_visit_history);
				// footPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }
				// else if (type == LayoutType.PRODUCT_DETAIL)
				// {
				// footPromptTV.setText(R.string.release_to_see_product_description);
				// footPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }
				// else
				// {
				// footPromptTV.setText(R.string.release_to_see_product_description);
				// footPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }

			}
			else
			{
				mFootView.setVisibility(View.INVISIBLE);
				// footPromptImage.setVisibility(View.VISIBLE);
				// if (type == LayoutType.HOME)
				// {
				// footPromptTV.setText(R.string.recent_visit_history);
				// footPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }
				// else if (type == LayoutType.PRODUCT_DETAIL)
				// {
				// footPromptTV.setText(R.string.pull_up_to_see_product_description);
				// footPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }
				// else
				// {
				// footPromptTV.setText(R.string.pull_up_to_see_product_description);
				// footPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }

			}

		}
		else if (newTopMargin > -mHeadViewHeight)
		{

			// 前一个
			if (!isHashPrevious)
			{
				mHeaderView.setVisibility(View.INVISIBLE);
				headPromptTV.setText(R.string.no_more_data);
				headPromptImage.setVisibility(View.GONE);
			}
			else if (newTopMargin > mHeadViewHeight)
			{
				if (type == LayoutType.PRODUCT_DETAIL)
				{
					mHeaderView.setVisibility(View.VISIBLE);
					headPromptImage.setVisibility(View.VISIBLE);
					headPromptTV.setText(R.string.pull_down_to_see_product_details);
					headPromptImage.setImageResource(R.drawable.ic_pull_down);
				}
				else if (type == LayoutType.HOME)
				{
					mHeaderView.setVisibility(View.VISIBLE);
					headPromptImage.setVisibility(View.VISIBLE);
					headPromptTV.setText(R.string.pull_down_to_return_to_homepage);
					headPromptImage.setImageResource(R.drawable.ic_pull_down);
				}
				else
				{
					mHeaderView.setVisibility(View.INVISIBLE);
				}
				// headPromptImage.setVisibility(View.VISIBLE);
				//
				// if (type == LayoutType.HOME)
				// {
				// headPromptTV.setText(R.string.recent_visit_history);
				// headPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }
				// else if (type == LayoutType.PRODUCT_DETAIL)
				// {
				// headPromptTV.setText(R.string.release_to_See_other_details);
				// headPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }
				// else
				// {
				// headPromptTV.setText(R.string.release_to_See_other_details);
				// headPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }
			}
			else
			{
				if (type == LayoutType.PRODUCT_DETAIL)
				{
					mHeaderView.setVisibility(View.VISIBLE);
					headPromptImage.setVisibility(View.VISIBLE);
					headPromptTV.setText(R.string.pull_down_to_see_product_details);
					headPromptImage.setImageResource(R.drawable.ic_pull_down);
				}
				else if (type == LayoutType.HOME)
				{
					mHeaderView.setVisibility(View.VISIBLE);
					headPromptImage.setVisibility(View.VISIBLE);
					headPromptTV.setText(R.string.pull_down_to_return_to_homepage);
					headPromptImage.setImageResource(R.drawable.ic_pull_down);
				}
				else
				{
					headPromptImage.setVisibility(View.INVISIBLE);
				}
				// headPromptImage.setVisibility(View.VISIBLE);
				// if (type == LayoutType.HOME)
				// {
				// headPromptTV.setText(R.string.recent_visit_history);
				// headPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }
				// else if (type == LayoutType.PRODUCT_DETAIL)
				// {
				// headPromptTV.setText(R.string.pull_down_to_See_other_details);
				// headPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }
				// else
				// {
				// headPromptTV.setText(R.string.pull_down_to_See_other_details);
				// headPromptImage.setImageResource(R.drawable.ic_home_recent_visit_history);
				// }
			}

		}
		else
		{
			moveTo(-mHeadViewHeight, ANIMATION_DURATION);
		}

		invalidate();
		return params.topMargin;
	}

	/**
	 * 获取当前header view 的topMargin
	 */
	private int getHeaderTopMargin()
	{
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		return params.topMargin;
	}

	private PullToNextI pullToNextI;

	private LayoutType type;

	public void setPullToNextI(PullToNextI pullToNextI)
	{
		this.pullToNextI = pullToNextI;
	}

	protected interface PullToNextI
	{
		public void previous();

		public void next();

	}

	enum PullStateE
	{

		PULL_STATE_NONE, PULL_STATE_DOWN, PULL_STATE_UP, PULL_STATE_REFRESH
	}

	public void setHashPrevious(boolean isHashPrevious)
	{
		this.isHashPrevious = isHashPrevious;
	}

	public void setHashNext(boolean isHashNext)
	{
		this.isHashNext = isHashNext;
	}

	public void setLayoutType(LayoutType type)
	{
		this.type = type;
	}
}
