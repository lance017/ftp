package com.lance.ftp.controller;

import com.lance.ftp.Common.ServerResponse;
import com.lance.ftp.service.IFileService;
import com.lance.ftp.utils.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller

public class index {

    @Autowired
    private IFileService iFileService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/ceshi")
    @ResponseBody
    public String ceshi() {
        return PropertiesUtil.getProperty("ftp.server.ip");
    }

    @RequestMapping("/upload")
    @ResponseBody
    public ServerResponse fileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file == null) {
            return null;
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iFileService.upload(file,path);
    }


}
