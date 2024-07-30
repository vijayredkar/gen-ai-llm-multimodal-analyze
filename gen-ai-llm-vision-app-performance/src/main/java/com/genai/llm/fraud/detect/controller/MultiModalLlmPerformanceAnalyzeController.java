package com.genai.llm.fraud.detect.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genai.llm.fraud.detect.service.ImageAnalyze;
import com.genai.llm.fraud.detect.service.RetrievalService;

@RestController
@RequestMapping(value = "/gen-ai/v1/llm")
public class MultiModalLlmPerformanceAnalyzeController  
{	
	@Autowired
	private RetrievalService retrievalSvc;

	@GetMapping("/vision-examine")
	public ResponseEntity<String> visionExamineSimple(@RequestParam(defaultValue = "") String systemMsg, @RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "") String imgSrc, @RequestParam(defaultValue = "no-file-to-load") String file, @RequestParam(defaultValue = "-1") int maxLimit, @RequestParam(defaultValue = "false") Boolean refreshDb, @RequestParam(defaultValue = "-1") double minScore, @RequestParam(defaultValue = "0.4") Float temperature, @RequestParam(defaultValue = "N") String executeOnLocalMachine) throws IOException, InterruptedException
	{
		String response = retrievalSvc.orchestrateVision(systemMsg, text, imgSrc, maxLimit, minScore, temperature, executeOnLocalMachine);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
		
	@PostMapping("/vision-examine")
	public ResponseEntity<String> visionExamineBig(@RequestBody ImageAnalyze body, @RequestParam(defaultValue = "-1") int maxLimit, @RequestParam(defaultValue = "false") Boolean refreshDb, @RequestParam(defaultValue = "-1") double minScore, @RequestParam(defaultValue = "N") String executeOnLocalMachine) throws IOException, InterruptedException
	{
		String response = retrievalSvc.orchestrateVision(body.getSystemMsg(), body.getText(), body.getImgSrc(), maxLimit, minScore, body.getTemperature(), executeOnLocalMachine);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
