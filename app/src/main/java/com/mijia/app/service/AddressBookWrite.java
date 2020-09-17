package com.mijia.app.service;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mijia.app.MyApp;
import com.mijia.app.bean.PhoneBean;
import com.mijia.app.utils.PhoneUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class AddressBookWrite {

    private Gson mGson = new Gson();
    private List<PhoneBean> phoneList;

    public AddressBookWrite() {
        PhoneUtil phoneUtil = new PhoneUtil(Utils.getApp());
        phoneList = phoneUtil.getPhone();
    }

    public boolean write(String absolutePath) {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            InputStreamReader inputreader = new InputStreamReader(new FileInputStream(absolutePath), "UTF-8");
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line = "";
            //分行读取
            while ((line = buffreader.readLine()) != null) {
                stringBuffer.append(line);
            }
            List<LinkedTreeMap<String, String>> list = mGson.fromJson(stringBuffer.toString(), List.class);


            for (LinkedTreeMap<String, String> map : list) {
                if (!containsBookList(map)) {
                    // 插入数据
//                    addConnetion(map);
//                    testAddContacts(map.get("personName"),map.get("phoneNumber"));
                    addContact(MyApp.getInstance(),map.get("personName"),map.get("phoneNumber"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private boolean containsBookList(LinkedTreeMap<String, String> phone) {
        if (phoneList == null || phoneList.isEmpty())
            return false;

        String name = phone.get("personName");
        String number = phone.get("phoneNumber");

        for (PhoneBean locationPhone : phoneList) {
            String locationName = locationPhone.getPersonName();
            String locationNumber = locationPhone.getPhoneNumber();
            if (TextUtils.equals(name, locationName) && TextUtils.equals(number, locationNumber)) {
                return true;
            }
        }
        return false;
    }


    //一步一步添加数据
    public void testAddContacts(String name,String number){
        //插入raw_contacts表，并获取_id属性
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = MyApp.getInstance().getContentResolver();
        ContentValues values = new ContentValues();
        long contact_id = ContentUris.parseId(resolver.insert(uri, values));
        //插入data表
        uri = Uri.parse("content://com.android.contacts/data");
        //add Name
        values.put("raw_contact_id", contact_id);
        values.put(ContactsContract.Data.MIMETYPE,"vnd.android.cursor.item/name");
        values.put("data2", "zdong");
        values.put("data1", "xzdong");
        resolver.insert(uri, values);
        values.clear();
        //add Phone
        values.put("raw_contact_id", contact_id);
        values.put(ContactsContract.Data.MIMETYPE,"vnd.android.cursor.item/phone_v2");
        values.put("data2", "2");   //手机
        values.put("data1", "87654321");
        resolver.insert(uri, values);
        values.clear();
        //add email
        values.put("raw_contact_id", contact_id);
        values.put(ContactsContract.Data.MIMETYPE,"vnd.android.cursor.item/email_v2");
        values.put("data2", "2");   //单位
        values.put("data1", "xzdong@xzdong.com");
        resolver.insert(uri, values);
    }

    public static void addContact(Context context, String name, String phoneNumber) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();

        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        // 向联系人URI添加联系人名字
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        // 电话类型
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        // 向联系人电话号码URI添加电话号码
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        //以下为插入e-mail信息，不需要可以注释掉
//        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
//        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
//        // 联系人的Email地址
//        values.put(ContactsContract.CommonDataKinds.Email.DATA, "zhangphil@xxx.com");
//        // 电子邮件的类型
//        values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
//        // 向联系人Email URI添加Email数据
//        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

//        Toast.makeText(context, "联系人数据添加成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 添加信息到通讯录
     */
    public void addConnetion(LinkedTreeMap<String, String>  phoneBean) {
        //2 获取数据
        String name = phoneBean.get("personName");
        String phone =  phoneBean.get("phoneNumber");

        //2.1 定义uri   raw:原始的
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        //2.2 先查询一下raw_contacts表中一共有几条数据 行数+1 就是contact_id的值
        ContentResolver contentResolver = Utils.getApp().getContentResolver();

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        int count = cursor.getCount();
        int contact_id = count + 1; // 代表当前联系人的id

        //3 把数据插入到联系人数据库，由于联系人的数据库也是通过内容提供者暴露出来，所以我们直接通过内容解析者去操作数据库
        ContentValues values = new ContentValues();
        values.put("contact_id", contact_id);
        contentResolver.insert(uri, values);

        //4 把name,phone,email插入到data表
        ContentValues nameValues = new ContentValues();
        nameValues.put("data1", name); // 把数据插入到data1列
        nameValues.put("raw_contact_id", contact_id); // 告诉数据库我们插入的数据属于哪条联系人
        nameValues.put("mimetype", "vnd.android.cursor.item/name"); // 告诉数据库插入的数据的数据类型
        contentResolver.insert(dataUri, nameValues);

        //5 把phone 插入到data表
        ContentValues phoneValues = new ContentValues();
        phoneValues.put("data2", phone); // 把数据插入到data1列
        phoneValues.put("raw_contact_id", contact_id); // 告诉数据库我们插入的数据属于哪条联系人
        phoneValues.put("mimetype", "vnd.android.cursor.item/phone_v2"); // 告诉数据库插入的数据的数据类型
        contentResolver.insert(dataUri, phoneValues);

    }

}
