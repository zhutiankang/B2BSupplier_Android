package com.micen.suppliers.view.message;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.message.MessageBehaviorRecordActivity;
import com.micen.suppliers.module.message.MessageBehaviorRecordBuyerComInfo;


@EFragment(R.layout.fragment_message_buyer_cominfo)
public class BuyerComInfoMessageFragment extends Fragment
{
	@ViewById(R.id.mbr_cominfo_tv_company)
	protected TextView cominfoTvCompany;
	@ViewById(R.id.mbr_cominfo_tv_tel)
	protected TextView cominfoTvTel;
	@ViewById(R.id.mbr_cominfo_tv_faxes)
	protected TextView cominfoTvFaxes;
	@ViewById(R.id.mbr_cominfo_tv_homepage)
	protected TextView cominfoTvHomepage;
	@ViewById(R.id.mbr_cominfo_tv_email)
	protected TextView cominfoTvEmail;

	private boolean isLoaded = false;

	public BuyerComInfoMessageFragment()
	{

	}

	@AfterViews
	protected void initView()
	{
		((MessageBehaviorRecordActivity) getActivity()).getContactsInfo();
	}

	public void setData(MessageBehaviorRecordBuyerComInfo bean)
	{
		if (!isLoaded && bean != null)
		{
			// 填充数据
			if (!Utils.isEmpty(bean.companyName))
				cominfoTvCompany.setText(bean.companyName);
			if (!Utils.isEmpty(bean.tel))
				cominfoTvTel.setText(bean.tel);
			if (!Utils.isEmpty(bean.fax))
				cominfoTvFaxes.setText(bean.fax);
			if (!Utils.isEmpty(bean.homePage))
				cominfoTvHomepage.setText(bean.homePage);
			if (!Utils.isEmpty(bean.email))
				cominfoTvEmail.setText(bean.email);
			isLoaded = true;
		}
	}

}
