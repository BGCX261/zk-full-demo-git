package org.hxzon.easywork.model.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hxzon.project.BaseEntity;

@SuppressWarnings("serial")
@Entity
public class HTag extends BaseEntity {

    @Column(columnDefinition = DefineVarchar2048)
    @Size(max = DefineSize64)
    // @NotBlank
    @Email
    private String title;

    //

    public HTag() {
    }

    public HTag(String title) {
        this.title = title;
    }

    //
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
