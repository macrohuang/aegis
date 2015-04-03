/**
 * Baidu.com Inc. Copyright (c) 2000-2012 All Rights Reserved.
 */
package com.baidu.fengchao.skynet.repository;

import com.baidu.fengchao.aegis.api.BlockedRuleChangedCallback;
import com.baidu.fengchao.aegis.bo.BlockedPoint;
import com.baidu.fengchao.ether.api.ChangedCallBack;
import com.baidu.fengchao.ether.api.IConfCenterClient;
import com.baidu.fengchao.skynet.factory.BlockedRuleFactory;
import com.baidu.fengchao.skynet.util.ThreadLocalHelper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.PatternMatchUtils;

/**
 * 
 * @author zhengjiaqing [ http://jiaqing.me ]
 * @author Tianlai Huang(huangtianlai@baidu.com)
 * @version $Id: BlockedPointCache.java, v 0.1 2012-12-12 ����3:23:01 zhengjiaqing
 *          Exp $
 */
public class BlockedPointLocalCache implements ChangedCallBack, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(BlockedPointLocalCache.class);

    private final Map<String, BlockedPoint> blockedPoints = new LinkedHashMap<String, BlockedPoint>();

    private static final Set<BlockedRuleChangedCallback> CALLBACKS = new CopyOnWriteArraySet<BlockedRuleChangedCallback>();

    private ApplicationContext context;
    private IConfCenterClient confCenterClient;
    private String ruleFile;

    private synchronized void initBlockedPoints() {
        if (this.blockedPoints.size() != 0) {
            logger.warn("[AEGIS]BlockedPoint has been init, size [{}]", this.blockedPoints.size());
            return;
        }
        refreshBlockedRules();
    }

    /**
     * 
     * @see com.baidu.fengchao.ether.api.ChangedCallBack#fileChanged(java.lang.String)
     */
    public void fileChanged(String fullString) {
        logger.warn("[AEGIS]ConfCenter file changed, params[{}]", fullString);
        refreshBlockedRules();
    }

    private synchronized void refreshBlockedRules() {
        try {
            String rulesString = getConfCenterClient().getConfFileAll(getRuleFile());
            if (StringUtils.isNotBlank(rulesString)) {
                String[] rules = StringUtils.split(rulesString, "\n");
                BlockedRuleFactory.paddingRules(context, rules, blockedPoints);
            } else {
                logger.warn("[AEGIS]No any rule on confcenter, refresh rules over.");
            }
            getConfCenterClient().addConfListener(getRuleFile(), this);
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

    @Override
    public void folderChanged(Object[] newValue) {
        logger.warn("[AEGIS]ConfCenter folder changed");
        refreshBlockedRules();
    }

    @Override
    public String getType() {
        return ChangedCallBack.TYPE_NORMARL;
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
     * @return the confCenterClient
     */
    public IConfCenterClient getConfCenterClient() {
        return confCenterClient;
    }

    /**
     * @param confCenterClient the confCenterClient to set
     */
    public void setConfCenterClient(IConfCenterClient confCenterClient) {
        this.confCenterClient = confCenterClient;
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
        //加入callback
        Map<String, BlockedRuleChangedCallback> callbacksByName = context
                .getBeansOfType(BlockedRuleChangedCallback.class);
        for (BlockedRuleChangedCallback callback : callbacksByName.values()) {
            BlockedPointLocalCache.CALLBACKS.add(callback);
        }
        initBlockedPoints();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.ether.api.ChangedCallBack#fileChanged(byte[])
     */
    @Override
    public void fileChanged(byte[] newValue) {
        logger.warn("[AEGIS]ConfCenter file changed, params[{}]", new String(newValue));
        refreshBlockedRules();
    }
}
