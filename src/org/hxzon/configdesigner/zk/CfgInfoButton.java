package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgInfo;
import org.zkoss.zul.Button;

@SuppressWarnings("serial")
public class CfgInfoButton extends Button {

    private CfgInfo cfgInfo;

    public CfgInfoButton(CfgInfo cfgInfo) {
        this.cfgInfo = cfgInfo;
    }

    public CfgInfo getCfgInfo() {
        return cfgInfo;
    }

}
