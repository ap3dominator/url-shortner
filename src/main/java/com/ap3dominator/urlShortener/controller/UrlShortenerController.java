package com.ap3dominator.urlShortener.controller;

import com.ap3dominator.urlShortener.model.Url;
import com.ap3dominator.urlShortener.model.UrlDto;
import com.ap3dominator.urlShortener.model.UrlErrorResponseDto;
import com.ap3dominator.urlShortener.model.UrlResponseDto;
import com.ap3dominator.urlShortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class UrlShortenerController {
    @Autowired
    private UrlService urlService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto){
        Url urlToRet = urlService.generateShortLink(urlDto);
        if(urlToRet != null){
            UrlResponseDto urlResponseDto = new UrlResponseDto();
            urlResponseDto.setOriginalUrl(urlToRet.getOriginalUrl());
            urlResponseDto.setExpirationDate(urlToRet.getExpirationDate());
            urlResponseDto.setShortLink(urlToRet.getShortLink());

            return new ResponseEntity<UrlResponseDto>(urlResponseDto , HttpStatus.OK);
        }

        UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
        urlErrorResponseDto.setStatus("404");
        urlErrorResponseDto.setError("there was an error processing your request. please try again.");
        return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto , HttpStatus.OK);
    }

    @GetMapping("{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(
                @PathVariable String shortLink ,
                HttpServletResponse response) throws IOException {

        if(StringUtils.isEmpty(shortLink)){
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setStatus("400");
            urlErrorResponseDto.setError("Invalid Url!");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto , HttpStatus.OK);
        }

        Url urlToRet = urlService.getEncodedUrl(shortLink);

        if(urlToRet == null){
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setStatus("400");
            urlErrorResponseDto.setError("Url Does Not Exists, or Expired!");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto , HttpStatus.OK);
        }

        if(urlToRet.getExpirationDate().isBefore(LocalDateTime.now())) {
            urlService.deleteShortLink(urlToRet);
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setStatus("400");
            urlErrorResponseDto.setError("Url is Expired, Please generate new Url!");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
        }

        response.sendRedirect(urlToRet.getOriginalUrl());
        return null;

    }

}
