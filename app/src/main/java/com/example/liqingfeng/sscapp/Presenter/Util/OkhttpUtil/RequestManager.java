package com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 封装了universaoImageLoader 方法
 */
public class RequestManager {
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");//mdiatype 这个需要和服务端保持一致
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final String TAG = RequestManager.class.getSimpleName();
    private static final String BASE_URL = UrlConfig.bnsBaseUrl;//请求接口根地址
    private static volatile RequestManager mInstance;//单利引用
    public static final int TYPE_GET = 0;//get请求
    public static final int TYPE_POST_JSON = 1;//post请求参数为json
    public static final int TYPE_POST_FORM = 2;//post请求参数为表单
    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private Handler okHttpHandler;//全局处理子线程和M主线程通信
    public static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();//存放cookie的地方
    private Gson gson= CheckStatuss.gson;

    /**
     * 初始化RequestManager
     */
    public RequestManager(Context context) {
        //初始化OkHttpClient
        mOkHttpClient = new OkHttpClient().newBuilder()
                .cookieJar( new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put( url.host(), cookies );
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get( url.host() );
                        return cookies != null ? cookies : new ArrayList<Cookie>(  );
                    }
                } )
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
                .build();
        //初始化Handler
        okHttpHandler = new Handler(context.getMainLooper());
    }

    /**
     * 获取单例引用
     *
     * @return
     */
    public static RequestManager getInstance(Context context) {
        RequestManager inst = mInstance;
        if (inst == null) {
            synchronized (RequestManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new RequestManager(context.getApplicationContext());
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * okHttp异步请求统一入口
     * @param actionUrl   接口地址
     * @param requestType 请求类型
     * @param paramsMap   请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     **/
    public <T> Call requestAsyn(String actionUrl, int requestType, HashMap<String, String> paramsMap,boolean withToken, ReqCallBack<T> callBack) {
        Call call = null;
        switch (requestType) {
            case TYPE_GET:
                call = requestGetByAsyn(actionUrl, paramsMap,withToken, callBack);
                break;
            case TYPE_POST_JSON:
                call = requestPostByAsyn(actionUrl, paramsMap,withToken, callBack);
                break;
            case TYPE_POST_FORM:
                call = requestPostByAsynWithForm(actionUrl, paramsMap,withToken, callBack);
                break;
        }
        return call;
    }

    /**
     * 同步get访问
     *
     * @param actionUrl 接口地址
     * @param paramsMap 参数信息
     * @param withToken 是否带Token
     * @return 返回信息
     */
    public ResponseModel requestGetBySyn(String actionUrl, HashMap<String, String> paramsMap, boolean withToken) {
        StringBuilder tempParams = new StringBuilder();
        int pos = 0;
        for (String key : paramsMap.keySet()) {
            if (pos > 0) {
                tempParams.append( "&" );
            }
            try {
                tempParams.append( String.format( "%s=%s", key, URLEncoder.encode( paramsMap.get( key ), "utf-8" ) ) );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            pos++;
        }
        String requestUrl = String.format( "%s%s?%s", BASE_URL, actionUrl, tempParams.toString() );
        final Request request = addHeaders( withToken ).url( requestUrl ).build();
        final Call call = mOkHttpClient.newCall( request );
        FutureTask<ResponseModel> futureTask = new FutureTask<>( new Callable<ResponseModel>() {
            @Override
            public ResponseModel call() throws Exception {
                try {
                    String response = call.execute().body().string();
                    return gson.fromJson( response, ResponseModel.class );
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } );
        new Thread( futureTask ).start();
        try {
            return futureTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * okHttp get异步请求
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @return
     */
    private <T> Call requestGetByAsyn(String actionUrl, HashMap<String, String> paramsMap, boolean withToken, final ReqCallBack<T> callBack) {
        StringBuilder tempParams = new StringBuilder();
        try {
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            String requestUrl = String.format("%s%s?%s", BASE_URL, actionUrl, tempParams.toString());
            final Request request = addHeaders(withToken).url(requestUrl).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        //Log.e(TAG, "response ----->" + string);
                        //解析数据
                        ResponseModel object = gson.fromJson(string,ResponseModel.class);
                        successCallBack((T) object, callBack);
                    } else {
                        failedCallBack("服务器错误", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * okHttp post异步请求
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @return
     */
    private <T> Call requestPostByAsyn(String actionUrl, HashMap<String, String> paramsMap,boolean withToken, final ReqCallBack<T> callBack) {
        try {
            String json = gson.toJson( paramsMap );
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
            String requestUrl = String.format("%s%s", BASE_URL, actionUrl);
            final Request request = addHeaders(withToken).url(requestUrl).post(body).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        //Log.e(TAG, "response ----->" + string);
                        ResponseModel object = gson.fromJson(string,ResponseModel.class);
                        successCallBack((T) object, callBack);
                    } else {
                        failedCallBack("服务器错误", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * okHttp post异步请求表单提交
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @return
     */
    private <T> Call requestPostByAsynWithForm(String actionUrl, HashMap<String, String> paramsMap,boolean withToken, final ReqCallBack<T> callBack) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                builder.add(key, paramsMap.get(key));
            }
            RequestBody formBody = builder.build();
            String requestUrl = String.format("%s%s", BASE_URL, actionUrl);
            final Request request = addHeaders(withToken).url(requestUrl).post(formBody).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        //Log.e(TAG, "response ----->" + string);
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack("服务器错误", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            //Log.e(TAG, e.toString());
        }
        return null;
    }

    /**
     *无参数的get请求访问
     * @param actionUrl 请求接口地址
     * @param callBack  请求数据的回调
     * @param <T>       泛型数据支持
     * @return
     */
    public  <T> Call requestGetWithoutParam(String actionUrl,boolean withToken, final ReqCallBack<T> callBack) {
        try {
            final Request request = addHeaders(withToken).url( BASE_URL+actionUrl ).build();
            final Call call = mOkHttpClient.newCall( request );
            call.enqueue( new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    //Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        //Log.e(TAG, "response ----->" + string);
                        ResponseModel object = gson.fromJson(string,ResponseModel.class);
                        successCallBack((T) object, callBack);
                    } else {
                        failedCallBack("服务器错误", callBack);
                    }
                }
            } );
            return call;
        }catch (Exception e) {
            //Log.e( TAG,e.toString() );
        }
        return null;
    }

    /**
     * 异步Put方法
     * @param actionUrl 接口地址
     * @param paramsMap 数据键值对
     * @param withToken 是否带Token
     * @param callBack  回调结果
     * @param <T>       泛型
     * @return      回调响应结果
     */
    public <T> Call requestPutWithParam(String actionUrl, HashMap<String, String> paramsMap, boolean withToken, final ReqCallBack<T> callBack ) {
        try {
            String json = gson.toJson( paramsMap );
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
            String requestUrl = String.format("%s%s", BASE_URL, actionUrl);
            final Request request = addHeaders(withToken).url(requestUrl).put(body).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    //Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        //Log.e(TAG, "response ----->" + string);
                        ResponseModel object = gson.fromJson(string,ResponseModel.class);
                        successCallBack((T) object, callBack);
                    } else {
                        failedCallBack("服务器错误", callBack);
                    }
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, e.toString());
        }
        return null;
    }
    public interface ReqCallBack<T> {
        /**
         * 响应成功
         */
        void onReqSuccess(T result);

        /**
         * 响应失败
         */
        void onReqFailed(String errorMsg);
    }

    /**
     * 统一为请求添加头信息
     * @param isLogin 是否进行的是登陆操作
     * @return
     */
    private Request.Builder addHeaders(boolean isLogin) {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE)
                .addHeader("appVersion", "3.2.0")
                .addHeader("Content-Type","application/json;charset=utf-8");
        if(isLogin) {
            builder.addHeader( "Authorization", UserConstant.tokenCode );
        }
        return builder;
    }

    /**
     * 统一同意处理成功信息
     * @param result
     * @param callBack
     * @param <T>
     */
    private <T> void successCallBack(final T result, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

    /**
     * 统一处理失败信息
     * @param errorMsg
     * @param callBack
     * @param <T>
     */
    private <T> void failedCallBack(final String errorMsg, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorMsg);
                }
            }
        });
    }
}
