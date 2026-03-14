package com.ailearningassistant.document.service.impl;

import com.ailearningassistant.common.api.PageResult;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.common.util.SecurityUtils;
import com.ailearningassistant.document.config.DocumentStorageProperties;
import com.ailearningassistant.document.dto.DocumentPageQueryRequest;
import com.ailearningassistant.document.dto.DocumentUploadRequest;
import com.ailearningassistant.document.entity.LearningDocument;
import com.ailearningassistant.document.mapper.LearningDocumentMapper;
import com.ailearningassistant.document.service.LearningDocumentService;
import com.ailearningassistant.document.vo.LearningDocumentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LearningDocumentServiceImpl extends ServiceImpl<LearningDocumentMapper, LearningDocument>
        implements LearningDocumentService {

    private static final String DEFAULT_PARSE_STATUS = "PENDING";
    private static final String DEFAULT_SUMMARY_STATUS = "PENDING";

    private final DocumentStorageProperties documentStorageProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LearningDocumentVO uploadDocument(DocumentUploadRequest request) {
        MultipartFile file = request.getFile();
        if (file == null || file.isEmpty()) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "Uploaded file must not be empty");
        }

        String originalFileName = sanitizeOriginalFileName(file.getOriginalFilename());
        String extension = getAndValidateExtension(originalFileName);
        Long userId = SecurityUtils.getUserId();
        String storageFileName = UUID.randomUUID() + "." + extension;
        String storageRelativePath = buildStorageRelativePath(userId, storageFileName);
        String fileUrl = buildFileUrl(storageRelativePath);
        Path targetPath = resolveUploadRoot().resolve(storageRelativePath);

        try {
            Files.createDirectories(targetPath.getParent());
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            LocalDateTime now = LocalDateTime.now();
            LearningDocument document = LearningDocument.builder()
                    .userId(userId)
                    .title(resolveTitle(request.getTitle(), originalFileName))
                    .fileName(originalFileName)
                    .fileUrl(fileUrl)
                    .fileType(extension)
                    .fileSize(file.getSize())
                    .parseStatus(DEFAULT_PARSE_STATUS)
                    .summaryStatus(DEFAULT_SUMMARY_STATUS)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            boolean saved = save(document);
            if (!saved) {
                deleteUploadedFileQuietly(targetPath);
                throw new BusinessException(StatusCode.FILE_UPLOAD_FAILED);
            }
            return LearningDocumentVO.fromEntity(document);
        } catch (IOException ex) {
            throw new BusinessException(StatusCode.FILE_UPLOAD_FAILED);
        } catch (RuntimeException ex) {
            deleteUploadedFileQuietly(targetPath);
            throw ex;
        }
    }

    @Override
    public PageResult<LearningDocumentVO> pageMyDocuments(DocumentPageQueryRequest request) {
        Long userId = SecurityUtils.getUserId();
        Page<LearningDocument> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<LearningDocument> resultPage = lambdaQuery()
                .eq(LearningDocument::getUserId, userId)
                .like(StringUtils.hasText(request.getTitle()), LearningDocument::getTitle, request.getTitle())
                .orderByDesc(LearningDocument::getCreatedAt)
                .page(page);
        return PageResult.from(resultPage, LearningDocumentVO::fromEntity);
    }

    @Override
    public LearningDocumentVO getMyDocumentDetail(Long id) {
        return LearningDocumentVO.fromEntity(getMyDocumentEntity(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMyDocument(Long id) {
        LearningDocument document = getMyDocumentEntity(id);
        Path filePath = resolveUploadRoot().resolve(extractStorageRelativePath(document.getFileUrl()));
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new BusinessException(StatusCode.FILE_DELETE_FAILED);
        }

        boolean removed = removeById(document.getId());
        if (!removed) {
            throw new BusinessException(StatusCode.DOCUMENT_NOT_FOUND);
        }
    }

    @Override
    public LearningDocument getMyDocumentEntity(Long id) {
        return getOwnedDocument(id, SecurityUtils.getUserId());
    }

    @Override
    public void updateDocumentStatuses(Long id, String parseStatus, String summaryStatus) {
        boolean updated = lambdaUpdate()
                .eq(LearningDocument::getId, id)
                .set(LearningDocument::getParseStatus, parseStatus)
                .set(LearningDocument::getSummaryStatus, summaryStatus)
                .set(LearningDocument::getUpdatedAt, LocalDateTime.now())
                .update();
        if (!updated) {
            throw new BusinessException(StatusCode.DOCUMENT_NOT_FOUND);
        }
    }

    private LearningDocument getOwnedDocument(Long id, Long userId) {
        LearningDocument document = lambdaQuery()
                .eq(LearningDocument::getId, id)
                .eq(LearningDocument::getUserId, userId)
                .one();
        if (document == null) {
            throw new BusinessException(StatusCode.DOCUMENT_NOT_FOUND);
        }
        return document;
    }

    private String sanitizeOriginalFileName(String originalFileName) {
        String cleanPath = StringUtils.cleanPath(
                StringUtils.hasText(originalFileName) ? originalFileName : "document");
        if (cleanPath.contains("..")) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "Invalid file name");
        }
        return cleanPath;
    }

    private String getAndValidateExtension(String originalFileName) {
        String extension = StringUtils.getFilenameExtension(originalFileName);
        if (!StringUtils.hasText(extension)) {
            throw new BusinessException(StatusCode.UNSUPPORTED_FILE_TYPE);
        }
        String normalizedExtension = extension.toLowerCase(Locale.ROOT);
        List<String> allowedExtensions = documentStorageProperties.getAllowedExtensions().stream()
                .map(item -> item.toLowerCase(Locale.ROOT))
                .toList();
        if (!allowedExtensions.contains(normalizedExtension)) {
            throw new BusinessException(StatusCode.UNSUPPORTED_FILE_TYPE.getCode(),
                    "Only pdf, docx, txt, md files are allowed");
        }
        return normalizedExtension;
    }

    private String resolveTitle(String title, String originalFileName) {
        if (StringUtils.hasText(title)) {
            return title.trim();
        }
        String filename = StringUtils.stripFilenameExtension(originalFileName);
        return StringUtils.hasText(filename) ? filename : "Untitled";
    }

    private String buildStorageRelativePath(Long userId, String storageFileName) {
        String dateFolder = LocalDate.now().toString().replace("-", "");
        return userId + "/" + dateFolder + "/" + storageFileName;
    }

    private String buildFileUrl(String storageRelativePath) {
        return "/uploads/" + storageRelativePath.replace("\\", "/");
    }

    private Path resolveUploadRoot() {
        return Path.of(documentStorageProperties.getUploadDir()).toAbsolutePath().normalize();
    }

    private String extractStorageRelativePath(String fileUrl) {
        String prefix = "/uploads/";
        if (fileUrl != null && fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        }
        throw new BusinessException(StatusCode.DOCUMENT_NOT_FOUND);
    }

    private void deleteUploadedFileQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
