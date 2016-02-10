package com.micen.suppliers.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micen.suppliers.R;


public class PageStatusView extends RelativeLayout implements OnClickListener
{
	private int pageStatusLogo;
	private String pageStatusText;
	private String pageStatusLinkText;
	private int pageStatusLinkTextImg;
	private String pageStatusLinkTextprefixion;
	// 后缀
	private String pageStatusLinkTextsuffix;

	private LinearLayout emptyLayout;
	private ImageView emptyLogo;
	private TextView emptyMsg;
	private TextView emptyLink;
	private TextView emptyLinksuffix;
	private LinearLayout networkLayout;
	private TextView networkRefresh;

	private LinkClickListener listener;
	private PageStatus status;

	public interface LinkClickListener
	{
		public void onClick(PageStatus status);
	}

	public enum PageStatus
	{
		PageEmpty, PageEmptyLink, PageNetwork;
	}

	public PageStatusView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initAttributeSet(context, attrs);
		initView();
	}

	public PageStatusView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initAttributeSet(context, attrs);
		initView();
	}

	private void initAttributeSet(Context context, AttributeSet attrs)
	{
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageStatusView);
		pageStatusLogo = a.getResourceId(R.styleable.PageStatusView_pageStatusLogo, R.drawable.ic_network_error);
		pageStatusLinkTextImg = a.getResourceId(R.styleable.PageStatusView_pageStatusLinkTextImg,
				R.drawable.ic_network_error);
		pageStatusText = a.getString(R.styleable.PageStatusView_pageStatusText);
		pageStatusLinkText = a.getString(R.styleable.PageStatusView_pageStatusLinkText);
		pageStatusLinkTextprefixion = a.getString(R.styleable.PageStatusView_pageStatusLinkTextprefixion);
		pageStatusLinkTextsuffix = a.getString(R.styleable.PageStatusView_pageStatusLinkTextsuffix);
		a.recycle();
	}

	private void initView()
	{
		LayoutInflater.from(getContext()).inflate(R.layout.page_status_layout, this);
		emptyLayout = (LinearLayout) findViewById(R.id.page_empty_layout);
		emptyLogo = (ImageView) findViewById(R.id.page_empty_logo);
		emptyMsg = (TextView) findViewById(R.id.page_empty_msg);
		emptyLink = (TextView) findViewById(R.id.page_empty_link);
		emptyLinksuffix = (TextView) findViewById(R.id.page_empty_link_suffix);
		networkLayout = (LinearLayout) findViewById(R.id.page_network_layout);
		networkRefresh = (TextView) findViewById(R.id.page_network_refresh);

		emptyLogo.setBackgroundResource(pageStatusLogo);
		emptyMsg.setText(pageStatusText);
		emptyLink.setText(pageStatusLinkText);
		emptyLinksuffix.setText(pageStatusLinkTextsuffix);

		emptyLink.setOnClickListener(this);
		networkRefresh.setOnClickListener(this);
	}

	public void setMode(PageStatus status)
	{
		this.status = status;
		switch (status)
		{
		case PageEmpty:
			emptyLayout.setVisibility(View.VISIBLE);
			networkLayout.setVisibility(View.GONE);
			emptyLink.setVisibility(View.GONE);
			emptyLinksuffix.setVisibility(View.GONE);
			emptyLogo.setBackgroundResource(pageStatusLogo);
			emptyMsg.setText(pageStatusText);
			break;
		case PageEmptyLink:
			emptyLink.setVisibility(View.VISIBLE);
			emptyLayout.setVisibility(View.VISIBLE);
			networkLayout.setVisibility(View.GONE);
			emptyLink.setVisibility(View.VISIBLE);
			emptyLinksuffix.setVisibility(View.VISIBLE);
			emptyLogo.setBackgroundResource(pageStatusLinkTextImg);
			emptyMsg.setText(pageStatusLinkTextprefixion);
			break;
		case PageNetwork:
			emptyLink.setVisibility(View.GONE);
			emptyLinksuffix.setVisibility(View.GONE);
			emptyLayout.setVisibility(View.GONE);
			networkLayout.setVisibility(View.VISIBLE);
			break;
		}
	}

	public void setLinkOrRefreshOnClickListener(LinkClickListener listener)
	{
		this.listener = listener;
	}

	@Override
	public void onClick(View v)
	{
		if (listener != null)
		{
			listener.onClick(status);
		}
	}

}
