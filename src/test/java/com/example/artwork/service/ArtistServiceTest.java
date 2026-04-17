package com.example.artwork.service;

import com.example.artwork.client.ScoringClient;
import com.example.artwork.config.ScoringProperties;
import com.example.artwork.dto.ScoreRequest;
import com.example.artwork.model.Artist;
import com.example.artwork.model.Judge;
import com.example.artwork.repository.ArtistRepository;
import com.example.artwork.repository.JudgeRepository;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.assertNull;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private JudgeRepository judgeRepository;

    @Mock
    private ScoringClient scoringClient;

    @Mock
    private ScoringProperties scoringProperties;

    @InjectMocks
    private ArtistService artistService;

    @Before
    public void setUp() {
        when(scoringProperties.getBaseUrl()).thenReturn("");
    }

    @Test
    public void test_getAllArtits() {
        Judge judge = new Judge();
        judge.setId(1L);

        Artist artist1 = new Artist();
        artist1.setName("Artist1");
        artist1.setJudge(judge);

        Artist artist2 = new Artist();
        artist2.setName("Artist2");
        artist2.setJudge(judge);

        List<Artist> mockArtists = asList(artist1, artist2);
        when(artistRepository.findAll()).thenReturn(mockArtists);

        assertThat(artistService.getAllArtists()).containsExactly(artist1, artist2);
    }

    @Test
    public void test_createArtist_savesArtist_whenJudgeExists() {
        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("JudgeName");

        Artist artist = new Artist();
        artist.setId(99L);
        artist.setName("Azadeh");
        artist.setArtName("Painting");
        artist.setScore(2);

        when(judgeRepository.findFirstByOrderByIdAsc()).thenReturn(judge);

        Artist saved = new Artist();
        saved.setId(1L);
        saved.setName("Azadeh");
        saved.setArtName("Painting");
        saved.setScore(2);
        saved.setJudge(judge);

        when(artistRepository.save(any(Artist.class))).thenReturn(saved);

        Artist result = artistService.createArtist(artist);

        assertEquals("Azadeh", result.getName());
        assertEquals("Painting", result.getArtName());
        assertEquals(judge, result.getJudge());

        ArgumentCaptor<Artist> captor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository).save(captor.capture());

        Artist passedToSave = captor.getValue();
        assertEquals(judge, passedToSave.getJudge());
        assertNull(passedToSave.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createArtist_throwsException_whenJudgeIsNull() {
        when(judgeRepository.findFirstByOrderByIdAsc()).thenReturn(null);

        Artist artist = new Artist();
        artist.setName("Azadeh");

        artistService.createArtist(artist);
    }

    @Test
    public void test_updateArtistScoreById_and_returnsSavedArtist() {
        long artistId = 1L;
        int newScore = 3;

        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("name");

        Artist existingArtist = new Artist("Azadeh", "Painting", 2, judge);
        existingArtist.setId(artistId);

        Artist replaced = new Artist("Azadeh", "Painting", newScore, judge);
        replaced.setId(artistId);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(Artist.class))).thenReturn(replaced);
        when(scoringProperties.getBaseUrl()).thenReturn(null);

        Artist result = artistService.updateArtistScore(artistId, newScore);

        assertThat(result.getScore()).isEqualTo(newScore);

        ArgumentCaptor<Artist> captor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository).save(captor.capture());

        Artist passed = captor.getValue();
        assertThat(passed.getScore()).isEqualTo(newScore);
    }

    @Test
    public void test_updateArtistScore_acceptsMaxBoundary_three_and_returnsSavedArtist() {
        long artistId = 1L;
        int newScore = 3;

        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("name");

        Artist existingArtist = new Artist("Azadeh", "Painting", 1, judge);
        existingArtist.setId(artistId);

        Artist savedArtist = new Artist("Azadeh", "Painting", newScore, judge);
        savedArtist.setId(artistId);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);
        when(scoringProperties.getBaseUrl()).thenReturn(null);

        Artist result = artistService.updateArtistScore(artistId, newScore);

        assertEquals(Integer.valueOf(newScore), result.getScore());

        ArgumentCaptor<Artist> captor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository).save(captor.capture());
        assertEquals(Integer.valueOf(newScore), captor.getValue().getScore());

        InOrder inOrder = inOrder(artistRepository);
        inOrder.verify(artistRepository).save(existingArtist);
    }

    @Test
    public void test_updateArtistScore_acceptsMinBoundary_zero_and_returnsSavedArtist() {
        long artistId = 1L;
        int newScore = 0;

        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("name");

        Artist existingArtist = new Artist("Azadeh", "Painting", 2, judge);
        existingArtist.setId(artistId);

        Artist savedArtist = new Artist("Azadeh", "Painting", newScore, judge);
        savedArtist.setId(artistId);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);
        when(scoringProperties.getBaseUrl()).thenReturn(null);

        Artist result = artistService.updateArtistScore(artistId, newScore);

        assertEquals(Integer.valueOf(newScore), result.getScore());

        ArgumentCaptor<Artist> captor = ArgumentCaptor.forClass(Artist.class);
        verify(artistRepository).save(captor.capture());
        assertEquals(Integer.valueOf(newScore), captor.getValue().getScore());

        InOrder inOrder = inOrder(artistRepository);
        inOrder.verify(artistRepository).save(existingArtist);
    }

    @Test
    public void test_updateArtistScore_rejectsBelowMin_minusOne_and_throwsException() {
        long artistId = 1L;
        int invalidScore = -1;

        try {
            artistService.updateArtistScore(artistId, invalidScore);
        } catch (IllegalArgumentException e) {
            assertEquals("Score out of range", e.getMessage());
        }

        verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    public void test_updateArtistScore_rejectsAboveMax_four_and_throwsException() {
        long artistId = 1L;
        int invalidScore = 4;

        try {
            artistService.updateArtistScore(artistId, invalidScore);
        } catch (IllegalArgumentException e) {
            assertEquals("Score out of range", e.getMessage());
        }

        verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    public void test_updateArtistScore_submitsToScoringClient_whenBaseUrlConfigured() {
        long artistId = 1L;
        int newScore = 3;

        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("name");

        Artist existingArtist = new Artist("Azadeh", "Painting", 1, judge);
        existingArtist.setId(artistId);

        Artist savedArtist = new Artist("Azadeh", "Painting", newScore, judge);
        savedArtist.setId(artistId);

        when(scoringProperties.getBaseUrl()).thenReturn("http://localhost:8089");
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);

        Artist result = artistService.updateArtistScore(artistId, newScore);

        assertEquals(Integer.valueOf(newScore), result.getScore());
        verify(scoringClient).submitScore(any(ScoreRequest.class));
    }

    @Test
    public void test_updateArtistScore_doesNotSubmitToScoringClient_whenBaseUrlBlank() {
        long artistId = 1L;
        int newScore = 2;

        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("name");

        Artist existingArtist = new Artist("Azadeh", "Painting", 1, judge);
        existingArtist.setId(artistId);

        Artist savedArtist = new Artist("Azadeh", "Painting", newScore, judge);
        savedArtist.setId(artistId);

        when(scoringProperties.getBaseUrl()).thenReturn("");
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);

        artistService.updateArtistScore(artistId, newScore);

        verifyNoInteractions(scoringClient);
    }

    @Test
    public void test_updateArtistScore_doesNotSubmitToScoringClient_whenBaseUrlNull() {
        long artistId = 1L;
        int newScore = 2;

        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("name");

        Artist existingArtist = new Artist("Azadeh", "Painting", 1, judge);
        existingArtist.setId(artistId);

        Artist savedArtist = new Artist("Azadeh", "Painting", newScore, judge);
        savedArtist.setId(artistId);

        when(scoringProperties.getBaseUrl()).thenReturn(null);
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);

        Artist result = artistService.updateArtistScore(artistId, newScore);

        assertEquals(Integer.valueOf(newScore), result.getScore());
        verify(artistRepository).findById(artistId);
        verify(artistRepository).save(any(Artist.class));
        verifyNoInteractions(scoringClient);
    }

    @Test
    public void test_getAllArtists_byJudge() {
        Judge judge = new Judge();
        judge.setId(1L);

        Artist artist1 = new Artist();
        artist1.setName("Artist1");
        artist1.setJudge(judge);

        Artist artist2 = new Artist();
        artist2.setName("Artist2");
        artist2.setJudge(judge);

        List<Artist> mockArtists = asList(artist1, artist2);
        when(artistRepository.findByJudge(judge)).thenReturn(mockArtists);

        List<Artist> result = artistService.getArtistsByJudge(judge);
        assertThat(result).containsExactly(artist1, artist2);
    }

    @Test
    public void test_getArtistsByJudge_returnsEmptyList_whenNoArtistsExist() {
        Judge judge = new Judge();
        judge.setId(1L);

        when(artistRepository.findByJudge(judge)).thenReturn(asList());

        List<Artist> result = artistService.getArtistsByJudge(judge);
        assertThat(result).isEmpty();
    }

    @Test
    public void test_getHighestScore_returnsHighestScore_whenNotNull() {
        Artist artist = new Artist();
        artist.setName("Azadeh");
        artist.setScore(3);

        when(artistRepository.findByScoreGreaterThan(2)).thenReturn(asList(artist));

        List<Artist> result = artistService.getArtistWithHighestScore();
        assertThat(result).containsExactly(artist);
    }

    @Test
    public void test_getArtistsWithHighScores_returnsEmptyList_whenNoMatch() {
        when(artistRepository.findByScoreGreaterThan(2)).thenReturn(asList());

        List<Artist> result = artistService.getArtistWithHighestScore();
        assertThat(result).isEmpty();
    }

    @Test
    public void test_deleteArtistById_deleteArtistsWhenExists() {
        long artistId = 1L;

        Artist artist = new Artist();
        artist.setId(artistId);
        artist.setName("Azadeh");

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));

        artistService.deleteArtistById(artistId);

        verify(artistRepository).deleteById(artistId);
    }

    @Test
    public void test_getEmployeeById_found() {
        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("name");

        Artist artist = new Artist("Azadeh", "Painting", 3, judge);

        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));

        assertThat(artistService.getArtistById(1L)).isSameAs(artist);
    }

    @Test
    public void test_getEmployeeById_notFound() {
        when(artistRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThat(artistService.getArtistById(1L)).isNull();
    }

    @Test
    public void test_updateArtistScore_throwsException_whenArtistNotFound() {
        long missingId = 999L;

        when(artistRepository.findById(missingId)).thenReturn(Optional.empty());

        try {
            artistService.updateArtistScore(missingId, 2);
        } catch (RuntimeException e) {
            assertEquals("Artist not found", e.getMessage());
        }

        verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    public void test_deleteArtistById_throwsExceptionWhenArtistNotFound() {
        long artistId = 999L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        try {
            artistService.deleteArtistById(artistId);
            org.junit.Assert.fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            org.junit.Assert.assertEquals("Artist Not found", e.getMessage());
        }

        verify(artistRepository).findById(artistId);
        verify(artistRepository, never()).deleteById(anyLong());
    }
}
