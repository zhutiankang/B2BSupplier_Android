package com.micen.suppliers.module.message;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageContent implements Parcelable
{
	/**
	 * 分配者ID
	 */
	public String allocateOperatorId;

	public String date;
	
	/**
	 * 是否已经分配
	 */
	public String isAllocate;

	/**
	 * 是否包含附件
	 */
	public String isAttached;

	/**
	 * 是否已经回复
	 */
	public String isReplied;

	/**
	 * 是否未读
	 */
	public String isRead;

	/**
	 * 标示发件箱中的邮件收件人是否已读，0-未读，1-已读，""-未知
	 */
	public String receiverReadFlag;

	public String mailId;

	public MessageReceiver receiver;

	public MessageSender sender;

	public String subject;

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(allocateOperatorId);
		dest.writeString(mailId);
		dest.writeString(date);
		dest.writeString(isAllocate);
		dest.writeString(isRead);
		dest.writeString(isReplied);
		dest.writeString(receiverReadFlag);
		dest.writeParcelable(receiver, flags);
		dest.writeParcelable(sender, flags);
		dest.writeString(subject);

	}

	public static final Parcelable.Creator<MessageContent> CREATOR = new Parcelable.Creator<MessageContent>()
	{
		public MessageContent createFromParcel(Parcel in)
		{
			MessageContent messageContent = new MessageContent();
			messageContent.allocateOperatorId = in.readString();
			messageContent.mailId = in.readString();
			messageContent.date = in.readString();
			messageContent.isAllocate = in.readString();
			messageContent.isRead = in.readString();
			messageContent.isReplied = in.readString();
			messageContent.receiverReadFlag = in.readString();
			messageContent.receiver = in.readParcelable(MessageReceiver.class.getClassLoader());
			messageContent.sender = in.readParcelable(MessageSender.class.getClassLoader());
			messageContent.subject = in.readString();
			return messageContent;
		}

		public MessageContent[] newArray(int size)
		{
			return new MessageContent[size];
		}
	};

}
