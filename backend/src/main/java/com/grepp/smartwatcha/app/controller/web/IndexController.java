package com.grepp.smartwatcha.app.controller.web;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendHighestRatedResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendPersonalResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendUserBasedResponse;
import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.index.IndexService;
import com.grepp.smartwatcha.app.model.index.dto.IndexMovieDto;
import com.grepp.smartwatcha.app.model.notification.NotificationService;
import com.grepp.smartwatcha.app.model.recommend.RecommendPersonalMovieService;
import com.grepp.smartwatcha.app.model.recommend.RecommendUserBasedMovieService;
import com.grepp.smartwatcha.app.model.recommend.service.RecommendHighestRatedJpaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    private final IndexService indexService;
    private final RecommendHighestRatedJpaService recommendService;
    private final RecommendPersonalMovieService personalMovieService;
    private final RecommendUserBasedMovieService userBasedMovieService;
    private final NotificationService notificationService;
    
    @GetMapping("/")
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<IndexMovieDto> newMovies = indexService.findByReleaseDate();
        List<IndexMovieDto> randomMovies = indexService.findByRandom();
        List<IndexMovieDto> lightMovies = indexService.findLightMovies();
        List<MovieRecommendHighestRatedResponse> top10Movies = recommendService.getTop10RatedMovies();

        if (userDetails != null) {
            List<MovieRecommendPersonalResponse> personalTol10Movies = personalMovieService.getTop10PersonalMovies(
                    userDetails.getId());
            List<IndexMovieDto> interestedMovie = indexService.findByInterest(userDetails.getId());
            List<MovieRecommendUserBasedResponse> userMovies = userBasedMovieService.getTop10UserBasedMovies(
                    userDetails.getId());

            Long unreadCount = notificationService.countUnread(userDetails.getId());
            model.addAttribute("unreadCount", unreadCount);
            model.addAttribute("userMovies", userMovies);
            model.addAttribute("personalTop10Movies", personalTol10Movies);
            model.addAttribute("interestedMovie", interestedMovie);
        }

        model.addAttribute("newMovies", newMovies);
        model.addAttribute("randomMovies", randomMovies);
        model.addAttribute("lightMovies", lightMovies);
        model.addAttribute("top10Movies", top10Movies);

        return "index";
    }
}
