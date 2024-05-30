package com.exam.controller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.exam.model.User;
import com.exam.model.exam.Quiz;
import com.exam.model.exam.Result;
import com.exam.repo.ResultRepository;
import com.exam.service.ResultService;
import com.exam.service.impl.ResultServiceImpl;
import com.exam.service.impl.S3StorageService;


@RestController
@CrossOrigin
@RequestMapping("/result")
public class ResultController {
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private ResultRepository resultRepository;
	
	@Autowired
	private ResultServiceImpl resultServiceImpl;
	
	@Autowired
	private S3StorageService s3StorageService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@PostMapping("/")
	public Result addResult(@RequestBody Result result) throws IOException, InterruptedException {
		Result submitted= this.resultService.addResult(result);
		
		long  rId=submitted.getrId();
//		final String url="http://localhost:9990/result/"+rId;
//		long  rId=94;
//		Result result2=resultServiceImpl.getResult(rId);
		final String url = "http://localhost:9990/result/" + rId;

	    // Make an HTTP GET request to getResult endpoint
	    Result result2 = restTemplate.getForObject(url, Result.class);
		ByteArrayInputStream s3ResultPdf= resultServiceImpl.createResultPdf(submitted.getrId());
		MultipartFile multipartFile = new MockMultipartFile(
                "file",         // Parameter name (you can change this if needed)
                "filename.txt", // Original filename
                "text/plain",   // Content type
                s3ResultPdf      // InputStream containing the content
        );
		String uploadInS3 =	s3StorageService.uploadFile(multipartFile);
		return submitted;
	}
	
	@GetMapping("/{rId}")
	public Result getResult(@PathVariable("rId") Long rId) {
		return this.resultService.getResult(rId);
	}
	
	@GetMapping("/all/{user_id}")
	public List<Result> getAllResult(@PathVariable ("user_id") User user_id){
		return this.resultService.allResult(user_id);
	}
	@GetMapping("resultByQuiz/{user_id}/{quiz_id}")
	public List<Result> getAllResultByUserAndQuiz(@PathVariable("user_id")User user_id,@PathVariable("quiz_id")Quiz quiz_id){
		return this.resultService.allResultByUserAndQuiz(user_id, quiz_id);
	}
	@GetMapping("/resultPdf/{quizId}")
	public ResponseEntity<InputStreamResource> createPdf(@PathVariable("quizId") Long quizId) {
		ByteArrayInputStream resultPdf=resultService.createResultPdf(quizId);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Disposition", "inline;file=Quiz-Result.pdf");
		return ResponseEntity
				.ok()
				.headers(httpHeaders)
				.contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(resultPdf));
				
		
	}
 
}
