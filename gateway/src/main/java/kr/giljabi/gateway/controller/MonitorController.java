package kr.giljabi.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/monitor")
public class MonitorController {
    @RequestMapping("/list")
    public String monitorView() {
        return "monitor/monitor-list";
    }
}