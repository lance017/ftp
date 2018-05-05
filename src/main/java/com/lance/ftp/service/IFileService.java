package com.lance.ftp.service;


import com.lance.ftp.Common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {

    ServerResponse upload(MultipartFile file, String path);

}
