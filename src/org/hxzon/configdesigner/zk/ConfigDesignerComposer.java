package org.hxzon.configdesigner.zk;

import org.apache.tapestry5.json.JSONObject;
import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.util.CfgUtil;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ConfigDesignerComposer extends GenericForwardComposer<Window> {

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        CfgInfo root = CfgUtil.getInfo();
        Object value = CfgUtil.getValue();
        JSONObject rootParent = new JSONObject();
        rootParent.put(root.getId(), value);
        comp.appendChild(new CfgValuePanel(root, rootParent));
    }
}
