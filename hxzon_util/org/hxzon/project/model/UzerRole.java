package org.hxzon.project.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hxzon.project.BaseEntity;

@MappedSuperclass
public class UzerRole extends BaseEntity {

    private static final long serialVersionUID = 7570429986649512051L;

    @Column(columnDefinition = DefineVarchar32)
    @NotBlank
    @Size(max = DefineSize32)
    private String roleName;

    @Column(columnDefinition = DefineVarchar32)
    @NotBlank
    @Size(max = DefineSize32)
    private String rolegroupName;

    @Column(columnDefinition = DefineVarchar32)
    @NotBlank
    @Size(max = DefineSize32)
    private String roleDesc;

    private int rolegroupOrder;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRolegroupName() {
        return rolegroupName;
    }

    public void setRolegroupName(String rolegroupName) {
        this.rolegroupName = rolegroupName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public int getRolegroupOrder() {
        return rolegroupOrder;
    }

    public void setRolegroupOrder(int rolegroupOrder) {
        this.rolegroupOrder = rolegroupOrder;
    }
}
