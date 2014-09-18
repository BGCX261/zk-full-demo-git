package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zul.Textbox;

@SuppressWarnings("serial")
public class CfgValueKeybox extends Textbox implements CfgValueHolder {

    private CfgValue cfgValue;

    public CfgValueKeybox(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
        setValue(Dt.toString(cfgValue.getKey(), ""));
        this.setConstraint("no empty");
    }

    @Override
    public void saveValue() {
        cfgValue.setKey(getValue());
    }

    //============
    public CfgValue getCfgValue() {
        return cfgValue;
    }
}
