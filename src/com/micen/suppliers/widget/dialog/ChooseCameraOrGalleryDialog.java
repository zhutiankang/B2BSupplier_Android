package com.micen.suppliers.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.micen.suppliers.R;


public class ChooseCameraOrGalleryDialog extends DialogFragment
{

	private TextView tvGallery;
	private TextView tvCamera;

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.purchase_dialog_choosefromgalleryTextView:
				setResult(1);

				break;
			case R.id.purchase_dialog_takephotosTextView:
				setResult(2);
				break;
			default:
				break;
			}

		}
	};

	public static ChooseCameraOrGalleryDialog newInstance()
	{
		ChooseCameraOrGalleryDialog dialog = new ChooseCameraOrGalleryDialog();
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// builder.setAdapter(adapter, clickListener);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_purchase_dialog_choose_pic, null);
		tvGallery = (TextView) view.findViewById(R.id.purchase_dialog_choosefromgalleryTextView);
		tvCamera = (TextView) view.findViewById(R.id.purchase_dialog_takephotosTextView);

		tvGallery.setOnClickListener(clickListener);
		tvCamera.setOnClickListener(clickListener);
		// builder.setSingleChoiceItems(adapter, -1, clickListener);
		builder.setView(view);
		return builder.create();
	}

	// 设置返回数据
	protected void setResult(int which)
	{
		// 判断是否设置了targetFragment
		if (getTargetFragment() == null)
			return;

		Intent intent = new Intent();
		intent.putExtra("choose", which);
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
		dismiss();

	}

}
