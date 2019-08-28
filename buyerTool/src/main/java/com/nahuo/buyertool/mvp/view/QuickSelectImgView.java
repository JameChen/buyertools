package com.nahuo.buyertool.mvp.view;

import com.nahuo.buyertool.model.MediaStoreImage;
import com.nahuo.buyertool.mvp.MvpView;

import java.util.List;

/**
 * Created by ZZB on 2015/7/9 17:18
 */
public interface QuickSelectImgView extends MvpView{

    public void showLoading(boolean show);

    public void onRecentImgsLoaded(List<MediaStoreImage> paths);


}
