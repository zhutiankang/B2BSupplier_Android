package com.micen.suppliers.view.purchase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.micen.suppliers.R;
import com.micen.suppliers.util.ImageUtil;


public class ImageBrowserFragment extends Fragment implements OnClickListener
{
	private ImageView ivBack;
	private LinearLayout llBack;
	private RelativeLayout rlTitle;

	private String imUri;
	private ImageView iv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_image_browser, container, false);
		initView(view);
		return view;
	}

	private void initView(View view)
	{
		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		llBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		ivBack.setImageResource(R.drawable.ic_title_back_blue);
		llBack.setOnClickListener(this);
		llBack.setBackgroundResource(R.drawable.bg_common_white_btn);

		rlTitle = (RelativeLayout) view.findViewById(R.id.rl_common_title);

		rlTitle.setBackgroundColor(getResources().getColor(R.color.color_ffffff));
		iv = (ImageView) view.findViewById(R.id.image_browser_iv);
		ImageUtil.getImageLoader().displayImage(imUri, iv, ImageUtil.getRecommendImageOptions());
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null)
		{
			imUri = bundle.getString("imUri");
		}
	}

	public static ImageBrowserFragment newInstance(String imUri)
	{
		ImageBrowserFragment fragment = new ImageBrowserFragment();
		Bundle bundle = new Bundle();
		bundle.putString("imUri", imUri);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onClick(View v)
	{
		getActivity().finish();
	}
}
