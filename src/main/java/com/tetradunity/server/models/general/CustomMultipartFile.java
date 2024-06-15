package com.tetradunity.server.models.general;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public record CustomMultipartFile(String name, String originalFilename, String contentType,
                                  byte[] content) implements MultipartFile {
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.content.length == 0;
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        throw new UnsupportedOperationException("transferTo is not implemented");
    }
}
