package com.tryhard.myvoa.util;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;


public class MvoaRetrofit {
    private static final String ENDPOINT = "http://www.51voa.com";

    /**
     * 服务接口
     */
    private interface RetrofitInterface {
        @GET("/{sortOfArticalList}")
        public Observable<Response> connectToListOfArticle(@Path("sortOfArticalList") String sortOfArticalList);
    }

    private static final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).setLogLevel(RestAdapter.LogLevel.FULL).build();

    private static final RetrofitInterface retrofitInterface = restAdapter.create(RetrofitInterface.class);

    /**
     * 将服务接口返回的数据，封装成{@link rx.Observable}
     * 这种写法适用于将旧代码封装
     */
    public static Observable<Response> getHtmlObservable(String websiteWithOutHead) {
        return retrofitInterface.connectToListOfArticle(websiteWithOutHead);
    }
}
