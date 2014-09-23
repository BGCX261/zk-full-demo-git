package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgType;
import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;

public class CfgValueInputCompHolder implements CfgValueHolder {

    private CfgValue cfgValue;
    private Component inputComponent;

    public CfgValueInputCompHolder(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
        CfgType type = cfgValue.getCfgInfo().getType();
        if (type == CfgType.Boolean) {
            Checkbox checkbox = new Checkbox();
            checkbox.setChecked(Dt.toBoolean(cfgValue.getValue(), false));
            inputComponent = checkbox;
        }
        if (type == CfgType.String) {
            Textbox textbox = new Textbox();
            textbox.setValue(Dt.toString(cfgValue.getValue(), ""));
            textbox.setConstraint(new CfgValueInputConstraint(cfgValue.getCfgInfo()));
            textbox.setMultiline(cfgValue.getCfgInfo().isTextArea());
            if (textbox.isMultiline()) {
                textbox.setRows(8);
                textbox.setCols(150);
            } else {
                textbox.setCols(100);
            }
            inputComponent = textbox;
        }
        if (type == CfgType.Integer || type == CfgType.Real) {
            Textbox textbox = new Textbox();
            textbox.setValue(Dt.toString(cfgValue.getValue(), ""));
            textbox.setConstraint(new CfgValueInputConstraint(cfgValue.getCfgInfo()));
            inputComponent = textbox;
        }
    }

    @Override
    public void saveValue() {
        if (inputComponent instanceof Checkbox) {
            cfgValue.setValue(((Checkbox) inputComponent).isChecked());
            return;
        } else if (inputComponent instanceof Textbox) {
            cfgValue.setValue(((Textbox) inputComponent).getValue());
        }
    }

    //============
    public CfgValue getCfgValue() {
        return cfgValue;
    }

    public Component getInputComponent() {
        return inputComponent;
    }
}
