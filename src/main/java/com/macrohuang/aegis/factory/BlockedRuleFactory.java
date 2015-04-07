/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2012 All Rights Reserved.
 */
package com.macrohuang.aegis.factory;

import com.macrohuang.aegis.bo.BlockedPoint;
import com.macrohuang.aegis.bo.BlockedRule;
import com.macrohuang.aegis.bo.BlockedRule.BlockedRuleBuilder;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * @version $Id: BlockedRuleFactory.java, v 0.1 2012-12-12 4:00:55 Exp $
 * 
 * @author huangtianlai
 * 
 */
public class BlockedRuleFactory {
    private static final Logger logger = LoggerFactory.getLogger(BlockedRuleFactory.class);

    private static final String KV_SPLIT = "=";
    private static final String KEY_II = "//";

	private static final String ARGS_SPLIT = "-X";

	private static final List<String> SUPPORTED_FIELDS = BlockedRule.getAllSupportedArg();

	public static void paddingRules(ApplicationContext contexts, String[] origConf,
			Map<String, BlockedPoint> blockedPoints) {
        if (origConf == null || origConf.length == 0) {
            return;
        }

        Set<Entry<String, BlockedPoint>> pointSet = blockedPoints.entrySet();
        for (Entry<String, BlockedPoint> entry : pointSet) {
            entry.getValue().clearRules();
        }

        for (String string : origConf) {
            if (string == null || string.trim().equals("")) {
                continue;
            }
            String blockedPointID = string.substring(0, string.indexOf(KV_SPLIT)).trim();
            String blockedPointConf = string.substring(string.indexOf(KV_SPLIT) + 1).trim();
            if (StringUtils.isBlank(blockedPointID) || blockedPointID.startsWith(KEY_II)
                    || StringUtils.isBlank(blockedPointConf)) {
                continue;
            }
			BlockedPoint blockedPoint = blockedPoints.get(blockedPointID);
			if (blockedPoint == null) {
                blockedPoint = new BlockedPoint(blockedPointID);
				blockedPoints.put(blockedPointID, blockedPoint);
			}

			BlockedRuleBuilder ruleBuilder = new BlockedRuleBuilder(contexts);

            String[] args = StringUtils.splitByWholeSeparator(blockedPointConf, ARGS_SPLIT);

            for (String arg : args) {
				if (StringUtils.isBlank(arg)) {
					continue;
				}

                String[] argKV = StringUtils.split(arg, ":");

                if (argKV.length != 2) {
                    logger.warn("[AEGIS]rule param is null! arg=" + arg);
                    continue;
                }
				String key = argKV[0], val = argKV[1];
				try {
					if (SUPPORTED_FIELDS.contains(key.toLowerCase())) {
						Method method = ruleBuilder.getClass().getDeclaredMethod(key.toLowerCase(), String.class);
						method.invoke(ruleBuilder, val);
					}
                } catch (Exception e) {
                    logger.error("Error while build rule:", e);
                } finally {
					blockedPoint.putParam(key, val);
				}
            }

			blockedPoint.addRule(ruleBuilder.build());
            logger.info("[AEGIS]BlockedPoint[{}] add a rule[{}]",
					new Object[] { blockedPoint.toString(), ruleBuilder.toString() });
        }

    }
}
