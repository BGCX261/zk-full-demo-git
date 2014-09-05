package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgInfo;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class CfgValueLink extends Button {

    private CfgInfo cfgInfo;
    private Object cfgValue;

    public CfgValueLink(final CfgInfo cfgInfo, final Object cfgValue) {
        this.cfgInfo = cfgInfo;
        this.cfgValue = cfgValue;
        this.setLabel(cfgInfo.getLabel());
        this.addEventListener(Events.ON_CLICK, new EventListener<MouseEvent>() {

            @Override
            public void onEvent(MouseEvent event) throws Exception {
                Window dialog = new Window();
                dialog.setParent(CfgValueLink.this.getParent());
                dialog.appendChild(new CfgValuePanel(cfgInfo, cfgValue));
                dialog.setMode(Window.MODAL);
                dialog.setTitle(cfgInfo.getLabel());
                dialog.setClosable(true);
                dialog.setSizable(true);
                dialog.setWidth("50%");
                dialog.setHeight("50%");
            }

        });
    }

}
