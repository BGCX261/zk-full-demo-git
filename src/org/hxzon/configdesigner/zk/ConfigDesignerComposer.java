package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.configdesigner.util.CfgUtil;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ConfigDesignerComposer extends GenericForwardComposer<Window> {

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        CfgValue cfgValue = CfgUtil.getValue();
        comp.appendChild(new CfgValuePanel(cfgValue));
    }
}
