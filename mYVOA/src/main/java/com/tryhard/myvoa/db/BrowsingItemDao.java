package com.tryhard.myvoa.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.tryhard.myvoa.bean.BrowsingItem;
import com.tryhard.myvoa.bean.InformationItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen on 2015/10/22.
 */
public class BrowsingItemDao {
    private Dao<BrowsingItem, Integer> browsingItemDaoOpe;
    private DatabaseHelper helper;

    public BrowsingItemDao(Context context){
        helper = DatabaseHelper.getHelper(context);
        try {
            browsingItemDaoOpe = helper.getDao(BrowsingItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //添加一条InformationItem记录
    public  void add(BrowsingItem item){
        try
        {
            if(queryByWebsite(item.getmWebsite()).isEmpty())
                browsingItemDaoOpe.create(item);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public List<BrowsingItem> queryByWebsite(String website){
        List<BrowsingItem> list = new ArrayList<BrowsingItem>();
        try {
            list =  browsingItemDaoOpe.queryBuilder().where()
                    .eq(BrowsingItem.WEBSITE_B_FIELD_NAME, website)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BrowsingItem> getAllItems(){
        List<BrowsingItem> list = null;
        try {
            list = browsingItemDaoOpe.queryBuilder().orderBy("id",false).query();
          //  list = browsingItemDaoOpe.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return list;
    }
}
