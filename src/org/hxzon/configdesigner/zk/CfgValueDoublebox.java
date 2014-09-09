package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zul.Doublebox;

@SuppressWarnings("serial")
public class CfgValueDoublebox extends Doublebox {

    private CfgValue cfgValue;

    public CfgValueDoublebox(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
        setValue(Dt.toDouble(cfgValue.getValue(), 0));
    }

    public void saveValue() {
        cfgValue.setValue(getValue());
    }

    //==========
    public CfgValue getCfgValue() {
        return cfgValue;
    }
}
