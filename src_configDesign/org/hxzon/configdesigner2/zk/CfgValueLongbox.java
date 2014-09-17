package org.hxzon.configdesigner2.zk;

import org.hxzon.configdesigner2.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zul.Longbox;

@SuppressWarnings("serial")
public class CfgValueLongbox extends Longbox implements CfgValueHolder {

    private CfgValue cfgValue;

    public CfgValueLongbox(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
        setValue(Dt.toLong(cfgValue.getValue(), 0));
    }

    @Override
    public void saveValue() {
        cfgValue.setValue(getValue());
    }

    //==========
    public CfgValue getCfgValue() {
        return cfgValue;
    }
}
