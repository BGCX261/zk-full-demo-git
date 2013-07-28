package org.hxzon.easywork.model.base;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hxzon.easywork.model.common.Uzer;

@MappedSuperclass
public class Upload extends org.hxzon.project.model.Upload {

    private static final long serialVersionUID = 1601793802697969503L;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    private Uzer user;

    @Column(columnDefinition = DefineVarchar32)
    @Size(max = DefineSize32)
    private String username;

    public Uzer getUser() {
        return user;
    }

    public void setUser(Uzer user) {
        this.user = user;
        if (user != null) {
            this.username = user.getUsername();
        } else {
            this.username = null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
