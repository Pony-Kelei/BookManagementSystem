package com.whu.iss.bookmanagesystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kelei on 2017/5/27.
 */

public class BookListAcitivity extends Activity {
    private Handler handler;
    List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
    ListView listView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booklist);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

        listView = (ListView) this.findViewById(R.id.listView);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("请稍候，正在读取信息...");
        progressDialog.show();

        new BookListAcitivity.LoadListThread().start();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                progressDialog.dismiss();
                if(msg.what==1){
                    //创建SimpleAdapter适配器将数据绑定到item显示控件上
                    SimpleAdapter adapter = new SimpleAdapter(BookListAcitivity.this, data, R.layout.item,
                            new String[]{"id","bookname", "price", "date"}, new int[]{R.id.id,R.id.bookname, R.id.price, R.id.date});
                    //实现列表的显示
                    listView.setAdapter(adapter);
                    //条目点击事件
                    listView.setOnItemClickListener(new ItemClickListener());
                }else {
                    Toast.makeText(BookListAcitivity.this, "读取服务器数据失败，请检查你的网络连接", Toast.LENGTH_LONG).show();
                }
            }
        };
    }
    //获取点击事件
    private final class ItemClickListener implements AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);
            String personid = data.get("id").toString();
            Toast.makeText(getApplicationContext(), personid, Toast.LENGTH_LONG).show();
        }
    }

    public class LoadListThread extends Thread{
        public void run(){
            String url="http://221.180.249.223:8888/quick4j/rest/books/books.json?pageSize=10&pageNo=";
            Message m=Message.obtain();
            try{
                String res="";
                for(int i=1;i<5000;i++){
                    String temp=HttpRequest.sendGet(url+i);
                    if(temp.equals("[]")){
                        break;
                    }
                    res+=temp+",";
                }
                if(!res.isEmpty()){
                    List<Book> books=new LinkedList<>();
                    res=res.substring(0,res.length()-1);
                    try{
                        String titles=jsonTest2(res,"bookname");
                        String prices=jsonTest2(res,"price");
                        String dates=jsonTest2(res,"years");
                        String ids=jsonTest2(res,"id");

                        String []titleArray=titles.split(",");
                        String []dateArray=dates.split(",");
                        String []priceArray=prices.split(",");
                        String []idArray=ids.split(",");

                        for(int i=0;i<dateArray.length;i++){
                            HashMap<String, Object> item = new HashMap<String, Object>();
                            item.put("id",idArray[i]);
                            item.put("bookname",titleArray[i]);
                            item.put("price", priceArray[i]);
                            item.put("date", dateArray[i]);
                            data.add(item);
                        }
//                m.obj=bookNum;
                m.what=1;
            }catch (Exception e){
                e.printStackTrace();
            }
            handler.sendMessage(m);
        }
    }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String jsonTest2(String content,String key) throws JSONException {
        content=content.replace("[","");
        content=content.replace("]","");
        String content1="{\"books\":["+content;
        content1+="]}";
        JSONObject json= new JSONObject(content1);
        JSONArray jsonArray=json.getJSONArray("books");
        String loginNames="";
        for(int i=0;i<jsonArray.length();i++){
            JSONObject user=(JSONObject) jsonArray.get(i);
            String userName=String.valueOf(user.get(key));
            if(userName.isEmpty()){
                userName="NULL";
            }
            if(i==jsonArray.length()-1){
                loginNames+=userName;
            }else{
                loginNames+=userName+",";
            }
        }
        return loginNames;
    }

    }
