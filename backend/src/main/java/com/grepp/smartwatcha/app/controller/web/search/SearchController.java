package com.grepp.smartwatcha.app.controller.web.search;

import com.grepp.smartwatcha.app.controller.api.search.SmartSearchApi;
import com.grepp.smartwatcha.app.controller.api.search.payload.SmartSearchApiRequest;
import com.grepp.smartwatcha.app.controller.api.search.payload.SmartSearchResponse;
import com.grepp.smartwatcha.app.model.search.SearchService;
import com.grepp.smartwatcha.app.model.search.service.SearchJpaService;
import com.grepp.smartwatcha.app.model.search.dto.SearchResultDto;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;
    private final SmartSearchApi smartSearchApi;

    @GetMapping
    public String index() {
        return "search/search";
    }

    @GetMapping("result")
    public String searchResult(
            @RequestParam(required = true) String type,
            @RequestParam(required = true) String intent,
            @RequestParam(required = true) String query,
            @AuthenticationPrincipal UserDetails user,
            Model model
    ){

//        if (bindingResult.hasErrors()) {
//            throw new CommonException(ResponseCode.BAD_REQUEST);
//        }

        List<SearchResultDto> searchResultDtos = new ArrayList<>();

        if (type.equals("generalSearch")) {
            switch (intent.toLowerCase()) {
                case "title":
                    searchResultDtos = searchService.findByTitle(query);
                    break;
                case "year":
                    searchResultDtos = searchService.findByYear(Integer.parseInt(query));
                    break;
                case "country":
                    searchResultDtos = searchService.findByCountry(query);
                    break;
                case "genre":
                    searchResultDtos = searchService.findByGenre(query);
                    break;
                case "director":
                    searchResultDtos = searchService.findByDirector(query);
                    break;
                case "writer":
                    searchResultDtos = searchService.findByWriter(query);
                    break;
                case "actor":
                    searchResultDtos = searchService.findByActor(query);
                    break;
            }
        } else if (type.equals("smartSearch")) {
            if (user == null) {
                throw new AccessDeniedException("Smart search requires login.");
            }
            String token = "123qwe!@#";

            SmartSearchApiRequest request = new SmartSearchApiRequest();
            request.setIntent(intent);
            request.setQuery(query);
            try{
                SmartSearchResponse response = smartSearchApi.call(token, request);
                log.info("smartSearch response: {}", response);
                List<Long> movieIdList = response.getMovieIds().stream()
                        .map(SmartSearchResponse.MovieWrapper::getMovie)
                        .toList();

                searchResultDtos = searchService.findByIds(movieIdList);
            } catch (Exception e){
                throw new CommonException(ResponseCode.BAD_REQUEST, e);
            }
        }
        log.info("searchResultDtos: {}", searchResultDtos);

        model.addAttribute("searchResult", searchResultDtos);


        return "search/search-result";
    }

}
