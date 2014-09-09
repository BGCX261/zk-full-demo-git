package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zul.Longbox;

@SuppressWarnings("serial")
public class CfgValueLongbox extends Longbox {

    private CfgValue cfgValue;

    public CfgValueLongbox(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
        setValue(Dt.toLong(cfgValue.getValue(), 0));
    }

    public void saveValue() {
        cfgValue.setValue(getValue());
    }

    //==========
    public CfgValue getCfgValue() {
        return cfgValue;
    }
}
