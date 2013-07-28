package org.hxzon.project.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import org.hxzon.project.BaseEntity;

@MappedSuperclass
public class Upload extends BaseEntity {

    private static final long serialVersionUID = 1601793802697969503L;

//	@Column(columnDefinition = "varchar(32) default ''")
//	@Size(max = 32)
//	private String ownerType;
//
//	@Column(columnDefinition = "varchar(32) ")
//	@Size(max = 32)
//	private String ownerId;

    @Column(columnDefinition = DefineVarchar2048)
    @Size(max = DefineSize2048)
    private String fileName;

    @Column(columnDefinition = DefineVarchar2048)
    @Size(max = DefineSize2048)
    private String filePath;

    @Column(columnDefinition = DefineVarchar2048)
    @Size(max = DefineSize2048)
    private String fileType;

//	public String getOwnerType() {
//		return ownerType;
//	}
//
//	public void setOwnerType(String ownerType) {
//		this.ownerType = ownerType;
//	}
//
//	public String getOwnerId() {
//		return ownerId;
//	}
//
//	public void setOwnerId(String ownerId) {
//		this.ownerId = ownerId;
//	}

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String toString() {
        return this.fileName + "[" + this.fileType + "]";
    }

}
