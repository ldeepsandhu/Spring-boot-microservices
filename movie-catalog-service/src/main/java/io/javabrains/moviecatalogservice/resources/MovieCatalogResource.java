package io.javabrains.moviecatalogservice.resources;

import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.Rating;
import io.javabrains.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource
{
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId)
    {

        List<Rating> ratingList = restTemplate.getForObject("http://ratings-data-service/ratingsData/users/"+userId, UserRating.class).getRatingList();

        return ratingList.stream().map(rating ->
        {
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" +rating.getMovieId(), Movie.class);
            /*Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/" +rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block(); */
            return new CatalogItem(movie.getName(), "test", rating.getRating());
        }).collect(Collectors.toList());

        //return Collections.singletonList(new CatalogItem("Transformer","Action Movie", 4));
    }
}
