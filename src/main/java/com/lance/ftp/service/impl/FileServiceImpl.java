package com.lance.ftp.service.impl;

import com.google.common.collect.Lists;
import com.lance.ftp.Common.ServerResponse;
import com.lance.ftp.service.IFileService;
import com.lance.ftp.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public ServerResponse upload(MultipartFile file, String path) {
        String filename = file.getOriginalFilename();
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, filename);
        List<String> list = null;
        try {
            file.transferTo(targetFile);
            //文件已经上传成功了
            list = FTPUtil.uploadFile("/upload", Lists.newArrayList(targetFile),true);
            //已经上传到ftp服务器上
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }

        //A:abc.jpg
        //B:abc.jpg
        if (list != null && list.size() != 0) {
            return ServerResponse.createBySuccess("上传图片成功", list);
        }
        return ServerResponse.createByErrorMessage("上传图片失败");

    }
}
