package cl.buildersoft.consoleUtils.bean;

import java.io.File;
import java.io.Serializable;

public class MyFile implements Serializable {
	private static final long serialVersionUID = -2449887038799612547L;
	private String name = null;
	private String pathName = null;
	private String md5 = null;
	private Long size = null;
	private Long lastModified = null;

	public MyFile(File file, String md5Checksum) {
		setLastModified(Long.valueOf(file.lastModified()));
		setMd5(md5Checksum);
		setPathName(file.getAbsolutePath());
		setSize(Long.valueOf(file.length()));
		setName(file.getName());
	}

	public String getPathName() {
		return this.pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getMd5() {
		return this.md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public Long getSize() {
		return this.size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	public boolean equals(Object otherFile) {
		Boolean out = null;
		if (otherFile == null) {
			out = Boolean.valueOf(false);
		} else if (otherFile == this) {
			out = Boolean.valueOf(true);
		} else if (otherFile instanceof MyFile) {
			MyFile myFile = (MyFile) otherFile;

			Boolean isPathName = Boolean.valueOf(myFile.getPathName().equals(getPathName()));
			Boolean isMd5 = Boolean.valueOf(myFile.getMd5().equals(getMd5()));
			Boolean isSize = Boolean.valueOf(myFile.getSize().equals(getSize()));
			Boolean isLastModified = Boolean.valueOf(myFile.getLastModified().equals(getLastModified()));

			out = Boolean.valueOf((isPathName.booleanValue()) && (isMd5.booleanValue()) && (isSize.booleanValue())
					&& (isLastModified.booleanValue()));
		} else {
			out = Boolean.valueOf(false);
		}

		return out.booleanValue();
	}

	public String toString() {
		return "MyFile [name=" + this.name + ", md5=" + this.md5 + "]";
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
