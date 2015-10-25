package com.tryhard.myvoa.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.tryhard.myvoa.bean.InformationItem;
import com.tryhard.myvoa.ui.activity.MainActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen on 2015/10/22.
 */
public class InformationItemDao {
    private Context context;
    private Dao<InformationItem, Integer> infoItemDaoOpe;
    private DatabaseHelper helper;

    public InformationItemDao(Context context){
        this.context = context;
        helper = DatabaseHelper.getHelper(context.getApplicationContext());
        try {
            infoItemDaoOpe = helper.getDao(InformationItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //添加一条InformationItem记录
    public  void add(InformationItem item){
        try
        {
            if(queryByWebsite(item.getWebsite()).isEmpty())
                infoItemDaoOpe.create(item);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public List<InformationItem> queryByWebsite(String website){
        List<InformationItem> list = null;
        try {
             list =  infoItemDaoOpe.queryBuilder().where()
                    .eq(InformationItem.WEBSITE_FIELD_NAME, website)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<InformationItem> getAllItemsByInfoSort(String SortOfInfomation){
        ArrayList<InformationItem> list = new ArrayList<>();
        try {
            List temp = infoItemDaoOpe.queryBuilder().where()
                    .eq(InformationItem.SORT_FIELD_NAME, SortOfInfomation)
                    .query();
            if(temp != null)
                list = new ArrayList(temp);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return list;
    }

    public void updateItem(InformationItem item){

        // 更新item
        try {
            UpdateBuilder<InformationItem, Integer> updateBuilder =
                    infoItemDaoOpe.updateBuilder();
            updateBuilder.updateColumnValue(InformationItem.BITMAPOS_FIELD_NAME, item.getBitmapOs());
            updateBuilder.updateColumnValue(InformationItem.ISSCANED_FIELD_NAME, item.getIsScaned());

            updateBuilder.where().eq(InformationItem.WEBSITE_FIELD_NAME, item.getWebsite());
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
