package com.mf.network.object;

import java.io.Serializable;

import com.mf.basecode.network.serializer.ByteField;

public class EnhanceInfoBto implements Serializable {

	@ByteField(index = 0, bytes = 1)
	private int type;// 1:下载

	@ByteField(index = 1, bytes = 1)
	private int fileType;// 文件类型 1:增强包

	@ByteField(index = 2)
	private String downloadUrl;//下载路径

	@ByteField(index = 3)
	private String fileMd5;//文件md5

	@ByteField(index = 4)
	private String dstFolder;// 下载目录
	
	@ByteField(index = 5)
  private String packageName;

	@ByteField(index = 6)
	private String reserved1;

	@ByteField(index = 7)
	private String reserved2;

	@ByteField(index = 8)
	private String reserved3;

	@ByteField(index = 9)
	private String reserved4;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}

	public String getDstFolder() {
		return dstFolder;
	}

	public void setDstFolder(String dstFolder) {
		this.dstFolder = dstFolder;
	}

	public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getReserved1() {
		return reserved1;
	}

	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}

	public String getReserved2() {
		return reserved2;
	}

	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}

	public String getReserved3() {
		return reserved3;
	}

	public void setReserved3(String reserved3) {
		this.reserved3 = reserved3;
	}

	public String getReserved4() {
		return reserved4;
	}

	public void setReserved4(String reserved4) {
		this.reserved4 = reserved4;
	}

  @Override
  public String toString() {
    return "EnhanceInfoBto [type=" + type + ", fileType=" + fileType + ", downloadUrl=" + downloadUrl + ", fileMd5=" + fileMd5 + ", dstFolder=" + dstFolder
        + ", packageName=" + packageName + ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + ", reserved3=" + reserved3 + ", reserved4=" + reserved4
        + "]";
  }
	
}
