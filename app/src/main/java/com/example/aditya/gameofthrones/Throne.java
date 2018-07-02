package com.example.aditya.gameofthrones;

public class Throne {

    private String mCharacterName;
    private String mHouse;
    private String mCharacterTitle;
    private String mThumbnailLink;
    private String mLocations;
    private String mMotherName;
    private String mFatherName;

    public Throne(String characterName, String house, String characterTitle, String thumbnailLink, String motherName, String fatherName){
        mCharacterName = characterName;
        mHouse = house;
        mCharacterTitle = characterTitle;
        mThumbnailLink = thumbnailLink;
        mMotherName = motherName;
        mFatherName = fatherName;
    }

    public Throne(String characterName, String house, String characterTitle, String thumbnailLink, String locations, String motherName, String fatherName){
        mCharacterName = characterName;
        mHouse = house;
        mCharacterTitle = characterTitle;
        mThumbnailLink = thumbnailLink;
        mLocations = locations;
        mMotherName = motherName;
        mFatherName = fatherName;
    }

    public String getCharacterName() {
        return mCharacterName;
    }

    public String getCharacterTitle() {
        return mCharacterTitle;
    }

    public String getHouse() {
        return mHouse;
    }

    public String getThumbnailLink() {
        return mThumbnailLink;
    }

    public String getLocations() {
        return mLocations;
    }

    public String getMotherName() {
        return mMotherName;
    }

    public String getFatherName() {
        return mFatherName;
    }
}
