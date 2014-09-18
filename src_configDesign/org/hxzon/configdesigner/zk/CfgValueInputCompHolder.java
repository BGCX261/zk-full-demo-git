package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgType;
import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Longbox;
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
            textbox.setMultiline(cfgValue.getCfgInfo().isTextArea());
            if (textbox.isMultiline()) {
                textbox.setRows(8);
                textbox.setCols(150);
            } else {
                textbox.setCols(100);
            }
            inputComponent = textbox;
        }
        if (type == CfgType.Integer) {
            Longbox longbox = new Longbox();
            longbox.setValue(Dt.toLong(cfgValue.getValue(), 0));
            inputComponent = longbox;
        }
        if (type == CfgType.Real) {
            Doublebox doublebox = new Doublebox();
            doublebox.setValue(Dt.toDouble(cfgValue.getValue(), 0));
            inputComponent = doublebox;
        }
    }

    @Override
    public void saveValue() {
        if (inputComponent instanceof Checkbox) {
            cfgValue.setValue(((Checkbox) inputComponent).isChecked());
            return;
        } else if (inputComponent instanceof Textbox) {
            cfgValue.setValue(((Textbox) inputComponent).getValue());
        } else if (inputComponent instanceof Longbox) {
            cfgValue.setValue(((Longbox) inputComponent).getValue());
        } else if (inputComponent instanceof Doublebox) {
            cfgValue.setValue(((Doublebox) inputComponent).getValue());
        }
//        CfgValueValidator validator = cfgValue.getCfgInfo().getValidator();
//        String value = getValue();
//        if (validator != null) {
//            cfgValue.setValue(validator.convert(value));
//        } else {
//            cfgValue.setValue(value);
//        }
    }

    //============
    public CfgValue getCfgValue() {
        return cfgValue;
    }

    public Component getInputComponent() {
        return inputComponent;
    }
}
