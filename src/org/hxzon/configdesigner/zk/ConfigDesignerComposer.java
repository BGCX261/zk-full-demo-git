package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.configdesigner.util.CfgUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;

@SuppressWarnings("serial")
public class ConfigDesignerComposer extends GenericForwardComposer<Component> {

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        CfgValue cfgValue = CfgUtil.getValue();
        comp.appendChild(new CfgValueViewer(cfgValue).getView());
    }
}
