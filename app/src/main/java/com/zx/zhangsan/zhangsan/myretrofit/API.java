package com.zx.zhangsan.zhangsan.myretrofit;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {
    // @GET注解的作用:采用Get方法发送网络请求
    // getNews(...) = 接收网络请求数据的方法
    // 其中返回类型为Call<News>，News是接收数据的类（即上面定义的News类）
    // 如果想直接获得Responsebody中的内容，可以定义网络请求返回值为Call<ResponseBody>
    @GET("zhaoxiao/word")
    Call<ResponseBody> getNews(@Query("num") String num, @Query("page")String page);

    @POST("pad-service/UpdateSYJ.htm")
    Call<ResponseBody> postNews(@Body Map<String,String> map);
}
