package net.yura.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Yura Mamyrin
 */
public class Cache {

    // as this class is only used on j2se and android, we should use proper logging
    static final Logger logger = Logger.getLogger(Cache.class.getName());
    public static final boolean DEBUG = false;

    File cacheDir;

    public Cache(String appName) {

        String tmpDir = System.getProperty("java.io.tmpdir");

        cacheDir = new File(new File(tmpDir),appName+".cache");

        if (DEBUG) logger.log(Level.INFO, "starting {0}", cacheDir);

        File dir = cacheDir;
        for (;;) {
            if (dir.exists()) {
                if (!dir.isDirectory() || !dir.canWrite()) {
                    throw new RuntimeException("can not write to dir: "+dir);
                }
                if (DEBUG) logger.info("can write to: "+dir);
                break;
            }
            else {
                dir = dir.getParentFile();
            }
        }
    }

    private File getFileName(String uid) {
        try {
            String fileName = URLEncoder.encode(uid, "UTF-8");
            return new File(cacheDir, fileName);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void put(String key, byte[] value) {

        File file = getFileName(key);
        if (file.exists()) {
            logger.log(Level.WARNING, "already has file: {0}", key);
        }
        else {
            try {

                if (DEBUG) logger.log(Level.INFO, "saving to cache: {0}", key);

                if (!cacheDir.isDirectory()) {
                    if (DEBUG) logger.info("Going to make dir "+cacheDir);
                    if (!cacheDir.mkdirs()) {
                        if (!cacheDir.isDirectory()) {
                            throw new RuntimeException("can not make cache dir: "+cacheDir);
                        }
                        //else: the dir must have been made from another thread, so everything is ok anyway.
                    }
                }

                FileOutputStream out = new FileOutputStream(file);
                out.write(value);
                out.close();
            }
            catch (Exception ex) {
                boolean exists = file.exists();
                boolean deleted = false;
                if (exists) {
                    deleted = file.delete();
                }
                logger.log(Level.WARNING, 
                        "failed to save data to file: "+file+
                        " exists="+exists+
                        " deleted="+deleted+
                        " key="+key+
                        " inDir="+cacheDir+
                        " exists="+cacheDir.exists()+
                        " isDir="+cacheDir.isDirectory(), ex);
            }
        }
    }

    public InputStream get(String key) {
        File file = getFileName(key);
        if (file.exists()) {
            try {

                if (DEBUG) logger.log(Level.INFO, "getting from cache: {0}", key);

                file.setLastModified(System.currentTimeMillis());
                return new FileInputStream(file);
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        else {
            if (DEBUG) logger.log(Level.INFO, "key not found: {0}", key);
        }
        return null;
    }

    public boolean containsKey(String key) {
        File file = getFileName(key);
        return file.exists();
    }

}
