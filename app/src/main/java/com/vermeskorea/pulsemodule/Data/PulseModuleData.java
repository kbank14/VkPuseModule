package com.vermeskorea.pulsemodule.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kbank14 on 2018-01-08.
 */

public class PulseModuleData {
    public static class PulseParamInfo {
        public int Distance = 0;                     // 길이
        public int Action = 'L';                     // 동작 'L'=Low, 'H'=High
        public int PulseCount = 0;                 // 동작 펄수 개수
    }

    public int inPulseCount = 1000;   // 입력펄스 개수
    public int inPulseLength = 1;    // inPulseCount당 이동거리 1mm
    public int cutLeft = 1;          // 지연 컷 %(왼쪽)
    public int cutRight = 1;         // 지연 컷(오른쪽)

    private List<List<PulseParamInfo>> pulseArray;

    private static PulseModuleData mPulseModuleData = null;

    public static synchronized PulseModuleData getInstance() {
        if (mPulseModuleData == null)
            mPulseModuleData = new PulseModuleData();

        return mPulseModuleData;
    }

    public int calcTargetPulse(int distance) {
        if (inPulseCount <= 0 ||
                inPulseLength <= 0)
            return 0;

        int target = (int) ((double) distance / (double) inPulseLength * inPulseCount);

        if (cutLeft > 0)
            target -= (double) cutLeft / 100.0 * target;
        if (cutRight > 0)
            target -= (double) cutRight / 100.0 * target;

        return target;
    }

    public PulseModuleData() {
        pulseArray = new ArrayList<List<PulseParamInfo>>();
        for (int i = 0; i < 8; i++) {
            pulseArray.add(new ArrayList<PulseParamInfo>());
        }
    }

    public synchronized void add(int mode, PulseParamInfo info) {
        try {
            pulseArray.get(mode).add(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void edit(int mode, int index, PulseParamInfo info) {
        try {
            List<PulseParamInfo> list = pulseArray.get(mode);
            PulseParamInfo val = list.get(index);
            val.Distance = info.Distance;
            val.Action = info.Action;
            val.PulseCount = info.PulseCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PulseParamInfo> get(int mode) {
        try {
            return pulseArray.get(mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PulseParamInfo get(int mode, int index) {
        try {
            return pulseArray.get(mode).get(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void delete(int mode) {
        try {
            List<PulseParamInfo> list = pulseArray.get(mode);
            list.remove(list.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void clear() {
        try {
            for (int i = 0; i < pulseArray.size(); i++) {
                List<PulseParamInfo> list = pulseArray.get(i);
                list.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fileDir = "/sdcard";
    private String fileName = fileDir + "/vkpulse.txt";

    private String vkDeviceDir = "/sys/class/vk";
    private String vkDevicePath = vkDeviceDir + "/vk_file";

    public synchronized void setConfigValue() {
        File dir = makeDirectory(vkDeviceDir);
        File file = openFile(dir, vkDevicePath);
        if (file == null || file.exists() == false)
            return;

        String write = fileName;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(write.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean loadFile() {
        File dir = makeDirectory(fileDir);
        File file = openFile(dir, fileName);
        if (file == null || file.exists() == false)
            return false;

        clear();
        try {
            FileInputStream fis = new FileInputStream(file);
            int readcount = (int) file.length();
            byte[] buffer = new byte[readcount];
            fis.read(buffer);
            String stringBuffer = new String(buffer);

            fis.close();

            String[] ar = stringBuffer.split("\n");
            for (int i = 0; i < ar.length; i++) {
                String[] line = ar[i].split(" ");
                if (line.length >= 5) {
                    switch (line[0]) {
                        case "C":
                            inPulseCount = Integer.parseInt(line[1]);
                            inPulseLength = Integer.parseInt(line[2]);
                            cutLeft = Integer.parseInt(line[3]);
                            cutRight = Integer.parseInt(line[4]);
                            break;
                        case "D":
                            PulseParamInfo info = new PulseParamInfo();
                            int mode = Integer.parseInt(line[1]);
                            //int index = Integer.parseInt(line[2]);
                            info.Distance = Integer.parseInt(line[3]);
                            info.Action = line[4].charAt(0);
                            info.PulseCount = Integer.parseInt(line[5]);

                            add(mode, info);
                            break;
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean writeDevice(String path, String content) {
        File f = new File(path);
        if (f == null || f.exists() == false)
            return false;

        StringBuffer output = new StringBuffer();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(f));
            writer.write(content);
            writer.close();

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    String setFilePath = "/sys/class/vk/vk_set";

    private void setParam(String line) {
        writeDevice(setFilePath, line);
    }

    public synchronized boolean saveFile() {
        File dir = makeDirectory(fileDir);
        File file = newFile(dir, fileName);
        if (file == null || file.exists() == false)
            return false;

        // C <inPulseCount> <inPulseLength> <cutLeft> <cutRight>
        String write = String.format("C %d %d %d %d\n",
                inPulseCount,
                inPulseLength,
                cutLeft,
                cutRight);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(write.getBytes());
            fos.flush();

             for (int i = 0; i < 8 && i < pulseArray.size(); i++) {
                List<PulseParamInfo> list = pulseArray.get(i);
                for (int j = 0; j < list.size(); j++) {
                    PulseParamInfo val = list.get(j);

                    // D <mode> <index> <length> <action> <pulse count>
                    write = String.format("D %d %d %d %c %d\n",
                            i,
                            j,
                            val.Distance,
                            val.Action,
                            val.PulseCount);
                    fos.write(write.getBytes());
                    fos.flush();
                }
            }
            fos.close();

            setFile();

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public synchronized boolean setFile() {
        setParam("@");

        // C <inPulseCount> <inPulseLength> <cutLeft> <cutRight>
        String write = String.format("C %d %d %d %d\n",
                inPulseCount,
                inPulseLength,
                cutLeft,
                cutRight);

        try {
            setParam(write);

            for (int i = 0; i < 8 && i < pulseArray.size(); i++) {
                List<PulseParamInfo> list = pulseArray.get(i);
                for (int j = 0; j < list.size(); j++) {
                    PulseParamInfo val = list.get(j);

                    // D <mode> <index> <length> <action> <pulse count>
                    write = String.format("D %d %d %d %c %d\n",
                            i,
                            j,
                            val.Distance,
                            val.Action,
                            val.PulseCount);
                    setParam(write);
                }
            }

            setParam("?");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean deleteFile(File file) {
        boolean result;
        if (file != null && file.exists()) {
            file.delete();
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    private File makeDirectory(String dir_path) {
        File dir = new File(dir_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    private File newFile(File dir, String file_path) {
        File file = null;

        if (dir.isDirectory()) {
            file = new File(file_path);
            if (file != null && file.exists())
                file.delete();

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private File openFile(File dir, String file_path) {
        File file = null;
        if (dir.isDirectory()) {
            file = new File(file_path);
        }
        return file;
    }

    private boolean isFile(File file) {
        boolean result;
        if (file != null && file.exists() && file.isFile()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    private void RunDevice() {
        String sysfs = "/sys/class/vk/vk_timer_sysfs";
    }
}
