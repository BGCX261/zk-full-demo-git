package org.hxzon.configdesigner.zk;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.json.JSONObject;
import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.util.Dt;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

@SuppressWarnings("serial")
public class CfgValuePanel extends Panel {

    private CfgInfo cfgInfo;
    private Object cfgValue;

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
            Combobox combobox = new Combobox();
            List<CfgInfo> miss = new ArrayList<CfgInfo>();
            JSONObject json = (JSONObject) cfgValue;
            boolean isPreSimple = false;
            for (CfgInfo cInfo : cfgInfo.getParts()) {
                Object cv = json.get(cInfo.getId());
                if (cv == null) {
                    miss.add(cInfo);
                } else {
                    Label label = new Label(cInfo.getLabel());
                    Component cc = createComponent(cInfo, cv);
                    if (cInfo.getType() < CfgInfo.Type_Combo) {
                        Hlayout hl = new Hlayout();
                        hl.appendChild(label);
                        hl.appendChild(cc);
                        vl.appendChild(hl);
                        isPreSimple = true;
                    } else {
                        if (isPreSimple) {
                            vl.appendChild(new Label());
                        }
                        isPreSimple = false;
                        vl.appendChild(label);
                        vl.appendChild(cc);
                        vl.appendChild(new Label());
                    }
                }
            }
            if (!miss.isEmpty()) {
                combobox.setModel(new ListModelList<CfgInfo>(miss));
                pc.appendChild(combobox);
            }
            break;
        case CfgInfo.Type_Map:
            break;
        case CfgInfo.Type_List:
            break;
        }
        pc.appendChild(vl);
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
            Textbox textbox = new Textbox();
            textbox.setValue(Dt.toString(value, ""));
            return textbox;
        case CfgInfo.Type_Struct:
        case CfgInfo.Type_List:
        case CfgInfo.Type_Map:
            return info.isEmbed() ? new CfgValueLink(info, value) : new CfgValuePanel(info, value);
        default:
            return new Label();
        }
    }

    public static class CfgComboitemRenderer implements ComboitemRenderer<CfgInfo> {

        @Override
        public void render(Comboitem item, CfgInfo data, int index) throws Exception {
            item.setLabel(data.getLabel());
        }

    }

    //===============

}
