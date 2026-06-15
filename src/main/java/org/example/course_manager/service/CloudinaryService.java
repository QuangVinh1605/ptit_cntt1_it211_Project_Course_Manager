package org.example.course_manager.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {
        // Kiểm tra định dạng file
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("application/pdf") ||
                        contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new RuntimeException("Chỉ chấp nhận file PDF hoặc Word (doc, docx)");
        }

        if (file.getSize() > 15 * 1024 * 1024) {
            throw new RuntimeException("Kích thước file vượt quá 15MB");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "course_manager/submissions",
                            "allowed_formats", new String[]{"pdf", "doc", "docx"}
                    ));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload file lên Cloudinary: " + e.getMessage());
        }
    }
}