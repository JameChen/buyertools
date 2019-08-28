
package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * 微店款式实体类
 * */
public class ShopItemListResultModel {

    @Expose
    private int PageIndex;// 页次
    @Expose
    private int Total;// 总数
    @Expose
    private List<ShopItemListModelX> Datas;// 商品
    
    
	public int getPageIndex() {
		return PageIndex;
	}
	public void setPageIndex(int pageIndex) {
		PageIndex = pageIndex;
	}
	public int getTotal() {
		return Total;
	}
	public void setTotal(int total) {
		Total = total;
	}
	public List<ShopItemListModelX> getDatas() {
		return Datas;
	}
	public void setDatas(List<ShopItemListModelX> datas) {
		Datas = datas;
	}
    

}
