package com.grepp.smartwatcha.app.controller.web.search;

import com.grepp.smartwatcha.app.controller.api.search.SmartSearchApi;
import com.grepp.smartwatcha.app.controller.api.search.payload.SmartSearchApiRequest;
import com.grepp.smartwatcha.app.controller.api.search.payload.SmartSearchApiResponse;
import com.grepp.smartwatcha.app.model.search.SearchService;
import com.grepp.smartwatcha.app.model.search.dto.SearchResultDto;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.response.PageResponse;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Value("${app.internalToken}")
    private String token;

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
            @RequestParam(required = true, defaultValue = "0") Integer page,
            @RequestParam(required = true, defaultValue = "9") Integer size,
            @AuthenticationPrincipal UserDetails user,
            Model model
    ){

        String queryKey = searchService.buildQueryKey(type, intent, query);
        String queryHash = searchService.hash(queryKey);
        if (searchService.isHash(queryHash)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
            Page<SearchResultDto> pages = searchService.getPaged(queryHash, pageable);
            PageResponse<SearchResultDto> searchResultDtos = new PageResponse<>("/search/result/paged", pages, 5);

            model.addAttribute("searchResult", searchResultDtos);
            model.addAttribute("queryHash", queryHash);
            return "search/search-result";
        }

        List<Long> ids = new ArrayList<>();

        if (type.equals("generalSearch")) {
            switch (intent.toLowerCase()) {
                case "title":
                    ids = searchService.findByTitle(query);
                    break;
                case "year":
                    ids = searchService.findByYear(Integer.parseInt(query));
                    break;
                case "country":
                    ids = searchService.findByCountry(query);
                    break;
                case "genre":
                    ids = searchService.findByGenre(query);
                    break;
                case "director":
                    ids = searchService.findByDirector(query);
                    break;
                case "writer":
                    ids = searchService.findByWriter(query);
                    break;
                case "actor":
                    ids = searchService.findByActor(query);
                    break;
                default:
                    throw new CommonException(ResponseCode.BAD_REQUEST, new RuntimeException("잘못된 검색 태그입니다."));
            }
        } else if (type.equals("smartSearch")) {
            if (user == null) {
                throw new AccessDeniedException("Smart search requires login.");
            }

            if(intent.equals("recommend") || intent.equals("search")) {

                SmartSearchApiRequest request = new SmartSearchApiRequest();
                request.setIntent(intent);
                request.setQuery(query);

                try{
                    SmartSearchApiResponse response = smartSearchApi.call(token, request);
                    log.info("smartSearch response: {}", response);
                    ids = response.getMovieIds().stream()
                            .map(SmartSearchApiResponse.MovieWrapper::getMovie)
                            .toList();

                } catch (Exception e){
                    throw new CommonException(ResponseCode.BAD_REQUEST, e);
                }
            } else {
                throw new CommonException(ResponseCode.BAD_REQUEST, new RuntimeException("잘못된 검색 태그입니다."));
            }
        }

        List<SearchResultDto> results = searchService.searchAndCache(queryHash, ids);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<SearchResultDto> pages = searchService.getPaged(queryHash, pageable);
        PageResponse<SearchResultDto> searchResultDtos = new PageResponse<>("/search/result/paged", pages, 5);

        model.addAttribute("searchResult", searchResultDtos);
        model.addAttribute("queryHash", queryHash);
        return "search/search-result";
    }

    @GetMapping("result/paged")
    public String searchResult(
            @RequestParam(required = true, defaultValue = "0") Integer page,
            @RequestParam(required = true, defaultValue = "9") Integer size,
            @RequestParam String queryHash,
            Model model
    ){

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<SearchResultDto> pages = searchService.getPaged(queryHash, pageable);
        PageResponse<SearchResultDto> searchResultDtos = new PageResponse<>("/search/result/paged", pages, 5);

        model.addAttribute("searchResult", searchResultDtos);
        model.addAttribute("queryHash", queryHash);
        return "search/search-result";
    }

}
