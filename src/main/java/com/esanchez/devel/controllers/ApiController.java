package com.esanchez.devel.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.esanchez.devel.domain.services.BashService;

import java.util.Map;

@RestController
public class ApiController {

	@Autowired
	private BashService bashService;
	
    private String param1;
    private String param2;

    // Endpoint para recibir los parámetros del formulario
    @PostMapping("/start-script")
    public ResponseEntity<String> startScript(@RequestBody Map<String, String> params) {
        this.param1 = params.get("param1");
        this.param2 = params.get("param2");

        return ResponseEntity.ok("Script iniciado");
    }

    // Endpoint SSE para ejecutar el script y enviar el output en tiempo real
    @GetMapping("/execute")
    public SseEmitter executeScript() {
    	
    	return bashService.executeCommand(param1, param2);
    }
}
