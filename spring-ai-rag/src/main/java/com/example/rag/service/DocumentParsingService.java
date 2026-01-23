package com.example.rag.service;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DocumentParsingService {

    private final Tika tika = new Tika();

    public String extractText(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            Parser parser = new AutoDetectParser();
            ParseContext context = new ParseContext();

            parser.parse(inputStream, handler, metadata, context);
            return handler.toString().trim();
        } catch (SAXException | TikaException e) {
            throw new IOException("Failed to parse document", e);
        }
    }

    public String detectContentType(MultipartFile file) {
        try {
            return tika.detect(file.getInputStream());
        } catch (IOException e) {
            return file.getContentType();
        }
    }

    public boolean isSupportedContentType(String contentType) {
        return contentType != null && (
                contentType.equals("application/pdf") ||
                contentType.equals("text/plain") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                contentType.equals("application/msword")
        );
    }

    public String getFileExtension(String contentType) {
        if (contentType == null) return "";

        switch (contentType) {
            case "application/pdf":
                return ".pdf";
            case "text/plain":
                return ".txt";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return ".docx";
            case "application/msword":
                return ".doc";
            default:
                return "";
        }
    }
}