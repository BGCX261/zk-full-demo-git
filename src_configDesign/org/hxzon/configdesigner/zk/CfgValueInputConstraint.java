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
                    throw new WrongValueException(comp, "请不要大于" + validator.getMaxInteger());
                }
                if (vl < validator.getMinInteger()) {
                    throw new WrongValueException(comp, "请不要小于" + validator.getMinInteger());
                }
            } catch (NumberFormatException e) {
                throw new WrongValueException(comp, "请输入整数");
            }
        }
        if (type == CfgType.Real) {
            try {
                Double vd = Double.parseDouble(value);
                if (validator == null) {
                    return;
                }
                if (vd > validator.getMaxDouble()) {
                    throw new WrongValueException(comp, "请不要大于" + validator.getMaxDouble());
                }
                if (vd < validator.getMinDouble()) {
                    throw new WrongValueException(comp, "请不要小于" + validator.getMinDouble());
                }
            } catch (NumberFormatException e) {
                throw new WrongValueException(comp, "请输入数字");
            }
        }
        if (type == CfgType.String) {
            if (validator == null) {
                return;
            }
            if (value.length() > validator.getMaxlen()) {
                throw new WrongValueException(comp, "字符串的最大长度为" + validator.getMaxlen());
            }
            if (value.length() < validator.getMinlen()) {
                throw new WrongValueException(comp, "字符串的最小长度为" + validator.getMinlen());
            }
        }
    }

}
