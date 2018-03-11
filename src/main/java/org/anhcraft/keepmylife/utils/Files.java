package org.anhcraft.keepmylife.utils;

import org.anhcraft.keepmylife.KeepMyLife;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Files {
    public static void setFileFromJar(String jarpath, String pluginspath) {
        File f = new File(pluginspath);
        if(!f.exists()){
            try {
                InputStream stream = KeepMyLife.plugin.getClass().getResourceAsStream(jarpath);
                BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),
                        StandardCharsets.UTF_8));
                String str;
                while ((str = in.readLine()) != null) {
                    w.write(str+"\n");
                }
                w.flush();
                w.close();
                in.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
