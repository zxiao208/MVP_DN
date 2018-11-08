package com.zx.zhangsan.zhangsan;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zx.zhangsan.zhangsan.framework.annotation.ContentView;
import com.zx.zhangsan.zhangsan.framework.annotation.Event;
import com.zx.zhangsan.zhangsan.framework.annotation.InjectUtils2;
import com.zx.zhangsan.zhangsan.framework.annotation.OnClick;
import com.zx.zhangsan.zhangsan.framework.annotation.ViewInject;
import com.zx.zhangsan.zhangsan.myretrofit.API;
import com.zx.zhangsan.zhangsan.utils.HttpTask;
import com.zx.zhangsan.zhangsan.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    String urlStr = "http://10.6.2.167:5051/pos-inner/";
    String urlmock = "https://getman.cn/mock/zhaoxiao";

    @ViewInject(R.id.tv_view)
    private TextView tv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectUtils2.inject(this);
        if (tv_text != null) {
            Toast.makeText(this, "tv_text不为空", Toast.LENGTH_LONG).show();
        }

    }
    //Xutils 2.0的写法
//    @OnClick({R.id.tv_view})
//    public void click(View v){
//        Toast.makeText(this,"点击了TextView", Toast.LENGTH_LONG).show();
//    }

    //Xutils 3.0的写法
    @Event(value = {R.id.tv_view}, type = View.OnClickListener.class, setter = "setOnClickListener", method = "onClick")
    private void click(View v) {
//        Toast.makeText(this,"点击了TextView", Toast.LENGTH_LONG).show();
        login();
    }

    private void login() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("column","20:59:a0:10:10:fe");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpTask httpTask = new HttpTask(new HttpUtils.OnHttpResultListener() {
        @Override
        public void onResult(String result) {
            Toast.makeText(MainActivity.this,"登录状态："+result,Toast.LENGTH_SHORT).show();
        }
    });
    httpTask.execute("http://10.6.2.167:5051/pos-inner/pad-base/getSyjBysyjId.htm",jsonObject.toString());

}
}
