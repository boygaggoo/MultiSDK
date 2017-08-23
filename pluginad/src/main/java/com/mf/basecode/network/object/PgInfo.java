package com.mf.basecode.network.object;

import com.mf.basecode.network.serializer.ByteField;

public class PgInfo {
  
  @ByteField(index = 0)
  private String packageName;

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  @Override
  public String toString() {
    return "PackageInfo [packageName=" + packageName + "]";
  }
  
}
