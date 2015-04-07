package com.macrohuang.aegis.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class FileChangedWatcherTest {
    @Test
    public void testGetAbsoluteFile() throws IOException {
        String filename = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "test";
        File f = new File(filename);
        f.delete();
        f.createNewFile();
        File f2 = FileChangedWatcher.getAbsoluteFile(filename);
        Assert.assertEquals(f, f2);
        File f3 = FileChangedWatcher.getAbsoluteFile("classpath:test");
        Assert.assertEquals(f2, f3);
        f.delete();
    }

    @Test
    public void testAddCallback() throws InterruptedException, IOException {
        final AtomicBoolean hasChanged = new AtomicBoolean(false);
        FileChangedWatcher.getInstance().addCallback("test", new FileChangedCallback() {

            @Override
            public void fileChanged(File file) {
                System.out.println("File:" + file.getName() + " has been changed");
                try {
                    System.out.println(FileUtils.readFileToString(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hasChanged.set(true);

            }
        });
        Thread.sleep(2000);
        Assert.assertTrue(hasChanged.get());
        hasChanged.set(false);
        Thread.sleep(2000);
        Assert.assertFalse(hasChanged.get());

        File f = FileChangedWatcher.getAbsoluteFile("test");
        FileWriter fw = new FileWriter(f);
        fw.write("This is a test");
        fw.close();

        Thread.sleep(2000);
        Assert.assertTrue(hasChanged.get());
        f.delete();
    }
}
