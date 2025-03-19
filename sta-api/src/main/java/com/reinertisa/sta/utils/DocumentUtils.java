package com.reinertisa.sta.utils;

import com.reinertisa.sta.dto.Document;
import com.reinertisa.sta.dto.User;
import com.reinertisa.sta.entity.DocumentEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class DocumentUtils {

    public static Document fromDocumentEntity(DocumentEntity documentEntity, User createdBy, User updatedBy) {
        Document document = new Document();
        BeanUtils.copyProperties(documentEntity, document);
        document.setOwnerName(createdBy.getFirstName() + " " + createdBy.getLastName());
        document.setOwnerEmail(createdBy.getEmail());
        document.setOwnerPhone(createdBy.getPhone());
        document.setOwnerLastLogin(createdBy.getLastLogin());
        document.setUpdaterName(updatedBy.getFirstName() + " " + updatedBy.getLastName());
        return document;
    }

    public static String getDocumentUri(String filename) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(String.format("/documents/%s", filename))
                .toUriString();
    }

    public static String setIcon(String fileExtension) {
        String extension = StringUtils.trimAllWhitespace(fileExtension);
        if (extension.equalsIgnoreCase("DOC") || extension.equalsIgnoreCase("DOCX")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/word-icon.svg";
        }
        if (extension.equalsIgnoreCase("XLS") || extension.equalsIgnoreCase("XLSX")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/excel-icon.svg";
        }
        if (extension.equalsIgnoreCase("PDF")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/pdf-icon.svg";
        } else {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/word-icon.svg";
        }
    }
}
