package com.example.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.content.Context;
import android.os.Environment;

import com.example.dao.SettingDao;
import com.example.model.Setting;

public class FTPManager {
    private final  static int FTPPORT=21;//FTP端口
    private final static String IpAdress="123.57.41.84";
    private final  static String FTPUSER="zhengbeidanci";//FTP用户名
    private final static  String FTPPASSWORD="zhengbeidanci";//FTP密码


    public static final String dbName="now_word.db";//数据库文件名称
    public final static String localPath= "/data/data/" + "com.example.now_word" + "/databases/" + dbName;//本地文件路径
    public final static String ServerPhotoPath="/photo/";//服务器图片存储路径
    public final static String ServerDataBasePath="/DataBase/";//服务器数据库存储路径

    private SettingDao settingDao;

    FTPClient ftpClient = null;
    public FTPManager(Context context) {
        ftpClient = new FTPClient();
        settingDao=new SettingDao(context);
    }

    // 连接到ftp服务器
    public synchronized boolean connect() throws Exception {
        boolean bool = false;
        if (ftpClient.isConnected()) {//判断是否已登陆
            ftpClient.disconnect();
        }
        ftpClient.setDataTimeout(2000);//设置连接超时时间
        ftpClient.setControlEncoding("utf-8");
        ftpClient.connect(IpAdress,FTPPORT );
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            if (ftpClient.login(FTPUSER, FTPPASSWORD)) {
                bool = true;
                System.out.println("ftp连接成功");
            }
        }
        return bool;
    }

    // 创建文件夹
    public boolean createDirectory(String path) throws Exception {
        boolean bool = false;
        String directory = path.substring(0, path.lastIndexOf("/") + 1);
        int start = 0;
        int end = 0;
        if (directory.startsWith("/")) {
            start = 1;
        }
        end = directory.indexOf("/", start);
        while (true) {
            String subDirectory = directory.substring(start, end);
            if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                ftpClient.makeDirectory(subDirectory);
                ftpClient.changeWorkingDirectory(subDirectory);
                bool = true;
            }
            start = end + 1;
            end = directory.indexOf("/", start);
            if (end == -1) {
                break;
            }
        }
        return bool;
    }

    /**
     * 实现上传文件的功能
     *
     * @param localPath 本地文件路径
     * @param serverPath 服务器文件路径
     * @return
     */
    public synchronized boolean uploadFile(String localPath, String serverPath)
            throws Exception {
        // 上传文件之前，先判断本地文件是否存在
        File localFile = new File(localPath);
        if (!localFile.exists()) {
            System.out.println("本地文件不存在");
            return false;
        }
        //System.out.println("本地文件存在，名称为：" + localFile.getName());
        createDirectory(serverPath); // 如果文件夹不存在，创建文件夹
        //System.out.println("服务器文件存放路径：" + serverPath + localFile.getName());
        String fileName = settingDao.getUserID()+"_"+localFile.getName();
        // 如果本地文件存在，服务器文件也在，上传文件，这个方法中也包括了断点上传
        long localSize = localFile.length(); // 本地文件的长度
        FTPFile[] files = ftpClient.listFiles(fileName);
        long serverSize = 0;
        if (files.length == 0) {
            System.out.println("服务器文件不存在");
            serverSize = 0;
        } else {
            serverSize = files[0].getSize(); // 服务器文件的长度
        }
        if (localSize <= serverSize) {
            if (ftpClient.deleteFile(fileName)) {
                System.out.println("服务器文件存在,删除文件,开始重新上传");
                serverSize = 0;
            }
        }
        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
        // 进度
        long step = localSize / 100;
        long process = 0;
        long currentSize = 0;
        // 好了，正式开始上传文件
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setRestartOffset(serverSize);
        raf.seek(serverSize);
        OutputStream output = ftpClient.appendFileStream(fileName);
        byte[] b = new byte[1024];
        int length = 0;
        while ((length = raf.read(b)) != -1) {
            output.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 10 == 0) {
                    System.out.println("上传进度：" + process);
                }
            }
        }
        output.flush();
        output.close();
        raf.close();
        if (ftpClient.completePendingCommand()) {
            System.out.println("文件上传成功");
            return true;
        } else {
            System.out.println("文件上传失败");
            return false;
        }
    }

    /**
     * 实现下载文件功能，可实现断点下载
     *
     * @param localPath 本地文件路径
     * @param serverPath 服务器文件路径
     * @return
     */
    public synchronized boolean downloadFile(String localPath, String serverPath)
            throws Exception {
        File localFile = new File(localPath);
        serverPath=serverPath+settingDao.getUserID()+"_"+localFile.getName();
        // 先判断服务器文件是否存在
        FTPFile[] files = ftpClient.listFiles(serverPath);
        if (files.length == 0) {
            System.out.println("服务器文件不存在");
            return false;
        }
        System.out.println("远程文件存在,名字为：" + files[0].getName());
        // 接着判断下载的文件是否能断点下载
        long serverSize = files[0].getSize(); // 获取远程文件的长度

        long localSize = 0;
        if (localFile.exists()) {
            localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
            if (localSize >= serverSize) {
//                //System.out.println("文件已经下载完了");
                File file = new File(localPath);
//                file.delete();
//                System.out.println("本地文件存在，删除成功，开始重新下载");
//                return false;
                if(file.delete()) {
                    System.out.println("本地文件存在，删除成功，开始重新下载");
                    localSize=0;
                }
            }
        }
        // 进度
        long step = serverSize / 100;
        long process = 0;
        long currentSize = 0;
        // 开始准备下载文件
        ftpClient.enterLocalActiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        OutputStream out = new FileOutputStream(localFile, true);
        ftpClient.setRestartOffset(localSize);
        InputStream input = ftpClient.retrieveFileStream(serverPath);
        byte[] b = new byte[1024];
        int length = 0;
        while ((length = input.read(b)) != -1) {
            out.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 10 == 0) {
                    System.out.println("下载进度：" + process);
                }
            }
        }
        out.flush();
        out.close();
        input.close();
        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
        if (ftpClient.completePendingCommand()) {
            System.out.println("文件下载成功");
            return true;
        } else {
            System.out.println("文件下载失败");
            return false;
        }
    }

    // 如果ftp上传打开，就关闭掉
    public void closeFTP() throws Exception {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }
}