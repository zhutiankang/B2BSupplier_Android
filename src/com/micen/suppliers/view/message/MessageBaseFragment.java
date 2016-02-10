package com.micen.suppliers.view.message;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.State;
import com.focustech.common.widget.pulltorefresh.PullToRefreshListView;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.message.MessageActivity;
import com.micen.suppliers.activity.message.MessageDetailActivity_;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.adapter.message.MessageListAdapter;
import com.micen.suppliers.adapter.message.MessageSalesmanListAdapter;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.PopupManager;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.module.message.MessageContent;
import com.micen.suppliers.module.message.MessageSalesman;
import com.micen.suppliers.module.message.MessageSalesmans;
import com.micen.suppliers.module.message.Messages;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.umeng.analytics.MobclickAgent;


@EFragment
public abstract class MessageBaseFragment extends Fragment implements OnRefreshListener2<ListView>,
		OnItemClickListener, OnItemLongClickListener, OnClickListener
{
	@ViewById(R.id.broadcast_page_status)
	protected PageStatusView statusView;
	@ViewById(R.id.lv_mail_list)
	protected PullToRefreshListView pullToListView;
	@ViewById(R.id.progress_bar)
	protected RelativeLayout progressBar;

	protected int pageIndex = 1;
	protected MessageListAdapter adapter;
	protected ListView lvMailList;
	protected boolean isRefresh = true;
	// 获取到的邮件信息
	protected ArrayList<MessageContent> mailDataList;

	protected boolean isShowRefreshToast = true;
	protected Messages messages;
	protected boolean isLoadFirst = true;
	// 记录最后一条数据的时间戳
	protected String sendTime = "";

	protected PopupWindow selectPop;
	protected LinearLayout popDistributed;
	protected LinearLayout popDelect;
	protected CommonDialog dialog;
	protected CommonDialog distributeDialog;
	protected ArrayList<MessageSalesman> salesmanList;
	protected MessageSalesmanListAdapter salesmanAdapter;
	protected String operatorId = "";
	protected int onLongClickPosition = 0;
	protected String readStatus;
	protected String allocationStatus;

	protected String action = "";

	/**
	 * 用户所分配的业务员
	 */
	private int lastIndex = -1;

	protected OnMessageChangedListener mListener;

	public interface OnMessageChangedListener
	{
		public void setUnReadMessageNum(String unReadMessageNum);

		public void setMessageNum(boolean isShow, String messageNum);

		public String getAction();

		public String getReadStatus();

		public boolean getDistractPermission();

		public boolean getCheckUnReadMessageTAG();

		public String getFragmentTag(int pos);

		public void setUnReadMessage();

		public void setUnreadClickable(boolean isClickable);

	}

	public MessageBaseFragment()
	{

	}

	@Override
	public void onAttach(Activity activity)
	{
		mListener = (OnMessageChangedListener) activity;
		action = mListener.getAction();
		// TODO Auto-generated method stub
		super.onAttach(activity);

	}

	/**
	 * 获取子类的标示
	 * @return
	 */
	public abstract String getChildTag();

	/**
	 * 获取子类邮件的分配状态
	 * @return
	 */
	protected abstract String getAllocateStatus();

	/**
	 * 加载数据
	 * refreshUnreadData
	 */
	public abstract void onLoadData(boolean refreshUnreadData);

	@AfterViews
	protected void initView()
	{
		initBroadcast();
		pullToListView.getLoadingLayoutProxy().setLastUpdatedLabel(
				SharedPreferenceManager.getInstance().getString(getChildTag(), ""));
		pullToListView.setOnRefreshListener(this);
		pullToListView.setOnPullEventListener(pullEventListener);
		pullToListView.setMode(Mode.BOTH);
		// 不显示指示器
		pullToListView.setShowIndicator(false);
		mailDataList = new ArrayList<MessageContent>();
		lvMailList = pullToListView.getRefreshableView();
		lvMailList.setOnItemClickListener(this);
		lvMailList.setOnItemLongClickListener(this);
		// lvMailList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		statusView.setLinkOrRefreshOnClickListener(new LinkClickListener()
		{
			@Override
			public void onClick(PageStatus status)
			{
				switch (status)
				{
				case PageEmptyLink:
					startActivity(new Intent(getActivity(), PurchaseActivity_.class));
					break;
				case PageNetwork:
					progressBar.setVisibility(View.VISIBLE);
					statusView.setVisibility(View.GONE);
					isRefresh = true;
					startGetMailList();
					break;
				default:
					break;
				}
			}
		});
		// 设置请求第一页数据
		// if (getChildTag().equals(((MessageActivity) getActivity()).getFragmentTag(0)))
		// ((MessageActivity) getActivity()).changeTitleStatus(0);
		if (mListener.getCheckUnReadMessageTAG() && mListener.getDistractPermission())
		{
			if (getChildTag().equals(mListener.getFragmentTag(2)))
			{
				mListener.setUnReadMessage();
			}
		}
		else
		{
			if (getChildTag().equals(mListener.getFragmentTag(0)))
			{
				((MessageActivity) getActivity()).changeTitleStatus(0);
			}
		}

	}

	/**
	 * 注册广播
	 */
	protected void initBroadcast()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag)
				+ MessageConstantDefine.distributed.toString());
		intentFilter.addAction(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag)
				+ MessageConstantDefine.read.toString());
		intentFilter.addAction(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag)
				+ MessageConstantDefine.delete.toString());
		intentFilter.addAction(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag)
				+ MessageConstantDefine.reply.toString());
		getActivity().registerReceiver(mRefreshBroadcastReceiver, intentFilter);
	}

	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String broadcastAction = intent.getAction();
			if (!Utils.isEmpty(broadcastAction))
				if (broadcastAction.equals(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag)
						+ MessageConstantDefine.distributed.toString()))
				{
					final String distributedMailId = intent.getStringExtra(MessageConstantDefine.distributed.toString()
							+ MessageConstantDefine.mailId.toString());
					final String distributedOperatorId = intent
							.getStringExtra(MessageConstantDefine.distributedOperatorId.toString());
					refreshMessageListByDistributed(distributedMailId, distributedOperatorId);
				}
				else if (broadcastAction.equals(MessageConstantDefine
						.getValue(MessageConstantDefine.BroadcastActionTag) + MessageConstantDefine.read.toString()))
				{
					// 详情页加载到数据后，需要更新列表页中的已读状态
					final String readMailId = intent.getStringExtra(MessageConstantDefine.read.toString()
							+ MessageConstantDefine.mailId.toString());
					refreshMessageListByRead(readMailId);
				}
				else if (broadcastAction.equals(MessageConstantDefine
						.getValue(MessageConstantDefine.BroadcastActionTag) + MessageConstantDefine.delete.toString()))
				{
					// 执行删除操作
					final String deleteMailId = intent.getStringExtra(MessageConstantDefine.delete.toString()
							+ MessageConstantDefine.mailId.toString());
					refreshMessageListByDelete(deleteMailId);
				}
				else if (broadcastAction.equals(MessageConstantDefine
						.getValue(MessageConstantDefine.BroadcastActionTag) + MessageConstantDefine.reply.toString()))
				{
					// 修改回复状态
					final String replyMailId = intent.getStringExtra(MessageConstantDefine.reply.toString()
							+ MessageConstantDefine.mailId.toString());
					refreshMessageListByReply(replyMailId);
				}
		}
	};

	/**
	 * 请求邮件列表--更新请求参数
	 */
	public void startGetMailList()
	{
		if (isRefresh)
		{
			pageIndex = 1;
			sendTime = "";
		}
		allocationStatus = getAllocateStatus();
		readStatus = mListener.getReadStatus();
		mListener.setUnreadClickable(false);
		RequestCenter.getMessageList(getMailListListener, String.valueOf(pageIndex), "20", action, allocationStatus,
				readStatus, sendTime);
	}

	/**
	 * 1.获取数据成功时，更新主界面中各分类的邮件数量
	 * 方法：setMessageNum();
	 * 2.分离未读邮件信息
	 * 3.如果没有未读邮件，设置只显示未读隐藏，否则设置显示
	 * 对应方法：setUnReadShow();
	 */
	DisposeDataListener getMailListListener = new DisposeDataListener()
	{
		@Override
		public void onSuccess(Object arg0)
		{
			activityFinishProtect();
			messages = (Messages) arg0;
			refreshComplete();
			if (messages.content != null && messages.content.mail.size() != 0)
			{
				statusView.setVisibility(View.GONE);
				pullToListView.setVisibility(View.VISIBLE);
				if (isRefresh)
				{
					mailDataList.clear();
				}
				for (int i = 0; i < messages.content.mail.size(); i++)
				{
					mailDataList.add(messages.content.mail.get(i));
				}
				if (adapter == null || isRefresh)
				{
					activityFinishProtect();
					adapter = new MessageListAdapter(getActivity(), mailDataList, action);
					lvMailList.setAdapter(adapter);
				}
				else
				{
					adapter.setData(mailDataList);
				}
				// 3.sendTime 设置成最后一条数据的时间戳
				if ("1".equals(readStatus))
				{
					mListener.setUnReadMessageNum(messages.content.recentMailNum);
				}
				if (!"0".equals(messages.content.unAllocateNum))
				{
					// 设置未分配数
					mListener.setMessageNum(true, messages.content.unAllocateNum);
				}
				else
				{

					setMessageNum();
				}
				sendTime = mailDataList.get(mailDataList.size() - 1).date;
			}
			else
			{
				if (isRefresh)
				{
					if ("0".equals(action) && "0".equals(allocationStatus) && "0".equals(readStatus))
					{
						statusView.setMode(PageStatus.PageEmptyLink);
					}
					else
					{
						statusView.setMode(PageStatus.PageEmpty);
					}
					statusView.setVisibility(View.VISIBLE);
					pullToListView.setVisibility(View.GONE);
					if ("1".equals(readStatus))
					{
						mListener.setUnReadMessageNum("0");
					}
					setMessageNum();
				}
			}
			if (isRefresh)
			{
				if (isShowRefreshToast)
				{
					ToastUtil.toast(getActivity(), R.string.refresh_success);
				}
				isRefresh = false;
			}
		}

		@Override
		public void onDataAnomaly(Object failedReason)
		{
			activityFinishProtect();
			refreshComplete();
			setMessageNum();
			statusView.setVisibility(View.VISIBLE);
			statusView.setMode(PageStatus.PageEmpty);
			// 下面注释是为了让用户在第一次加载数据失败时，还可以下拉刷新
			pullToListView.setVisibility(View.GONE);
			ToastUtil.toast(getActivity(), failedReason);
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			refreshComplete();
			setMessageNum();
			if (adapter == null || isRefresh)
			{
				statusView.setVisibility(View.VISIBLE);
				statusView.setMode(PageStatus.PageNetwork);
				// 下面注释是为了让用户在第一次加载数据失败时，还可以下拉刷新
				pullToListView.setVisibility(View.GONE);
			}
			ToastUtil.toast(getActivity(), anomalyMsg);
		}
	};

	private void activityFinishProtect()
	{
		if (getActivity() == null || getActivity().isFinishing())
			return;
	}

	private void setMessageNum()
	{
		if ("1".equals(allocationStatus) && "0".equals(readStatus))
		{
			// 设置未读条数目不显示
			mListener.setMessageNum(false, "0");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id)
	{
		operatorId = mailDataList.get(pos).allocateOperatorId;
		Intent intent = new Intent(getActivity(), MessageDetailActivity_.class);
		intent.putExtra("position", String.valueOf(pos));
		intent.putParcelableArrayListExtra("mailDataList", mailDataList);
		intent.putExtra(MessageConstantDefine.action.toString(), action);
		getActivity().startActivity(intent);
		if ("0".equals(action))
		{
			MobclickAgent.onEvent(getActivity(), "17");
			SysManager.analysis(R.string.c_type_click, R.string.c017);
		}
		else
		{
			MobclickAgent.onEvent(getActivity(), "49");
			SysManager.analysis(R.string.c_type_click, R.string.c049);
		}
	}

	/**
	 * 删除对应mailId的询盘
	 * @param deleteMailId
	 */
	private void refreshMessageListByDelete(String deleteMailId)
	{
		// 传递mailId，通过mailId获取到对应的Position，进行remove
		final int delectPosition = getPositionByMailId(deleteMailId);
		if (-1 != delectPosition)
		{
			if ("0".equals(action))
			{

				if ("0".equals(mailDataList.get(delectPosition).isAllocate))
				{
					refreshUnDistributedNum();
				}

				if ("1".equals(readStatus) && "0".equals(mailDataList.get(delectPosition).isRead))
				{
					messages.content.recentMailNum = Integer.parseInt(messages.content.recentMailNum) - 1 + "";

					// 需要修改上方的未读数
					mListener.setUnReadMessageNum(messages.content.recentMailNum + "");
				}
			}
			mailDataList.remove(delectPosition);
			if (mailDataList.size() == 0)
			{
				statusView.setVisibility(View.VISIBLE);
				statusView.setMode(PageStatus.PageEmpty);
				pullToListView.setVisibility(View.GONE);
			}
			adapter.notifyDataSetChanged();

		}
	}

	/**
	 * 更新列表页中的分配业务员
	 * @param distributedMailId
	 */
	private void refreshMessageListByDistributed(String distributedMailId, String distributedOperatorId)
	{
		final int distributedPosition = getPositionByMailId(distributedMailId);
		if (-1 != distributedPosition)
		{
			mailDataList.get(distributedPosition).allocateOperatorId = distributedOperatorId;
			if ("0".equals(mailDataList.get(distributedPosition).isAllocate))
			{
				refreshUnDistributedNum();
				mailDataList.get(distributedPosition).isAllocate = "1";
			}
			// 未分配页面中删除该条数据(进行分配且成功)
			if ("1".equals(allocationStatus))
			{
				if ("1".equals(readStatus) && "0".equals(mailDataList.get(distributedPosition).isRead))
				{
					messages.content.recentMailNum = Integer.parseInt(messages.content.recentMailNum) - 1 + "";
					// 需要修改上方的未读数
					mListener.setUnReadMessageNum(messages.content.recentMailNum + "");
				}
				mailDataList.remove(distributedPosition);
				if (mailDataList.size() == 0)
				{
					statusView.setVisibility(View.VISIBLE);
					statusView.setMode(PageStatus.PageEmpty);
					pullToListView.setVisibility(View.GONE);
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 更新列表页中的是否已读状态 
	 * @param readMailId
	 */
	private void refreshMessageListByRead(String readMailId)
	{
		final int readPosition = getPositionByMailId(readMailId);
		if (-1 != readPosition)
		{
			// TODO 未读筛选下，阅读询盘后进行删除相应询盘
			// if ("1".equals(readStatus))
			// {
			// mailDataList.remove(readPosition);
			// if (mailDataList.size() == 0)
			// {
			// statusView.setVisibility(View.VISIBLE);
			// statusView.setMode(PageStatus.PageEmpty);
			// pullToListView.setVisibility(View.GONE);
			// }
			// }
			if ("0".equals(mailDataList.get(readPosition).isRead) && "1".equals(readStatus))
			{
				final int num = Integer.parseInt(messages.content.recentMailNum);
				messages.content.recentMailNum = (num - 1) + "";
				// 需要修改上方的未读数
				mListener.setUnReadMessageNum(messages.content.recentMailNum + "");
			}
			mailDataList.get(readPosition).isRead = "1";
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 更新列表页中是否已回复状态
	 * @param replyMailId
	 */
	private void refreshMessageListByReply(String replyMailId)
	{
		final int replyPosition = getPositionByMailId(replyMailId);
		if (-1 != replyPosition)
		{
			mailDataList.get(replyPosition).isReplied = "1";
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 根据mailId获取对应询盘的位置
	 * @param targetMailId
	 * @return -1：未找到对应询盘位置
	 */
	private int getPositionByMailId(String targetMailId)
	{
		int targetPosition = -1;
		for (int i = 0; i < mailDataList.size(); i++)
		{
			if (!Utils.isEmpty(targetMailId) && targetMailId.equals(mailDataList.get(i).mailId))
			{
				targetPosition = i;
				break;
			}
		}
		return targetPosition;
	}

	/**
	 * 刷新邮件列表
	 * @param isShowRefreshToast	是否显示刷新成功Toast
	 */
	public void startRefreshMailList(boolean isShowRefreshToast)
	{
		this.isShowRefreshToast = isShowRefreshToast;
		/**
		* 底层进度条显示,则不显示Dialog,底层进度条显示过后才提示对话框
		*/
		if (isLoadFirst)
		{
			startGetMailList();
			isLoadFirst = false;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
	{
		this.isRefresh = true;
		Util.saveChildLastRefreshTime(getActivity(), getChildTag());
		startGetMailList();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
	{
		pageIndex++;
		startGetMailList();
	}

	private OnPullEventListener<ListView> pullEventListener = new OnPullEventListener<ListView>()
	{
		@Override
		public void onPullEvent(PullToRefreshBase<ListView> refreshView, State state, Mode direction)
		{
			switch (direction)
			{
			case PULL_FROM_START:
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
						SharedPreferenceManager.getInstance().getString(getChildTag(), ""));
				break;
			default:
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
				break;
			}
		}
	};

	/**
	 * 刷新完成
	 */
	protected void refreshComplete()
	{
		if (getActivity() == null || getActivity().isFinishing())
			return;
		mListener.setUnreadClickable(true);
		progressBar.setVisibility(View.GONE);
		CommonProgressDialog.getInstance().dismissProgressDialog();
		if (pullToListView != null)
		{
			pullToListView.onRefreshComplete();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adaptView, View v, int pos, long id)
	{
		onLongClickPosition = pos;
		operatorId = mailDataList.get(pos).allocateOperatorId;
		// 针对不同用户，提供不同的长按机制
		if (((MessageActivity) this.getActivity()).getDistractPermission())
		{
			initPopMenu(v);
		}
		else
		{
			// 只提供删除功能
			deleteToMakeSure();
		}
		return true;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.message_item_pop_undistributed:
			// 打开分配界面,传递当前邮件ID
			selectPop.dismiss();
			getDistributeSalesman();
			MobclickAgent.onEvent(getActivity(), "18");
			SysManager.analysis(R.string.c_type_click, R.string.c018);
			break;
		case R.id.message_item_pop_delete:
			selectPop.dismiss();
			deleteToMakeSure();
			MobclickAgent.onEvent(getActivity(), "19");
			SysManager.analysis(R.string.c_type_click, R.string.c019);
			break;
		}
	}

	/**
	 * 分配操作
	 */
	private void getDistributeSalesman()
	{
		// 获取业务员数据
		CommonProgressDialog.getInstance().showCancelableProgressDialog(getActivity(),
				getActivity().getString(R.string.mic_loading));
		RequestCenter.getSalesman(getSalesmanListener);
	}

	DisposeDataListener getSalesmanListener = new DisposeDataListener()
	{
		@Override
		public void onSuccess(Object obj)
		{
			activityFinishProtect();
			if (null != distributeDialog && distributeDialog.isShowing())
				distributeDialog.dismiss();
			CommonProgressDialog.getInstance().dismissProgressDialog();
			MessageSalesmans salesmans = (MessageSalesmans) obj;
			if (salesmans.content != null && salesmans.content.size() != 0)
			{
				salesmanList = new ArrayList<MessageSalesman>();
				salesmanList.clear();
				for (int i = 0; i < salesmans.content.size(); i++)
				{
					salesmanList.add(salesmans.content.get(i));
				}
				activityFinishProtect();
				salesmanAdapter = new MessageSalesmanListAdapter(salesmanList, getActivity(), operatorId);
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				View view = inflater.inflate(R.layout.dialog_message_allocation, null);
				ListView distributedListView = (ListView) view.findViewById(R.id.message_allocation_pop_lv);
				LayoutParams params = distributedListView.getLayoutParams();
				params.width = LayoutParams.MATCH_PARENT;
				if (salesmanList.size() < 8)
				{
					params.height = LayoutParams.WRAP_CONTENT;
				}
				else
				{
					params.height = Utils.toDip(getActivity(), 365);
				}
				distributedListView.setLayoutParams(params);
				distributedListView.setAdapter(salesmanAdapter);
				distributedListView.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> arg0, View v, int pos, long id)
					{

						if (!Utils.isEmpty(operatorId))
						{

							if (!operatorId.equals(salesmanList.get(pos).operatorId))
							{
								lastIndex = pos;
								for (int i = 0; i < salesmanList.size(); i++)
								{
									if (i == pos)
									{
										salesmanList.get(i).isAllocationed = "1";
									}
									else
									{
										salesmanList.get(i).isAllocationed = "0";
									}
								}
							}
							else
							{
								lastIndex = -1;
								for (int i = 0; i < salesmanList.size(); i++)
								{
									salesmanList.get(i).isAllocationed = "0";
								}
							}
							salesmanAdapter.refreshData(salesmanList);
						}
					}
				});
				activityFinishProtect();
				distributeDialog = new CommonDialog(getActivity());
				distributeDialog.setCancelBtnText(getString(R.string.cancel)).setDialogConfirmBtnCamcel(false)
						.setConfirmBtnText(getString(R.string.confirm)).setDialogContentView(view)
						.setConfirmDialogListener(new DialogClickListener()
						{
							@Override
							public void onDialogClick()
							{
								// 进行分配操作
								// 发送网络请求，成功后，修改对应询盘的业务员ID
								if (lastIndex == -1)
								{
									distributeDialog.dismiss();
								}
								else
								{
									CommonProgressDialog.getInstance().showProgressDialog(getActivity(),
											getString(R.string.message_sent_distributing));
									String distributeOperatorId = salesmanList.get(lastIndex).operatorId;
									String distributeMailId = mailDataList.get(onLongClickPosition).mailId;
									RequestCenter.assignSalesman(assignSalesmanListener, distributeOperatorId,
											distributeMailId);
								}
							}
						}).build();
				distributeDialog.setOnDismissListener(new OnDismissListener()
				{
					@Override
					public void onDismiss(DialogInterface dialog)
					{
						lastIndex = -1;
					}
				});
			}
			else
			{
				ToastUtil.toast(getActivity(), "无有效业务员");
			}
		}

		@Override
		public void onDataAnomaly(Object failedReason)
		{
			activityFinishProtect();
			CommonProgressDialog.getInstance().dismissProgressDialog();
			ToastUtil.toast(getActivity(), failedReason);
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			CommonProgressDialog.getInstance().dismissProgressDialog();
			ToastUtil.toast(getActivity(), anomalyMsg);

		}
	};

	DisposeDataListener assignSalesmanListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			activityFinishProtect();
			ToastUtil.toast(getActivity(), getString(R.string.message_allocation_success));
			startSendDistributedBroadCast();
			distributeDialog.dismiss();
			CommonProgressDialog.getInstance().dismissProgressDialog();
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			ToastUtil.toast(getActivity(), getString(R.string.message_allocation_failed));
			CommonProgressDialog.getInstance().dismissProgressDialog();
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			ToastUtil.toast(getActivity(), getString(R.string.message_allocation_failed));
			CommonProgressDialog.getInstance().dismissProgressDialog();
		}
	};

	/**
	 * 弹出删除确定框
	 */
	private void deleteToMakeSure()
	{
		// 弹框确认是否确定删除
		if (dialog != null)
		{
			dialog.show();
		}
		else
		{
			dialog = new CommonDialog(getActivity());
			dialog.setCancelBtnText(getString(R.string.cancel)).setConfirmBtnText(getString(R.string.confirm))
					.setConfirmDialogListener(new DialogClickListener()
					{
						@Override
						public void onDialogClick()
						{

							// 进行删除操作,发送删除请求
							// 通过onLongClickPosition来获取到对应邮件标识，发送删除请求
							CommonProgressDialog.getInstance().showCancelableProgressDialog(getActivity(),
									getActivity().getString(R.string.message_deleting));
							RequestCenter.deleteMessage(deleteMessageListener,
									mailDataList.get(onLongClickPosition).mailId, action);
						}
					}).buildSimpleDialog(getString(R.string.message_make_sure_to_delect));
		}
	}

	DisposeDataListener deleteMessageListener = new DisposeDataListener()
	{
		@Override
		public void onSuccess(Object obj)
		{
			activityFinishProtect();
			startSendCommonBroadCast(MessageConstantDefine.delete.toString(),
					mailDataList.get(onLongClickPosition).mailId);
			ToastUtil.toast(getActivity(), getString(R.string.message_make_sure_to_delect_success));
			CommonProgressDialog.getInstance().dismissProgressDialog();
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			ToastUtil.toast(getActivity(), anomalyMsg);
			CommonProgressDialog.getInstance().dismissProgressDialog();
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			ToastUtil.toast(getActivity(), anomalyMsg);
			CommonProgressDialog.getInstance().dismissProgressDialog();
		}
	};

	/**
	 * 刷新未分配条数
	 */
	private void refreshUnDistributedNum()
	{
		// 本地化删除数据，更新未分配条数
		int num = Integer.parseInt(messages.content.unAllocateNum);
		messages.content.unAllocateNum = num - 1 + "";
		// 设置未读条数
		if ("0".equals(messages.content.unAllocateNum))
		{
			setMessageNum();
		}
		else
		{
			mListener.setMessageNum(true, messages.content.unAllocateNum);
		}

	}

	/**
	 * 长按事件选择pop
	 * @param v
	 */
	public void initPopMenu(View v)
	{
		int windowHeigh = Utils.getWindowHeightPix(getActivity());
		int windowWidth = Utils.getWindowWidthPix(getActivity());
		int[] viewLocation = new int[2];
		v.getLocationOnScreen(viewLocation);
		int viewY = viewLocation[1]; // y 坐标
		int xoff = windowWidth - Utils.toDip(getActivity(), 151) - 15;
		int yoff = 0;
		if (windowHeigh - viewY < Utils.toDip(getActivity(), 137))
		{
			yoff = Utils.toDip(getActivity(), -200);
		}
		else
		{
			yoff = Utils.toDip(getActivity(), -15);
		}
		selectPop = PopupManager.getInstance().showSelectTypePopupDown(getActivity(),
				getActivity().getLayoutInflater().inflate(R.layout.pop_message_list_item, null), v,
				Utils.toDip(getActivity(), 151), Utils.toDip(getActivity(), 137), xoff, yoff);
		popDistributed = (LinearLayout) selectPop.getContentView().findViewById(R.id.message_item_pop_undistributed);
		popDelect = (LinearLayout) selectPop.getContentView().findViewById(R.id.message_item_pop_delete);
		popDistributed.setOnClickListener(this);
		popDelect.setOnClickListener(this);
	}

	protected void startSendCommonBroadCast(String tag, String tagValue)
	{
		Intent intent = new Intent();
		intent.putExtra(tag + MessageConstantDefine.mailId.toString(), tagValue);
		intent.setAction(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag) + tag);
		getActivity().sendBroadcast(intent);
	}

	private void startSendDistributedBroadCast()
	{
		Intent intent = new Intent();
		intent.putExtra(MessageConstantDefine.distributed.toString() + MessageConstantDefine.mailId.toString(),
				mailDataList.get(onLongClickPosition).mailId);
		intent.putExtra(MessageConstantDefine.distributedOperatorId.toString(), salesmanList.get(lastIndex).operatorId);
		intent.setAction(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag)
				+ MessageConstantDefine.distributed.toString());
		getActivity().sendBroadcast(intent);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		getActivity().unregisterReceiver(mRefreshBroadcastReceiver);
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		if ("0".equals(action))
		{
			MobclickAgent.onPageStart(getString(R.string.p10002));
			SysManager.analysis(R.string.p_type_page, R.string.p10002);
		}
		else
		{
			MobclickAgent.onPageStart(getString(R.string.p10006));
			SysManager.analysis(R.string.p_type_page, R.string.p10006);

		}
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		if ("0".equals(action))
		{
			MobclickAgent.onPageEnd(getString(R.string.p10002));
		}
		else
		{
			MobclickAgent.onPageEnd(getString(R.string.p10006));
		}
	}
}
