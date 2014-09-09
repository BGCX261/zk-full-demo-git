package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.configdesigner.util.CfgUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;

@SuppressWarnings("serial")
public class CfgValuePartAddButton extends Button {

    public CfgValuePartAddButton(final CfgInfo cfgInfo,//
            final CfgValuePanel panel) {
        this.setLabel(cfgInfo.getLabel());
        this.addEventListener(Events.ON_CLICK, new EventListener<MouseEvent>() {

            @Override
            public void onEvent(MouseEvent event) throws Exception {
                CfgValue cfgValue = CfgUtil.getCfgParser().buildCfgValue_withNull(cfgInfo, null);
                cfgValue.setParent(panel.getCfgValue());
                Component cComponent = CfgValueZkUtil.createComponent(cfgValue);
                if (cfgInfo.isEmbed()) {
                    Label label = new Label(cfgValue.getLabel());
                    Button delBtn = new CfgValueDeleteButton(cfgValue, panel);
                    Hlayout cTitilePanel = new Hlayout();
                    cTitilePanel.appendChild(label);
                    cTitilePanel.appendChild(delBtn);
                    panel.getMainPanel().appendChild(cTitilePanel);
                    panel.getMainPanel().appendChild(cComponent);
                } else {
                    panel.getMainPanel().appendChild(cComponent);
                }
                CfgValuePartAddButton.this.setParent(null);
            }

        });
    }

}
