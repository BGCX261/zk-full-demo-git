package org.hxzon.easywork.model.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

import org.hxzon.project.BaseEntity;

@Entity
public class HLog extends BaseEntity {
    private static final long serialVersionUID = 7570429986649512051L;

    @Column(columnDefinition = DefineVarchar32)
    @Size(max = DefineSize32)
    private String username = "guest";

    @Column(columnDefinition = DefineVarchar2048)
    @Size(max = DefineSize2048)
    private String logType = "xxx";

    @Column(columnDefinition = DefineVarchar2048)
    @Size(max = DefineSize2048)
    private String overview = "xxx";

    @Column(columnDefinition = DefineVarchar2048)
    @Size(max = DefineSize2048)
    private String message = "xxx";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return "[" + this.username + "]" + this.message;
    }

}
