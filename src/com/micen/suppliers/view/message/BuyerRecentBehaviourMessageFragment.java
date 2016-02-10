package com.micen.suppliers.view.message;

import java.util.ArrayList;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.adapter.message.MessageBehaviourSourcingListAdapter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.message.MessageBehaviorRecordBuyerRecentBehaviour;
import com.micen.suppliers.module.message.MessageBehaviorRecordRFQ;
import com.micen.suppliers.util.Util;
import com.umeng.analytics.MobclickAgent;


@EFragment(R.layout.fragment_message_buyer_recent_behaviour)
public class BuyerRecentBehaviourMessageFragment extends Fragment implements OnClickListener, OnItemClickListener
{
	@ViewById(R.id.mbr_behaviour_tv_year_message)
	protected TextView behaviourTvYearMessage;
	@ViewById(R.id.mbr_behaviour_tv_week_message)
	protected TextView behaviourTvWeekMessage;
	@ViewById(R.id.mbr_behaviour_tv_season_message)
	protected TextView behaviourTvSeasonMessage;

	@ViewById(R.id.mbr_behaviour_key_lv_sourcing_list)
	protected ListView KeyLvSourcingList;
	@ViewById(R.id.mbr_behaviour_key_tv_search_keyword)
	protected TextView KeyTvSearchKeyword;
	@ViewById(R.id.mbr_behaviour_key_tv_catalogue)
	protected TextView keyTvCatalogue;

	@ViewById(R.id.mbr_behaviour_season_message_ll)
	protected LinearLayout seasonMessageLl;
	@ViewById(R.id.mbr_behaviour_key_ll)
	protected LinearLayout keyLl;
	@ViewById(R.id.mbr_behaviour_key_ll_sourcing)
	protected LinearLayout keyLlSourcing;
	@ViewById(R.id.mbr_behaviour_key_ll_search_keyword)
	protected LinearLayout keyLlSearchKeyword;
	@ViewById(R.id.mbr_behaviour_key_ll_catalogue)
	protected LinearLayout keyLlCatalogue;
	@ViewById(R.id.mbr_behaviour_key_ll_search_line)
	protected View keyLlSearchLine;
	@ViewById(R.id.mbr_behaviour_key_ll_catalogue_line)
	protected View keyLlCatalogueLine;

	private boolean isLoaded = false;

	private MessageBehaviourSourcingListAdapter adapter;

	private ArrayList<MessageBehaviorRecordRFQ> rfqList;

	public BuyerRecentBehaviourMessageFragment()
	{

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.mbr_behaviour_season_message_ll:
			if (View.VISIBLE == keyLl.getVisibility())
			{
				keyLl.setVisibility(View.GONE);
				setCompoundDrawables(behaviourTvSeasonMessage, R.drawable.ic_behavior_arrows_down);
			}
			else
			{
				keyLl.setVisibility(View.VISIBLE);
				setCompoundDrawables(behaviourTvSeasonMessage, R.drawable.ic_behavior_arrows_up);
				MobclickAgent.onEvent(getActivity(), "57");
				SysManager.analysis(R.string.c_type_click, R.string.c057);
			}
			break;

		default:
			break;
		}
	}

	public void setData(MessageBehaviorRecordBuyerRecentBehaviour bean)
	{
		if (!isLoaded && bean != null)
		{
			// 填充数据
			if (!Utils.isEmpty(bean.sendInquiriesToMe))
			{
				behaviourTvYearMessage.setText("第" + bean.sendInquiriesToMe + "次给我发询盘");
			}
			else
			{
				behaviourTvYearMessage.setText("第1次给我发询盘");
			}
			if (!Utils.isEmpty(bean.peers) && !Utils.isEmpty(bean.sendPeerInquiries))
				behaviourTvWeekMessage.setText("针对" + bean.peers + "家同行发送了" + bean.sendPeerInquiries + "封询盘");
			if (!Utils.isEmpty(bean.sendInquiries))
				behaviourTvSeasonMessage.setText("向供应商累计发送" + bean.sendInquiries + "封询盘");

			if (bean.rfq != null && bean.rfq.size() != 0 || bean.recentHotKeywords != null
					&& bean.recentHotKeywords.size() != 0 || bean.recentHotcategories != null
					&& bean.recentHotcategories.size() != 0)
			{
				seasonMessageLl.setOnClickListener(this);
				if (bean.rfq != null && bean.rfq.size() != 0)
				{
					rfqList = bean.rfq;
					adapter = new MessageBehaviourSourcingListAdapter(getActivity(), rfqList);
					KeyLvSourcingList.setAdapter(adapter);
					// 计算ListView的高度
					Util.setListViewHeightBasedOnChildren(KeyLvSourcingList);
					KeyLvSourcingList.setOnItemClickListener(this);
				}
				else
				{
					keyLlSearchLine.setVisibility(View.GONE);
					keyLlSourcing.setVisibility(View.GONE);
				}
				if (bean.recentHotKeywords != null && bean.recentHotKeywords.size() != 0)
				{
					StringBuffer buffer = new StringBuffer();
					for (int i = 0; i < bean.recentHotKeywords.size(); i++)
					{
						buffer.append(bean.recentHotKeywords.get(i));
						if (bean.recentHotKeywords.size() != i)
						{

							buffer.append("\r\n");
						}
					}
					KeyTvSearchKeyword.setText(buffer.toString());
				}
				else
				{
					keyLlSearchKeyword.setVisibility(View.GONE);
					keyLlSearchLine.setVisibility(View.GONE);
				}
				if (bean.recentHotcategories != null && bean.recentHotcategories.size() != 0)
				{
					StringBuffer buffer = new StringBuffer();
					for (int i = 0; i < bean.recentHotcategories.size(); i++)
					{
						buffer.append(bean.recentHotcategories.get(i));
						if (bean.recentHotcategories.size() != i)
						{

							buffer.append("\r\n");
						}
					}
					keyTvCatalogue.setText(buffer.toString());
				}
				else
				{
					keyLlCatalogue.setVisibility(View.GONE);
					keyLlCatalogueLine.setVisibility(View.GONE);
				}
			}
			else
			{
				seasonMessageLl.setEnabled(false);
				setCompoundDrawables(behaviourTvSeasonMessage, R.color.transparent);
			}
			isLoaded = true;
		}

	}

	/**
	 * 设置Title部分的名称右侧图片
	 * @param id 图片资源：R.drawable.xxx
	 */
	private void setCompoundDrawables(TextView v, int id)
	{
		Drawable drawable = getResources().getDrawable(id);
		// 必须设置图片大小，否则不显示
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		v.setCompoundDrawables(null, null, drawable, null);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int pos, long id)
	{
		if ("1".equals(rfqList.get(pos).rfqStatus))
		{
			// 用户点击item，获取到对应项的url，进行跳转
			// 需要进行判断，是否有对应的url
			Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
			intent.putExtra("fragment", "rfqinfo");
			// intent.putExtra("fragment", "rfqneedquote");
			intent.putExtra("rfqid", rfqList.get(pos).rfqId);
			// TODO 需要接口提供字段
			intent.putExtra("isrecommend", "0");
			intent.putExtra("quoteSource", "1");
			startActivity(intent);
		}
		else
		{
			ToastUtil.toast(getActivity(), rfqList.get(pos).reason);
		}
		MobclickAgent.onEvent(getActivity(), "58");
		SysManager.analysis(R.string.c_type_click, R.string.c058);
	}

}
