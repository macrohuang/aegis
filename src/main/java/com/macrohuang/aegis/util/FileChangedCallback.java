package com.macrohuang.aegis.util;

import java.io.File;

/**
 * 文件变化回调接口
 * 
 * @author Macro Huang
 * @date 2015-4-3
 * @version 1.0
 *
 */
public interface FileChangedCallback {
    /**
     * 当所监控的文件发生变化时，会调用该方法，将目标文件传给监控者
     * 
     * @param file 目标文件
     */
    void fileChanged(File file);
}
