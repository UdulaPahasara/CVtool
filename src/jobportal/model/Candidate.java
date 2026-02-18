package jobportal.model;

public class Candidate {
    private String id;
    private String name;
    private String skills;
    private int experience;
    private String education;
    private int score;

    public Candidate(String id, String name, String skills, int experience, String education) {
        this.id = id;
        this.name = name;
        this.skills = skills;
        this.experience = experience;
        this.education = education;
        this.score = calculateScore();
    }

    // Score Calculation Logic
    private int calculateScore() {
        int score = 0;

        // Experience weight
        score = score + (experience * 10);

        // Education weight
        if (education.equalsIgnoreCase("Masters")) {
            score += 30;
        }
        else if (education.equalsIgnoreCase("Degree")) {
            score += 20;
        }
        else
            score += 10;

        // Skills weight ()
        if (skills.toLowerCase().contains("java")){
            score += 25;
        }
        if (skills.toLowerCase().contains("sql")) {
            score += 15;
        }

        return score;
    }

    public int getScore() { return score; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSkills() { return skills; }
    public int getExperience() { return experience; }
    public String getEducation() { return education; }
}
