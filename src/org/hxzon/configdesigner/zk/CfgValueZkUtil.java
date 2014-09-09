package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.core.CfgValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;

public class CfgValueZkUtil {
    public static Component createComponent(CfgValue cfgValue) {
        CfgInfo info = cfgValue.getCfgInfo();
        int type = info.getType();
        switch (type) {
        case CfgInfo.Type_Boolean:
            return new CfgValueCheckbox(cfgValue);
        case CfgInfo.Type_Integer:
            return new CfgValueLongbox(cfgValue);
        case CfgInfo.Type_Real:
            return new CfgValueDoublebox(cfgValue);
        case CfgInfo.Type_String:
            return new CfgValueTextbox(cfgValue);
        case CfgInfo.Type_Struct:
        case CfgInfo.Type_List:
        case CfgInfo.Type_Map:
            return info.isEmbed() ? new CfgValuePanel(cfgValue) : new CfgValueLink(cfgValue);
        default:
            return new Label();
        }
    }
}
