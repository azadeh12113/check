package com.example.artwork.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.artwork.model.Artist;
import com.example.artwork.service.ArtistService;
import com.example.artwork.model.Judge;
import com.example.artwork.repository.ArtistRepository;
import com.example.artwork.repository.JudgeRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "scoring.base-url=http://localhost:8089")
public class ScoreIntegrationIT {

    private static WireMockServer wireMockServer;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private JudgeRepository judgeRepository;

    @BeforeClass
    public static void startWireMock() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
    }

    @AfterClass
    public static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Before
    public void setUp() {
        wireMockServer.resetAll();
        artistRepository.deleteAll();
        judgeRepository.deleteAll();
    }

    @Test
    public void updateArtistScore_submitsScoreToWireMockService() {
        wireMockServer.stubFor(post(urlEqualTo("/scoring/scores"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"status\":\"success\"}")));

        Judge judge = new Judge();
        judge.setName("Default Judge");
        Judge savedJudge = judgeRepository.save(judge);

        Artist artist = new Artist("Azadeh", "Painting", 1, savedJudge);
        Artist savedArtist = artistRepository.save(artist);

        Artist updatedArtist = artistService.updateArtistScore(savedArtist.getId(), 3);

        wireMockServer.verify(postRequestedFor(urlEqualTo("/scoring/scores")));

        assertThat(artistRepository.findById(savedArtist.getId())).get().extracting(Artist::getScore).isEqualTo(3);
        wireMockServer.verify(postRequestedFor(urlEqualTo("/scoring/scores"))
            .withRequestBody(equalToJson("{\"artistId\":" + savedArtist.getId() + ",\"score\":3}")));
    }
}
