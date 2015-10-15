package com.tryhard.myvoa.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.tryhard.myvoa.R;
import com.tryhard.myvoa.bean.Information;
import com.tryhard.myvoa.bean.InformationItem;
import com.tryhard.myvoa.ui.fragment.ListOfArticleFragment;

public class ListOfArticleSimpleActivity extends ArticleListSimpleActivity {

	public static String extra_data="ListOfArticleSimpleActivity.extra_data";
	public static Intent makeIntent(Context mContext, InformationItem infoItem){
		return new Intent(mContext, ArticleContentActivity.class).putExtra(ArticleContentActivity.content_extra_data, infoItem);
	}


	@Override
	protected Fragment createFragment() {
		return new ListOfArticleFragment();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: {
				if (NavUtils.getParentActivityName(this) != null) {
					NavUtils.navigateUpFromSameTask(this);
				}

			}
		}
		return super.onOptionsItemSelected(item);
	}


}