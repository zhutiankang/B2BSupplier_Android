package com.micen.suppliers.view.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.micen.suppliers.R;


public class QRCodeResultFragment extends Fragment implements OnClickListener
{
	private LinearLayout llTitleBack;
	private TextView tvName;
	private ImageView ivBack;
	private TextView tvContent;
	private TextView tvContentUrlOPen;

	private boolean isHtmlUrl = false;
	private String qrCodeContent;
	private String URL_REGEXP = "^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$";

	public static QRCodeResultFragment newInstance(String qrCodeContent)
	{
		QRCodeResultFragment fragment = new QRCodeResultFragment();
		Bundle bundle = new Bundle();
		bundle.putString("qrCodeContent", qrCodeContent);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Bundle bundle = getArguments();
		qrCodeContent = bundle.getString("qrCodeContent");
		// Pattern p = Pattern.compile(URL_REGEXP, Pattern.CASE_INSENSITIVE);
		// Matcher m = p.matcher(qrCodeContent);
		// if (m.find())
		// {
		// isHtmlUrl = true;
		// }
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_qrcode_result, null);
		initView(view);
		return view;
	}

	private void initView(View view)
	{
		llTitleBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		tvName = (TextView) view.findViewById(R.id.common_title_name);
		tvContent = (TextView) view.findViewById(R.id.qrcode_result_content);
		ivBack.setBackgroundResource(R.drawable.ic_title_back);
		if (isHtmlUrl)
		{
			tvName.setText(getResources().getString(R.string.qrcode_title_name_html_url));
			tvContentUrlOPen = (TextView) view.findViewById(R.id.qrcode_result_content_open);
			tvContentUrlOPen.setVisibility(View.VISIBLE);
			tvContentUrlOPen.setOnClickListener(this);
		}
		else
		{
			tvName.setText(getResources().getString(R.string.qrcode_title_name_text_content));
		}
		llTitleBack.setOnClickListener(this);
		llTitleBack.setBackgroundResource(R.drawable.bg_common_btn);
		tvContent.setText(qrCodeContent);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.common_ll_title_back:
			getActivity().setResult(Activity.RESULT_OK);
			getActivity().finish();
			break;
		case R.id.qrcode_result_content_open:
			getActivity().startActivity(
					new Intent().setAction("android.intent.action.VIEW").setData(Uri.parse(qrCodeContent)));
			break;
		}

	}

}
