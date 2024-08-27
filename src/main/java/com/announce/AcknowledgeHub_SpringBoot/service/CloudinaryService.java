//package com.announce.AcknowledgeHub_SpringBoot.service;
//
//import com.announce.AcknowledgeHub_SpringBoot.entity.Announcement;
//import com.announce.AcknowledgeHub_SpringBoot.repository.AnnouncementRepository;
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Map;
//
//@Service
//public class CloudinaryService {
//
//    @Autowired
//    private Cloudinary cloudinary;
//
//    @Autowired
//    private AnnouncementRepository announcementRepository;
//
//    private final String folderName = "YNWA";
//
//    public Map<String, Object> uploadFile(MultipartFile file) throws IOException {
//        String originalFilename = file.getOriginalFilename();
//        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String fileNameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
//
//        // Determine resource type based on file extension
//        String resourceType = getResourceType(fileExtension);
//
//        // Use the original file name without extension for publicId
//        String publicId = folderName + "/" + fileNameWithoutExtension;
//
//        // Prepare upload parameters
//        Map<String, Object> uploadParams = ObjectUtils.asMap(
//                "public_id", publicId,
//                "resource_type", resourceType
//        );
//
//        // Upload file
//        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
//
//        // Save the file's public_id and resource_type to the database
//        Announcement announcement = new Announcement();
//        announcement.setPublicId(publicId);
//        announcement.setResourceType(resourceType);
//        announcementRepository.save(announcement);
//
//        return uploadResult;
//    }
//
//    public Map<String, Object> getFileDetails(String publicId) {
//        try {
//            // Retrieve the resource type from the database
//            Announcement announcement = announcementRepository.findByPublicId(publicId);
//            String resourceType = announcement.getResourceType();
//
//            Map<String, Object> params = ObjectUtils.asMap(
//                    "resource_type", resourceType
//            );
//            return cloudinary.api().resource(publicId, params);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public byte[] downloadFile(String publicId) throws IOException {
//        // Get file details to obtain the URL
//        Map<String, Object> fileDetails = getFileDetails(publicId);
//        if (fileDetails != null) {
//            String secureUrl = (String) fileDetails.get("secure_url");
//            return downloadFromUrl(secureUrl);
//        }
//        return null;
//    }
//
//    private byte[] downloadFromUrl(String urlString) throws IOException {
//        URL url = new URL(urlString);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//
//        try (InputStream inputStream = connection.getInputStream();
//             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//            return outputStream.toByteArray();
//        }
//    }
//
//    // Helper method to determine resource type
//    private String getResourceType(String fileExtension) {
//        switch (fileExtension) {
//            case ".jpg":
//            case ".jpeg":
//            case ".png":
//            case ".gif":
//                return "image";
//            case ".mp4":
//            case ".avi":
//            case ".mov":
//                return "video";
//            case ".mp3":
//            case ".wav":
//                return "audio";
//            case ".pdf":
//            case ".xls":
//            case ".xlsx":
//            case ".zip":
//                return "raw";
//            default:
//                return "auto"; // Default to 'auto' for any other file types
//        }
//    }
//
//    // Helper method to check if preview is supported
//    private boolean isPreviewSupported(String fileExtension) {
//        return fileExtension.equals(".pdf") || fileExtension.equals(".xls") || fileExtension.equals(".xlsx");
//    }
//
//    // Method to generate a preview for supported file types
//    private void generatePreview(MultipartFile file, String publicId, String fileExtension) throws IOException {
//        if (fileExtension.equals(".pdf")) {
//            // Generate and upload a preview image for the first page of the PDF
//            Map<String, Object> pdfParams = ObjectUtils.asMap(
//                    "resource_type", "image",
//                    "page", 1
//            );
//            cloudinary.uploader().upload(file.getBytes(), pdfParams);
//        } else if (fileExtension.equals(".xls") || fileExtension.equals(".xlsx")) {
//            // Generate and upload a thumbnail image for the first sheet of the Excel file
//            byte[] thumbnailBytes = generateExcelThumbnail(file);
//            if (thumbnailBytes != null) {
//                Map<String, Object> thumbnailParams = ObjectUtils.asMap(
//                        "public_id", publicId + "_thumb",
//                        "resource_type", "image"
//                );
//                cloudinary.uploader().upload(thumbnailBytes, thumbnailParams);
//            }
//        }
//    }
//
//    // Placeholder method to generate an Excel file thumbnail
//    private byte[] generateExcelThumbnail(MultipartFile file) {
//        // Implement logic to create a thumbnail image from the Excel file
//        // This might involve converting the first sheet into an image
//        return null; // Replace with actual image byte array
//    }
//}