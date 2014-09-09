package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zul.Checkbox;

@SuppressWarnings("serial")
public class CfgValueCheckbox extends Checkbox {

    private CfgValue cfgValue;

    public CfgValueCheckbox(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
        setChecked(Dt.toBoolean(cfgValue.getValue(), false));
    }

    public void saveValue() {
        cfgValue.setValue(isChecked());
    }

    //
    public CfgValue getCfgValue() {
        return cfgValue;
    }
}
