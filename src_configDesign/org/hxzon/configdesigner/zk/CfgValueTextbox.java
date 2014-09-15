package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zul.Textbox;

@SuppressWarnings("serial")
public class CfgValueTextbox extends Textbox implements CfgValueHolder {

    private CfgValue cfgValue;

    public CfgValueTextbox(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
        setValue(Dt.toString(cfgValue.getValue(), ""));
        setMultiline(cfgValue.getCfgInfo().isTextArea());
        if (isMultiline()) {
            setRows(8);
            setCols(150);
        } else {
            setCols(100);
        }
    }

    @Override
    public void saveValue() {
        cfgValue.setValue(getValue());
    }

    //============
    public CfgValue getCfgValue() {
        return cfgValue;
    }
}
