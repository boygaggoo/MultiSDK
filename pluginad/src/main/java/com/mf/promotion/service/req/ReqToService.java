package com.mf.promotion.service.req;

import java.util.ArrayList;

import android.content.Context;

import com.mf.data.PromDBU;
import com.mf.model.AdDbInfo;

public abstract class ReqToService {
  protected Context                mContext;
  
  
  public String getAdIds(int promType){
    String ids = "";
    ArrayList<AdDbInfo> list =  PromDBU.getInstance(mContext).queryAdInfo(promType);
    for (AdDbInfo adDbInfo : list) {
      ids = ids+adDbInfo.getAdId()+",";
    }
    return ids;
  }



}
