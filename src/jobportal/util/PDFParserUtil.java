package jobportal.util;

import jobportal.model.Candidate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PDFParserUtil {

    public static Candidate extractCandidateInfo(String filePath, String candidateId) throws IOException {
        String pdfText = extractText(filePath);

        String name = extractField(pdfText, "Name:");
        String jobRole = extractField(pdfText, "Role:");
        String skills = extractField(pdfText, "Skills:");
        String experienceStr = extractField(pdfText, "Experience:");
        String education = extractField(pdfText, "Education:");

        if (name == null || name.isEmpty())
            name = "Unknown";
        if (jobRole == null || jobRole.isEmpty())
            jobRole = "Unknown";
        if (skills == null || skills.isEmpty())
            skills = "";
        if (experienceStr == null || experienceStr.isEmpty())
            experienceStr = "0 years";
        if (education == null || education.isEmpty())
            education = "Unknown";

        int expScore = getExperienceScore(experienceStr);

        return new Candidate(candidateId, name, skills, expScore, education, jobRole);
    }

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

    private static String extractField(String text, String fieldName) {
        String[] lines = text.split("\r\n|\r|\n");
        for (String line : lines) {
            if (line.trim().startsWith(fieldName)) {
                return line.substring(fieldName.length()).trim();
            }
        }
        return "";
    }

    private static int getExperienceScore(String exp) {
        exp = exp.toLowerCase();
        if (exp.contains("1-2 years"))
            return 20;
        if (exp.contains("2-4 years"))
            return 30;
        if (exp.contains("5-9 years"))
            return 60;
        if (exp.contains("10+ years") || exp.contains("10 years") || exp.contains("11 years")
                || exp.contains("12 years"))
            return 80;
        return 0;
    }
}
