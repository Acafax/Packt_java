package org.example.springprojektzespolowy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.photo.PhotoDto;
import org.springframework.stereotype.Component;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
@Component
public class ImageUtils {

    public static void saveImageFromBytes(byte[] imageData, String filePath) throws IOException {
        ByteArrayInputStream big = new ByteArrayInputStream(imageData);
        BufferedImage image = ImageIO.read(big);
        File file = new File(filePath);
        ImageIO.write(image, "png", file);

    }

    public boolean photoValidator(byte[] file, String contentType ) throws BadRequestException {
        if (file.length==0) throw new BadRequestException();

        if (contentType == null || !contentType.equals("image/webp")){
            throw new UnsupportedMediaTypeStatusException("Only WEBP files are allowed.");
        }
        return true;
    }

    public byte[] convertToWebp(byte[] imageBytes, float quality) throws IOException, IllegalArgumentException {
        BufferedImage originalImage;

        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            originalImage = ImageIO.read(bis);
            if (originalImage == null) {
                throw new IOException("Could not read image from input bytes. Format might be unsupported or data corrupted.");
            }
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (!writers.hasNext()) throw new IllegalArgumentException("WEBP ImageWriter not found. Sprawdz czy uwzgledniono zaleznosci webp-imageio");

        ImageWriter writer = writers.next();

        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        if (writeParam.canWriteCompressed()) {

            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType("Lossy"); // Lub inna nazwa typu odczytana z logów
            writeParam.setCompressionQuality(quality);

        }else {
            log.warn("Compression not supported by WEBP writer.");
        }
        byte[] webpBytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(bos)) { // ImageIO zarządza strumieniem wyjściowym

            writer.setOutput(ios);
            writer.write(null, new IIOImage(originalImage, null, null), writeParam);
            ios.flush();
            webpBytes=bos.toByteArray();

        }finally {
            writer.dispose();
        }
        return webpBytes;

    }



}
