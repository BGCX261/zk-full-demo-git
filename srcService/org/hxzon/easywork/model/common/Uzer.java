package org.hxzon.easywork.model.common;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hxzon.easywork.model.security.Role;

@Entity
public class Uzer extends org.hxzon.project.model.Uzer {

    public static final int Department_None = 0;
    public static final int Department_Financial = 1;
    public static final int Department_Buyer = 2;
    public static final int Department_Production = 3;
    public static final int Department_Hardware = 4;
    public static final int Department_Software = 5;
    public static final int Department_ContractAuditor = 6;

    private static final long serialVersionUID = 7570429986649512051L;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "uzer_role", //
    joinColumns = @JoinColumn(name = "uzer_id"), //
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    @OrderBy("roleName")
    private List<Role> roles;

    @Column(columnDefinition = DefineVarchar2048)
    @Size(max = DefineSize2048)
    // @NotBlank
    @Email
    private String email;

    @Column(length = 2)
    private int department = Department_None;

    //
    public String toString() {
        return this.getRealname() + "(" + this.getUsername() + ")";
    }

    public Uzer() {
    }

    public Uzer(String username, String password, String realname, int department) {
        setUsername(username);
        setPassword(password);
        setRealname(realname);
        setDepartment(department);
    }

    //
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDepartment() {
        return department;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

}
