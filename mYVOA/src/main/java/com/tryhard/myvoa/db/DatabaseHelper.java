package com.tryhard.myvoa.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.tryhard.myvoa.bean.BrowsingItem;
import com.tryhard.myvoa.bean.Information;
import com.tryhard.myvoa.bean.InformationItem;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chen on 2015/10/22.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "MVOA.db";
    private Map<String, Dao> daos = new HashMap<String, Dao>();
    private static DatabaseHelper instance;
    private static Dao<InformationItem, Integer> infoItemDao;
    private Dao<BrowsingItem, Integer> browsingItemDao;

    private DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try
        {
            TableUtils.createTable(connectionSource, InformationItem.class);
            TableUtils.createTable(connectionSource, BrowsingItem.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try
        {
            TableUtils.dropTable(connectionSource, InformationItem.class, true);
            TableUtils.dropTable(connectionSource, BrowsingItem.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    public static synchronized DatabaseHelper getHelper(Context context)
    {
        if (instance == null)
        {
            synchronized (DatabaseHelper.class)
            {
                if (instance == null)
                    instance = new DatabaseHelper(context.getApplicationContext());
            }
        }

        try {
            infoItemDao = instance.getDao(InformationItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public synchronized Dao getDao(Class clazz) throws SQLException
    {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className))
        {
            dao = daos.get(className);
        }
        if (dao == null)
        {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    /*
    public  Dao<InformationItem, Integer> getInfoDao() throws SQLException
    {
        if(infoItemDao == null)
            infoItemDao = getDao(InformationItem.class);
        return infoItemDao;
    }

    public  Dao<BrowsingItem, Integer> getBrowseDao() throws SQLException
    {
        if(browsingItemDao == null)
            browsingItemDao= getDao(BrowsingItem.class);
        return browsingItemDao;
    }
*/
    @Override
    public void close()
    {
        super.close();

        for (String key : daos.keySet())
        {
            Dao dao = daos.get(key);
            dao = null;
        }
    }
}
