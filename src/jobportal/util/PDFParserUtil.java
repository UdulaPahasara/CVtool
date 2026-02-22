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

        // Convert experience string into experience score (int)
        int experienceScore = convertExperienceToScore(experienceStr);

        // Candidate constructor expects int experienceScore
        return new Candidate(candidateId, name, skills, experienceScore, education, jobRole);
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

    // ✅ Convert experience text from PDF into a score
    // Example mappings:
    // "1-2 years" -> 20
    // "2-4 years" -> 30
    // "5-9 years" -> 60
    // "10+ years" -> 80
    private static int convertExperienceToScore(String exp) {
        if (exp == null) return 0;

        exp = exp.toLowerCase().trim();

        // Normalized checks
        if (exp.contains("1-2") || exp.contains("1 to 2") || exp.contains("1–2"))
            return 20;

        if (exp.contains("2-4") || exp.contains("2 to 4") || exp.contains("2–4"))
            return 30;

        if (exp.contains("5-9") || exp.contains("5 to 9") || exp.contains("5–9"))
            return 60;

        if (exp.contains("10+") || exp.contains("10 plus") || exp.contains("10 years") ||
                exp.contains("11 years") || exp.contains("12 years") || exp.contains("13 years") ||
                exp.contains("14 years") || exp.contains("15 years"))
            return 80;

        // If PDF contains a number like "6 years", we can try to detect it
        int years = extractYearsNumber(exp);
        if (years >= 10) return 80;
        if (years >= 5) return 60;
        if (years >= 2) return 30;
        if (years >= 1) return 20;

        return 0;
    }

    // Attempts to extract numeric years from text like "6 years"
    private static int extractYearsNumber(String exp) {
        String digits = exp.replaceAll("[^0-9]", " ").trim();
        if (digits.isEmpty()) return 0;

        // Take the first number found
        String[] parts = digits.split("\\s+");
        try {
            return Integer.parseInt(parts[0]);
        } catch (Exception e) {
            return 0;
        }
    }
}