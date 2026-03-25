package com.example.artwork.controller;

import com.example.artwork.service.ArtistService;
import com.example.artwork.model.Artist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistRestController {

    private final ArtistService artistService;

    @Autowired
    public ArtistRestController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PostMapping("/new")
    public Artist createArtist(@RequestBody Artist artist) {
        return artistService.createArtist(artist); 
    }

    @GetMapping
    public List<Artist> getAllArtists() {
        return artistService.getAllArtists();
    }
    
    @PutMapping("/updateScore/{id}")
    public Artist updateScore(@PathVariable long id, @RequestBody Artist artist) {
        return artistService.updateArtistScore(id, artist.getScore());
    }
    
    
    @GetMapping("/highestScore")
    public List<Artist> getArtistsWithHighestScore() {
        return artistService.getArtistWithHighestScore();
    }
   
    @DeleteMapping("/{id}")
    public void deleteArtist(@PathVariable Long id) {
        artistService.deleteArtistById(id);
    }


}
