package jobportal.model;

public class Candidate {

    private String id;
    private String name;
    private String skills;
    private int experienceScore;
    private String education;
    private String jobRole;


    private int educationScore;
    private int skillScore;
    private int totalScore;

    public Candidate(String id, String name, String skills,
                     int experienceScore, String education, String jobRole) {

        this.id = id;
        this.name = name;
        this.skills = skills;
        this.experienceScore = experienceScore;
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
        if (experienceScore >= 1 && experienceScore < 3) {
            experienceScore = 10;
        } else if (experienceScore >= 3 && experienceScore < 5) {
            experienceScore = 15;
        } else if (experienceScore >= 5) {
            experienceScore = 20;
        } else {
            experienceScore = 0;
        }

        totalScore = experienceScore + educationScore + skillScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public String getJobRole() {
        return jobRole;
    }

    public int getExperience() {
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