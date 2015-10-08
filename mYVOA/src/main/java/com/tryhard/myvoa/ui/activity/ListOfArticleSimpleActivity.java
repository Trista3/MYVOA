package com.tryhard.myvoa.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.ImageView;

import com.tryhard.myvoa.R;
import com.tryhard.myvoa.ui.fragment.ListOfArticleFragment;

public class ListOfArticleSimpleActivity extends ArticleListSimpleActivity {

	@Override
	protected Fragment createFragment() {
		//如果不是MainActivity，可返回上一级

		return new ListOfArticleFragment();
	}
}