package org.hxzon.configdesigner.zk;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.util.Dt;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Textbox;

@SuppressWarnings("serial")
public class CfgValuePanel extends Panel {

    private CfgInfo cfgInfo;
    private Object parentValue;
    private String myKey;
    private int myIndex;
    private Object myValue;

    public CfgValuePanel(CfgInfo cfgInfo, Object parentValue) {
        this.cfgInfo = cfgInfo;
        this.parentValue = parentValue;
        JSONObject mapParent = (JSONObject) parentValue;
        myValue = mapParent.get(cfgInfo.getId());
        init();
    }

    public CfgValuePanel(CfgInfo cfgInfo, Object parentValue, String myKey) {
        this.cfgInfo = cfgInfo;
        this.parentValue = parentValue;
        this.myKey = myKey;
        JSONObject mapParent = (JSONObject) parentValue;
        myValue = mapParent.get(myKey);
        init();
    }

    public CfgValuePanel(CfgInfo cfgInfo, Object parentValue, int myIndex) {
        this.cfgInfo = cfgInfo;
        this.parentValue = parentValue;
        this.myIndex = myIndex;
        JSONArray listParent = (JSONArray) parentValue;
        myValue = listParent.get(myIndex);
        init();
    }

    private void init() {
        int type = cfgInfo.getType();
        Panelchildren pc = new Panelchildren();
        switch (type) {
        case CfgInfo.Type_Struct:
            Combobox combobox = new Combobox();
            List<CfgInfo> miss = new ArrayList<CfgInfo>();
            JSONObject json = (JSONObject) myValue;
            for (CfgInfo cInfo : cfgInfo.getParts()) {
                Object cv = json.get(cInfo.getId());
                if (cv == null) {
                    miss.add(cInfo);
                } else {
                    Component cc = createComponent(cInfo, cv);
                    pc.appendChild(cc);
                }
            }
            combobox.setModel(new ListModelList<CfgInfo>(miss));
            pc.appendChild(combobox);
            break;
        case CfgInfo.Type_Map:
            break;
        case CfgInfo.Type_List:
            break;
        }
        this.appendChild(pc);
    }

    private Component createComponent(CfgInfo info, Object value) {
        return createComponent(info, value, null, 0);
    }

    private Component createComponent(CfgInfo info, Object value, String mapKey, int listIndex) {
        int type = cfgInfo.getType();
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
            //return new CfgValuePanel(info, value);
        case CfgInfo.Type_List:
            //return new CfgValuePanel(info, value, listIndex);
        case CfgInfo.Type_Map:
            //return new CfgValuePanel(info, value, mapKey);
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
