package com.example.artwork.dto;

public class ScoreRequest {

    private Long artistId;
    private int score;

    public ScoreRequest() {
    }

    public ScoreRequest(Long artistId, int score) {
        this.artistId = artistId;
        this.score = score;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
