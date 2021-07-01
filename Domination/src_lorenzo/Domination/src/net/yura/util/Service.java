package net.yura.util;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * After the Service class from Sun and the Apache project.
 * With help from Fr�d�ric Miserey.
 *
 * @credits Fr�d�ric Miserey, Joseph Oettinger
 * @author Matthias L. Jugel
 * @version $id$
 */
public class Service {

  /**
   * this needs to be public as on android i need to change this as the build
   * process does not actually allow me to put a file into META-INF/services/
   * http://www.davidwong.com.au/blog/2011/07/using-a-custom-serviceloader-in-android/
   */
  public static String SERVICES_LOCATION = "META-INF/services/";
    
  static HashMap services = new HashMap();

  public static synchronized Iterator providerClasses(Class cls) {
    return providers(cls, false);
  }

  public static synchronized Iterator providers(Class cls) {
    return providers(cls, true);
  }

  public static synchronized Iterator providers(Class cls, boolean instantiate) {
    ClassLoader classLoader = cls.getClassLoader();
    if (classLoader == null) {
        classLoader = Thread.currentThread().getContextClassLoader();
    }
    String providerFile = SERVICES_LOCATION + cls.getName();

    // check whether we already loaded the provider classes
    List providers = (List) services.get(providerFile);
    if (providers != null) {
      return providers.iterator();
    }

    // create new list of providers
    providers = new ArrayList();
    services.put(providerFile, providers);

    try {
        /**
         * This can return 2 files, 1 inside the jar file and 1 inside the build environment 
         */
      Enumeration providerFiles = classLoader.getResources(providerFile);

      if (providerFiles.hasMoreElements()) {
        // cycle through the provider files and load classes
        while (providerFiles.hasMoreElements()) {
          try {
            URL url = (URL) providerFiles.nextElement();
            Reader reader = new InputStreamReader(url.openStream(), "UTF-8");
            if (instantiate) {
              loadResource(reader, classLoader, providers);
            } else {
              loadClasses(reader, classLoader, providers);
            }
          } catch (Exception ex) {
            //ex.printStackTrace();
            // Just try the next file...
          }
        }
      } else {
        // Workaround for broken classloaders, e.g. Orion
        InputStream is = classLoader.getResourceAsStream(providerFile);
        if (is == null) {
          providerFile = providerFile.substring(providerFile.lastIndexOf('.') + 1);
          is = classLoader.getResourceAsStream(providerFile);
        }
        if (is != null) {
          Reader reader = new InputStreamReader(is, "UTF-8");
          loadResource(reader, classLoader, providers);
        }
      }
    } catch (IOException ioe) {
      //ioe.printStackTrace();
      // ignore exception
    }
    return providers.iterator();
  }

  private static List loadClasses(Reader input, ClassLoader classLoader, List classes) throws IOException {
    BufferedReader reader = new BufferedReader(input);

    String line = reader.readLine();
    while (line != null) {
      try {
        // First strip any comment...
        int idx = line.indexOf('#');
        if (idx != -1) {
          line = line.substring(0, idx);
        }

        // Trim whitespace.
        line = line.trim();

        // load class if a line was left
        if (line.length() > 0) {
          // Try and load the class
          classes.add(classLoader.loadClass(line));
        }
      } catch (Exception ex) {
          // this means a name in the file can not be found as a class
        ex.printStackTrace();
        // Just try the next line
      }
      line = reader.readLine();
    }
    return classes;
  }

  private static void loadResource(Reader input, ClassLoader classLoader, List providers) throws IOException {
    List classes = new ArrayList();
    loadClasses(input, classLoader, classes);
    Iterator iterator = classes.iterator();
    while (iterator.hasNext()) {
      Class klass = (Class) iterator.next();
      try {
        Object obj = klass.newInstance();
        // stick it into our vector...
        providers.add(obj);
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      //Logger.debug("Service: loaded "+ obj.getClass().getName());
    }
  }
}
