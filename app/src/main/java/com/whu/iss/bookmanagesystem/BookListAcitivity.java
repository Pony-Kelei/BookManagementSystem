package com.whu.iss.bookmanagesystem;

import android.app.Activity;
import android.os.Bundle;
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


        ListView listView = (ListView) this.findViewById(R.id.listView);
        String url="http://software.whu.edu.cn:8080/quick4j/rest/books/books.json?pageSize=10&pageNo=";
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

                for(int i=0;i<titleArray.length;i++){
                    Book b=new Book();
                    b.setTitle(titleArray[i]);
                    b.setPrice(priceArray[i]);
                    b.setPublishDate(dateArray[i]);
                    b.setId(idArray[i]);
                    books.add(b);
                }

                List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
                for(Book p : books){
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put("id",p.getId());
                    item.put("bookname", p.getTitle());
                    item.put("price", p.getPrice());
                    item.put("date", p.getPublishDate());
                    data.add(item);
                }
                //创建SimpleAdapter适配器将数据绑定到item显示控件上
                SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item,
                        new String[]{"id","bookname", "price", "date"}, new int[]{R.id.id,R.id.bookname, R.id.price, R.id.date});
                //实现列表的显示
                listView.setAdapter(adapter);
                //条目点击事件
                listView.setOnItemClickListener(new ItemClickListener());
            }catch (Exception e){
                return;
            }
        }else {
            Toast.makeText(BookListAcitivity.this,"读取服务器数据失败",Toast.LENGTH_SHORT ).show();
        }
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
            if(i==jsonArray.length()-1){
                loginNames+=userName;
            }else{
                loginNames+=userName+",";
            }
        }
        return loginNames;
    }

    }
