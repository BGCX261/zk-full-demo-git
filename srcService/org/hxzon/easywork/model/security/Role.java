package org.hxzon.easywork.model.security;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Size;

import org.hxzon.easywork.model.common.Uzer;
import org.hxzon.project.BaseEntity;

@Entity
public class Role extends BaseEntity {

    private static final long serialVersionUID = -878783539334292324L;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission", //
    joinColumns = @JoinColumn(name = "role_id"), //
    inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @OrderBy("permissionName")
    private List<Permission> permissions;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "uzer_role", //
    joinColumns = @JoinColumn(name = "role_id"), //
    inverseJoinColumns = @JoinColumn(name = "uzer_id"))
    @OrderBy("username")
    private List<Uzer> users;

    @Column(columnDefinition = DefineVarchar32)
    @Size(max = DefineSize32)
    private String roleName;

    @Column(columnDefinition = DefineVarchar32)
    @Size(max = DefineSize32)
    private String displayName;

    @Column(columnDefinition = DefineVarchar16)
    @Size(max = DefineSize16)
    private String roleGroup;//= RoleUtil.RoleGroupName_Custom;

    private boolean inUse = true;

    public String toString() {
        return displayName + "[" + roleName + "]";
    }

    public Role() {
    }

    public Role(String roleName, String displayName, String roleGroup) {
        this.roleName = roleName;
        this.displayName = displayName;
        this.roleGroup = roleGroup;
    }

    //
    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Uzer> getUsers() {
        return users;
    }

    public void setUsers(List<Uzer> users) {
        this.users = users;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRoleGroup() {
        return roleGroup;
    }

    public void setRoleGroup(String roleGroup) {
        this.roleGroup = roleGroup;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

}
