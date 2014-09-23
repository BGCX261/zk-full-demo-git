package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.core.CfgType;
import org.hxzon.configdesigner.core.CfgValueValidator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

public class CfgValueInputConstraint implements Constraint {

    private CfgInfo cfgInfo;

    public CfgValueInputConstraint(CfgInfo cfgInfo) {
        this.cfgInfo = cfgInfo;
    }

    @Override
    public void validate(Component comp, Object value_) throws WrongValueException {
        if (value_ == null) {
            return;
        }
        String value = (String) value_;
        value = value.trim();
        if (value.isEmpty()) {
            return;
        }
        CfgType type = cfgInfo.getType();
        CfgValueValidator validator = cfgInfo.getValidator();
        if (type == CfgType.Integer) {
            try {
                Long vl = Long.parseLong(value);
                if (validator == null) {
                    return;
                }
                if (vl > validator.getMaxInteger()) {
                    throw new WrongValueException(comp, "错误：数字不能大于" + validator.getMaxInteger());
                }
                if (vl < validator.getMinInteger()) {
                    throw new WrongValueException(comp, "错误：数字不能小于" + validator.getMinInteger());
                }
            } catch (NumberFormatException e) {
                throw new WrongValueException(comp, "错误：不是整数");
            }
        }
        if (type == CfgType.Real) {
            try {
                Double vd = Double.parseDouble(value);
                if (validator == null) {
                    return;
                }
                if (vd > validator.getMaxDouble()) {
                    throw new WrongValueException(comp, "错误：数字不能大于" + validator.getMaxDouble());
                }
                if (vd < validator.getMinDouble()) {
                    throw new WrongValueException(comp, "错误：数字不能小于" + validator.getMinDouble());
                }
            } catch (NumberFormatException e) {
                throw new WrongValueException(comp, "错误：不是数字");
            }
        }
        if (type == CfgType.String) {
            if (validator == null) {
                return;
            }
            if (value.length() > validator.getMaxlen()) {
                throw new WrongValueException(comp, "错误：字符串长度不能大于" + validator.getMaxlen());
            }
            if (value.length() < validator.getMinlen()) {
                throw new WrongValueException(comp, "错误：字符串长度不能小于" + validator.getMinlen());
            }
        }
    }

}
