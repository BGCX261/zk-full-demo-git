package org.hxzon.project;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
@DynamicInsert
@DynamicUpdate
public class SimpleEntity implements Serializable {

    private static final long serialVersionUID = -5342595206501703154L;

    public static final int DefineIdSize = 36;
    public static final int DefineSize16 = 16;
    public static final int DefineSize32 = 32;
    public static final int DefineSize64 = 64;
    public static final int DefineSize128 = 128;
    public static final int DefineSize256 = 256;
    public static final int DefineSize512 = 512;
    public static final int DefineSize1024 = 1024;
    public static final int DefineSize2048 = 2048;
    public static final String StringDefaultValue = " default ''";
    public static final String Varchar = "varchar";
    public static final String DefineVarchar16 = Varchar + "(16)" + StringDefaultValue;
    public static final String DefineVarchar32 = Varchar + "(32)" + StringDefaultValue;
    public static final String DefineVarchar64 = Varchar + "(64)" + StringDefaultValue;
    public static final String DefineVarchar128 = Varchar + "(128)" + StringDefaultValue;
    public static final String DefineVarchar256 = Varchar + "(256)" + StringDefaultValue;
    public static final String DefineVarchar512 = Varchar + "(512)" + StringDefaultValue;
    public static final String DefineVarchar1024 = Varchar + "(1024)" + StringDefaultValue;
    public static final String DefineVarchar2048 = Varchar + "(2048)" + StringDefaultValue;

    @Id
    //@GeneratedValue(generator = "hibernateuuid")
    //@GenericGenerator(name = "hibernateuuid", strategy = "uuid")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = DefineIdSize)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(id).hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SimpleEntity)) {
            return false;
        }
        SimpleEntity other = (SimpleEntity) o;
        return new EqualsBuilder().append(this.id, other.getId()).isEquals();
    }
}
