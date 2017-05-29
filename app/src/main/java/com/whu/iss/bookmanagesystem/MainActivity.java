package com.whu.iss.bookmanagesystem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.ActionBar;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 *
 * @version 1.0 whu.iss
 * Created by kelei on 2017/5/27.
 */

import com.whu.iss.bookmanagesystem.util.BookUtil;

public class MainActivity extends AppCompatActivity {

    private Button scanButton;
    private Button queryButton;
    private TextView t1;
    private ProgressDialog progressDialog;
    private Handler handler;
    private String bookNum="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

        t1=(TextView)findViewById(R.id.textView);
        scanButton=(Button) findViewById(R.id.button1);
        queryButton=(Button)findViewById(R.id.button2);
        new MainActivity.ChangTextViewThread().start();

//        String url="http://221.180.249.223:8888/quick4j/rest/books/number";
//        bookNum=HttpRequest.sendGet(url);
//
//        if(bookNum.isEmpty()){
//            bookNum="0";
//        }
     //   t1.setText(Html.fromHtml("目前系统内共有图书<font color=\"#FF0000\"><b><tt>"+bookNum+"</tt></b></font>本"));

        //接收来自下载线程的消息
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        Book book= (Book)msg.obj;
                        //进度条消失
                        progressDialog.dismiss();
                        if(book==null){
                            Toast.makeText(MainActivity.this, "没有找到这本书", Toast.LENGTH_LONG).show();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                        else{
                            Intent intent=new Intent(MainActivity.this,BookViewActivity.class);
                            //通过Intent 传递 Object，需要让该实体类实现Parceable接口
                            intent.putExtra(Book.class.getName(),book);
                            startActivity(intent);
                        }
                        break;
                    case 2:
                        String num=(String)msg.obj;
                        if(num.isEmpty()){
                            bookNum="0";
                        }
                        t1.setText(Html.fromHtml("目前系统内共有图书<font color=\"#FF0000\"><b><tt>"+bookNum+"</tt></b></font>本"));
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "异常", Toast.LENGTH_LONG).show();
                }
            }
        };
        scanButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ShowToast") @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this,"扫码",Toast.LENGTH_SHORT ).show();
                Intent intent=new Intent(MainActivity.this,CaptureActivity.class);
                startActivityForResult(intent,100);
            }
        });
        queryButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ShowToast") @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this,"查询中，请稍后",Toast.LENGTH_SHORT ).show();
                Intent intent=new Intent(MainActivity.this,BookListAcitivity.class);
                startActivity(intent);
            }
        });

    }

    /*
     *	从MainActivity 开启扫描跳到 CaptureActivity，扫描到ISBN 返回到 MainActivity
     *	返回的 ISBN码 绑定在Intent中
     */
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(((requestCode==100)||(resultCode== Activity.RESULT_OK))&&data!=null){
            //判断网络是否连接
            if(BookUtil.isNetworkConnected(this)){
                progressDialog=new ProgressDialog(this);
                progressDialog.setMessage("请稍候，正在读取信息...");
                progressDialog.show();
                String urlstr="https://api.douban.com/v2/book/isbn/"+data.getExtras().getString("result");
                //扫到ISBN后，启动下载线程下载图书信息
                new MainActivity.LoadParseBookThread(urlstr).start();
            }
            else {
                Toast.makeText(this, "网络异常，请检查你的网络连接", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
     * @version 1.0
     * @author JayFang
     * @describe 异步下载并解析图书信息的线程类，线程结束后会发送Message消息，带有解析之后的Book对象
     */
    private class LoadParseBookThread extends Thread
    {
        private String url;

        //通过构造函数传递url地址
        public LoadParseBookThread(String urlstr)
        {
            url=urlstr;
        }

        public void run()
        {
            Message msg=Message.obtain();
            String result=BookUtil.getHttpRequest(url);
            try {
                Book book=new BookUtil().parseBookInfo(result);
                //给主线程UI界面发消息，提醒下载信息，解析信息完毕
                msg.obj=book;
                msg.what=1;
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
        }
    }

    public class ChangTextViewThread extends Thread{
        public void run(){
            String url="http://221.180.249.223:8888/quick4j/rest/books/number";
            Message m=Message.obtain();
            try{
                bookNum=HttpRequest.sendGet(url);
                m.obj=bookNum;
                m.what=2;
            }catch (Exception e){
                e.printStackTrace();
            }
           handler.sendMessage(m);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
