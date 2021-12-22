package ua.com.serverhelp.mytasktracking.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestRest {

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public ResponseEntity<String> test(){
        log.error("test log");
        return ResponseEntity.ok("OK");
    }
}
