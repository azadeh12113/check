package com.example.artwork.controller;



import com.example.artwork.service.ArtistService;
import com.example.artwork.model.Artist;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;
    private static final String ATTR_ARTISTS = "artists";
    private static final String ATTR_MESSAGE = "message";
    private static final String VIEW_ARTISTS = "artists";
    private static final String NO_ARTIST_MESSAGE = "No artist";
    private static final String REDIRECT_ARTISTS = "redirect:/artists";

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    
    }
    @GetMapping
    public String getAllArtists(Model model) {
        List<Artist> artists = artistService.getAllArtists();
        model.addAttribute(ATTR_ARTISTS, artists);
        model.addAttribute(ATTR_MESSAGE, artists.isEmpty() ? NO_ARTIST_MESSAGE : "");
        return VIEW_ARTISTS;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("artist", new Artist());
        return "new-artist"; 
    }

    @PostMapping
    public String createArtist(@ModelAttribute Artist artist) {  	 	
        artistService.createArtist(artist);
        return REDIRECT_ARTISTS;    
    }

    @GetMapping("/delete/{id}")
    public String deleteArtist(@PathVariable Long id) {
        artistService.deleteArtistById(id);
        return REDIRECT_ARTISTS;
    }
    
    @GetMapping("/edit/{id}")
    public String showEditScoreForm(@PathVariable Long id, Model model) {
        Artist artist = artistService.getArtistById(id);
        model.addAttribute("artist", artist);
        model.addAttribute(ATTR_MESSAGE , artist == null ? "No artist found with id: " + id : "");
        return "edit-score"; 
    }

    @PostMapping("/edit/{id}")
    public String updateScore(@PathVariable Long id, @RequestParam int score) {
        artistService.updateArtistScore(id, score);
        return REDIRECT_ARTISTS;
    }
    
    @GetMapping("/highestScore")
    public String showHighestScoreArtists(Model model) {
        List<Artist> topArtists = artistService.getArtistWithHighestScore();
        model.addAttribute(ATTR_ARTISTS, topArtists);
        model.addAttribute(ATTR_MESSAGE, topArtists.isEmpty() ? "No artist found" : "");
        return "high-score";
    }
}
