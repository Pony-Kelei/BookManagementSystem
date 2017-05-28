package com.whu.iss.bookmanagesystem;
import com.whu.iss.bookmanagesystem.R;
import com.whu.iss.bookmanagesystem.Book;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @version 1.0
 * @author kelei
 * @describe 图书信息显示Activity
 */

public class BookViewActivity extends Activity {
    private Intent intent;
    private EditText tv_rate,tv_price,tv_title,tv_author,tv_publisher,tv_date,tv_isbn,tv_summary;
    private EditText tv_page,tv_tags;
    private Button modifyButton;
    private Button submitButton;
    private ImageView image;

    
    private Book book; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookview);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

        tv_title=(EditText)findViewById(R.id.bookview_title);
        tv_author=(EditText)findViewById(R.id.bookview_author);
        tv_publisher=(EditText)findViewById(R.id.bookview_publisher);
        tv_date=(EditText)findViewById(R.id.bookview_publisherdate);
        tv_isbn=(EditText)findViewById(R.id.bookview_isbn);
        tv_summary=(EditText)findViewById(R.id.bookview_summary);
        tv_rate=(EditText)findViewById(R.id.bookview_rate);
        tv_price=(EditText)findViewById(R.id.bookview_price);
        tv_page=(EditText)findViewById(R.id.bookview_pages);
      //  tv_content=(EditText)findViewById(R.id.bookview_content);
        tv_tags=(EditText)findViewById(R.id.bookview_tag);
     //   tv_authorinfo=(EditText)findViewById(R.id.bookview_authorinfo);
        image=(ImageView)findViewById(R.id.bookview_cover);
        modifyButton=(Button)findViewById(R.id.buttonid1);
        submitButton=(Button)findViewById(R.id.buttonid2);
        modifyButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ShowToast") @Override
            public void onClick(View v) {
                tv_title.setEnabled(true);
                tv_author.setEnabled(true);
                tv_publisher.setEnabled(true);
                tv_date.setEnabled(true);
                tv_isbn.setEnabled(true);
                tv_price.setEnabled(true);
                tv_tags.setEnabled(true);
                tv_rate.setEnabled(true);
                tv_page.setEnabled(true);
            }
        });
       submitButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast") @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String postContent="code="+book.getISBN()+"&bookname="+book.getTitle()+"&years="+book.getPublishDate()
                        +"&price="+book.getPrice().substring(0,book.getPrice().length()-1)+"&pic="+book.getMapUrl()+"&type=0";
                String url="http://software.whu.edu.cn:8080/quick4j/rest/books/books";

                boolean postSuccess=HttpRequest.sendPost(url, postContent);
                if(postSuccess){
                    Toast.makeText(BookViewActivity.this,"提交成功",Toast.LENGTH_SHORT ).show();
                    Intent intent=new Intent(BookViewActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(BookViewActivity.this,"提交失败",Toast.LENGTH_SHORT ).show();
                }
            }
        });

        //获取从MainActivity中传来的Book
        intent=getIntent();
        book=(Book)intent.getParcelableExtra(Book.class.getName());

        //将Book信息显示在控件上
        if(book.getRate().equals("0.0"))
            tv_rate.setText("少于10人评价");
        else
            tv_rate.setText("评分:"+book.getRate()+"分");
        tv_rate.setEnabled(false);
        tv_title.setText(book.getTitle());
        tv_title.setEnabled(false);
        tv_author.setText("作者:"+book.getAuthor());
        tv_author.setEnabled(false);
        tv_publisher.setText("出版社:"+book.getPublisher());
        tv_publisher.setEnabled(false);
        tv_date.setText("出版时间:"+book.getPublishDate());
        tv_date.setEnabled(false);
        tv_isbn.setText("ISBN:"+book.getISBN());
        tv_isbn.setEnabled(false);
        tv_summary.setText(book.getSummary().substring(0,88)+"....");
        tv_summary.setEnabled(false);
        tv_page.setText("页数:"+book.getPage());
        tv_page.setEnabled(false);
        tv_price.setText("定价:"+book.getPrice());
        tv_price.setEnabled(false);
        tv_tags.setText("标签:"+book.getTag());
        tv_tags.setEnabled(false);
        image.setImageBitmap(book.getBitmap()); 

    }
}
