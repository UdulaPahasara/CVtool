package jobportal.model;

public class Candidate {

    private final String id;
    private final String name;
    private final String skills;
    private final String experience;
    private final String education;
    private final String jobRole;

    private int experienceScore;
    private int educationScore;
    private int skillScore;
    private int totalScore;

    public Candidate(String id, String name, String skills,
            String experience, String education, String jobRole) {

        this.id = id;
        this.name = name;
        this.skills = skills;
        this.experience = experience;
        this.education = education;
        this.jobRole = jobRole;

        calculateScore();
    }

    private void calculateScore() {

        // Education weight
        if (education.equalsIgnoreCase("PhD")) {
            educationScore = 40;
        } else if (education.equalsIgnoreCase("Masters")) {
            educationScore = 30;
        } else if (education.equalsIgnoreCase("Degree")) {
            educationScore = 20;
        } else if (education.equalsIgnoreCase("High National Diploma")) {
            educationScore = 10;
        } else if (education.equalsIgnoreCase("Diploma")) {
            educationScore = 5;
        } else {
            educationScore = 0;
        }

        // Skill weight
        skillScore = 0;
        String[] skillList = skills.toLowerCase().split(",\\s*");
        for (String s : skillList) {
            String skill = s.trim();

            if (skill.equals("java"))
                skillScore += 25;
            else if (skill.equals("python"))
                skillScore += 20;
            else if (skill.equals("sql"))
                skillScore += 15;
            else if (skill.equals("c#"))
                skillScore += 25;
            else if (skill.equals("c"))
                skillScore += 25;
            else if (skill.equals("c++"))
                skillScore += 25;
            else if (skill.equals("javascript"))
                skillScore += 25;
            else if (skill.equals("html/css"))
                skillScore += 15;
            else if (skill.equals("spring-boot"))
                skillScore += 30;
            else if (skill.equals("react"))
                skillScore += 30;
        }

        // Experience weight
        experienceScore = 0;
        if (experience != null) {
            String exp = experience.toLowerCase().trim();

            if (exp.contains("1-2") || exp.contains("1 to 2") || exp.contains("1–2")) {
                experienceScore = 20;
            } else if (exp.contains("2-4") || exp.contains("2 to 4") || exp.contains("2–4")) {
                experienceScore = 30;
            } else if (exp.contains("5-9") || exp.contains("5 to 9") || exp.contains("5–9")) {
                experienceScore = 60;
            } else if (exp.contains("10+") || exp.contains("10 plus") || exp.contains("10 years") ||
                    exp.contains("11 years") || exp.contains("12 years") || exp.contains("13 years") ||
                    exp.contains("14 years") || exp.contains("15 years")) {
                experienceScore = 80;
            } else {
                String digits = exp.replaceAll("[^0-9]", " ").trim();
                if (!digits.isEmpty()) {
                    String[] parts = digits.split("\\s+");
                    try {
                        int years = Integer.parseInt(parts[0]);
                        if (years >= 10)
                            experienceScore = 80;
                        else if (years >= 5)
                            experienceScore = 60;
                        else if (years >= 2)
                            experienceScore = 30;
                        else if (years >= 1)
                            experienceScore = 20;
                    } catch (Exception e) {
                        experienceScore = 0;
                    }
                }
            }
        }

        // Total score = experience score + education score + skill score
        totalScore = experienceScore + educationScore + skillScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public String getJobRole() {
        return jobRole;
    }

    public int getExperienceScore() {
        return experienceScore;
    }

    public String getEducation() {
        return education;
    }

    public String getSkills() {
        return skills;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return "ID: " + id +
                " | Name: " + name +
                " | Job Role: " + jobRole +
                " | Score: " + totalScore;
    }
}