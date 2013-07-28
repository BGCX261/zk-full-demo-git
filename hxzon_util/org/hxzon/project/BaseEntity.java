package org.hxzon.project;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hxzon.util.DateFormatUtil;

@MappedSuperclass
@DynamicInsert
@DynamicUpdate
public class BaseEntity extends SimpleEntity {

    private static final long serialVersionUID = -5342595206501703154L;

    @Column(columnDefinition = DefineVarchar32)
    private String createTime;

    @Column(columnDefinition = DefineVarchar32)
    private String updateTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void updateTimeInfo() {
        if (getId() == null) {
            this.setCreateTime(DateFormatUtil.formatToUniteTime());
            this.setUpdateTime(this.createTime);
        } else {
            this.setUpdateTime(DateFormatUtil.formatToUniteTime());
        }
    }

}
