package com.seulmae.seulmae.global.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class FileUtil {
    private static final List<String> PUBLIC_KEY_EXTENSIONS = Arrays.asList("cer", "crt", "pem", "der");

    public static void uploadFile(String filePath, String fileName, MultipartFile file) throws IOException {
        File dir = new File(filePath);
        Path path = Paths.get(dir + "/");

        if (!Files.isDirectory(path)) {
            dir.mkdirs();
        }

        File saveFile = new File(filePath, fileName);
        file.transferTo(saveFile);
    }

    public static ResponseEntity<byte[]> getImage(String imagePath, String imageName) throws IOException {
        File file = new File(imagePath + "/" + imageName);

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", Files.probeContentType(file.toPath()));

        return new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
    }


    public static void downloadImageFromUrl(String filePath, String fileName, String url) {
        File dir = new File(filePath);
        String filePathWithName = filePath + "/" + fileName;
        Path path = Paths.get(filePathWithName);

        if (!Files.isDirectory(path)) {
            dir.mkdirs();
        }

        // 이미지 다운로드
        byte[] imageBytes = new RestTemplate().getForObject(url, byte[].class);

        // 파일 저장
        try {
            Files.write(path, imageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteImage(String imagePath, String imageName) {
        File image = new File(imagePath, imageName);
        boolean deleted = image.delete();

        if (!deleted) {
            log.info("이미지 삭제에 실패했습니다.");
        }

    }

    public static String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
            return originalFilename.substring(dotIndex + 1).toLowerCase();
        } else {
            return ""; // 확장자가 없는 경우
        }
    }

    // URL에서 확장자 추출 메서드
    public static String getFileExtension(String url) {
        int dotIndex = url.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < url.length() - 1) {
            return url.substring(dotIndex + 1).toLowerCase();
        } else {
            return ""; // 확장자가 없는 경우
        }
    }

    public static boolean isAllowedExtension(String fileExtension) {
        String lowerCaseFileExtension = fileExtension.toLowerCase();
        for (String extension : PUBLIC_KEY_EXTENSIONS) {
            if (lowerCaseFileExtension.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static File multipartFileConvertToFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(multipartFile.getBytes());
        }
        return convFile;
    }
  
    public static String extractUrlName(String url) {
        try {
            URL urlObj = new URL(url);
            String path = urlObj.getPath();
            /** 수정한 부분 **/
            String query = urlObj.getQuery();

            if (query != null && query.contains("fname=")) {
                String fname = extractFname(query);
                System.out.println("fname = " + fname);
                return fname.substring(fname.lastIndexOf('/') + 1);
            }

            /** 수정 마지막 부분 **/

            return path.substring(path.lastIndexOf('/') + 1);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL: " + url, e);
        }
    }

    private static String extractFname(String url) {
        String keyword = "fname=";
        int startIndex = url.indexOf(keyword);

        if (startIndex != -1) { // fname=이 존재하는 경우
            return url.substring(startIndex + keyword.length());
        } else {
            throw new RuntimeException("URL에 fname 파라미터가 없습니다.");
        }
    }

    private static String checkMimeType(File file, String path) {
        String mimeType = getMimeType(file);
        String ext = mimeType.replaceAll("image/", "");
        ext = ext.replaceAll("jpeg", "jpg");

        return path + "." + ext;
    }

    private static String getMimeType(File file) {
        String mimeType;
        try {
            mimeType = new Tika().detect(file);
            return mimeType;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
