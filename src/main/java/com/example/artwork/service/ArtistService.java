package com.example.artwork.service;

import org.springframework.stereotype.Service;

import com.example.artwork.client.ScoringClient;
import com.example.artwork.config.ScoringProperties;
import com.example.artwork.dto.ScoreRequest;
import com.example.artwork.repository.ArtistRepository;
import com.example.artwork.repository.JudgeRepository;
import com.example.artwork.model.Artist;
import com.example.artwork.model.Judge;

import java.util.List;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final JudgeRepository judgeRepository;
    private final ScoringClient scoringClient;
    private final ScoringProperties scoringProperties;

    public ArtistService(ArtistRepository artistRepository,
                         JudgeRepository judgeRepository,
                         ScoringClient scoringClient,
                         ScoringProperties scoringProperties) {
        this.artistRepository = artistRepository;
        this.judgeRepository = judgeRepository;
        this.scoringClient = scoringClient;
        this.scoringProperties = scoringProperties;
    }

    public Artist createArtist(Artist artist) {
    	
        Judge judge = judgeRepository.findFirstByOrderByIdAsc();
        
        if (judge == null) {
			throw new IllegalArgumentException("No judge found in database");

         }
        artist.setJudge(judge);
        artist.setId(null);
        return artistRepository.save(artist);
    }
    
    public Artist updateArtistScore(Long id, int newScore) {
    	
    	if (newScore < 0 || newScore > 3) {
    	    throw new IllegalArgumentException("Score out of range");
    	}

	    Artist artist = artistRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Artist not found"));
	    artist.setScore(newScore);
	    Artist updated = artistRepository.save(artist);

	    if (scoringProperties.getBaseUrl() != null && !scoringProperties.getBaseUrl().isBlank()) {
	        scoringClient.submitScore(new ScoreRequest(updated.getId(), newScore));
	    }

	    return updated;
	}
    
	public List<Artist> getArtistsByJudge(Judge judge) {
		return artistRepository.findByJudge(judge);
	}
	
	public List<Artist> getArtistWithHighestScore() {
		
		return artistRepository.findByScoreGreaterThan(2);
	}
	
	public List<Artist> getAllArtists() {
	    return artistRepository.findAll();
	}

	public Artist getArtistById(Long id) {
		
	    return artistRepository.findById(id).orElse(null);

	}
	
	public void deleteArtistById(long id) {
		Artist artist = artistRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Artist Not found"));
	
		artistRepository.deleteById(artist.getId());
	}
	
}
	
	
	

