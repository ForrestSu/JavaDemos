package com.sq.parser.itextpdf;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import static com.itextpdf.kernel.pdf.canvas.parser.EventType.RENDER_IMAGE;

public class ExtractImage implements IEventListener {
    int index = 0;
    String outDir;

    public ExtractImage(String outDir) {
        this.outDir = outDir;
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return Collections.singleton(RENDER_IMAGE);
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof ImageRenderInfo) {
            final ImageRenderInfo imageRenderInfo = (ImageRenderInfo) data;
            final PdfImageXObject image = imageRenderInfo.getImage();
            final String extName = image.identifyImageFileExtension();

            Path filePath = new File(outDir, "Image" + index++ + "." + extName).toPath();
            System.out.println("Image: " + filePath);
            try {
                Files.write(filePath, image.getImageBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
