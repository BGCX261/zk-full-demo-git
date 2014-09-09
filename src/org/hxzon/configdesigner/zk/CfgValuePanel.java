package org.hxzon.configdesigner.zk;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.json.JSONObject;
import org.hxzon.configdesigner.core.CfgInfo;
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

    private CfgInfo cfgInfo;
    private Object cfgValue;
    private static final ListitemRenderer<CfgInfo> listitemRenderer = new CfgListitemRenderer();

    public CfgValuePanel(CfgInfo cfgInfo, Object cfgValue) {
        this.cfgInfo = cfgInfo;
        this.cfgValue = cfgValue;
        init();
    }

    private void init() {
        int type = cfgInfo.getType();
        Panelchildren pc = new Panelchildren();
        Vlayout vl = new Vlayout();
        switch (type) {
        case CfgInfo.Type_Struct:
            List<CfgInfo> miss = new ArrayList<CfgInfo>();
            JSONObject json = (JSONObject) cfgValue;
            for (CfgInfo cInfo : cfgInfo.getParts()) {
                Object cv = json.get(cInfo.getId());
                if (cv == null) {
                    miss.add(cInfo);
                } else {
                    Component cc = createComponent(cInfo, cv);
                    if (cInfo.isEmbed()) {
                        Label label = new Label(cInfo.getLabel());
                        Button delBtn = new Button("-");
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
                listbox.setModel(new ListModelList<CfgInfo>(miss));
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

    private Component createComponent(CfgInfo info, Object value) {
        int type = info.getType();
        switch (type) {
        case CfgInfo.Type_Boolean:
            Checkbox checkbox = new Checkbox();
            checkbox.setChecked(Dt.toBoolean(value, false));
            return checkbox;
        case CfgInfo.Type_Integer:
            Longbox longbox = new Longbox();
            longbox.setValue(Dt.toLong(value, 0));
            return longbox;
        case CfgInfo.Type_Real:
            Doublebox doublebox = new Doublebox();
            doublebox.setValue(Dt.toDouble(value, 0));
            return doublebox;
        case CfgInfo.Type_String:
            if (info.isTextArea()) {
                Textarea textarea = new Textarea();
                textarea.setValue(Dt.toString(value, ""));
                return textarea;
            } else {
                Textbox textbox = new Textbox();
                textbox.setValue(Dt.toString(value, ""));
                return textbox;
            }
        case CfgInfo.Type_Struct:
        case CfgInfo.Type_List:
        case CfgInfo.Type_Map:
            return info.isEmbed() ? new CfgValuePanel(info, value) : new CfgValueLink(info, value);
        default:
            return new Label();
        }
    }

    public static class CfgListitemRenderer implements ListitemRenderer<CfgInfo> {

        @Override
        public void render(Listitem item, CfgInfo data, int index) throws Exception {
            item.setLabel(data.getLabel());
        }

    }

    //===============

}
