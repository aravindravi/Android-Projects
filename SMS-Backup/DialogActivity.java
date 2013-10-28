package com.learning.dialogactivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.learning.dialogactivity.valueObjects.MessageClass;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DialogActivity extends Activity {

	private String replyMsg;
	private String msgAddress;
	private String msgBody;
	private String msgContact;
	private MessageClass newMsg;
	private ArrayList<MessageClass> msgArray;
	private int currentMsgIndex;
	private TextView addressView;
	private TextView bodyView;
	private TextView msgCountView;
	private MessageClass displayedMsg;
	private String msgId;
	private String msgDate;
	private TextView dateView;
	private String contactId;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Toast.makeText(DialogActivity.this, "Create", Toast.LENGTH_SHORT)
				.show();
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		msgArray = new ArrayList<MessageClass>();
		currentMsgIndex = 0;
		// setTitle("New Message");
		// setTitleColor(0x00ff00);

		Bundle b = getIntent().getExtras();
		msgAddress = b.get("address").toString();
		msgContact = getContactName(msgAddress);
		msgBody = b.get("body").toString();
		msgDate = b.get("date").toString();
		msgId = "" + b.get("msgid").toString();
		MessageClass firstMsg = new MessageClass(msgContact, msgAddress,
				msgBody, msgDate, msgId,contactId);
		msgArray.add(firstMsg);
		displayedMsg = firstMsg;
		addressView = (TextView) findViewById(R.id.addressTxt);
		addressView.setText(msgContact + " says:");
		bodyView = (TextView) findViewById(R.id.bodyTxt);
		bodyView.setText(msgBody);
		bodyView.setMovementMethod(ScrollingMovementMethod.getInstance());
		dateView = (TextView) findViewById(R.id.dateTxt);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.valueOf(firstMsg.getDate()));
		SimpleDateFormat formatter = new SimpleDateFormat("kk:mm");
		dateView.setText(formatter.format(calendar.getTime()));
		Toast.makeText(DialogActivity.this, displayedMsg.getMsgId(),
				Toast.LENGTH_SHORT).show();
		markRead();
	}

	public void markUnread(View v) {
		// Uri uri = Uri.parse("content://sms/inbox");
		// Cursor cursor = this.getContentResolver().query(
		// uri,
		// null,
		// "address=? and date>?" , new String[]
		// {displayedMsg.getAddress(),displayedMsg.getDate() }, null);
		// Log.v("Address",displayedMsg.getAddress());
		// Log.v("Date",displayedMsg.getDate());
		// if (cursor.moveToNext()) {
		// msgId = cursor.getString(cursor.getColumnIndex("_id"));
		//
		// }
		// String count=""+cursor.getCount();
		// Toast.makeText(DialogActivity.this, count,
		// Toast.LENGTH_SHORT).show();
		ContentValues values = new ContentValues();
		values.put("READ", false);
		this.getContentResolver().update(Uri.parse("content://sms/inbox"),
				values, "_id=?", new String[] { displayedMsg.getMsgId() });
		Toast.makeText(DialogActivity.this, displayedMsg.getMsgId(),
				Toast.LENGTH_SHORT).show();
	}

	private void markRead() {
		Uri uri = Uri.parse("content://sms/inbox");
		Cursor cursor = this.getContentResolver().query(uri, null, "read=0",
				null, null);
		if (cursor.moveToNext()) {
			msgId = cursor.getString(cursor.getColumnIndex("_id"));
		}
		ContentValues values = new ContentValues();
		values.put("READ", true);
		this.getContentResolver().update(Uri.parse("content://sms/inbox"),
				values, "_id=" + msgId, null);
	}

	public String getContactName(String address) {
		Uri contactUri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(address));
		Cursor cursor = this.getContentResolver().query(
				contactUri,
				new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME,
						ContactsContract.PhoneLookup._ID }, null, null, null);

		String contactName = new String();
		if (cursor.moveToFirst()) {
			contactName = cursor.getString(cursor
					.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
			contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.PhoneLookup._ID));
			Log.v("ContactId", contactId);

		} else
			contactName = address;
		cursor.close();
		return contactName;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Toast.makeText(DialogActivity.this, "New Intent", Toast.LENGTH_SHORT)
				.show();
		Bundle b = intent.getExtras();
		msgAddress = b.get("address").toString();
		msgContact = getContactName(msgAddress);
		msgBody = b.get("body").toString();
		msgDate = b.get("date").toString();
		msgId = b.get("msgid").toString();
		newMsg = new MessageClass(msgContact, msgAddress, msgBody, msgDate,
				msgId,contactId);
		// newMsg.setAddress(msgAddress);
		// newMsg.setBody(msgBody);
		// newMsg.setContact(msgContact);
		msgArray.add(newMsg);
		msgCountView = (TextView) findViewById(R.id.msgCount);
		msgCountView.setVisibility(1);
		msgCountView.setText((currentMsgIndex + 1) + "/" + msgArray.size());
		Button nextBtn = (Button) findViewById(R.id.nextBtn);
		nextBtn.setEnabled(true);
		markRead();
	}

	public void getPrevMsg(View v) {
		if (currentMsgIndex > 0) {
			displayedMsg = msgArray.get(--currentMsgIndex);
			setMessageValues(displayedMsg);
		}
	}

	public void getNextMsg(View v) {
		if (currentMsgIndex < msgArray.size()) {
			displayedMsg = msgArray.get(++currentMsgIndex);
			setMessageValues(displayedMsg);
		}

	}

	private void setMessageValues(MessageClass msg) {
		addressView.setText(msg.getContact() + " says:");
		bodyView.setText(msg.getBody());
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.valueOf(msg.getDate()));
		SimpleDateFormat formatter = new SimpleDateFormat("kk:mm");
		dateView.setText(formatter.format(calendar.getTime()));
		Button nextBtn = (Button) findViewById(R.id.nextBtn);
		Button prevBtn = (Button) findViewById(R.id.prevBtn);

		if (currentMsgIndex > 0) {
			if (!prevBtn.isEnabled())
				prevBtn.setEnabled(true);
		} else {
			if (prevBtn.isEnabled())
				prevBtn.setEnabled(false);
		}

		if (currentMsgIndex < msgArray.size() - 1) {
			if (!nextBtn.isEnabled())
				nextBtn.setEnabled(true);
		}

		else {
			if (nextBtn.isEnabled())
				nextBtn.setEnabled(false);
		}
		msgCountView.setText((currentMsgIndex + 1) + "/" + msgArray.size());

	}

	public void closeDialog(View v) {
		this.finish();
	}

	public void replySMS(View v) {
		TextView replyTxt = (TextView) findViewById(R.id.replyTxt);
		replyMsg = replyTxt.getText().toString();
		sendSMS(displayedMsg.getAddress(), replyMsg);
		replyTxt.setText(null);
	}

	private void sendSMS(String phoneNumber, String message) {
		try {
			SmsManager sms = SmsManager.getDefault();
			ArrayList<String> smsParts = sms.divideMessage(message);
			sms.sendMultipartTextMessage(phoneNumber, null, smsParts, null,
					null);
			Toast.makeText(DialogActivity.this, "Reply sent",
					Toast.LENGTH_SHORT);
			ContentValues values = new ContentValues();
			values.put("address", phoneNumber);
			values.put("body", message);
			this.getContentResolver().insert(Uri.parse("content://sms/sent"),
					values);
			// this.finish();
		} catch (Exception e) {
			Log.v("SMSFault", e.getMessage());
			Toast.makeText(DialogActivity.this, "Error:" + e.getMessage(),
					Toast.LENGTH_SHORT);
		}

	}
}
