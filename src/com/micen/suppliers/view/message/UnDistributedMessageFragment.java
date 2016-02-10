package com.micen.suppliers.view.message;

import org.androidannotations.annotations.EFragment;

import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.micen.suppliers.R;


@EFragment(R.layout.fragment_message_list)
public class UnDistributedMessageFragment extends MessageBaseFragment
{

	/**
	 * 未分配询盘
	 */
	private static final String ALLOCATESTATUS = "1";

	public UnDistributedMessageFragment()
	{

	}

	@Override
	public String getChildTag()
	{
		return UnDistributedMessageFragment.class.getName();
	}

	@Override
	protected String getAllocateStatus()
	{
		return ALLOCATESTATUS;
	}

	@Override
	public void onLoadData(boolean refreshUnreadData)
	{
		if (refreshUnreadData)
		{
			isLoadFirst = true;
			isRefresh = true;
			if (getActivity() != null && !getActivity().isFinishing() && !progressBar.isShown()
					&& !CommonProgressDialog.getInstance().isShowing())
			{
				CommonProgressDialog.getInstance().showCancelableProgressDialog(getActivity(),
						getActivity().getString(R.string.mic_loading));
			}
		}
		startRefreshMailList(false);
	}
}
