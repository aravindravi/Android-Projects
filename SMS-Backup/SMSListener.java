package com.learning.dialogactivity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSListener extends BroadcastReceiver {
	SmsMessage[] msg;
	private String msgAddress = new String();
	private String msgBody = new String();
	private String msgDate;
	private String msgId;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		// String msgId=new String();
		 Uri uri = Uri.parse("content://sms/inbox");
		 Cursor cursor = context.getContentResolver().query(uri, null, null,
		 null, null);
		 if(cursor.moveToNext())
		 {
		 msgId = cursor.getString(cursor.getColumnIndex("_id"));
		 }
		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			msg = new SmsMessage[pdus.length];

			for (int i = 0; i < msg.length; i++) {
				msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				msgAddress = msg[i].getOriginatingAddress();
				msgBody += msg[i].getMessageBody().toString();
				
				// Log.v("MSG ID", msgId);
			}
			
			msgDate = ""+msg[0].getTimestampMillis();

			// ContentValues values = new ContentValues();
			// values.put("read", true);
			// context.getContentResolver().update(Uri.parse("content://sms/inbox"),
			// values, "_id=" + msgId, null);

			// Uri contactUri = Uri.withAppendedPath(
			// ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
			// Uri.encode(msgAddress));
			// Cursor cursor = context.getContentResolver().query(contactUri,
			// new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME },
			// null, null, null);
			//
			// if (cursor.moveToFirst())
			// contactName = cursor
			// .getString(cursor
			// .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
			//
			// else
			// contactName = msgAddress;
			Intent dialogIntent = new Intent(context, DialogActivity.class);
			dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK	| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// dialogIntent.putExtra("contact", contactName);
			dialogIntent.putExtra("address", msgAddress);
			dialogIntent.putExtra("body", msgBody);
			dialogIntent.putExtra("date", msgDate);
			dialogIntent.putExtra("msgid", msgId);
			// dialogIntent.putExtra("msgid", msgId);
			context.startActivity(dialogIntent);

		}

	}
}
