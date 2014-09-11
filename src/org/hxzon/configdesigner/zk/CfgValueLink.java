package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgValue;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class CfgValueLink extends Hlayout {

    public CfgValueLink(final CfgValue cfgValue) {
        Label label = new Label(cfgValue.getCfgInfo().getLabel());
        Button btn = new Button();
        btn.setImage("images/easyicon_open.png");
        btn.setTooltiptext("打开");
        btn.addEventListener(Events.ON_CLICK, new EventListener<MouseEvent>() {

            @Override
            public void onEvent(MouseEvent event) throws Exception {
                Window dialog = new Window();
                dialog.setParent(CfgValueLink.this.getParent());
                dialog.appendChild(new CfgValuePaneInfo(cfgValue).getView());
                dialog.setMode(Window.MODAL);
                dialog.setTitle(cfgValue.getCfgInfo().getLabel());
                dialog.setClosable(true);
                dialog.setSizable(true);
                dialog.setWidth("50%");
                dialog.setHeight("50%");
            }

        });
        this.appendChild(label);
        this.appendChild(btn);
    }

}
