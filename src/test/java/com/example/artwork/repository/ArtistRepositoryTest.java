package com.example.artwork.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import com.example.artwork.model.Artist;
import com.example.artwork.model.Judge;


@RunWith(SpringRunner.class)
@DataJpaTest
@EntityScan("com.example.artwork.model") 

public class ArtistRepositoryTest {
	
	    @Autowired
	    private ArtistRepository artistRepository;
	    
	    @Autowired
	    private TestEntityManager entityManager;
	    
	    @Test
	    public void testJpaMapping() {

	    	Judge judge = new Judge();
	        judge.setName("Judge1");
	        Judge savedJudge = entityManager.persistFlushFind(judge);

	        Artist artist = new Artist();
	        artist.setName("Azadeh");
	        artist.setArtName("Painter");
	        artist.setScore(3);
	        artist.setJudge(savedJudge);
	        Artist savedArtist = entityManager.persistFlushFind(artist);

	        assertThat(savedArtist.getId()).isNotNull();
	        assertThat(savedArtist.getId()).isPositive();
	        assertThat(savedArtist.getName()).isEqualTo("Azadeh");
	        assertThat(savedArtist.getArtName()).isEqualTo("Painter");
	        assertThat(savedArtist.getScore()).isEqualTo(3);
	        assertThat(savedArtist.getJudge()).isEqualTo(savedJudge);

	        LoggerFactory
	        .getLogger(ArtistRepositoryTest.class)
			.info("Saved: {}", savedArtist);
	    }

	    @Test
	    public void testFindByScoreGreaterThan() {
	    	
	        Judge judge = entityManager.persistFlushFind(new Judge("Judge1"));
	        Artist artist1 = entityManager.persistFlushFind(new Artist("Azadeh", "painting", 3, judge));
	        Artist artist2 = entityManager.persistFlushFind(new Artist("Sara", "sculpture", 3, judge));
	        entityManager.persistFlushFind(new Artist("Mina", "photo", 2, judge));
	        
	        List<Artist> results = artistRepository.findByScoreGreaterThan(2);

	        assertThat(results).containsExactly(artist1,artist2);
	    }

	    
	}
