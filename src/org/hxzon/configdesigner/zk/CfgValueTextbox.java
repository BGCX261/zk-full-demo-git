package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zul.Textbox;

@SuppressWarnings("serial")
public class CfgValueTextbox extends Textbox {

    private CfgValue cfgValue;

    public CfgValueTextbox(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
        setValue(Dt.toString(cfgValue.getValue(), ""));
        setMultiline(cfgValue.getCfgInfo().isTextArea());
    }

    public void saveValue() {
        cfgValue.setValue(getValue());
    }

    //============
    public CfgValue getCfgValue() {
        return cfgValue;
    }
}
