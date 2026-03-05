package jobportal.util;

import jobportal.model.Candidate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PDFParserUtil {

    // Extract candidate details from the PDF and create Candidate object
    public static Candidate extractCandidateInfo(String filePath, String candidateId) throws IOException {
        String pdfText = extractText(filePath);

        // Extract fields using "FieldName:" format
        String name = extractField(pdfText, "Name:");
        String jobRole = extractField(pdfText, "Role:");
        String skills = extractField(pdfText, "Skills:");
        String experienceStr = extractField(pdfText, "Experience:");
        String education = extractField(pdfText, "Education:");

        // Default values if fields are missing
        if (name == null || name.trim().isEmpty())
            name = "Unknown";
        if (jobRole == null || jobRole.trim().isEmpty())
            jobRole = "Unknown";
        if (skills == null || skills.trim().isEmpty())
            skills = "";
        if (experienceStr == null || experienceStr.trim().isEmpty())
            experienceStr = "0 years";
        if (education == null || education.trim().isEmpty())
            education = "Unknown";

        // Candidate constructor expects string experience
        return new Candidate(candidateId, name, skills, experienceStr, education, jobRole);
    }

    // Reads text content from a PDF file
    private static String extractText(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            } else {
                return "";
            }
        }
    }

    // Extracts value after a given field name, line by line
    private static String extractField(String text, String fieldName) {
        String[] lines = text.split("\r\n|\r|\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith(fieldName)) {
                return trimmed.substring(fieldName.length()).trim();
            }
        }
        return "";
    }

}