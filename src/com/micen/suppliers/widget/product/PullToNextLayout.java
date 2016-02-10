package com.micen.suppliers.widget.product;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.micen.suppliers.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class PullToNextLayout extends FrameLayout
{

	private static final int ANIMATION_DURATION = 500;

	private boolean isAnimating;

	private PullToNextAdapter mAdapter;

	private OnItemSelectListener mOnItemSelectListener;

	private int bgColor;

	public enum LayoutType
	{
		HOME, PRODUCT_DETAIL;
	}

	/**
	 * Type to determine tips of pulling up and down
	 */
	private LayoutType type;

	public void setLayoutType(LayoutType type)
	{
		this.type = type;
	}

	// 用于产品详情页，判断当前显示是哪一个页面
	public boolean isPrevious;

	private PullToNextView.PullToNextI mPullToNextI = new PullToNextView.PullToNextI()
	{
		@Override
		public void previous()
		{
			PullToNextLayout.this.previous();
			isPrevious = true;
		}

		@Override
		public void next()
		{
			PullToNextLayout.this.next();
			isPrevious = false;
		}
	};

	public PullToNextLayout(Context context)
	{
		super(context);
		init(null);
	}

	public PullToNextLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs);
	}

	public PullToNextLayout(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs)
	{

		if (attrs != null)
		{
			TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PullToNextLayout);

			bgColor = typedArray.getColor(R.styleable.PullToNextLayout_underLayoutColor,
					getResources().getColor(R.color.color_f2f2f2));

			typedArray.recycle();
		}
		else
		{
			bgColor = getResources().getColor(R.color.color_f2f2f2);
		}
		currentPTE = newPullToNextView(R.id.contentFL1);
		previousPTE = newPullToNextView(R.id.contentFL2);
		nextPTE = newPullToNextView(R.id.contentFL3);
		setUnderLayerBackground(bgColor);
	}

	public void setUnderLayerBackground(int bgColor)
	{
		this.setBackgroundColor(bgColor);
		setPullToNextViewBackground(currentPTE, bgColor);
		setPullToNextViewBackground(previousPTE, bgColor);
		setPullToNextViewBackground(nextPTE, bgColor);
	}

	private void setPullToNextViewBackground(PullToNextEntity pte, int bgColor)
	{
		if (null != pte.getPullToNextView())
		{
			pte.getPullToNextView().setBackgroundColor(bgColor);
		}
	}

	private PullToNextEntity currentPTE;
	private PullToNextEntity previousPTE;
	private PullToNextEntity nextPTE;

	// private PullToNextEntity originalCurrentPTE;
	// private PullToNextEntity originalPreviousPTE;
	// private PullToNextEntity originalNextPTE;

	public void setAdapter(PullToNextAdapter pullToNextAdapter)
	{

		this.setAdapter(pullToNextAdapter, 0);
	}

	public void setAdapter(PullToNextAdapter pullToNextAdapter, int currentIndex)
	{

		if (null == pullToNextAdapter || pullToNextAdapter.getCount() <= 0)
		{
			return;
		}

		if (currentIndex < 0)
		{
			currentIndex = 0;
		}
		else if (currentIndex >= pullToNextAdapter.getCount())
		{
			currentIndex = pullToNextAdapter.getCount() - 1;
		}

		removeAllViews();
		this.mAdapter = pullToNextAdapter;
		currentPTE = newPullToNextView(R.id.contentFL1);
		// originalCurrentPTE = currentPTE;
		previousPTE = newPullToNextView(R.id.contentFL2);
		// originalPreviousPTE = previousPTE;
		nextPTE = newPullToNextView(R.id.contentFL3);
		// originalNextPTE = nextPTE;
		currentPTE.setPosition(currentIndex);
		addPullToNextView(0, currentPTE);
		invalidateView(currentIndex);
	}

	private void invalidateView(int mCurItem)
	{

		if (mCurItem - 1 >= 0)
		{
			// 前面的 Fragment
			previousPTE.setPosition(mCurItem - 1);
			addPullToNextView(0, previousPTE);
			previousPTE.reset(mAdapter.getFm());
		}
		if (mCurItem + 1 >= 0 && mCurItem + 1 < mAdapter.getCount())
		{
			// 后面 Fragment
			nextPTE.setPosition(mCurItem + 1);
			nextPTE.reset(mAdapter.getFm());
			addPullToNextView(0, nextPTE);

		}
		currentPTE.reset(mAdapter.getFm());
		if (null != mOnItemSelectListener)
		{
			mOnItemSelectListener.onSelectItem(currentPTE.getPosition(), currentPTE.getPullToNextView());
		}

	}

	/**
	 * 初始化 PullToNextEntity实体
	 *
	 * @param frameLayoutId
	 * @return
	 */
	public PullToNextEntity newPullToNextView(int frameLayoutId)
	{

		PullToNextEntity entity = new PullToNextEntity();

		PullToNextView pullToNextView = new PullToNextView(getContext());
		FrameLayout frameLayout = new FrameLayout(getContext());
		frameLayout.setId(frameLayoutId);
		pullToNextView.setContentView(frameLayout);
		pullToNextView.setBackgroundColor(getResources().getColor(R.color.color_f2f2f2));
		pullToNextView.setPullToNextI(mPullToNextI);
		pullToNextView.setTag(frameLayoutId);
		pullToNextView.setLayoutType(type);
		entity.setFrameId(frameLayoutId);
		entity.setPullToNextView(pullToNextView);
		return entity;
	}

	/**
	 * 将pullToNextView 添加到 PullToNextLayout 控件中
	 *
	 * @param index
	 * @param pullToNextView
	 */
	public void addPullToNextView(int index, PullToNextEntity pullToNextView)
	{

		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		View view = pullToNextView.getPullToNextView();
		if (view.getParent() != null && (view.getParent() instanceof ViewGroup))
		{
			ViewGroup viewGroup = (ViewGroup) view.getParent();
			viewGroup.removeView(view);
		}
		this.addView(pullToNextView.getPullToNextView(), index, layoutParams);
		FragmentTransaction transaction = mAdapter.getFm().beginTransaction();
		Fragment f = mAdapter.getFm().findFragmentByTag("position" + pullToNextView.getPosition());
		if (f != null)
		{
			// TODO
			// if (f instanceof HomeTopFragment)
			// {
			//
			// transaction.replace(pullToNextView.getFrameId(), new HomeTopFragment_(),
			// "position" + pullToNextView.getPosition());
			// }
			// else if (f instanceof HomeBottomFragment)
			// {
			// transaction.replace(pullToNextView.getFrameId(), new HomeBottomFragment_(),
			// "position" + pullToNextView.getPosition());
			// }
			// else
			// {
			// transaction.attach(f);
			// }
		}
		else
		{
			transaction.add(pullToNextView.getFrameId(), mAdapter.getItem(pullToNextView.getPosition()), "position"
					+ pullToNextView.getPosition());
		}

		transaction.commitAllowingStateLoss();

		if (pullToNextView.getPosition() == 0)
		{
			pullToNextView.getPullToNextView().setHashPrevious(false);
		}
		else
		{
			pullToNextView.getPullToNextView().setHashPrevious(true);
		}
		if (pullToNextView.getPosition() == mAdapter.getCount() - 1)
		{
			pullToNextView.getPullToNextView().setHashNext(false);
		}
		else
		{
			pullToNextView.getPullToNextView().setHashNext(true);
		}
	}

	public void previous()
	{

		remove(nextPTE);

		ObjectAnimator out = ObjectAnimator.ofFloat(currentPTE.getPullToNextView(), "translationY", 0,
				getMeasuredHeight());

		out.setDuration(ANIMATION_DURATION);
		out.addListener(new Animator.AnimatorListener()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				isAnimating = true;
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{

				removeView(currentPTE.getPullToNextView());
				PullToNextEntity tempPTE = currentPTE;

				PullToNextEntity temp2PTE = nextPTE;

				currentPTE = previousPTE;
				previousPTE = temp2PTE;
				nextPTE = tempPTE;
				isAnimating = false;
				invalidateView(currentPTE.getPosition());

			}

			@Override
			public void onAnimationCancel(Animator animation)
			{

			}

			@Override
			public void onAnimationRepeat(Animator animation)
			{

			}
		});

		ObjectAnimator in = ObjectAnimator.ofFloat(previousPTE.getPullToNextView(), "translationY",
				-getMeasuredHeight(), 0);
		in.setDuration(ANIMATION_DURATION);
		AnimatorSet set = new AnimatorSet();
		set.setInterpolator(new DecelerateInterpolator());
		set.playTogether(out, in);
		set.start();

	}

	private void remove(PullToNextEntity pullToNextEntity)
	{

		if (pullToNextEntity.getPullToNextView() != null)
		{

			if (pullToNextEntity.isAttach())
			{

				removeView(pullToNextEntity.getPullToNextView());

				FragmentTransaction transaction = mAdapter.getFm().beginTransaction();

				Fragment f = mAdapter.getFm().findFragmentByTag("position" + pullToNextEntity.getPosition());

				if (f != null)
				{
					mAdapter.getFm().beginTransaction().detach(f).commit();

				}

			}
		}

	}

	private void next()
	{

		remove(previousPTE);

		ObjectAnimator out = ObjectAnimator.ofFloat(currentPTE.getPullToNextView(), "translationY", 0,
				-getMeasuredHeight());
		out.setDuration(ANIMATION_DURATION);
		out.addListener(new Animator.AnimatorListener()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				isAnimating = true;
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{

				removeView(currentPTE.getPullToNextView());

				PullToNextEntity tempPTE = currentPTE;

				PullToNextEntity temp2PTE = previousPTE;

				currentPTE = nextPTE;
				previousPTE = tempPTE;

				nextPTE = temp2PTE;
				isAnimating = false;

				invalidateView(currentPTE.getPosition());

			}

			@Override
			public void onAnimationCancel(Animator animation)
			{

			}

			@Override
			public void onAnimationRepeat(Animator animation)
			{

			}
		});
		ObjectAnimator in = ObjectAnimator.ofFloat(nextPTE.getPullToNextView(), "translationY", getMeasuredHeight(), 0);
		in.setDuration(ANIMATION_DURATION);
		AnimatorSet set = new AnimatorSet();
		set.setDuration(ANIMATION_DURATION);
		set.setInterpolator(new DecelerateInterpolator());
		set.playTogether(out, in);
		set.start();

	}

	public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener)
	{
		this.mOnItemSelectListener = onItemSelectListener;

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if (isAnimating)
		{
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

}
