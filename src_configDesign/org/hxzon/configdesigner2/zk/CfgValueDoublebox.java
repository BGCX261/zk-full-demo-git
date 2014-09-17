package org.hxzon.configdesigner2.zk;

import org.hxzon.configdesigner2.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zul.Doublebox;

@SuppressWarnings("serial")
public class CfgValueDoublebox extends Doublebox implements CfgValueHolder {

    private CfgValue cfgValue;

    public CfgValueDoublebox(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
        setValue(Dt.toDouble(cfgValue.getValue(), 0));
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
