package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.Dt;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;

public class CfgValueZkUtil {
    public static Component createComponent(CfgValue cfgValue) {
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
            Textbox textbox = new Textbox();
            textbox.setMultiline(info.isTextArea());
            textbox.setValue(Dt.toString(cfgValue.getValue(), ""));
            return textbox;
        case CfgInfo.Type_Struct:
        case CfgInfo.Type_List:
        case CfgInfo.Type_Map:
            return info.isEmbed() ? new CfgValuePanel(cfgValue) : new CfgValueLink(cfgValue);
        default:
            return new Label();
        }
    }
}
