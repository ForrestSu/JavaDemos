package com.sq.parser.itextpdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfDocumentContentParser;

import java.io.IOException;

public class TestITextPdf {

    public static void parsePdf(String pdf, String saveImagesDir) throws IOException {
        PdfDocument document = new PdfDocument(new PdfReader(pdf));
        PdfDocumentContentParser contentParser = new PdfDocumentContentParser(document);
        for (int page = 1; page <= document.getNumberOfPages(); page++) {
            contentParser.processContent(page, new ExtractImage(saveImagesDir));
        }
        document.close();
    }

    public static void main(String[] args) {

        final String inputPdf = "/Users/sq/Desktop/pdf_pic/1.pdf";
        final String outputDir = "/Users/sq/Desktop/pdf_pic/";
        final String saveText = outputDir + "result.txt";
        final String saveImagesDir = outputDir;
        try {
            parsePdf(inputPdf, saveImagesDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
