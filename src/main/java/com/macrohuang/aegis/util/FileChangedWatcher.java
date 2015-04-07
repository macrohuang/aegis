package com.macrohuang.aegis.util;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 一个本地文件变化监控器，当文件修改时间发生变化时，触发回调函数，暂时只支持精确文件，不支持目录和通配符
 * 
 * @author Macro Huang
 * @date 2015-4-3
 * @version 1.0
 *
 */
public class FileChangedWatcher {
    private static final FileChangedWatcher WATCHER = new FileChangedWatcher();
    private final Map<File, List<FileChangedCallback>> callbacksByFile =
        new ConcurrentHashMap<File, List<FileChangedCallback>>();
    private final Map<File, Long> lastModifyOfFile = new ConcurrentHashMap<File, Long>();
    private static final String CLASS_PATH_PREFIX = "classpath:";
    private long period = TimeUnit.SECONDS.toMillis(1);
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    class Worker implements Runnable {

        @Override
        public void run() {
            for (File file : lastModifyOfFile.keySet()) {
                if (file.lastModified() > lastModifyOfFile.get(file)) {
                    lastModifyOfFile.put(file, file.lastModified());
                    for (FileChangedCallback callback : callbacksByFile.get(file)) {
                        callback.fileChanged(file);
                    }
                }
            }
        }
    }

    private FileChangedWatcher() {
        executorService.scheduleAtFixedRate(new Worker(), 0, period, TimeUnit.MILLISECONDS);
    }

    /**
     * 设置扫描周期，单位为MS
     * 
     * @param period 扫描周期
     */
    public void setPeriod(long period) {
        this.period = period;
        executorService.shutdown();
        executorService = Executors.newScheduledThreadPool(2);
    }

    /**
     * 获取扫描周期
     * 
     * @return 扫描周期
     */
    public long getPeriod() {
        return period;
    }

    /**
     * 获取单例
     * 
     * @return 自身实例
     */
    public static FileChangedWatcher getInstance() {
        return WATCHER;
    }

    /**
     * 添加对某个文件的监控，只支持确定的文件，不支持文件夹和通配符
     * 
     * @param file 要监控的文件，除非以绝对路径开头，否则会以当前classpath为根路径进行查找，支持以classpath:开头指定classpath
     * @param callback 文件发生变化后的回调处理函数
     */
    public void addCallback(String file, FileChangedCallback callback) {
        assert file != null && file.trim().length() > 0;
        File f = getRealFile(file);
        // 暂不支持文件夹变更了，数据量可能会很大（比如整个文件系统），影响性能，看后续JDK版本有没有什么好的方式监控
        if (f.isDirectory()) {
            throw new RuntimeException("Watch on a directory is currently not supported!");
        }
        if (!callbacksByFile.containsKey(f)) {
            callbacksByFile.put(f, new CopyOnWriteArrayList<FileChangedCallback>());
        }
        if (!lastModifyOfFile.containsKey(f)) {
            lastModifyOfFile.put(f, Long.MIN_VALUE);
        }
        callbacksByFile.get(f).add(callback);
    }

    /**
     * 获取给定文件名的绝对路径，如果文件以绝对路径开头，则直抒返回，以相对路径或classpath:开头则返回当前classpath下的相对路径
     * 
     * @param file 文件名，可以是绝对路径、相对路径或者以classpath:开头的路径
     * @return 文件的绝对路径
     */
    public static File getRealFile(String file) {
        File f;
        // 使用classpath:开头或者相对路径的，需要获取classpath目录下的文件，否则使用绝对路径
        if (file.toLowerCase().startsWith(CLASS_PATH_PREFIX) || !file.startsWith(File.separator)) {
            String filename = file.replace(CLASS_PATH_PREFIX, "");
            f = new File(Thread.currentThread().getContextClassLoader().getResource("").getPath() + filename);
        } else {
            f = new File(file);
        }
        return f;
    }
}
