package com.handongkeji.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.handongkeji.common.Constants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class FileUtil {
    private static final String FOLDER_SEPARATOR = "/";
    private static final char EXTENSION_SEPARATOR = '.';

    static public String SDPATH = getSDPath();

    static public String getSDPath() {
        File sdDir = null;
        File imageDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存�?
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取根目�?
            String path = sdDir.getPath() + "/huijiachifan/";
            imageDir = new File(path);
            if (!imageDir.exists()) {
                //若不存在，创建目录，可以在应用启动的时�?�创�?
                imageDir.mkdirs();
            }
        }
        if (imageDir == null) return null;
        else return sdDir.getPath() + "/huijiachifan/";
    }

    static public void saveBitmap(Bitmap bm, String picName) {
        //Logger.e("", "保存图片");
        if (bm == null || TextUtils.isEmpty(picName)) return;
        try {
            File f = new File(SDPATH, picName + ".jpg");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //Logger.e("", "已经保存"+f.getAbsolutePath());
        } catch (FileNotFoundException e) {
            //Logger.w(TAG, e.getMessage());
        } catch (IOException e) {
            //Logger.w(TAG, e.getMessage());
        }
    }

    static public File createSDDir(String dirName) throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            //Logger.i(TAG, "createSDDir:" + dir.getAbsolutePath());
            //Logger.i(TAG, "createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    static public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }

    static public void delFile(String fileName) {
        File file = new File(SDPATH + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    static public void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除�?有文�?
            // 下面的代码容易报StackOverflowError
            /*else if (file.isDirectory())
                deleteDir(); // 递规的方式删除文件夹*/
        }
        dir.delete();// 删除目录本身
    }

    static public boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    /**
     * 功能：复制文件或者文件夹�?
     *
     * @param inputFile   源文�?
     * @param outputFile  目的文件
     * @param isOverWrite 是否覆盖(只针对文�?)
     * @throws IOException
     * @author 李旺�?
     * @date 2014�?09�?19�?
     */
    static public void copy(File inputFile, File outputFile, boolean isOverWrite)
            throws IOException {
        if (!inputFile.exists()) {
            throw new RuntimeException(inputFile.getPath() + "源目录不存在!");
        }
        copyPri(inputFile, outputFile, isOverWrite);
    }

    /**
     * 功能：为copy 做�?�归使用�?
     *
     * @param inputFile
     * @param outputFile
     * @param isOverWrite
     * @throws IOException
     * @author 李旺�?
     * @date 2014�?09�?19�?
     */
    private static void copyPri(File inputFile, File outputFile,
                                boolean isOverWrite) throws IOException {
        // 是个文件�?
        if (inputFile.isFile()) {
            copySimpleFile(inputFile, outputFile, isOverWrite);
        } else {
            // 文件�?
            if (!outputFile.exists()) {
                outputFile.mkdir();
            }
            // 循环子文件夹
            for (File child : inputFile.listFiles()) {
                copy(child,
                        new File(outputFile.getPath() + "/" + child.getName()),
                        isOverWrite);
            }
        }
    }

    /**
     * 功能：copy单个文件
     *
     * @param inputFile   源文�?
     * @param outputFile  目标文件
     * @param isOverWrite 是否允许覆盖
     * @throws IOException
     * @author 李旺�?
     * @date 2014�?09�?19�?
     */
    private static void copySimpleFile(File inputFile, File outputFile,
                                       boolean isOverWrite) throws IOException {
        // 目标文件已经存在
        if (outputFile.exists()) {
            if (isOverWrite) {
                if (!outputFile.delete()) {
                    throw new RuntimeException(outputFile.getPath() + "无法覆盖�?");
                }
            } else {
                // 不允许覆�?
                return;
            }
        }
        InputStream in = new FileInputStream(inputFile);
        OutputStream out = new FileOutputStream(outputFile);
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();
    }

    /**
     * 功能：删除文�?
     *
     * @param file 文件
     * @author 李旺�?
     * @date 2014�?09�?19�?
     */
    static public void delete(File file) {
        deleteFile(file);
    }

    /**
     * 功能：删除文件，内部递归使用
     *
     * @param file 文件
     * @return boolean true 删除成功，false 删除失败�?
     * @author 李旺�?
     * @date 2014�?09�?19�?
     */
    private static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        // 单文�?
        if (!file.isDirectory()) {
            boolean delFlag = file.delete();
            if (!delFlag) {
                throw new RuntimeException(file.getPath() + "删除失败�?");
            } else {
                return;
            }
        }
        // 删除子目�?
        for (File child : file.listFiles()) {
            deleteFile(child);
        }
        // 删除自己
        file.delete();
    }

    /**
     * 从文件路径中抽取文件的扩展名, 例如. "mypath/myfile.txt" -> "txt". * @author 李旺�?
     *
     * @param path 文件路径
     * @return 如果path为null，直接返回null�?
     * @date 2014�?09�?19�?
     */
    static public String getFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
        if (extIndex == -1) {
            return null;
        }
        int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (folderIndex > extIndex) {
            return null;
        }
        return path.substring(extIndex + 1);
    }

    /**
     * 从文件路径中抽取文件�?, 例如�? "mypath/myfile.txt" -> "myfile.txt"�? * @author 李旺�?
     *
     * @param path 文件路径�?
     * @return 抽取出来的文件名, 如果path为null，直接返回null�?
     * @date 2014�?09�?19�?
     */
    static public String getFilename(String path) {
        if (path == null) {
            return null;
        }
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        return (separatorIndex != -1 ? path.substring(separatorIndex + 1)
                : path);
    }

    public static String readTextFile(File file) {
        if (!file.exists()) {
            return "";
        }
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            char[] buffer = new char[1024];
            int len = 0;
            StringBuilder sb = new StringBuilder();
            while ((len = fileReader.read(buffer)) != -1){
                sb.append(buffer,0,len);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 功能：保存文件�??
     *
     * @param content 字节
     * @param file    保存到的文件
     * @throws IOException
     * @author 李旺�?
     * @date 2014�?09�?19�?
     */
    static public void save(byte[] content, File file) throws IOException {
        if (file == null) {
            throw new RuntimeException("保存文件不能为空");
        }
        if (content == null) {
            throw new RuntimeException("文件流不能为�?");
        }
        InputStream is = new ByteArrayInputStream(content);
        save(is, file);
    }

    /**
     * 功能：保存文�?
     *
     * @param streamIn 文件�?
     * @param file     保存到的文件
     * @throws IOException
     * @author 李旺�?
     * @date 2014�?09�?19�?
     */
    static public void save(InputStream streamIn, File file) throws IOException {
        if (file == null) {
            throw new RuntimeException("保存文件不能为空");
        }
        if (streamIn == null) {
            throw new RuntimeException("文件流不能为�?");
        }
        // 输出�?
        OutputStream streamOut = null;
        // 文件夹不存在就创建�??
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        streamOut = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = streamIn.read(buffer, 0, 8192)) != -1) {
            streamOut.write(buffer, 0, bytesRead);
        }
        streamOut.close();
        streamIn.close();
    }



    public static void writeFile(File file, String data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data.getBytes("UTF-8"));
            fos.flush();
            fos = null;
        } catch (Exception ex) {
        }

    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    public static void genarateEmojiXML(ArrayList<String> cellArr) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("emojis");
            for (int i = 0; i < cellArr.size(); i++) {
                Element element = doc.createElement("id");
                Text content = doc.createTextNode(cellArr.get(i));
                element.appendChild(content);
                root.appendChild(element);
            }

            doc.appendChild(root);

            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String sd_path = Environment.getExternalStorageDirectory().getAbsolutePath();
                String xml_path = sd_path + "emoji_test.xml";

                t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(new File(xml_path))));
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //  保存对象
    public static <T extends Serializable> void writeObject(Class<T> clazz, T object) {
        File file = new File(Constants.CACHE_DIR + "object/" + clazz.getName());
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T> T readObject(Class<T> clazz) {
        File file = new File(Constants.CACHE_DIR + "object/" + clazz.getName());
        if (!file.exists()) {
            return null;
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            return (T) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public interface TextViewWraper {
        public void setText(CharSequence text);
    }

}