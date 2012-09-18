package com.prj.smarthome;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;


/* 用法:
 * IniReader reader = new IniReader("/sdcard/*.ini");
 * System.out.println(reader.getValue(section, key));
 * */

public class IniReader {
        //protected HashMap<String, Properties> sections = new HashMap<String, Properties>();
        protected HashMap<String, Properties> sections;// = new HashMap<String, Properties>();
        private transient String currentSecion;
        private transient Properties current;
        private int MAXSECTIONS = 256;
        private int MAXELEMS = 256;
        
        public int SectionCount = 0;
        public int elemCount = 0;
        
        public String[] Sections = new String[MAXSECTIONS];
        //public String[] Keys = new String[MAXELEMS];
        public String[] Names = new String[MAXELEMS];
/*
        public IniReader(String filename) throws IOException {
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                SectionCount = 0;
                keyCount = 0;
                read(reader);
                reader.close();
        }
        */
        public IniReader() {
        	sections = new HashMap<String, Properties>();
            SectionCount = 0;
            elemCount = 0;
        	/*
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            SectionCount = 0;
            keyCount = 0;
            read(reader);
            reader.close();
            */
        }
        
        protected void read(BufferedReader reader) throws IOException {
                String line;
                while ((line = reader.readLine()) != null) {
                        parseLine(line);
                }
        }
 
        protected void parseLine(String line) {
            line = line.trim();
            if (line.matches("\\[.*\\]")) {
                currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
                current = new Properties();
                sections.put(currentSecion, current);
                Sections[SectionCount]=currentSecion;
                SectionCount += 1;
                
            } else if (line.matches(".*=.*")) {
                if (current != null) {
                    int i = line.indexOf('=');
                        String name = line.substring(0, i);
                        String value = line.substring(i + 1);
                        current.setProperty(name, value);
                    }
 
            }
        }
        
        //清空(初始化)section
        public void clear() {
        	sections.clear();
        }        
        
        //装载文件并解析
        public void load (String filename) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            clear();
            SectionCount = 0;
            elemCount = 0;
            read(reader);
            reader.close();
        }
        

 
        //获取section节,name键的键值
        public String getValue(String section, String name) {
            Properties p = (Properties) sections.get(section);
             if (p == null) {
                return null;
            }
            String value = p.getProperty(name);
            return value;
        }
        
        //获取section节内所有键名name及键值key和元素数量
        public int getNames(String section) {
        	Enumeration<Object> e;
        	Properties p = (Properties) sections.get(section);
        	elemCount = 0;
        	for (e=p.keys();e.hasMoreElements();) {
        		Names[elemCount] = (String) e.nextElement();
        		elemCount+=1;
        	}
        	return elemCount;
        }
        
        //判断section是否存在
        boolean isExistSection(String section) {
        	Properties p = (Properties) sections.get(section);
        	if (null != p) {
        		return true;
        	} else {
        		return false;
        	}
        }
        
        //获取值 "12,34" 中 x坐标值 12 
        public int getX(String value) {
        	int x = 0;
        	int i = value.indexOf(",");
        	String Sx = value.substring(0, i);
        	x = Integer.parseInt(Sx);
        	return x;
        }
        
      //获取值 "12,34" 中 y坐标值 34 
        public int getY(String value) {
        	int y = 0;
        	int i = value.indexOf(",");
        	String Sy = value.substring(i+1);
        	y = Integer.parseInt(Sy);
        	return y;
        }
 

}