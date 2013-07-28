package org.hxzon.easywork.model.security;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hxzon.project.BaseEntity;

@Entity
public class Permission extends BaseEntity {
    private static final long serialVersionUID = 7187025102792388240L;

    @ManyToMany(fetch = FetchType.LAZY)
    //, mappedBy = "permissions")//使用mappedBy则Permission这一端不维护中间表
    @JoinTable(name = "role_permission", //
    joinColumns = @JoinColumn(name = "permission_id"), //
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    @OrderBy("roleName")
    private List<Role> roles;

    @Column(columnDefinition = DefineVarchar64)
    @Size(max = DefineSize64)
    private String permissionName;

    @Column(columnDefinition = DefineVarchar32)
    @Size(max = DefineSize32)
    private String displayName;

    @Transient
    private Permission parent;
    @Transient
    private List<Permission> children;

    public Permission() {
    }

    public Permission(String permissionName, String displayName, Permission parent) {
        this.displayName = displayName;
        if (parent != null) {
            this.parent = parent;
            parent.getChildren().add(this);
            this.permissionName = parent.getPermissionName() + "." + permissionName;
        } else {
            this.permissionName = permissionName;
        }
    }

    public String toString() {
        return displayName + "[" + permissionName + "]";
    }

    public int hashCode() {
        return new HashCodeBuilder().append(permissionName).toHashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Permission)) {
            return false;
        }
        Permission other = (Permission) o;
        return new EqualsBuilder().append(this.permissionName, other.permissionName).isEquals();
    }

    //
    public Permission getParent() {
        return parent;
    }

    public List<Permission> getChildren() {
        if (children == null) {
            children = new ArrayList<Permission>();
        }
        return children;
    }

    //
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
