package com.lance.ftp.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String pathNametype = "iso-8859-1";

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpPort = PropertiesUtil.getProperty("ftp.server.port");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.username");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.password");


    /**
     * 保存文件到FTP
     * @param remotePath 默认需要保存到的文件夹，可以针对不同的情况，设置不同
     * @param fileList 文件列表
     * @param isUseFileName 是否使用本来的文件名
     * @return 文件路径列表
     * @throws IOException
     */
    public static List<String> uploadFile(String remotePath, List<File> fileList, boolean isUseFileName) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, Integer.parseInt(ftpPort), ftpUser, ftpPass);
        logger.info("开始连接FTP服务器");
        List<String> result = ftpUtil.uploadFiles(remotePath, fileList, isUseFileName);
        logger.info("开始连接ftp服务器,结束上传,上传结果:{}");
        return result;
    }


    /**
     * 上传文件到ftp
     * @param remotePath 上传文件的要保存在ftp服务器力的路径
     * @param fileList 文件列表
     * @param isUseFileName 是否使用本来的文件名
     * @return 文件路径列表
     * @throws IOException
     */
    private List<String> uploadFiles(String remotePath, List<File> fileList, Boolean isUseFileName) throws IOException {
        FileInputStream fis = null;
        List<String> stringList = new ArrayList<>();
        if (connectServer(ftpIp, Integer.parseInt(ftpPort), ftpUser, ftpPass)) {
            try {
                // 切换目录失败，表示没有该目录, 需要新建该目录
                String path = new String(remotePath.getBytes(), pathNametype);
                if (!ftpClient.changeWorkingDirectory(path)) {
                    makeAllDirectory(path);
                    ftpClient.changeWorkingDirectory(path);
                }
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();

                for (File file : fileList) {
                    fis = new FileInputStream(file);
                    if (isUseFileName) {
                        String transAttachFile = new String(file.getName().getBytes(),pathNametype);
                        ftpClient.storeFile(transAttachFile,fis);
                        String realPath = StringUtils.removeEnd(remotePath, "/");
                        String pathNames = realPath + "/" + file.getName();
                        stringList.add(new String(pathNames.getBytes(),"UTF-8"));
                    } else {
                        String transAttachFile = changeName(file);
                        ftpClient.storeFile(transAttachFile,fis);
                        String realPath = StringUtils.removeEnd(remotePath, "/");
                        String pathNames = realPath + "/" + transAttachFile;
                        stringList.add(new String(pathNames.getBytes(),"UTF-8"));
                    }
                }
            } catch (IOException e) {
                logger.error("上传文件异常", e);
                e.printStackTrace();
                stringList.clear();
            } finally {
                fis.close();
                ftpClient.disconnect();
            }

        }
        return stringList;
    }


    /**
     * 根据文件随机一个文件名
     * @param file 文件
     * @return 随机的文件名
     * @throws UnsupportedEncodingException
     */
    private String changeName(File file) throws UnsupportedEncodingException {
        String fileName = file.getName();
        //abc.jpg
        if (fileName.contains(".")) {
            String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
            String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
            return new String(uploadFileName.getBytes(), pathNametype);
        } else {
            return UUID.randomUUID().toString();
        }
    }

    /**
     * 连接 ftp 服务器
     * @param ip 服务器地址
     * @param port 端口
     * @param user 用户名
     * @param pwd 密码
     * @return 是否连接
     */
    private boolean connectServer(String ip, int port, String user, String pwd){

        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip,port);
            isSuccess = ftpClient.login(user,pwd);
        } catch (SocketException e) {
            e.printStackTrace();
            logger.info("请检查FTP的IP地址 ！ ");
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }

    /**
     * 创建目录, 适合单层创建，如果有一层目录存在就创建失败
     * @param path 目录
     * @return 是否创建成功
     */
    private boolean makeDirectory(String path) {
        boolean result = true;
        try {
            result = ftpClient.makeDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
            logger.info("创建文件夹失败", e);
        }
        return result;
    }

    /**
     * 递归创建文件夹，如果存在就忽略，没有则创建
     * @param path 目录
     * @return 是否创建成功
     */
    private boolean makeAllDirectory(String path)  {
        boolean result = true;
        String realPath2 = StringUtils.removeStart(path, "/");
        String realPath = StringUtils.removeEnd(realPath2, "/");
        String[] strings = realPath.split("/");
        try {
            for (String s : strings) {
                if (ftpClient.changeWorkingDirectory(s)) {
                    continue;
                } else {
                    makeDirectory(s);
                    ftpClient.changeWorkingDirectory(s);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
            logger.info("创建文件夹失败", e);
        }
        return result;
    }





    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }



}
