package com.tryhard.myvoa.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tryhard.myvoa.util.PreferencesManager;
import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.Information;
import com.tryhard.myvoa.db.DBopenHelper;
import com.tryhard.myvoa.ui.activity.CreateInnerFragActivity;
import com.tryhard.myvoa.ui.activity.MainActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StudyFragment extends ListFragment {
    private List<Information> mInformations;
    private final static String WEBSITE = "http://www.51voa.com";
    public DBopenHelper dbOpenHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initInformationList();

        MyInformationAdapter adapter = new MyInformationAdapter((ArrayList<Information>) mInformations);
        setListAdapter(adapter);
    }

    private void initInformationList() {

        Resources res = getResources();
        String[] eTitle = res.getStringArray(R.array.study_array);

//        String[] eTitle = new String[]{"Biligual News", "Learn A Word", "Words And Idioms", "American English Mosaic",
//                "Go English", "Wordmaster", "America Cafe", "Intermediate American English", "English in a Minute", "How to Say it",
//                "President's Address", "Popular American", "Business Etiquette", "Sport English"
//        };

        String[] cTitle = new String[]{"˫������", "ѧ����", "����ϰ������", "����ѵ����", "��������", "�ʻ��ʦ", "���￧����", "�м�����Ӣ��", "һ����Ӣ��",
                "������ô˵", "������ͳ��˵", "��������", "�����������", "��������"
        };
        String[] website = new String[]{"/Bilingual_News_1.html", "/Learn_A_Word_1.html", "/Words_And_Idioms_1.html", "/American_English_Mosaic_1.html",
                "/Go_English_1.html", "/Word_Master_1.html", "/American_Cafe_1.html", "/Intermediate_American_English_1.html", "/English_in_a_Minute_1.html",
                "/How_American_English_1.html", "/President_Address_1.html", "/Popular_American_1.html", "/Business_Etiquette_1.html", "/Sports_English_1.html"
        };
        mInformations = new ArrayList<Information>();

        for (int i = 0; i < eTitle.length; i++) {
            Information information = new Information();
            information.setCtitle(cTitle[i]);
            information.setEtitle(eTitle[i]);
            information.setWebsite(WEBSITE + website[i]);
            mInformations.add(information);
        }
    }

    private class MyInformationAdapter extends ArrayAdapter<Information> {
        public MyInformationAdapter(ArrayList<Information> Informations) {
            super(getActivity(), 0, Informations);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.study_item, null);
            }

            Information info = getItem(position);

            TextView Etitle = (TextView) convertView.findViewById(R.id.Etitle);
            Etitle.setText(info.getEtitle());
            TextView Ctitle = (TextView) convertView.findViewById(R.id.Ctitle);
            Ctitle.setText(info.getCtitle());

            return convertView;
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Information info = mInformations.get(position);
        PreferencesManager sharedPrefer = new PreferencesManager(getActivity(), info.getEtitle());
        Map<String, String> params = sharedPrefer.getPreferences();
        info.isBuildTable = Boolean.getBoolean(params.get(info.getEtitle()));

        dbOpenHelper = new DBopenHelper(getActivity());
        String website = info.getWebsite();


        if (params.get(info.getEtitle()) == "false") {
            dbOpenHelper.onMyCreate(dbOpenHelper.getWritableDatabase(), website.substring(website.lastIndexOf("/") + 1, website.lastIndexOf(".")));
            info.isBuildTable = true;
            sharedPrefer.save(info.isBuildTable);
        }
        Intent intent = new Intent(getActivity(), CreateInnerFragActivity.class);
        intent.putExtra(MainActivity.INNER_WEBSITE, info.getWebsite());
        startActivity(intent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.study_list, container, false);
        ListView view = (ListView) v.findViewById(android.R.id.list);
        return v;
    }
}
