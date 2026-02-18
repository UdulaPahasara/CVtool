package jobportal.model;

public class Candidate {

    private String id;
    private String name;
    private String skills;
    private int experience;
    private String education;
    private String jobRole;

    private int experienceScore;
    private int educationScore;
    private int skillScore;
    private int totalScore;

    public Candidate(String id, String name, String skills,
                     int experience, String education) {

        this.id = id;
        this.name = name;
        this.skills = skills;
        this.experience = experience;
        this.education = education;
        this.jobRole = "N/A";

        calculateScore();
    }

    private void calculateScore() {

        // Experience weight
        experienceScore = experience * 10;

        // Education weight
        if (education.equalsIgnoreCase("Masters")) {
            educationScore = 30;
        } else if (education.equalsIgnoreCase("Degree")) {
            educationScore = 20;
        } else {
            educationScore = 10;
        }

        // Skill weight
        skillScore = 0;
        if (skills.toLowerCase().contains("java"))
            skillScore += 25;
        if (skills.toLowerCase().contains("sql"))
            skillScore += 15;

        totalScore = experienceScore + educationScore + skillScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public String getJobRole() {
        return jobRole;
    }

    public int getExperience() {
        return experience;
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