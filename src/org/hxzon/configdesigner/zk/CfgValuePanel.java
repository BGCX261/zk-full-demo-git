package org.hxzon.configdesigner.zk;

import java.util.ArrayList;
import java.util.List;

import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zhtml.Textarea;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

@SuppressWarnings("serial")
public class CfgValuePanel extends Panel {

    private static final CfgListitemRenderer listitemRenderer = new CfgListitemRenderer();
    private CfgValue cfgValue;

    public CfgValuePanel(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
        init();
    }

    private void init() {
        CfgInfo cfgInfo = cfgValue.getCfgInfo();
        int type = cfgInfo.getType();
        Panelchildren pc = new Panelchildren();
        Vlayout vl = new Vlayout();
        switch (type) {
        case CfgInfo.Type_Struct:
            List<CfgValue> miss = new ArrayList<CfgValue>();
            for (CfgValue c : cfgValue.getChildren()) {
                if (c.isNull()) {
                    miss.add(c);
                } else {
                    Component cc = createComponent(c);
                    if (c.getCfgInfo().isEmbed()) {
                        Label label = new Label(c.getLabel());
                        Button delBtn = new Button("删除");
                        Hlayout hl = new Hlayout();
                        hl.appendChild(label);
                        hl.appendChild(delBtn);
                        vl.appendChild(hl);
                        vl.appendChild(cc);
                    } else {
                        vl.appendChild(cc);
                    }
                }
            }
            if (!miss.isEmpty()) {
                Listbox listbox = new Listbox();
                listbox.setModel(new ListModelList<CfgValue>(miss));
                listbox.setItemRenderer(listitemRenderer);
                Hlayout hl = new Hlayout();
                hl.appendChild(vl);
                hl.appendChild(listbox);
                pc.appendChild(hl);
            } else {
                pc.appendChild(vl);
            }
            break;
        case CfgInfo.Type_Map:
            break;
        case CfgInfo.Type_List:
            break;
        }
        this.appendChild(pc);
    }

    private Component createComponent(CfgValue cfgValue) {
        CfgInfo info = cfgValue.getCfgInfo();
        int type = info.getType();
        switch (type) {
        case CfgInfo.Type_Boolean:
            Checkbox checkbox = new Checkbox();
            checkbox.setChecked(Dt.toBoolean(cfgValue.getValue(), false));
            return checkbox;
        case CfgInfo.Type_Integer:
            Longbox longbox = new Longbox();
            longbox.setValue(Dt.toLong(cfgValue.getValue(), 0));
            return longbox;
        case CfgInfo.Type_Real:
            Doublebox doublebox = new Doublebox();
            doublebox.setValue(Dt.toDouble(cfgValue.getValue(), 0));
            return doublebox;
        case CfgInfo.Type_String:
            if (info.isTextArea()) {
                Textarea textarea = new Textarea();
                textarea.setValue(Dt.toString(cfgValue.getValue(), ""));
                return textarea;
            } else {
                Textbox textbox = new Textbox();
                textbox.setValue(Dt.toString(cfgValue.getValue(), ""));
                return textbox;
            }
        case CfgInfo.Type_Struct:
        case CfgInfo.Type_List:
        case CfgInfo.Type_Map:
            return info.isEmbed() ? new CfgValuePanel(cfgValue) : new CfgValueLink(cfgValue);
        default:
            return new Label();
        }
    }

    public static class CfgListitemRenderer implements ListitemRenderer<CfgValue> {

        @Override
        public void render(Listitem item, CfgValue data, int index) throws Exception {
            item.setLabel(data.getLabel());
        }

    }

    //===============

}
