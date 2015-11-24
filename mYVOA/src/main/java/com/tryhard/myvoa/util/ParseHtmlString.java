package com.tryhard.myvoa.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.tryhard.myvoa.bean.InformationItem;
import com.tryhard.myvoa.ui.fragment.ListOfArticleFragment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;;
import rx.schedulers.Schedulers;

/**
 * Created by Chen on 2015/10/28.
 */
public class ParseHtmlString {
    //数据变量
    private String sortOfInformation;
    public ArrayList<InformationItem> informationItems;
    public List<String> websites = new ArrayList<>();
    public Bitmap bitmap = null;
    private static String WEBSITE_HEAD = "http://www.51voa.com";

    public ParseHtmlString(String sortOfInformation){
        this.sortOfInformation = sortOfInformation;
    }

    //取InformationItems
    public void getNewInformationItems(String websiteWithOutHead, final int getWhatType){
        informationItems = new ArrayList<>();
            MvoaRetrofit.getHtmlObservable(websiteWithOutHead)
                    .subscribeOn(Schedulers.io())
                    .doOnNext(new Action1<Response>() {
                        @Override
                        public void call(Response response) {
                            try {
                                OutputStream os = StreamTool.getOutputStream(response.getBody().in());
                                informationItems = ParseHtmlString.this.getParseInformationItems(Jsoup.parse(os.toString()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Response>() {
                        @Override
                        public void call(Response response) {
                            ListOfArticleFragment.saveInDao(null,informationItems,bitmap,getWhatType);
                        }
                    });

        }

    //取更多的网址
    public void getMoreWebsite(String websiteWithOutHead, final int getWhatType){
        websites = new ArrayList<>();
        MvoaRetrofit.getHtmlObservable(websiteWithOutHead)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        try {
                            OutputStream os = StreamTool.getOutputStream(response.getBody().in());
                            websites = ParseHtmlString.this.getWebsites(Jsoup.parse(os.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        ListOfArticleFragment.saveInDao(websites,null,bitmap,getWhatType);
                    }
                });
    }

    //取图片
    public void getImage(String websiteWithOutHead, final int getWhatType){
        MvoaRetrofit.getHtmlObservable(websiteWithOutHead)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        try {
                            OutputStream os = StreamTool.getOutputStream(response.getBody().in());
                            bitmap = ParseHtmlString.this.getBitmap(Jsoup.parse(os.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        ListOfArticleFragment.saveInDao(null,null,bitmap,getWhatType);
                    }
                });
    }


    //内部调用，将Document解析为ArrayList<InformationItem>
    private ArrayList<InformationItem> getParseInformationItems(Document doc) {
            Element content = doc.getElementById("list");
            Elements contentlist = content.getElementsByTag("li");
            ArrayList<InformationItem> items = new ArrayList<>();

            //开始解析
            for (org.jsoup.nodes.Element j : contentlist) {
                InformationItem infoItem = new InformationItem();

                Elements i = j.getElementsByTag("a");
                String text = j.text();
                String dateString = text.substring(text.lastIndexOf("(") + 1, text.lastIndexOf(")"));
                try {
                    infoItem.setDateTime(new SimpleDateFormat("yy-MM-dd").parse(dateString));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                infoItem.setDate(dateString);
                infoItem.setTitle(text.substring(0, text.lastIndexOf("(")));
                infoItem.setWebsite(i.attr("href"));
                infoItem.setmFromSortOfInformation(sortOfInformation);

                items.add(infoItem);
            }
            return items;
        }

    //内部调用，将Document解析出List<String> websites
    private List<String> getWebsites(Document doc){
        List<String> websites = new ArrayList<>();
        Element contentWeb = doc.getElementById("pagelist");
        Elements websitesList = contentWeb.getElementsByTag("a");
        for (org.jsoup.nodes.Element j : websitesList) {
            websites.add(j.attr("href"));
        }
        return websites;
    }


    //内部调用，将Document解析出网页图片bitmap
    private Bitmap getBitmap(Document doc) {
        Bitmap bitmap = null;
        Element content = doc.getElementById("content");
        Elements images = content.getElementsByTag("img");

        if (images.isEmpty())
            return bitmap;

        String photoWebsite = images.get(0).attr("src");
        try {
            InputStream is = (new URL(WEBSITE_HEAD + photoWebsite)).openStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }
}
