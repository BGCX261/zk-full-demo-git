package org.hxzon.easywork.model.base;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

@MappedSuperclass
public class AttachmentUpload extends Upload {

    private static final long serialVersionUID = 1L;
    @Column(columnDefinition = DefineVarchar32)
    @Size(max = DefineSize32)
    private String ownerId;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

}
