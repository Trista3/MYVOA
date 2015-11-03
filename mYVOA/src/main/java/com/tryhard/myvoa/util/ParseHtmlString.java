package com.tryhard.myvoa.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.tryhard.myvoa.bean.InformationItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
    private String sortOfInformation;
    public ArrayList<InformationItem> informationItems;
    public List<String> websites = new ArrayList<>();
    public Bitmap bitmap = null;
    private static String WEBSITE_HEAD = "http://www.51voa.com";

    public ParseHtmlString(String sortOfInformation){
        this.sortOfInformation = sortOfInformation;
    }

    //取InformationItems
    public ArrayList<InformationItem> getNewInformationItems(String websiteWithOutHead){
        informationItems = new ArrayList<>();
            RetrofitService.getHtmlObservable(websiteWithOutHead)
                    .subscribeOn(Schedulers.io())
                    .doOnNext(new Action1<Response>() {
                        @Override
                        public void call(Response response) {
                            try {
                                OutputStream os = StreamTool.getOutputStream(response.getBody().in());
                                String htmlString = os.toString();
                                Document doc = Jsoup.parse(htmlString);
                                informationItems = ParseHtmlString.this.getParseInformationItems(doc);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Response>() {
                        @Override
                        public void call(Response response) {

                        }
                    });

            return informationItems;
        }

       private ArrayList<InformationItem> getParseInformationItems(Document doc) {
            Element content = doc.getElementById("list");
            Elements contentlist = content.getElementsByTag("li");
            ArrayList<InformationItem> items = new ArrayList<InformationItem>();

            //开始解析
            for (org.jsoup.nodes.Element j : contentlist) {
                InformationItem infoItem = new InformationItem();

                Elements i = j.getElementsByTag("a");
                String text = j.text();
                infoItem.setDate(text.substring(text.lastIndexOf("(") + 1, text.lastIndexOf(")")));
                infoItem.setTitle(text.substring(0, text.lastIndexOf("(")));
                infoItem.setWebsite(i.attr("href"));
                infoItem.setmFromSortOfInformation(sortOfInformation);

                items.add(infoItem);
            }
            return items;
        }
    //取更多的网址
    public List<String> getMoreWebsite(String websiteWithOutHead){
        RetrofitService.getHtmlObservable(websiteWithOutHead)
                .subscribeOn(Schedulers.io())
               .subscribe(new Action1<Response>() {
                   @Override
                   public void call(Response response) {
                       try {
                           OutputStream os = StreamTool.getOutputStream(response.getBody().in());
                           String htmlString = os.toString();
                           Document doc = Jsoup.parse(htmlString);
                           websites = ParseHtmlString.this.getWebsites(doc);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
               });
        return websites;
    }

    private List<String> getWebsites(Document doc){
        List<String> websites = new ArrayList<>();
        Element contentWeb = doc.getElementById("pagelist");
        Elements websitesList = contentWeb.getElementsByTag("a");
        for (org.jsoup.nodes.Element j : websitesList) {
            websites.add(j.attr("href"));
        }
        return websites;
    }

    //取图片
    public Bitmap getImage(String websiteWithOutHead){
        RetrofitService.getHtmlObservable(websiteWithOutHead)
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        try {
                            OutputStream os = StreamTool.getOutputStream(response.getBody().in());
                            String htmlString = os.toString();
                            Document doc = Jsoup.parse(htmlString);
                            bitmap = ParseHtmlString.this.getBitmap(doc);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        return bitmap;
    }

    private Bitmap getBitmap(Document doc) {
        Bitmap bitmap = null;
        Element content = doc.getElementById("content");
        Elements images = content.getElementsByTag("img");

        if (images.isEmpty())
            return bitmap;

        String photoWebsite = images.get(0).attr("src");
        URL url = null;
        try {
            url = new URL(WEBSITE_HEAD + photoWebsite);
            InputStream is = url.openStream();
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
