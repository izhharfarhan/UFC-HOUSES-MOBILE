package com.example.ufc_houses.model;

// ModelFight.java
public class ModelFight {
    private String Fighter1;
    private String Fighter2;
    private String FightCategory;
    private String LiveLink;
    private String Thumbnail;
    private String EventDate;

    // Diperlukan oleh Firebase
    public ModelFight() {}

    public ModelFight(String fighter1, String fighter2, String fightCategory, String liveLink, String thumbnail, String eventDate) {
        Fighter1 = fighter1;
        Fighter2 = fighter2;
        FightCategory = fightCategory;
        LiveLink = liveLink;
        Thumbnail = thumbnail;
        EventDate = eventDate;
    }

    // Getter dan Setter
    public String getFighter1() { return Fighter1; }
    public void setFighter1(String fighter1) { Fighter1 = fighter1; }

    public String getFighter2() { return Fighter2; }
    public void setFighter2(String fighter2) { Fighter2 = fighter2; }

    public String getFightCategory() { return FightCategory; }
    public void setFightCategory(String fightCategory) { FightCategory = fightCategory; }

    public String getLiveLink() { return LiveLink; }
    public void setLiveLink(String liveLink) { LiveLink = liveLink; }

    public String getThumbnail() { return Thumbnail; }
    public void setThumbnail(String thumbnail) { Thumbnail = thumbnail; }

    public String getEventDate() { return EventDate; }
    public void setEventDate(String eventDate) { EventDate = eventDate; }
}
