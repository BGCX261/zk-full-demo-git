package org.hxzon.project.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hxzon.project.BaseEntity;
import org.hxzon.util.MD5Util;

@MappedSuperclass
public class Uzer extends BaseEntity {

    public static final String Username_Guess = "guest";

    private static final long serialVersionUID = 7570429986649512051L;

    @Column(columnDefinition = DefineVarchar32)
    @Size(max = DefineSize32)
    @NotBlank
    private String username = Username_Guess;

    @Column(columnDefinition = DefineVarchar32)
    @Size(max = DefineSize32)
    @NotBlank
    private String password;

    @Column(columnDefinition = DefineVarchar32)
    @Size(max = DefineSize32)
    @NotBlank
    private String realname;

    private boolean inUse;

    //
    public void md5password() {
        this.password = MD5Util.toMD5(this.username + this.password);
    }

    public String toString() {
        return this.realname + "(" + this.username + ")";
    }

    public boolean equals(Object o) {
        if (o instanceof Uzer) {
            Uzer other = (Uzer) o;
            return this.username.equals(other.username);
        }
        return false;
    }

    //
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

}
