package org.hxzon.easywork.model.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hxzon.easywork.model.base.Upload;

@SuppressWarnings("serial")
@Entity
public class HFile extends Upload {

    @Column(columnDefinition = DefineVarchar2048)
    @Size(max = DefineSize64)
    // @NotBlank
    @Email
    private String title;

    //

    public HFile() {
    }

    //
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
