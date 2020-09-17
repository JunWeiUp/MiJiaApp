package com.mijia.app.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.mijia.app.bean.PhoneBean;

import java.util.ArrayList;
import java.util.List;

public class PhoneUtil {


    // 号码
    public final static String NUM = ContactsContract.CommonDataKinds.Phone.NUMBER;
    // 联系人姓名
    public final static String NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

    //上下文对象
    private Context context;
    //联系人提供者的uri
    private Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    public PhoneUtil(Context context) {
        this.context = context;
    }

    //获取所有联系人
    public List<PhoneBean> getPhone() {
        List<PhoneBean> phoneDtos = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(phoneUri, new String[]{NUM, NAME}, null, null, null);
        while (cursor.moveToNext()) {
            PhoneBean phoneDto = new PhoneBean(cursor.getString(cursor.getColumnIndex(NAME)), cursor.getString(cursor.getColumnIndex(NUM)));
            phoneDtos.add(phoneDto);
        }
        return phoneDtos;
    }

    public void addContactPhoneNumber(Context context,String contactName, String phoneNumber) {
        ContentValues values = new ContentValues();
        Uri uri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long contact_id = ContentUris.parseId(uri);

        //插入data表
        uri = ContactsContract.Data.CONTENT_URI;
        String raw_contact_id = ContactsContract.Data.RAW_CONTACT_ID;
        String data2 = ContactsContract.Data.DATA2;
        String data1 = ContactsContract.Data.DATA1;
        //add Name
        values.put(raw_contact_id, contact_id);
        values.put(ContactsContract.CommonDataKinds.Phone.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(data2, contactName);
        values.put(data1, contactName);
        context.getContentResolver().insert(uri, values);
        values.clear();
        //add Phone
        values.put(raw_contact_id, contact_id);
        values.put(ContactsContract.CommonDataKinds.Phone.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(data2, phoneNumber);   //手机
        values.put(data1, phoneNumber);
        context.getContentResolver().insert(uri, values);
        values.clear();
        Toast.makeText(context, "添加新的联系人：" + contactName + " " + phoneNumber, Toast.LENGTH_SHORT).show();
    }


}
