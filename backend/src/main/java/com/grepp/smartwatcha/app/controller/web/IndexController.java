package com.grepp.smartwatcha.app.controller.web;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendHighestRatedResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendResponse;
import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.index.IndexService;
import com.grepp.smartwatcha.app.model.index.dto.IndexMovieDto;
import com.grepp.smartwatcha.app.model.notification.NotificationJpaService;
import com.grepp.smartwatcha.app.model.recommend.RecommendHighestRatedMovieService;
import com.grepp.smartwatcha.app.model.recommend.RecommendPersonalMovieService;
import com.grepp.smartwatcha.app.model.recommend.RecommendUserBasedMovieService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// TODO RecommendHighestRatedJpaService 사용에서 RecommendHighestRatedMovieService 사용으로 수정하게 되어 밑에 3줄 수정했습니다
//  import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendHighestRatedResponse;
//  private final RecommendUserBasedMovieService userBasedMovieService;,
//  List<MovieRecommendHighestRatedResponse> top10Movies = recommendService.getTop10HighestRatedMovies();

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    private final IndexService indexService;
    private final RecommendHighestRatedMovieService recommendService;
    private final RecommendPersonalMovieService personalMovieService;
    private final RecommendUserBasedMovieService userBasedMovieService;
    private final NotificationJpaService notificationService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        boolean isAdult = true;
        if (userDetails != null && userDetails.getUser() != null) {
            isAdult = Boolean.TRUE.equals(userDetails.getUser().getIsAdult());
        }
        List<IndexMovieDto> newMovies = indexService.findByReleaseDateByAge(isAdult);
        List<IndexMovieDto> randomMovies = indexService.findByRandomByAge(isAdult);
        List<IndexMovieDto> lightMovies = indexService.findLightMovies();
        List<MovieRecommendHighestRatedResponse> top10Movies = recommendService.getTop10HighestRatedMovies();

        if (userDetails != null) {
            List<MovieRecommendResponse> personalTol10Movies = personalMovieService.getTop10PersonalMovies(
                    userDetails.getId());
            List<IndexMovieDto> interestedMovie = indexService.findByInterest(userDetails.getId());
            List<MovieRecommendResponse> userMovies = userBasedMovieService.getTop10UserBasedMovies(
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
        model.addAttribute("isAdult", isAdult);

        return "index";
    }
}
