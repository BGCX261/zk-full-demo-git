package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgValue;
import org.zkoss.zul.Button;

@SuppressWarnings("serial")
public class CfgValueButton extends Button {

    private CfgValue cfgValue;

    public CfgValueButton(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
    }

    public CfgValue getCfgValue() {
        return cfgValue;
    }

}
