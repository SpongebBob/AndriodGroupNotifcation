package com.code.bmj.groupnotifycation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.code.bmj.com.code.bmj.tool.DataConvertTools;
import com.code.bmj.com.code.bmj.tool.TestComparator;
import com.ricky.database.CenterDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by SpongeBob_PC on 2015/8/25.
 */
public class   NotificationDetailActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    private static int rg_id = 2;
    private static final int REFRESH_COMPLETE = 0X110;
    private SwipeRefreshLayout mSwipeLayout;
    private RelativeLayout iv_back;
    String name = "";
    String title = "";
    String content = "";
    String time = "";
    String type = "";
    String joinername = "";
    String status_all = "";
    String sever_id = "";
    private RadioGroup rg;
    private TextView tv_creattime;
    private TextView tv_name;
    private TextView tv_title;
    private TextView tv_content;
    private Button bt_status;
    private TextView bt_fresh;
    private ListView list_all;
    private SimpleAdapter simpleAdapter;
    private WebSocketConnection mmConnection = new WebSocketConnection();
    private WebSocketConnection updateConnection;
    private WebSocketConnection changeconnection;
    private WebSocketConnection confirmconnection;

    private Boolean isConnected = false;
    private Boolean isupdateConnected = false;
    private String type11uri = NotificationConfig.BMJ_IP;
    String[] new_statuslist = null;
    String[] new_timelist = null;
    String[] new_namelist = null;

    public void confirm()
    {
        try {
            confirmconnection.connect(type11uri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    JSONObject request3 = new JSONObject();
                    try {
                        request3.put("type", "11");
                        request3.put("cmd", "changestatus");
                        CenterDatabase cd = new CenterDatabase(NotificationDetailActivity.this, null);
                        String creater_id = cd.getUID();
                        cd.close();
                        request3.put("uid", creater_id);
                        request3.put("id", sever_id);
                        request3.put("after", rg_id + "");
                        String s = request3.toString();
                        Log.d("test","changestatus:"+s);
                        confirmconnection.sendTextMessage(s);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
               //    Toast.makeText(getApplicationContext(), "尝试确认", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d("test","changge_fanhui"+payload);
                    String strUTF8 = null;
                    Toast.makeText(getApplicationContext(), "已提交回执", Toast.LENGTH_SHORT).show();
                    try {
                        strUTF8 = URLDecoder.decode(payload, "UTF-8");
                        JSONObject root = new JSONObject(strUTF8);
                        String after_type = root.getString("after");
                        SQLiteDatabase db;
                        db = (new N_LocalDataBase(getApplicationContext(), null)).getDataBase();
                        Cursor c = db.rawQuery("select * from group_notification where server_id='" + sever_id + "'", null);
                        if (c.getCount() > 0) {
                            c.moveToFirst();
                            name = c.getString(c.getColumnIndex("creatorName"));
                            title = c.getString(c.getColumnIndex("title"));
                            content = c.getString(c.getColumnIndex("content"));
                            time = c.getString(c.getColumnIndex("create_time"));
                            joinername = c.getString(c.getColumnIndex("joinerName"));
                            status_all = c.getString(c.getColumnIndex("status"));
                            sever_id = c.getString(c.getColumnIndex("server_id"));
                            String[] split_joinid = c.getString(c.getColumnIndex("joinerID")).split(",");
                            CenterDatabase cd = new CenterDatabase(NotificationDetailActivity.this, null);
                            String creater_id = cd.getUID();
                            cd.close();

                            int count = 0;
                            for (String st : split_joinid) {
                                if (st.equals(creater_id)) break;
                                count++;
                            }

                            String[] split_status = status_all.split(",");
                            if (split_status[count].equals("0") && after_type.equals("1")) {
                                split_status[count] = "1";
                            }
                            else   split_status[count] = rg_id+"";
//                            if (split_status[count].equals("1") && after_type.equals("2")) {
//                                split_status[count] = "2";
//                                Toast.makeText(getApplicationContext(), "已确认", Toast.LENGTH_SHORT).show();
//                            }
                            String newstatus = "";
                            for (int i = 0; i < split_status.length; i++) {
                                newstatus = newstatus + split_status[i] + ",";
                            }
                            newstatus = newstatus.substring(0, newstatus.length() - 1);
                           // Toast.makeText(getApplicationContext(), "更新status" + newstatus, Toast.LENGTH_SHORT).show();
                            db.execSQL("update group_notification set status='" + newstatus + "' where server_id='" + sever_id + "'");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //更新状态。
//                    if(!updateConnection.isConnected())
//                        initdetaillist();
//                    else{
//                        JSONObject request2 = new JSONObject();
//                        try {
//                            request2.put("type", "11");
//                            request2.put("id", sever_id);
//                            request2.put("cmd", "updatestatus");
//                            updateConnection.sendTextMessage(request2.toString());
//
//                         //   Toast.makeText(getApplicationContext(),"请求更新"+request2.toString(), Toast.LENGTH_SHORT).show();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }

                }

                @Override
                public void onClose(int code, String reason) {
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }

    }
    public void changestatus() {
        try {
            changeconnection.connect(type11uri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    isConnected = true;
                    JSONObject request3 = new JSONObject();
                    try {
                        SQLiteDatabase db;
                        db = (new N_LocalDataBase(getApplicationContext(), null)).getDataBase();
                        Cursor c = db.rawQuery("select * from group_notification where server_id='" + sever_id + "'", null);
                        if (c.getCount() > 0) {
                            c.moveToLast();
                            String ifread = c.getString(c.getColumnIndex("read"));
                         //   Toast.makeText(getApplicationContext(), "ifread:"+ifread, Toast.LENGTH_SHORT).show();

                            if(ifread==null||ifread.equals("false"))
                            {

                                request3.put("type", "11");
                                request3.put("cmd", "changestatus");
                                CenterDatabase cd = new CenterDatabase(NotificationDetailActivity.this, null);
                                String creater_id = cd.getUID();
                                cd.close();

                                request3.put("uid", creater_id);
                                request3.put("id", sever_id);
                                request3.put("after", "1");
                                db.execSQL("update group_notification set read='true' where server_id='" + sever_id + "'");
                            //    Toast.makeText(getApplicationContext(), "测试", Toast.LENGTH_SHORT).show();
                                String s = request3.toString();
                                changeconnection.sendTextMessage(s);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onTextMessage(final String payload) {
                    String strUTF8 = null;
                    try {
                        strUTF8 = URLDecoder.decode(payload, "UTF-8");
                        JSONObject root = new JSONObject(strUTF8);
                        String after_type = root.getString("after");
                        SQLiteDatabase db;
                        db = (new N_LocalDataBase(getApplicationContext(), null)).getDataBase();
                        Cursor c = db.rawQuery("select * from group_notification where server_id='" + sever_id + "'", null);
                        if (c.getCount() > 0) {
                            c.moveToFirst();
                            name = c.getString(c.getColumnIndex("creatorName"));
                            title = c.getString(c.getColumnIndex("title"));
                            content = c.getString(c.getColumnIndex("content"));
                            time = c.getString(c.getColumnIndex("create_time"));
                            joinername = c.getString(c.getColumnIndex("joinerName"));
                            status_all = c.getString(c.getColumnIndex("status"));
                            sever_id = c.getString(c.getColumnIndex("server_id"));
                            String[] split_joinid = c.getString(c.getColumnIndex("joinerID")).split(",");
                            CenterDatabase cd = new CenterDatabase(NotificationDetailActivity.this, null);
                            String creater_id = cd.getUID();
                            cd.close();
                            int count = 0;
                            for (String st : split_joinid) {
                                if (st.equals(creater_id)) break;
                                count++;
                            }

                            String[] split_status = status_all.split(",");
                            if (split_status[count].equals("0") && after_type.equals("1")) {
                                split_status[count] = "1";
                            }
                            if (split_status[count].equals("1") && after_type.equals("2")) {
                                split_status[count] = "2";
                             //   Toast.makeText(getApplicationContext(), "已确认", Toast.LENGTH_SHORT).show();
                            }
                            String newstatus = "";
                            for (int i = 0; i < split_status.length; i++) {
                                newstatus = newstatus + split_status[i] + ",";
                            }
                            newstatus = newstatus.substring(0, newstatus.length() - 1);
                        //    Toast.makeText(getApplicationContext(), "更新status" + newstatus, Toast.LENGTH_SHORT).show();
                            db.execSQL("update group_notification set status='" + newstatus + "' where server_id='" + sever_id + "'");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //防止并行线程没有更新

                }

                @Override
                public void onClose(int code, String reason) {
                    isConnected = false;
                }
            });
        } catch (WebSocketException e) {
        }
    }

    public void initdetaillist() {
        try {
            updateConnection.connect(type11uri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    isupdateConnected = true;
                    JSONObject request2 = new JSONObject();
                    try {
                        request2.put("type", "11");
                        request2.put("id", sever_id);
                        Log.d("test",sever_id);
                        request2.put("cmd", "updatestatus");
                        updateConnection.sendTextMessage(request2.toString());

                       // Toast.makeText(getApplicationContext(),"请求更新"+request2.toString(), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d("test", "群通知Json"+payload);
                    String strUTF8 = null;
                    try {
                        strUTF8 = URLDecoder.decode(payload, "UTF-8");
                        JSONObject root = new JSONObject(strUTF8);
                        String new_status = root.getString("statuslist");
                        String new_time = root.getString("timelist");
                        new_statuslist = new_status.split(",");
                        new_timelist = new_time.split(",");

                        String ready_status = "";
                        for (String num : new_statuslist) {
                            if (num.equals("0")) ready_status = ready_status + "未读,";
                            if (num.equals("1")) ready_status = ready_status + "已读,";
                            if (num.equals("2")) ready_status = ready_status + "已确认,";
                            if (num.equals("3")) ready_status = ready_status + "知道了,";
                            if (num.equals("4")) ready_status = ready_status + "不同意,";

                        }
                        ready_status = ready_status.substring(0, ready_status.length() - 1);
                        String[] split_ready_status = ready_status.split(",");
                        List<Map<String, Object>> listitems = new ArrayList<Map<String, Object>>();
                        for (int i = 0; i < new_statuslist.length; i++) {
                            Map<String, Object> item = new HashMap<String, Object>();
                            item.put("name", new_namelist[i]);
                            item.put("status_id", new_statuslist[i]);
                            item.put("status", split_ready_status[i]);
                            item.put("time_id", new_timelist[i]);
                            item.put("time", gethowlong(new_timelist[i]));
                            if(split_ready_status[i].equals("已读"))
                                item.put("ifread",R.drawable.notificationgreen);
                            else if (split_ready_status[i].equals("未读"))
                                item.put("ifread",R.drawable.notificationred);
                            else if (split_ready_status[i].equals("已确认"))
                                item.put("ifread",R.drawable.notificationyellow);
                            else item.put("ifread",R.drawable.notificationyellow);
                            listitems.add(item);

                        }
                        Collections.sort(listitems,  new MyComparator());


                        simpleAdapter = new SimpleAdapter(getApplicationContext(), listitems, R.layout.member_detail_item,
                                new String[]{"ifread","name", "status", "time"},
                                new int[]{R.id.timeline_item,R.id.member_name1, R.id.member_status, R.id.member_time});
                        list_all.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();
                        mSwipeLayout.setRefreshing(false);

                        // Toast.makeText(getApplicationContext(),"列表加载完成", Toast.LENGTH_SHORT).show();


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    isupdateConnected = false;
                }
            });
        } catch (WebSocketException e) {
          //  Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    private void initdate() {
        Intent intent = getIntent();
        String item_id = intent.getStringExtra("item_id");

        SQLiteDatabase db;
        db = (new N_LocalDataBase(getApplicationContext(), null)).getDataBase();
        Cursor c = db.rawQuery("select * from group_notification where _id='" + item_id + "'", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            name = c.getString(c.getColumnIndex("creatorName"));
            title = c.getString(c.getColumnIndex("title"));
            content = c.getString(c.getColumnIndex("content"));
            time = c.getString(c.getColumnIndex("create_time"));
            joinername = c.getString(c.getColumnIndex("joinerName"));
            status_all = c.getString(c.getColumnIndex("status"));
            sever_id = c.getString(c.getColumnIndex("server_id"));
            type = c.getString(c.getColumnIndex("type"));

            if(type.equals("receive"))
            {
                TextView statelist = (TextView) findViewById(R.id.tx_statelist);
                statelist.setVisibility(View.GONE);
                ListView lst = (ListView) findViewById(R.id.notification_detail_list);
                lst.setVisibility(View.GONE);


            }else
            {
                Button button_ok = (Button) findViewById(R.id.button_ok);
                button_ok.setVisibility(View.GONE);
                rg.setVisibility(View.GONE);
                TextView tx_huizhi = (TextView) findViewById(R.id.tx_huizhi);
                tx_huizhi.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(),"加载数据...", Toast.LENGTH_SHORT).show();
            }

            String[] split_name = joinername.split(",");
            new_namelist = split_name;

            list_all = (ListView) findViewById(R.id.notification_detail_list);
            changestatus();
            initdetaillist();
        }
        c.close();
        tv_creattime = (TextView) findViewById(R.id.textview_notifation_detail_time);
        tv_name = (TextView) findViewById(R.id.textview_notifation_detail_name);
        tv_title = (TextView) findViewById(R.id.textview_notifation_detail_lable);
        tv_content = (TextView) findViewById(R.id.textview_notifation_detail_content);
        tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());
        bt_status = (Button) findViewById(R.id.button_ok);

        tv_content.setText(content);
        tv_creattime.setText(gethowlong(time));
        tv_title.setText(title);
        tv_name.setText(name);

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notification_detail);

        changeconnection = new WebSocketConnection();
        updateConnection = new WebSocketConnection();
        confirmconnection = new WebSocketConnection();
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        rg = (RadioGroup) findViewById(R.id.rg);
        mSwipeLayout.setOnRefreshListener(this);
        initdate();

        bt_status = (Button) findViewById(R.id.button_ok);
        bt_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(!confirmconnection.isConnected())
                confirm();
                else{
                   JSONObject request3 = new JSONObject();
                   try {
                       request3.put("type", "11");
                       request3.put("cmd", "changestatus");
                       CenterDatabase cd = new CenterDatabase(NotificationDetailActivity.this, null);
                       String creater_id = cd.getUID();
                       cd.close();
                       request3.put("uid", creater_id);
                       request3.put("id", sever_id);
                       request3.put("after", rg_id+"");
                       String s = request3.toString();
                       Log.d("test","changestatus:"+"s");
                       confirmconnection.sendTextMessage(s);

                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
            }
        });
        iv_back = (RelativeLayout) findViewById(R.id.back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
               if(radioGroup.getCheckedRadioButtonId() == R.id.rgbtn_0) rg_id=2;
                else if(radioGroup.getCheckedRadioButtonId() == R.id.rgbtn_1) rg_id=3;
                else if (radioGroup.getCheckedRadioButtonId()== R.id.rgbtn_2) rg_id=4;
               // Toast.makeText(getApplicationContext(), rg_id+"", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public static String gethowlong(String st){
        if(st.equals(""))
            return "";
        long time=Long.valueOf(st);
        Date d1=new Date(time*1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d1);
        Calendar now=Calendar.getInstance();

        Date d2=now.getTime();
        long diff = d2.getTime() - d1.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
        long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
        if(calendar.get(Calendar.YEAR)!=now.get(Calendar.YEAR))
            return calendar.get(Calendar.YEAR)+"年"+(calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日";
        else if(days>5)
            return DataConvertTools.Calendar2String(calendar,0);
        else if(days>0)
            return DataConvertTools.Calendar2String(calendar,0);
        else if(hours>0)
            return DataConvertTools.Calendar2String(calendar,0);
        else if(minutes>0)
            return DataConvertTools.Calendar2String(calendar,0);
        else
            return "刚刚";
    }
    public static Date String2Date(String date){
        Date DateTime=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
        try {
            DateTime=sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateTime;
    }
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case REFRESH_COMPLETE:

                    mSwipeLayout.setRefreshing(false);
                    break;

            }
        };
    };


    @Override
    public void onRefresh() {
        if(!updateConnection.isConnected())
            initdetaillist();
        else{
            JSONObject request2 = new JSONObject();
            try {
                request2.put("type", "11");
                request2.put("id", sever_id);
                request2.put("cmd", "updatestatus");
                updateConnection.sendTextMessage(request2.toString());

                //  Toast.makeText(getApplicationContext(),"请求更新"+request2.toString(), Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }
}

class MyComparator implements Comparator<Map<String, Object>> {
    @Override
    public int compare(Map<String, Object> o1, Map<String, Object> o2) {

        String status1 = (String) o1.get("status_id");
        String status2 = (String) o2.get("status_id");
        String time1 = (String) o1.get("time_id");
        String time2 = (String) o2.get("time_id");
        if(status1.compareTo(status2)>0)
        {
            return -1;
        }else if (status1.compareTo(status2)<0)
        {
            return 1;
        }
        else
            return(-time1.compareTo(time2));

    }
}
