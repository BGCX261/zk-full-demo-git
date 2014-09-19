package org.hxzon.configdesigner.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.core.CfgParser;
import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;

public class CfgUtil {

    private static final Logger logger = LoggerFactory.getLogger(CfgUtil.class);
    private static CfgValue configer;
    private static Map<Integer, CfgValue> indexer = new HashMap<Integer, CfgValue>();;

    static {
        try {
            WebApp webapp = Executions.getCurrent().getDesktop().getWebApp();
            String xmlStr = FileUtils.readFileToString(new File(webapp.getRealPath("/WEB-INF/configDesigner.xml")), "utf8");
            CfgInfo info = CfgParser.parseSchema(xmlStr);
            String jsonStr = FileUtils.readFileToString(new File(webapp.getRealPath("/WEB-INF/config.json")), "utf8");
            Object json = JsonUtil.parseJson(jsonStr);
            configer = CfgParser.buildCfgValue(info, json, 1000, 1);
            indexCfg(configer);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static CfgValue getValue() {
        return configer;
    }

    public static void indexCfg(CfgValue cfg) {
        indexer.put(cfg.indexCode(), cfg);
        if (cfg.getCfgInfo().getType().isCombo()) {
            for (CfgValue childCfg : cfg.getChildren()) {
                indexCfg(childCfg);
            }
        }
    }

    public static CfgValue findCfg(int indexCode) {
        if (indexCode == 0) {
            return configer;
        }
        return indexer.get(indexCode);
    }

    public static Collection<CfgValue> cfgs() {
        return indexer.values();
    }

    public static Collection<CfgValue> findCfg(CfgInfo cfgInfo) {
        Collection<CfgValue> r = new HashSet<CfgValue>();
        for (CfgValue cfg : cfgs()) {
            if (cfg.getCfgInfo() == cfgInfo) {
                r.add(cfg);
            }
        }
        return r;
    }

}
