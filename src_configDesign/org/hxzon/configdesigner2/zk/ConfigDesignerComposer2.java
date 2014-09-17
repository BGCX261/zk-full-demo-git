package org.hxzon.configdesigner2.zk;

import org.hxzon.configdesigner2.core.CfgValue;
import org.hxzon.configdesigner2.util.CfgUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;

@SuppressWarnings("serial")
public class ConfigDesignerComposer2 extends GenericForwardComposer<Component> {

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        CfgValue cfgValue = CfgUtil.getValue();
        new CfgValueViewer2(cfgValue, comp);
    }
}
