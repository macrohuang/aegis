/**
 * Baidu.com Inc. Copyright (c) 2000-2012 All Rights Reserved.
 */
package com.macrohuang.aegis.repository;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.PatternMatchUtils;

import com.macrohuang.aegis.api.BlockedRuleChangedCallback;
import com.macrohuang.aegis.bo.BlockedPoint;
import com.macrohuang.aegis.factory.BlockedRuleFactory;
import com.macrohuang.aegis.util.FileChangedCallback;
import com.macrohuang.aegis.util.FileChangedWatcher;
import com.macrohuang.aegis.util.ThreadLocalHelper;

/**
 * 
 * @author Tianlai Huang(huangtianlai@baidu.com)
 */
public class BlockedPointLocalCache implements FileChangedCallback, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(BlockedPointLocalCache.class);

    private final Map<String, BlockedPoint> blockedPoints = new LinkedHashMap<String, BlockedPoint>();

    private static final Set<BlockedRuleChangedCallback> CALLBACKS = new CopyOnWriteArraySet<BlockedRuleChangedCallback>();

    private ApplicationContext context;
    private String ruleFile;

    private synchronized void initBlockedPoints() {
        if (this.blockedPoints.size() != 0) {
            logger.warn("[AEGIS]BlockedPoint has been init, size [{}]", this.blockedPoints.size());
            return;
        }
        try {
            refreshBlockedRules(FileUtils.readFileToString(FileChangedWatcher.getAbsoluteFile(ruleFile)));
        } catch (IOException e) {
            logger.error("Error while init rules", e);
            throw new RuntimeException("Error while init rules:" + e.getMessage());
        }
    }

    private synchronized void refreshBlockedRules(String ruleString) {
        try {
            if (StringUtils.isNotBlank(ruleString)) {
                String[] rules = StringUtils.split(ruleString, "\n");
                BlockedRuleFactory.paddingRules(context, rules, blockedPoints);
            } else {
                logger.warn("[AEGIS]No any rule on confcenter, refresh rules over.");
            }
        } catch (Exception e) {
            logger.error("[AEGIS]Refresh rules from confcenter fail!!", e);
        }
        for (BlockedRuleChangedCallback callback : CALLBACKS) {
            callback.ruleChanged();
        }
    }

    /**
     * @param blockedPointID
     * @return
     */
    public BlockedPoint getBlockedPoint(String blockedPointID) {
        BlockedPoint blockedPoint = null;
        if (blockedPoints.containsKey(blockedPointID)) {
            blockedPoint = blockedPoints.get(blockedPointID);
        } else {
            for (String pointKey : blockedPoints.keySet()) {
                if (PatternMatchUtils.simpleMatch(pointKey, blockedPointID)) {
                    blockedPoint = blockedPoints.get(pointKey);
                    break;
                }
            }
        }
        if (blockedPoint != null) {
            ThreadLocalHelper.bindParams(blockedPoint.getParams());
        }
        return blockedPoint;
    }

    public static void addCallback(BlockedRuleChangedCallback callback) {
        CALLBACKS.add(callback);
    }

    public static void removeCallback(BlockedRuleChangedCallback callback) {
        for (BlockedRuleChangedCallback cb : CALLBACKS) {
            if (cb.equals(callback)) {
                CALLBACKS.remove(callback);
            }
        }
    }

    /**
     * @return the ruleFile
     */
    public String getRuleFile() {
        return ruleFile;
    }

    /**
     * @param ruleFile the ruleFile to set
     */
    public void setRuleFile(String ruleFile) {
        this.ruleFile = ruleFile;
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        // 加入callback
        Map<String, BlockedRuleChangedCallback> callbacksByName = context
                .getBeansOfType(BlockedRuleChangedCallback.class);
        for (BlockedRuleChangedCallback callback : callbacksByName.values()) {
            BlockedPointLocalCache.CALLBACKS.add(callback);
        }
        FileChangedWatcher.getInstance().addCallback(ruleFile, this);
        initBlockedPoints();
    }

    @Override
    public void fileChanged(File file) {
        if (file.exists()) {
            try {
                String content = FileUtils.readFileToString(file);
                logger.warn("[AEGIS]ConfCenter file changed, params[{}]", content);
                refreshBlockedRules(content);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("File not exists:" + file.getAbsolutePath());
        }
    }
}
